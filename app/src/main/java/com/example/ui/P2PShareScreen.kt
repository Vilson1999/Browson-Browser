package com.example.ui

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.OpenableColumns
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material.icons.filled.WifiTethering
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.model.DownloadItem
import com.example.p2p.P2pConnectionStatus
import com.example.p2p.P2pDeviceItem
import com.example.p2p.P2pPermissionHelper
import com.example.p2p.P2pReceivedItem
import com.example.p2p.P2pRole
import com.example.p2p.P2pSendPayload
import com.example.p2p.WifiP2pManagerHelper
import com.example.ui.theme.LocalAppTheme
import java.io.File
import kotlin.math.cos
import kotlin.math.sin

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun P2PShareScreen(
    currentWebUrl: String = "",
    currentWebTitle: String = "",
    downloadItems: List<DownloadItem> = emptyList(),
    initialPayload: P2pSendPayload? = null,
    onOpenUrlInBrowser: (String, String) -> Unit = { _, _ -> },
    onClose: () -> Unit
) {
    val context = LocalContext.current
    val appTheme = LocalAppTheme.current

    BackHandler {
        onClose()
    }

    // Wi-Fi Direct Manager Helper
    val p2pHelper = remember { WifiP2pManagerHelper(context) }

    DisposableEffect(Unit) {
        onDispose {
            p2pHelper.stopAll()
        }
    }

    val role by p2pHelper.role.collectAsStateWithLifecycle()
    val status by p2pHelper.status.collectAsStateWithLifecycle()
    val statusMessage by p2pHelper.statusMessage.collectAsStateWithLifecycle()
    val thisDevice by p2pHelper.thisDevice.collectAsStateWithLifecycle()
    val discoveredDevices by p2pHelper.discoveredDevices.collectAsStateWithLifecycle()
    val groupDetails by p2pHelper.groupDetails.collectAsStateWithLifecycle()
    val transferProgress by p2pHelper.transferProgress.collectAsStateWithLifecycle()
    val receivedItems by p2pHelper.receivedItems.collectAsStateWithLifecycle()

    var selectedTabIndex by remember { mutableIntStateOf(if (initialPayload != null) 0 else 0) }

    // Selected payload for sending
    var currentPayload by remember {
        mutableStateOf<P2pSendPayload?>(
            initialPayload ?: if (currentWebUrl.isNotBlank()) {
                P2pSendPayload(
                    title = currentWebTitle.ifBlank { "Web Page Link" },
                    textContent = currentWebUrl,
                    type = "URL",
                    sizeBytes = currentWebUrl.toByteArray().size.toLong()
                )
            } else null
        )
    }

    // Permissions & System Toggles State
    var hasPermissions by remember { mutableStateOf(P2pPermissionHelper.hasPermissions(context)) }
    var isWifiOn by remember { mutableStateOf(P2pPermissionHelper.isWifiEnabled(context)) }
    var isLocationOn by remember { mutableStateOf(P2pPermissionHelper.isLocationEnabled(context)) }

    // Direct connect dialog
    var showDirectConnectDialog by remember { mutableStateOf(false) }
    var directIpInput by remember { mutableStateOf("192.168.49.1") }

    // Permission Request Launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { results ->
        hasPermissions = results.values.all { it }
        if (hasPermissions) {
            Toast.makeText(context, "Permissions granted! Starting Wi-Fi Direct...", Toast.LENGTH_SHORT).show()
            if (selectedTabIndex == 0) {
                p2pHelper.startSenderDiscovery()
            } else {
                p2pHelper.startReceiverMode()
            }
        } else {
            Toast.makeText(context, "Permissions required for offline device discovery", Toast.LENGTH_LONG).show()
        }
    }

    // File picker launcher
    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            val (name, size) = queryFileInfo(context, uri)
            currentPayload = P2pSendPayload(
                title = name,
                uri = uri,
                sizeBytes = size,
                type = "FILE"
            )
        }
    }

    // Recheck system settings periodically or on tab switch
    LaunchedEffect(selectedTabIndex, hasPermissions, isWifiOn, isLocationOn) {
        hasPermissions = P2pPermissionHelper.hasPermissions(context)
        isWifiOn = P2pPermissionHelper.isWifiEnabled(context)
        isLocationOn = P2pPermissionHelper.isLocationEnabled(context)

        if (hasPermissions && isWifiOn && isLocationOn) {
            if (selectedTabIndex == 0) {
                p2pHelper.startSenderDiscovery()
            } else {
                p2pHelper.startReceiverMode()
            }
        }
    }

    val backgroundColor = when {
        appTheme.isAmoled -> Color(0xFF000000)
        appTheme.isDark -> Color(0xFF0F172A)
        else -> Color(0xFFF8F9FA) // 95% clean white
    }

    val cardColor = when {
        appTheme.isAmoled -> Color(0xFF121214)
        appTheme.isDark -> Color(0xFF1E293B)
        else -> Color(0xFFFFFFFF)
    }

    val cardBorder = when {
        appTheme.isAmoled -> Color(0xFF27272A)
        appTheme.isDark -> Color(0xFF334155)
        else -> Color(0xFFE2E8F0)
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding(),
        containerColor = backgroundColor,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Offline P2P Share",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = appTheme.onSurface
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            // Android Version Badge
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(appTheme.primary.copy(alpha = 0.15f))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = P2pPermissionHelper.getAndroidVersionSummary().take(10),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = appTheme.primary
                                )
                            }
                        }
                        Text(
                            text = "Wi-Fi Direct • No Internet Needed",
                            style = MaterialTheme.typography.bodySmall,
                            color = appTheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = onClose,
                        modifier = Modifier.testTag("p2p_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = appTheme.onSurface
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            isWifiOn = P2pPermissionHelper.isWifiEnabled(context)
                            isLocationOn = P2pPermissionHelper.isLocationEnabled(context)
                            hasPermissions = P2pPermissionHelper.hasPermissions(context)
                            if (selectedTabIndex == 0) p2pHelper.startSenderDiscovery() else p2pHelper.startReceiverMode()
                            Toast.makeText(context, "Radar Refreshed", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.testTag("p2p_refresh_button")
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Refresh,
                            contentDescription = "Refresh Radar",
                            tint = appTheme.primary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = backgroundColor
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Diagnostic warnings if permissions or toggles are missing
            if (!hasPermissions || !isWifiOn || (!isLocationOn && P2pPermissionHelper.isLocationRequirementActive())) {
                P2pSystemChecksCard(
                    hasPermissions = hasPermissions,
                    isWifiOn = isWifiOn,
                    isLocationOn = isLocationOn,
                    onRequestPermissions = {
                        permissionLauncher.launch(P2pPermissionHelper.getRequiredPermissions())
                    },
                    onOpenWifiSettings = { P2pPermissionHelper.openWifiSettings(context) },
                    onOpenLocationSettings = { P2pPermissionHelper.openLocationSettings(context) },
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }

            // Sender / Receiver Dual Mode TabRow
            SecondaryTabRow(
                selectedTabIndex = selectedTabIndex,
                containerColor = backgroundColor,
                contentColor = appTheme.primary,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Tab(
                    selected = selectedTabIndex == 0,
                    onClick = {
                        selectedTabIndex = 0
                        p2pHelper.startSenderDiscovery()
                    },
                    text = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Send,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Send Files",
                                fontWeight = if (selectedTabIndex == 0) FontWeight.Bold else FontWeight.Medium
                            )
                        }
                    },
                    modifier = Modifier.testTag("p2p_tab_send")
                )
                Tab(
                    selected = selectedTabIndex == 1,
                    onClick = {
                        selectedTabIndex = 1
                        p2pHelper.startReceiverMode()
                    },
                    text = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.FileDownload,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Receive Files",
                                fontWeight = if (selectedTabIndex == 1) FontWeight.Bold else FontWeight.Medium
                            )
                        }
                    },
                    modifier = Modifier.testTag("p2p_tab_receive")
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Main Content based on Tab
            if (selectedTabIndex == 0) {
                // SENDER TAB CONTENT
                SenderTabContent(
                    payload = currentPayload,
                    discoveredDevices = discoveredDevices,
                    status = status,
                    statusMessage = statusMessage,
                    transferProgress = transferProgress,
                    onPickFile = { filePickerLauncher.launch("*/*") },
                    onSendCurrentPage = {
                        if (currentWebUrl.isNotBlank()) {
                            currentPayload = P2pSendPayload(
                                title = currentWebTitle.ifBlank { "Web Page" },
                                textContent = currentWebUrl,
                                type = "URL",
                                sizeBytes = currentWebUrl.toByteArray().size.toLong()
                            )
                            Toast.makeText(context, "Ready to send current page link", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(context, "No web page currently open", Toast.LENGTH_SHORT).show()
                        }
                    },
                    onDeviceSelected = { device ->
                        val p = currentPayload
                        if (p != null) {
                            p2pHelper.connectAndSend(device, p)
                        } else {
                            Toast.makeText(context, "Please select a file or link to send first", Toast.LENGTH_SHORT).show()
                        }
                    },
                    onDirectConnectClick = { showDirectConnectDialog = true },
                    onResetTransfer = { p2pHelper.resetTransferState() },
                    cardColor = cardColor,
                    cardBorder = cardBorder,
                    modifier = Modifier.weight(1f)
                )
            } else {
                // RECEIVER TAB CONTENT
                ReceiverTabContent(
                    status = status,
                    statusMessage = statusMessage,
                    groupDetails = groupDetails,
                    transferProgress = transferProgress,
                    receivedItems = receivedItems,
                    onOpenFile = { item ->
                        openReceivedFile(context, item, onOpenUrlInBrowser)
                    },
                    onShareFile = { item ->
                        shareReceivedFile(context, item)
                    },
                    cardColor = cardColor,
                    cardBorder = cardBorder,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }

    // Direct IP Dialog fallback
    if (showDirectConnectDialog) {
        AlertDialog(
            onDismissRequest = { showDirectConnectDialog = false },
            title = { Text("Direct Wi-Fi Transfer") },
            text = {
                Column {
                    Text(
                        "If the receiver device is not showing on the radar, ensure the other device is in 'Receive' mode and tap 'Send Direct'.",
                        fontSize = 13.sp,
                        color = appTheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = directIpInput,
                        onValueChange = { directIpInput = it },
                        label = { Text("Receiver IP Address") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val p = currentPayload
                        if (p != null) {
                            showDirectConnectDialog = false
                            p2pHelper.sendDirectToIp(directIpInput.trim(), p)
                        } else {
                            Toast.makeText(context, "Select a file first", Toast.LENGTH_SHORT).show()
                        }
                    }
                ) {
                    Text("Send Direct")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showDirectConnectDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun P2pSystemChecksCard(
    hasPermissions: Boolean,
    isWifiOn: Boolean,
    isLocationOn: Boolean,
    onRequestPermissions: () -> Unit,
    onOpenWifiSettings: () -> Unit,
    onOpenLocationSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF2F2)),
        border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(Color(0xFFF87171), Color(0xFFEF4444))))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Filled.Warning,
                    contentDescription = null,
                    tint = Color(0xFFDC2626),
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Action Required for Wi-Fi Direct",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = Color(0xFF991B1B)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))

            if (!hasPermissions) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "• Permissions Missing",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFFB91C1C)
                        )
                        Text(
                            text = P2pPermissionHelper.getPermissionRequirementsDescription(),
                            fontSize = 11.sp,
                            color = Color(0xFF7F1D1D)
                        )
                    }
                    Button(
                        onClick = onRequestPermissions,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626)),
                        modifier = Modifier.padding(start = 8.dp)
                    ) {
                        Text("Grant", fontSize = 12.sp)
                    }
                }
                Spacer(modifier = Modifier.height(6.dp))
            }

            if (!isWifiOn) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "• Wi-Fi is OFF (Must be turned ON)",
                        fontSize = 13.sp,
                        color = Color(0xFFB91C1C)
                    )
                    OutlinedButton(onClick = onOpenWifiSettings) {
                        Text("Turn On", fontSize = 12.sp)
                    }
                }
                Spacer(modifier = Modifier.height(6.dp))
            }

            if (!isLocationOn && P2pPermissionHelper.isLocationRequirementActive()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "• Location (GPS) is OFF",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFFB91C1C)
                        )
                        Text(
                            text = "Android 9–12 requires System Location toggle to be ON for Wi-Fi P2P scan results.",
                            fontSize = 11.sp,
                            color = Color(0xFF7F1D1D)
                        )
                    }
                    OutlinedButton(
                        onClick = onOpenLocationSettings,
                        modifier = Modifier.padding(start = 8.dp)
                    ) {
                        Text("Enable GPS", fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun SenderTabContent(
    payload: P2pSendPayload?,
    discoveredDevices: List<P2pDeviceItem>,
    status: P2pConnectionStatus,
    statusMessage: String,
    transferProgress: com.example.p2p.P2pTransferProgress,
    onPickFile: () -> Unit,
    onSendCurrentPage: () -> Unit,
    onDeviceSelected: (P2pDeviceItem) -> Unit,
    onDirectConnectClick: () -> Unit,
    onResetTransfer: () -> Unit,
    cardColor: Color,
    cardBorder: Color,
    modifier: Modifier = Modifier
) {
    val appTheme = LocalAppTheme.current

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // 1. Select Payload Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = cardColor),
                border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(cardBorder, cardBorder)))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "1. Select Item to Send",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = appTheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = onPickFile,
                            colors = ButtonDefaults.buttonColors(containerColor = appTheme.primary),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("p2p_pick_file_button")
                        ) {
                            Icon(Icons.Filled.FolderOpen, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Pick File", fontSize = 13.sp)
                        }

                        OutlinedButton(
                            onClick = onSendCurrentPage,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("p2p_send_page_button")
                        ) {
                            Icon(Icons.Filled.Link, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Share Page", fontSize = 13.sp)
                        }
                    }

                    if (payload != null) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(appTheme.primary.copy(alpha = 0.1f))
                                .padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = if (payload.type == "FILE") Icons.Filled.Description else Icons.Filled.Link,
                                contentDescription = null,
                                tint = appTheme.primary,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = payload.title,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 13.sp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    color = appTheme.onSurface
                                )
                                Text(
                                    text = if (payload.sizeBytes > 0) formatFileSize(payload.sizeBytes) else "Ready to stream",
                                    fontSize = 11.sp,
                                    color = appTheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }

        // 2. Transfer Progress Banner
        if (transferProgress.isTransferring || transferProgress.isCompleted || transferProgress.isError) {
            item {
                TransferProgressCard(
                    progress = transferProgress,
                    onDismiss = onResetTransfer,
                    cardColor = cardColor,
                    cardBorder = cardBorder
                )
            }
        }

        // 3. Radar Visual Scanner Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = cardColor),
                border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(cardBorder, cardBorder)))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "2. Nearby Radar Scanner",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = appTheme.onSurface,
                        modifier = Modifier.align(Alignment.Start)
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    // Radar Animation
                    RadarAnimationCanvas(
                        isScanning = status == P2pConnectionStatus.SCANNING,
                        radarColor = appTheme.primary,
                        modifier = Modifier.size(160.dp)
                    )

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = statusMessage,
                        fontSize = 12.sp,
                        color = appTheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        // 4. Discovered Devices List Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Discovered Receivers (${discoveredDevices.size})",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = appTheme.onSurface
                )
                Text(
                    text = "Tap to Send",
                    fontSize = 12.sp,
                    color = appTheme.primary,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        if (discoveredDevices.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = cardColor),
                    border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(cardBorder, cardBorder)))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Devices,
                            contentDescription = null,
                            tint = appTheme.onSurfaceVariant.copy(alpha = 0.5f),
                            modifier = Modifier.size(40.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "No Browson receivers detected yet",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 13.sp,
                            color = appTheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Open this app on the second phone and tap 'Receive Files'. Both devices will link automatically over Wi-Fi Direct.",
                            fontSize = 12.sp,
                            color = appTheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        OutlinedButton(
                            onClick = onDirectConnectClick,
                            modifier = Modifier.testTag("p2p_direct_connect_button")
                        ) {
                            Icon(Icons.Filled.WifiTethering, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Direct IP Send Fallback", fontSize = 12.sp)
                        }
                    }
                }
            }
        } else {
            items(discoveredDevices) { device ->
                DiscoveredDeviceCard(
                    device = device,
                    payload = payload,
                    onSelect = { onDeviceSelected(device) },
                    cardColor = cardColor,
                    cardBorder = cardBorder
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
fun ReceiverTabContent(
    status: P2pConnectionStatus,
    statusMessage: String,
    groupDetails: com.example.p2p.P2pGroupDetails?,
    transferProgress: com.example.p2p.P2pTransferProgress,
    receivedItems: List<P2pReceivedItem>,
    onOpenFile: (P2pReceivedItem) -> Unit,
    onShareFile: (P2pReceivedItem) -> Unit,
    cardColor: Color,
    cardBorder: Color,
    modifier: Modifier = Modifier
) {
    val appTheme = LocalAppTheme.current

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // 1. Receiver Beacon Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = cardColor),
                border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(cardBorder, cardBorder)))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF10B981).copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.WifiTethering,
                            contentDescription = null,
                            tint = Color(0xFF059669),
                            modifier = Modifier.size(36.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Ready to Receive Files",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = appTheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Autonomous Group Owner is active. Keep this screen open on this device.",
                        fontSize = 12.sp,
                        color = appTheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = statusMessage,
                        fontSize = 11.sp,
                        color = appTheme.primary,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        // 2. Incoming transfer banner
        if (transferProgress.isTransferring || transferProgress.isCompleted || transferProgress.isError) {
            item {
                TransferProgressCard(
                    progress = transferProgress,
                    onDismiss = {},
                    cardColor = cardColor,
                    cardBorder = cardBorder
                )
            }
        }

        // 3. Wi-Fi Direct Group Info Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = cardColor),
                border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(cardBorder, cardBorder)))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Filled.Info,
                            contentDescription = null,
                            tint = appTheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Wi-Fi Direct Group Details",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = appTheme.onSurface
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))

                    val networkName = groupDetails?.networkName?.ifBlank { "DIRECT-Browson-Host" } ?: "DIRECT-Browson-Host"
                    val ip = groupDetails?.hostIp?.ifBlank { "192.168.49.1" } ?: "192.168.49.1"

                    DetailRow(label = "Network (SSID)", value = networkName)
                    DetailRow(label = "Receiver IP", value = "$ip : 8988")
                    if (!groupDetails?.passphrase.isNullOrBlank()) {
                        DetailRow(label = "Direct Passcode", value = groupDetails?.passphrase ?: "")
                    }
                }
            }
        }

        // 4. Received Files Section
        item {
            Text(
                text = "Received Files (${receivedItems.size})",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = appTheme.onSurface
            )
        }

        if (receivedItems.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = cardColor),
                    border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(cardBorder, cardBorder)))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Filled.FileDownload,
                            contentDescription = null,
                            tint = appTheme.onSurfaceVariant.copy(alpha = 0.4f),
                            modifier = Modifier.size(36.dp)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "No files received yet",
                            fontSize = 13.sp,
                            color = appTheme.onSurfaceVariant
                        )
                    }
                }
            }
        } else {
            items(receivedItems) { item ->
                ReceivedItemCard(
                    item = item,
                    onOpen = { onOpenFile(item) },
                    onShare = { onShareFile(item) },
                    cardColor = cardColor,
                    cardBorder = cardBorder
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
fun DiscoveredDeviceCard(
    device: P2pDeviceItem,
    payload: P2pSendPayload?,
    onSelect: () -> Unit,
    cardColor: Color,
    cardBorder: Color
) {
    val appTheme = LocalAppTheme.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelect() }
            .testTag("p2p_device_${device.deviceAddress}"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor),
        border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(cardBorder, cardBorder)))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(appTheme.primary.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.PhoneAndroid,
                        contentDescription = null,
                        tint = appTheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = device.deviceName,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = appTheme.onSurface
                    )
                    Text(
                        text = "${device.statusText} • ${device.deviceAddress}",
                        fontSize = 11.sp,
                        color = appTheme.onSurfaceVariant
                    )
                }
            }

            Button(
                onClick = onSelect,
                colors = ButtonDefaults.buttonColors(containerColor = appTheme.primary),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Send", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun ReceivedItemCard(
    item: P2pReceivedItem,
    onOpen: () -> Unit,
    onShare: () -> Unit,
    cardColor: Color,
    cardBorder: Color
) {
    val appTheme = LocalAppTheme.current

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor),
        border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(cardBorder, cardBorder)))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (item.type == "FILE") Color(0xFF3B82F6).copy(alpha = 0.12f) else Color(0xFF10B981).copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (item.type == "FILE") Icons.Filled.Description else Icons.Filled.Link,
                        contentDescription = null,
                        tint = if (item.type == "FILE") Color(0xFF2563EB) else Color(0xFF059669),
                        modifier = Modifier.size(22.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = item.fileName,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = appTheme.onSurface
                    )
                    Text(
                        text = if (item.sizeBytes > 0) formatFileSize(item.sizeBytes) else item.textContent ?: "",
                        fontSize = 11.sp,
                        color = appTheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                OutlinedButton(
                    onClick = onShare,
                    modifier = Modifier.padding(end = 8.dp)
                ) {
                    Icon(Icons.Filled.Share, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Share", fontSize = 11.sp)
                }
                Button(
                    onClick = onOpen,
                    colors = ButtonDefaults.buttonColors(containerColor = appTheme.primary)
                ) {
                    Text(if (item.type == "URL") "Open Link" else "Open File", fontSize = 11.sp)
                }
            }
        }
    }
}

@Composable
fun TransferProgressCard(
    progress: com.example.p2p.P2pTransferProgress,
    onDismiss: () -> Unit,
    cardColor: Color,
    cardBorder: Color
) {
    val appTheme = LocalAppTheme.current

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor),
        border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(cardBorder, cardBorder)))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (progress.isCompleted) {
                        Icon(Icons.Filled.CheckCircle, contentDescription = null, tint = Color(0xFF10B981), modifier = Modifier.size(18.dp))
                    } else if (progress.isError) {
                        Icon(Icons.Filled.ErrorOutline, contentDescription = null, tint = Color(0xFFEF4444), modifier = Modifier.size(18.dp))
                    } else {
                        CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp, color = appTheme.primary)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (progress.isCompleted) "Transfer Complete" else if (progress.isError) "Transfer Error" else "Transferring...",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = appTheme.onSurface
                    )
                }
                IconButton(onClick = onDismiss, modifier = Modifier.size(24.dp)) {
                    Icon(Icons.Filled.Close, contentDescription = "Dismiss", modifier = Modifier.size(16.dp))
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = progress.fileName,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = appTheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(6.dp))

            LinearProgressIndicator(
                progress = { progress.progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = if (progress.isCompleted) Color(0xFF10B981) else appTheme.primary,
                trackColor = appTheme.onSurface.copy(alpha = 0.1f)
            )

            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${(progress.progress * 100).toInt()}% • ${String.format(java.util.Locale.US, "%.1f", progress.speedKBps)} KB/s",
                    fontSize = 11.sp,
                    color = appTheme.onSurfaceVariant
                )
                Text(
                    text = progress.statusMessage,
                    fontSize = 11.sp,
                    color = appTheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun DetailRow(label: String, value: String) {
    val appTheme = LocalAppTheme.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontSize = 12.sp, color = appTheme.onSurfaceVariant)
        Text(text = value, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = appTheme.onSurface)
    }
}

@Composable
fun RadarAnimationCanvas(
    isScanning: Boolean,
    radarColor: Color,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "radar")
    val sweepAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2400, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "sweep"
    )

    val pulseRadius by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1800, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "pulse"
    )

    Canvas(modifier = modifier) {
        val center = Offset(size.width / 2f, size.height / 2f)
        val maxRadius = size.minDimension / 2f

        // Concentric radar circles
        drawCircle(color = radarColor.copy(alpha = 0.1f), radius = maxRadius * 0.33f, center = center, style = Stroke(width = 1.dp.toPx()))
        drawCircle(color = radarColor.copy(alpha = 0.15f), radius = maxRadius * 0.66f, center = center, style = Stroke(width = 1.dp.toPx()))
        drawCircle(color = radarColor.copy(alpha = 0.25f), radius = maxRadius, center = center, style = Stroke(width = 1.5.dp.toPx()))

        // Crosshairs
        drawLine(
            color = radarColor.copy(alpha = 0.15f),
            start = Offset(center.x, center.y - maxRadius),
            end = Offset(center.x, center.y + maxRadius),
            strokeWidth = 1.dp.toPx()
        )
        drawLine(
            color = radarColor.copy(alpha = 0.15f),
            start = Offset(center.x - maxRadius, center.y),
            end = Offset(center.x + maxRadius, center.y),
            strokeWidth = 1.dp.toPx()
        )

        // Pulsing circle
        if (isScanning) {
            drawCircle(
                color = radarColor.copy(alpha = (1f - pulseRadius) * 0.35f),
                radius = maxRadius * pulseRadius,
                center = center
            )

            // Sweeping radar beam
            val rad = Math.toRadians(sweepAngle.toDouble())
            val endX = center.x + maxRadius * cos(rad).toFloat()
            val endY = center.y + maxRadius * sin(rad).toFloat()

            drawLine(
                brush = Brush.radialGradient(
                    colors = listOf(radarColor.copy(alpha = 0.8f), radarColor.copy(alpha = 0.05f)),
                    center = center,
                    radius = maxRadius
                ),
                start = center,
                end = Offset(endX, endY),
                strokeWidth = 2.dp.toPx(),
                cap = StrokeCap.Round
            )
        }

        // Center dot
        drawCircle(color = radarColor, radius = 5.dp.toPx(), center = center)
    }
}

fun queryFileInfo(context: Context, uri: Uri): Pair<String, Long> {
    var name = "Shared_File"
    var size = 0L
    try {
        context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
            if (cursor.moveToFirst()) {
                if (nameIndex != -1) name = cursor.getString(nameIndex) ?: name
                if (sizeIndex != -1) size = cursor.getLong(sizeIndex)
            }
        }
    } catch (_: Exception) {}
    return Pair(name, size)
}

fun formatFileSize(bytes: Long): String {
    if (bytes <= 0) return "0 B"
    val units = arrayOf("B", "KB", "MB", "GB")
    var digitGroups = (Math.log10(bytes.toDouble()) / Math.log10(1024.0)).toInt()
    digitGroups = digitGroups.coerceIn(0, units.size - 1)
    return String.format(java.util.Locale.US, "%.1f %s", bytes / Math.pow(1024.0, digitGroups.toDouble()), units[digitGroups])
}

fun openReceivedFile(context: Context, item: P2pReceivedItem, onOpenUrlInBrowser: (String, String) -> Unit) {
    if (item.type == "URL") {
        val url = item.textContent ?: item.fileName
        onOpenUrlInBrowser(url, item.fileName)
        return
    }

    try {
        val file = File(item.filePath)
        if (!file.exists()) {
            Toast.makeText(context, "File does not exist: ${item.fileName}", Toast.LENGTH_SHORT).show()
            return
        }
        val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, context.contentResolver.getType(uri) ?: "*/*")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(Intent.createChooser(intent, "Open with"))
    } catch (e: Exception) {
        Toast.makeText(context, "Cannot open file: ${e.message}", Toast.LENGTH_SHORT).show()
    }
}

fun shareReceivedFile(context: Context, item: P2pReceivedItem) {
    try {
        val intent = Intent(Intent.ACTION_SEND).apply {
            if (item.type == "URL") {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, item.textContent ?: item.fileName)
            } else {
                val file = File(item.filePath)
                val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
                setDataAndType(uri, context.contentResolver.getType(uri) ?: "*/*")
                putExtra(Intent.EXTRA_STREAM, uri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
        }
        context.startActivity(Intent.createChooser(intent, "Share via"))
    } catch (e: Exception) {
        Toast.makeText(context, "Share error: ${e.message}", Toast.LENGTH_SHORT).show()
    }
}
