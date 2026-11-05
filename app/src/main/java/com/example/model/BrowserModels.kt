package com.example.model

data class SpeedDialItem(
    val id: String,
    val title: String,
    val url: String,
    val initial: String = "",
    val colorHex: Long = 0xFF4285F4,
    val customIconUrl: String? = null
) {
    val faviconUrl: String
        get() = customIconUrl ?: "https://www.google.com/s2/favicons?sz=128&domain_url=${url.trim()}"
}

data class BookmarkFolder(
    val id: String,
    val name: String,
    val colorHex: Long = 0xFF10B981,
    val isDefault: Boolean = false
)

data class BookmarkItem(
    val id: String,
    val title: String,
    val url: String,
    val initial: String = "",
    val colorHex: Long = 0xFF10B981,
    val customIconUrl: String? = null,
    val showOnHomeScreen: Boolean = true,
    val folderId: String = "folder-mobile",
    val folderName: String = "Mobile Bookmarks",
    val timestamp: Long = System.currentTimeMillis()
) {
    val faviconUrl: String
        get() = customIconUrl ?: "https://www.google.com/s2/favicons?sz=128&domain_url=${url.trim()}"
}

data class BrowserTab(
    val id: String,
    val title: String,
    val url: String,
    val isHome: Boolean = true,
    val isIncognito: Boolean = false
)

data class HistoryItem(
    val id: String,
    val title: String,
    val url: String,
    val timestamp: Long = System.currentTimeMillis()
)

enum class DownloadCategory(val label: String, val extensions: List<String>) {
    ALL("all", emptyList()),
    VIDEO("video", listOf("mp4", "mkv", "webm", "avi", "mov")),
    AUDIO("audio", listOf("mp3", "wav", "aac", "ogg", "flac", "m4a")),
    TXT("txt", listOf("txt", "log", "md")),
    PDF("pdf", listOf("pdf")),
    XML("xml", listOf("xml", "json", "html")),
    APK("apk", listOf("apk", "xapk")),
    ZIP("zip", listOf("zip")),
    RAR("rar", listOf("rar")),
    TAR("tar", listOf("tar", "gz", "tar.gz", "7z")),
    OTHER("other", emptyList())
}

data class DownloadItem(
    val id: String,
    val fileName: String,
    val url: String,
    val totalBytes: Long,
    val downloadedBytes: Long,
    val speedBytesPerSec: Long = 0L,
    val isDownloading: Boolean = false,
    val extension: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

