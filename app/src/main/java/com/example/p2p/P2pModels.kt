package com.example.p2p

import android.net.Uri

enum class P2pRole {
    NONE,
    SENDER,
    RECEIVER
}

enum class P2pConnectionStatus {
    IDLE,
    SCANNING,
    CONNECTING,
    CONNECTED,
    ERROR
}

data class P2pDeviceItem(
    val deviceName: String,
    val deviceAddress: String,
    val status: Int,
    val statusText: String,
    val isGroupOwner: Boolean = false
)

data class P2pTransferProgress(
    val fileName: String = "",
    val totalBytes: Long = 0L,
    val bytesTransferred: Long = 0L,
    val progress: Float = 0f,
    val speedKBps: Double = 0.0,
    val isTransferring: Boolean = false,
    val isCompleted: Boolean = false,
    val isError: Boolean = false,
    val statusMessage: String = ""
)

data class P2pGroupDetails(
    val networkName: String = "",
    val passphrase: String = "",
    val hostIp: String = "192.168.49.1",
    val isOwner: Boolean = false,
    val clientCount: Int = 0
)

data class P2pSendPayload(
    val title: String,
    val uri: Uri? = null,
    val textContent: String? = null,
    val sizeBytes: Long = 0L,
    val type: String = "FILE" // FILE, URL, BOOKMARK
)

data class P2pReceivedItem(
    val id: String,
    val fileName: String,
    val filePath: String,
    val sizeBytes: Long,
    val timestamp: Long,
    val type: String, // FILE, URL, BOOKMARK
    val textContent: String? = null
)
