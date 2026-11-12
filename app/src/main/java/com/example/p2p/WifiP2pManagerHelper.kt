package com.example.p2p

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.NetworkInfo
import android.net.Uri
import android.net.wifi.WpsInfo
import android.net.wifi.p2p.WifiP2pConfig
import android.net.wifi.p2p.WifiP2pDevice
import android.net.wifi.p2p.WifiP2pDeviceList
import android.net.wifi.p2p.WifiP2pGroup
import android.net.wifi.p2p.WifiP2pInfo
import android.net.wifi.p2p.WifiP2pManager
import android.os.Environment
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.net.InetSocketAddress
import java.net.ServerSocket
import java.net.Socket
import java.util.UUID

class WifiP2pManagerHelper(private val context: Context) {

    companion object {
        private const val TAG = "WifiP2pHelper"
        const val P2P_PORT = 8988
        const val MAGIC_HEADER = "BROWSON_P2P_V1"
        const val BUFFER_SIZE = 64 * 1024
    }

    private val p2pManager: WifiP2pManager? =
        context.getSystemService(Context.WIFI_P2P_SERVICE) as? WifiP2pManager
    private var p2pChannel: WifiP2pManager.Channel? = null

    private val scope = CoroutineScope(Dispatchers.IO + Job())
    private var serverJob: Job? = null
    private var clientJob: Job? = null
    private var serverSocket: ServerSocket? = null
    private var isReceiverRegistered = false

    // State Flows
    private val _role = MutableStateFlow(P2pRole.NONE)
    val role: StateFlow<P2pRole> = _role.asStateFlow()

    private val _status = MutableStateFlow(P2pConnectionStatus.IDLE)
    val status: StateFlow<P2pConnectionStatus> = _status.asStateFlow()

    private val _statusMessage = MutableStateFlow("Ready for offline sharing")
    val statusMessage: StateFlow<String> = _statusMessage.asStateFlow()

    private val _thisDevice = MutableStateFlow<P2pDeviceItem?>(null)
    val thisDevice: StateFlow<P2pDeviceItem?> = _thisDevice.asStateFlow()

    private val _discoveredDevices = MutableStateFlow<List<P2pDeviceItem>>(emptyList())
    val discoveredDevices: StateFlow<List<P2pDeviceItem>> = _discoveredDevices.asStateFlow()

    private val _groupDetails = MutableStateFlow<P2pGroupDetails?>(null)
    val groupDetails: StateFlow<P2pGroupDetails?> = _groupDetails.asStateFlow()

    private val _transferProgress = MutableStateFlow(P2pTransferProgress())
    val transferProgress: StateFlow<P2pTransferProgress> = _transferProgress.asStateFlow()

    private val _receivedItems = MutableStateFlow<List<P2pReceivedItem>>(emptyList())
    val receivedItems: StateFlow<List<P2pReceivedItem>> = _receivedItems.asStateFlow()

    private val _connectedDevice = MutableStateFlow<P2pDeviceItem?>(null)
    val connectedDevice: StateFlow<P2pDeviceItem?> = _connectedDevice.asStateFlow()

    private val p2pReceiver = object : BroadcastReceiver() {
        @SuppressLint("MissingPermission")
        override fun onReceive(ctx: Context, intent: Intent) {
            when (intent.action) {
                WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION -> {
                    val state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1)
                    val isP2pEnabled = state == WifiP2pManager.WIFI_P2P_STATE_ENABLED
                    Log.d(TAG, "P2P State changed: enabled=$isP2pEnabled")
                    if (!isP2pEnabled) {
                        _statusMessage.value = "Wi-Fi Direct is disabled. Turn ON Wi-Fi."
                    }
                }
                WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION -> {
                    if (p2pChannel != null && P2pPermissionHelper.hasPermissions(context)) {
                        try {
                            p2pManager?.requestPeers(p2pChannel) { peerList: WifiP2pDeviceList? ->
                                val devices = peerList?.deviceList?.map { device ->
                                    val statusStr = when (device.status) {
                                        WifiP2pDevice.AVAILABLE -> "Available"
                                        WifiP2pDevice.INVITED -> "Connecting..."
                                        WifiP2pDevice.CONNECTED -> "Connected"
                                        WifiP2pDevice.FAILED -> "Failed"
                                        WifiP2pDevice.UNAVAILABLE -> "Unavailable"
                                        else -> "Unknown"
                                    }
                                    P2pDeviceItem(
                                        deviceName = if (device.deviceName.isNullOrBlank()) "Browson Device" else device.deviceName,
                                        deviceAddress = device.deviceAddress ?: "",
                                        status = device.status,
                                        statusText = statusStr,
                                        isGroupOwner = device.isGroupOwner
                                    )
                                } ?: emptyList()
                                _discoveredDevices.value = devices
                                Log.d(TAG, "Peers updated: ${devices.size} devices found")
                            }
                        } catch (e: SecurityException) {
                            Log.e(TAG, "SecurityException requesting peers", e)
                        }
                    }
                }
                WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION -> {
                    val networkInfo = intent.getParcelableExtra<NetworkInfo>(WifiP2pManager.EXTRA_NETWORK_INFO)
                    val p2pInfo = intent.getParcelableExtra<WifiP2pInfo>(WifiP2pManager.EXTRA_WIFI_P2P_INFO)

                    Log.d(TAG, "Connection changed: isConnected=${networkInfo?.isConnected}, groupFormed=${p2pInfo?.groupFormed}")

                    if (networkInfo?.isConnected == true && p2pInfo != null) {
                        _status.value = P2pConnectionStatus.CONNECTED
                        val hostIp = p2pInfo.groupOwnerAddress?.hostAddress ?: "192.168.49.1"

                        if (p2pInfo.isGroupOwner) {
                            _statusMessage.value = "Group Owner active. Listening on $hostIp:$P2P_PORT"
                        } else {
                            _statusMessage.value = "Connected to Receiver at $hostIp"
                        }

                        // Query group details for SSID / Passphrase
                        if (P2pPermissionHelper.hasPermissions(context) && p2pChannel != null) {
                            try {
                                p2pManager?.requestGroupInfo(p2pChannel) { group: WifiP2pGroup? ->
                                    if (group != null) {
                                        _groupDetails.value = P2pGroupDetails(
                                            networkName = group.networkName ?: "DIRECT-Browson",
                                            passphrase = group.passphrase ?: "",
                                            hostIp = hostIp,
                                            isOwner = group.isGroupOwner,
                                            clientCount = group.clientList?.size ?: 0
                                        )
                                    }
                                }
                            } catch (_: SecurityException) {}
                        }
                    } else {
                        if (_status.value == P2pConnectionStatus.CONNECTED) {
                            _status.value = P2pConnectionStatus.IDLE
                            _statusMessage.value = "Disconnected"
                        }
                    }
                }
                WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION -> {
                    val device = intent.getParcelableExtra<WifiP2pDevice>(WifiP2pManager.EXTRA_WIFI_P2P_DEVICE)
                    if (device != null) {
                        _thisDevice.value = P2pDeviceItem(
                            deviceName = if (device.deviceName.isNullOrBlank()) "My Browson" else device.deviceName,
                            deviceAddress = device.deviceAddress ?: "",
                            status = device.status,
                            statusText = "Self",
                            isGroupOwner = device.isGroupOwner
                        )
                    }
                }
            }
        }
    }

    init {
        initializeChannel()
    }

    private fun initializeChannel() {
        if (p2pChannel == null && p2pManager != null) {
            p2pChannel = p2pManager.initialize(context, context.mainLooper) {
                Log.w(TAG, "Wi-Fi P2P Channel disconnected! Re-initializing...")
                p2pChannel = null
            }
        }
    }

    fun register() {
        if (isReceiverRegistered) return
        val filter = IntentFilter().apply {
            addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION)
            addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION)
            addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION)
            addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION)
        }
        context.registerReceiver(p2pReceiver, filter)
        isReceiverRegistered = true
    }

    fun unregister() {
        if (isReceiverRegistered) {
            try {
                context.unregisterReceiver(p2pReceiver)
            } catch (_: Exception) {}
            isReceiverRegistered = false
        }
    }

    // -------------------------------------------------------------
    // RECEIVER ROLE: Creates Autonomous Group & Starts Server Socket
    // -------------------------------------------------------------
    @SuppressLint("MissingPermission")
    fun startReceiverMode() {
        _role.value = P2pRole.RECEIVER
        _status.value = P2pConnectionStatus.CONNECTING
        _statusMessage.value = "Starting Receiver group..."

        register()
        initializeChannel()

        val channel = p2pChannel ?: return
        val manager = p2pManager ?: return

        if (!P2pPermissionHelper.hasPermissions(context)) {
            _status.value = P2pConnectionStatus.ERROR
            _statusMessage.value = "Permissions missing for Wi-Fi Direct"
            return
        }

        // 1. Remove any old group to start fresh
        manager.removeGroup(channel, object : WifiP2pManager.ActionListener {
            override fun onSuccess() {
                createAutonomousReceiverGroup()
            }
            override fun onFailure(reason: Int) {
                // If no group was active, proceed anyway
                createAutonomousReceiverGroup()
            }
        })
    }

    @SuppressLint("MissingPermission")
    private fun createAutonomousReceiverGroup() {
        val channel = p2pChannel ?: return
        val manager = p2pManager ?: return

        manager.createGroup(channel, object : WifiP2pManager.ActionListener {
            override fun onSuccess() {
                _status.value = P2pConnectionStatus.CONNECTED
                _statusMessage.value = "Receiver group created. Waiting for sender..."
                Log.d(TAG, "Autonomous group created successfully")

                // Keep discovery active so sender can locate receiver on radar
                startDiscoverySilent()

                // Start Server Socket listener
                startServerSocket()
            }

            override fun onFailure(reason: Int) {
                val reasonMsg = when (reason) {
                    WifiP2pManager.P2P_UNSUPPORTED -> "P2P Unsupported on this device"
                    WifiP2pManager.BUSY -> "Wi-Fi Direct is Busy. Try again in a moment."
                    WifiP2pManager.ERROR -> "Wi-Fi Direct system error. Toggle Wi-Fi OFF & ON."
                    else -> "Failed to create group (code $reason)"
                }
                _status.value = P2pConnectionStatus.ERROR
                _statusMessage.value = reasonMsg
                Log.e(TAG, "Failed to create autonomous group: $reasonMsg")

                // Still attempt to start socket on default IP just in case
                startServerSocket()
            }
        })
    }

    private fun startServerSocket() {
        serverJob?.cancel()
        serverJob = scope.launch {
            try {
                serverSocket?.close()
                val server = ServerSocket(P2P_PORT)
                serverSocket = server
                Log.d(TAG, "Server socket listening on port $P2P_PORT")

                while (!server.isClosed) {
                    val clientSocket = server.accept()
                    Log.d(TAG, "Incoming connection from ${clientSocket.inetAddress}")
                    handleIncomingTransfer(clientSocket)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Server socket exception", e)
            }
        }
    }

    private fun handleIncomingTransfer(socket: Socket) {
        scope.launch {
            try {
                socket.soTimeout = 45000
                val dis = DataInputStream(BufferedInputStream(socket.getInputStream()))

                val magic = dis.readUTF()
                if (magic != MAGIC_HEADER) {
                    Log.e(TAG, "Invalid magic header: $magic")
                    socket.close()
                    return@launch
                }

                val payloadType = dis.readUTF()
                val fileName = dis.readUTF()
                val fileSize = dis.readLong()

                Log.d(TAG, "Receiving $payloadType: '$fileName' size=$fileSize bytes")

                _transferProgress.value = P2pTransferProgress(
                    fileName = fileName,
                    totalBytes = fileSize,
                    bytesTransferred = 0L,
                    progress = 0f,
                    speedKBps = 0.0,
                    isTransferring = true,
                    isCompleted = false,
                    isError = false,
                    statusMessage = "Receiving '$fileName'..."
                )

                if (payloadType == "URL" || payloadType == "BOOKMARK" || payloadType == "TEXT") {
                    val textContent = dis.readUTF()
                    val receivedItem = P2pReceivedItem(
                        id = UUID.randomUUID().toString(),
                        fileName = fileName,
                        filePath = "",
                        sizeBytes = fileSize,
                        timestamp = System.currentTimeMillis(),
                        type = payloadType,
                        textContent = textContent
                    )
                    _receivedItems.update { listOf(receivedItem) + it }
                    _transferProgress.value = _transferProgress.value.copy(
                        isTransferring = false,
                        isCompleted = true,
                        progress = 1f,
                        statusMessage = "Received: $fileName"
                    )
                } else {
                    // Binary File Transfer
                    val downloadsDir = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
                        ?: context.filesDir
                    if (!downloadsDir.exists()) downloadsDir.mkdirs()

                    val targetFile = File(downloadsDir, fileName)
                    val fos = FileOutputStream(targetFile)
                    val bos = BufferedOutputStream(fos)

                    val buffer = ByteArray(BUFFER_SIZE)
                    var totalRead = 0L
                    var bytesRead = 0
                    var lastUpdate = System.currentTimeMillis()
                    var lastBytes = 0L

                    while (totalRead < fileSize) {
                        val toRead = minOf(buffer.size.toLong(), fileSize - totalRead).toInt()
                        bytesRead = dis.read(buffer, 0, toRead)
                        if (bytesRead == -1) break
                        bos.write(buffer, 0, bytesRead)
                        totalRead += bytesRead

                        val now = System.currentTimeMillis()
                        if (now - lastUpdate >= 300 || totalRead == fileSize) {
                            val timeDiffSec = (now - lastUpdate) / 1000.0
                            val speed = if (timeDiffSec > 0) ((totalRead - lastBytes) / 1024.0) / timeDiffSec else 0.0
                            val pct = if (fileSize > 0) (totalRead.toFloat() / fileSize.toFloat()).coerceIn(0f, 1f) else 1f

                            _transferProgress.value = _transferProgress.value.copy(
                                bytesTransferred = totalRead,
                                progress = pct,
                                speedKBps = speed,
                                statusMessage = "Receiving: ${(pct * 100).toInt()}%"
                            )
                            lastUpdate = now
                            lastBytes = totalRead
                        }
                    }

                    bos.flush()
                    bos.close()
                    fos.close()

                    val receivedItem = P2pReceivedItem(
                        id = UUID.randomUUID().toString(),
                        fileName = fileName,
                        filePath = targetFile.absolutePath,
                        sizeBytes = totalRead,
                        timestamp = System.currentTimeMillis(),
                        type = "FILE",
                        textContent = null
                    )
                    _receivedItems.update { listOf(receivedItem) + it }

                    _transferProgress.value = _transferProgress.value.copy(
                        bytesTransferred = totalRead,
                        progress = 1f,
                        isTransferring = false,
                        isCompleted = true,
                        statusMessage = "Successfully received '$fileName' (${formatSize(totalRead)})"
                    )
                    Log.d(TAG, "File received successfully: ${targetFile.absolutePath}")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error receiving transfer", e)
                _transferProgress.value = _transferProgress.value.copy(
                    isTransferring = false,
                    isError = true,
                    statusMessage = "Transfer failed: ${e.message}"
                )
            } finally {
                try {
                    socket.close()
                } catch (_: Exception) {}
            }
        }
    }

    // -------------------------------------------------------------
    // SENDER ROLE: Scans Nearby Peers & Sends Payload Over Socket
    // -------------------------------------------------------------
    @SuppressLint("MissingPermission")
    fun startSenderDiscovery() {
        _role.value = P2pRole.SENDER
        _status.value = P2pConnectionStatus.SCANNING
        _statusMessage.value = "Scanning for nearby Browson devices on radar..."
        _discoveredDevices.value = emptyList()

        register()
        initializeChannel()

        val channel = p2pChannel ?: return
        val manager = p2pManager ?: return

        if (!P2pPermissionHelper.hasPermissions(context)) {
            _status.value = P2pConnectionStatus.ERROR
            _statusMessage.value = "Permissions missing for device discovery"
            return
        }

        // Remove any old group on sender to ensure clean scan
        manager.removeGroup(channel, object : WifiP2pManager.ActionListener {
            override fun onSuccess() {
                initiateDiscovery()
            }
            override fun onFailure(reason: Int) {
                initiateDiscovery()
            }
        })
    }

    @SuppressLint("MissingPermission")
    private fun initiateDiscovery() {
        val channel = p2pChannel ?: return
        val manager = p2pManager ?: return

        manager.discoverPeers(channel, object : WifiP2pManager.ActionListener {
            override fun onSuccess() {
                _status.value = P2pConnectionStatus.SCANNING
                _statusMessage.value = "Radar active. Looking for receiving devices..."
                Log.d(TAG, "Peer discovery started successfully")
            }

            override fun onFailure(reason: Int) {
                val reasonMsg = when (reason) {
                    WifiP2pManager.P2P_UNSUPPORTED -> "Wi-Fi Direct unsupported on this device"
                    WifiP2pManager.BUSY -> "Wi-Fi Direct hardware busy. Retrying in 2 seconds..."
                    WifiP2pManager.ERROR -> "Discovery error. Make sure Wi-Fi & Location (GPS on Android 9–12) are ON."
                    else -> "Discovery failed (code $reason)"
                }
                _status.value = P2pConnectionStatus.ERROR
                _statusMessage.value = reasonMsg
                Log.e(TAG, "discoverPeers failed: $reasonMsg")

                // If busy, retry once after short delay
                if (reason == WifiP2pManager.BUSY) {
                    scope.launch {
                        delay(2000)
                        if (_role.value == P2pRole.SENDER) {
                            initiateDiscovery()
                        }
                    }
                }
            }
        })
    }

    @SuppressLint("MissingPermission")
    private fun startDiscoverySilent() {
        val channel = p2pChannel ?: return
        val manager = p2pManager ?: return
        if (P2pPermissionHelper.hasPermissions(context)) {
            try {
                manager.discoverPeers(channel, null)
            } catch (_: Exception) {}
        }
    }

    @SuppressLint("MissingPermission")
    fun connectAndSend(device: P2pDeviceItem, payload: P2pSendPayload) {
        val channel = p2pChannel ?: return
        val manager = p2pManager ?: return

        _status.value = P2pConnectionStatus.CONNECTING
        _connectedDevice.value = device
        _statusMessage.value = "Connecting to '${device.deviceName}'..."

        val config = WifiP2pConfig().apply {
            deviceAddress = device.deviceAddress
            wps.setup = WpsInfo.PBC
            groupOwnerIntent = 0 // Prefer client so receiver stays Group Owner
        }

        manager.connect(channel, config, object : WifiP2pManager.ActionListener {
            override fun onSuccess() {
                _statusMessage.value = "Invitation sent to ${device.deviceName}. Handshaking..."
                Log.d(TAG, "Connect initiated to ${device.deviceAddress}")

                // Poll connection info
                pollConnectionAndSend(payload)
            }

            override fun onFailure(reason: Int) {
                val reasonMsg = when (reason) {
                    WifiP2pManager.BUSY -> "Device is busy. Please try again."
                    else -> "Connection failed (code $reason)"
                }
                _status.value = P2pConnectionStatus.ERROR
                _statusMessage.value = reasonMsg
                Log.e(TAG, "Connect failed: $reasonMsg")
            }
        })
    }

    private fun pollConnectionAndSend(payload: P2pSendPayload) {
        clientJob?.cancel()
        clientJob = scope.launch {
            var attempts = 0
            var connectedIp: String? = null

            while (attempts < 30 && connectedIp == null) {
                delay(1000)
                attempts++

                p2pChannel?.let { channel ->
                    p2pManager?.requestConnectionInfo(channel) { info: WifiP2pInfo? ->
                        if (info != null && info.groupFormed) {
                            connectedIp = info.groupOwnerAddress?.hostAddress ?: "192.168.49.1"
                        }
                    }
                }
            }

            val targetIp = connectedIp ?: "192.168.49.1"
            executeSendStream(targetIp, payload)
        }
    }

    fun sendDirectToIp(targetIp: String, payload: P2pSendPayload) {
        clientJob?.cancel()
        clientJob = scope.launch {
            executeSendStream(targetIp, payload)
        }
    }

    private suspend fun executeSendStream(targetIp: String, payload: P2pSendPayload) {
        _transferProgress.value = P2pTransferProgress(
            fileName = payload.title,
            totalBytes = payload.sizeBytes,
            bytesTransferred = 0L,
            progress = 0f,
            speedKBps = 0.0,
            isTransferring = true,
            isCompleted = false,
            isError = false,
            statusMessage = "Connecting to Receiver ($targetIp:$P2P_PORT)..."
        )

        var socket: Socket? = null
        try {
            // Retry connecting up to 5 times (allowing receiver socket to bind)
            var connected = false
            var tries = 0
            while (!connected && tries < 6) {
                tries++
                try {
                    socket = Socket()
                    socket.connect(InetSocketAddress(targetIp, P2P_PORT), 5000)
                    connected = true
                } catch (e: Exception) {
                    delay(1000)
                }
            }

            if (socket == null || !socket.isConnected) {
                throw Exception("Could not reach receiver at $targetIp:$P2P_PORT. Ensure Receiver screen is active.")
            }

            val dos = DataOutputStream(BufferedOutputStream(socket.getOutputStream()))

            dos.writeUTF(MAGIC_HEADER)
            dos.writeUTF(payload.type)
            dos.writeUTF(payload.title)
            dos.writeLong(payload.sizeBytes)

            if (payload.type == "URL" || payload.type == "BOOKMARK" || payload.type == "TEXT") {
                val text = payload.textContent ?: payload.title
                dos.writeUTF(text)
                dos.flush()

                _transferProgress.value = _transferProgress.value.copy(
                    progress = 1f,
                    isTransferring = false,
                    isCompleted = true,
                    statusMessage = "Successfully sent: ${payload.title}"
                )
            } else if (payload.uri != null) {
                val inputStream: InputStream? = context.contentResolver.openInputStream(payload.uri)
                if (inputStream == null) {
                    throw Exception("Unable to open selected file")
                }

                val buffer = ByteArray(BUFFER_SIZE)
                var bytesSent = 0L
                var read: Int
                var lastUpdate = System.currentTimeMillis()
                var lastBytes = 0L

                val totalSize = payload.sizeBytes

                while (inputStream.read(buffer).also { read = it } != -1) {
                    dos.write(buffer, 0, read)
                    bytesSent += read

                    val now = System.currentTimeMillis()
                    if (now - lastUpdate >= 300 || bytesSent == totalSize) {
                        val timeDiffSec = (now - lastUpdate) / 1000.0
                        val speed = if (timeDiffSec > 0) ((bytesSent - lastBytes) / 1024.0) / timeDiffSec else 0.0
                        val pct = if (totalSize > 0) (bytesSent.toFloat() / totalSize.toFloat()).coerceIn(0f, 1f) else 1f

                        _transferProgress.value = _transferProgress.value.copy(
                            bytesTransferred = bytesSent,
                            progress = pct,
                            speedKBps = speed,
                            statusMessage = "Sending: ${(pct * 100).toInt()}%"
                        )
                        lastUpdate = now
                        lastBytes = bytesSent
                    }
                }

                dos.flush()
                inputStream.close()

                _transferProgress.value = _transferProgress.value.copy(
                    bytesTransferred = bytesSent,
                    progress = 1f,
                    isTransferring = false,
                    isCompleted = true,
                    statusMessage = "Successfully sent '${payload.title}' (${formatSize(bytesSent)})"
                )
            }
        } catch (e: Exception) {
            Log.e(TAG, "Send stream failed", e)
            _transferProgress.value = _transferProgress.value.copy(
                isTransferring = false,
                isError = true,
                statusMessage = "Send failed: ${e.localizedMessage ?: "Connection error"}"
            )
        } finally {
            try {
                socket?.close()
            } catch (_: Exception) {}
        }
    }

    fun resetTransferState() {
        _transferProgress.value = P2pTransferProgress()
    }

    fun stopAll() {
        serverJob?.cancel()
        clientJob?.cancel()

        try {
            serverSocket?.close()
        } catch (_: Exception) {}
        serverSocket = null

        p2pChannel?.let { channel ->
            p2pManager?.let { manager ->
                try {
                    manager.removeGroup(channel, null)
                    manager.stopPeerDiscovery(channel, null)
                } catch (_: Exception) {}
            }
        }

        unregister()
        _role.value = P2pRole.NONE
        _status.value = P2pConnectionStatus.IDLE
        _statusMessage.value = "Idle"
        _connectedDevice.value = null
    }

    private fun formatSize(bytes: Long): String {
        if (bytes <= 0) return "0 B"
        val units = arrayOf("B", "KB", "MB", "GB")
        var digitGroups = (Math.log10(bytes.toDouble()) / Math.log10(1024.0)).toInt()
        digitGroups = digitGroups.coerceIn(0, units.size - 1)
        val formatted = String.format(java.util.Locale.US, "%.1f", bytes / Math.pow(1024.0, digitGroups.toDouble()))
        return "$formatted ${units[digitGroups]}"
    }
}
