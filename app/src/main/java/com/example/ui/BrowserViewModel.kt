package com.example.ui

import androidx.lifecycle.ViewModel
import com.example.model.BookmarkFolder
import com.example.model.BookmarkItem
import com.example.model.BrowserTab
import com.example.model.DownloadItem
import com.example.model.HistoryItem
import com.example.model.SpeedDialItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.UUID

enum class ThemeMode {
    SYSTEM,
    DARK,
    LIGHT,
    AMOLED
}

val DEFAULT_BOOKMARK_FOLDERS = listOf(
    BookmarkFolder("folder-all", "All Bookmarks", 0xFF6366F1, isDefault = true),
    BookmarkFolder("folder-mobile", "Mobile Bookmarks", 0xFF10B981, isDefault = true),
    BookmarkFolder("folder-favorites", "Favorites", 0xFFF59E0B, isDefault = true),
    BookmarkFolder("folder-reading", "Reading List", 0xFF3B82F6, isDefault = true),
    BookmarkFolder("folder-tools", "Work & Tools", 0xFFEC4899, isDefault = true)
)

data class BrowserUiState(
    val currentUrl: String = "",
    val searchInput: String = "",
    val isViewingWeb: Boolean = false,
    val webPageTitle: String = "New Tab",
    val canGoBack: Boolean = false,
    val canGoForward: Boolean = false,
    val speedDials: List<SpeedDialItem> = emptyList(),
    val bookmarks: List<BookmarkItem> = emptyList(),
    val bookmarkFolders: List<BookmarkFolder> = DEFAULT_BOOKMARK_FOLDERS,
    val selectedBookmarkFolderId: String = "folder-all",
    val tabs: List<BrowserTab> = emptyList(),
    val incognitoTabs: List<BrowserTab> = emptyList(),
    val isIncognitoMode: Boolean = false,
    val activeTabId: String = "tab-1",
    val showAddSpeedDialDialog: Boolean = false,
    val showAddBookmarkDialog: Boolean = false,
    val editingSpeedDialItem: SpeedDialItem? = null,
    val editingBookmarkItem: BookmarkItem? = null,
    val showMenuSheet: Boolean = false,
    val showTabsSheet: Boolean = false,
    val lastClosedTab: BrowserTab? = null,
    val showClosedTabPopup: Boolean = false,
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val showSettingsScreen: Boolean = false,
    val showHistoryScreen: Boolean = false,
    val showDownloadScreen: Boolean = false,
    val showBookmarkScreen: Boolean = false,
    val historyItems: List<HistoryItem> = emptyList(),
    val downloadItems: List<DownloadItem> = emptyList(),
    val activeToolMenuIds: List<String> = com.example.model.BrowserToolCatalog.DEFAULT_ACTIVE_IDS,
    val availableToolMenuIds: List<String> = com.example.model.BrowserToolCatalog.DEFAULT_AVAILABLE_IDS,
    val activeToolbarToolIds: List<String> = com.example.model.BrowserToolCatalog.DEFAULT_ACTIVE_TOOLBAR_IDS,
    val availableToolbarToolIds: List<String> = com.example.model.BrowserToolCatalog.DEFAULT_AVAILABLE_TOOLBAR_IDS,
    val reduceAnimations: Boolean = false,
    val animationScale: Float = 0.75f,
    val defaultSearchEngine: String = "Google",
    val uiGradientId: String? = null,
    val wallpaperMode: String = "SYSTEM",
    val wallpaperStyle: String = "mesh_prism",
    val customPrimaryHex: Long? = null,
    val customIconBgHex: Long? = null,
    val isDualColorActive: Boolean = false
)

class BrowserViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(
        BrowserUiState(
            speedDials = listOf(
                SpeedDialItem(UUID.randomUUID().toString(), "Google", "https://www.google.com", "G", 0xFF4285F4),
                SpeedDialItem(UUID.randomUUID().toString(), "YouTube", "https://www.youtube.com", "Y", 0xFFFF0000),
                SpeedDialItem(UUID.randomUUID().toString(), "Wikipedia", "https://www.wikipedia.org", "W", 0xFF2D3748),
                SpeedDialItem(UUID.randomUUID().toString(), "GitHub", "https://github.com", "GH", 0xFF181717),
                SpeedDialItem(UUID.randomUUID().toString(), "Reddit", "https://www.reddit.com", "R", 0xFFFF4500),
                SpeedDialItem(UUID.randomUUID().toString(), "X / Twitter", "https://x.com", "X", 0xFF0F1419),
                SpeedDialItem(UUID.randomUUID().toString(), "Amazon", "https://www.amazon.com", "A", 0xFFFF9900),
                SpeedDialItem(UUID.randomUUID().toString(), "BBC News", "https://www.bbc.com", "B", 0xFFBB1919)
            ),
            bookmarks = listOf(
                BookmarkItem(UUID.randomUUID().toString(), "Android Developers", "https://developer.android.com", "AD", 0xFF3DDC84, folderId = "folder-tools", folderName = "Work & Tools"),
                BookmarkItem(UUID.randomUUID().toString(), "TechCrunch", "https://techcrunch.com", "TC", 0xFF0A9928, folderId = "folder-mobile", folderName = "Mobile Bookmarks"),
                BookmarkItem(UUID.randomUUID().toString(), "Hacker News", "https://news.ycombinator.com", "HN", 0xFFFF6600, folderId = "folder-reading", folderName = "Reading List"),
                BookmarkItem(UUID.randomUUID().toString(), "Medium", "https://medium.com", "M", 0xFF000000, folderId = "folder-reading", folderName = "Reading List"),
                BookmarkItem(UUID.randomUUID().toString(), "Weather", "https://weather.com", "W", 0xFF1B73E8, folderId = "folder-favorites", folderName = "Favorites")
            ),
            tabs = listOf(
                BrowserTab("tab-1", "Home", "", isHome = true)
            ),
            historyItems = listOf(
                HistoryItem(
                    id = UUID.randomUUID().toString(),
                    title = "Google Search",
                    url = "https://www.google.com",
                    timestamp = System.currentTimeMillis() - 5 * 60 * 1000
                ),
                HistoryItem(
                    id = UUID.randomUUID().toString(),
                    title = "Android Developers - Jetpack Compose",
                    url = "https://developer.android.com/jetpack/compose",
                    timestamp = System.currentTimeMillis() - 25 * 60 * 1000
                ),
                HistoryItem(
                    id = UUID.randomUUID().toString(),
                    title = "GitHub - Browson Source Code",
                    url = "https://github.com",
                    timestamp = System.currentTimeMillis() - 80 * 60 * 1000
                ),
                HistoryItem(
                    id = UUID.randomUUID().toString(),
                    title = "Wikipedia - Material Design 3",
                    url = "https://en.wikipedia.org/wiki/Material_Design",
                    timestamp = System.currentTimeMillis() - 180 * 60 * 1000
                ),
                HistoryItem(
                    id = UUID.randomUUID().toString(),
                    title = "Reddit - r/AndroidDev Community",
                    url = "https://www.reddit.com/r/androiddev",
                    timestamp = System.currentTimeMillis() - 24 * 3600 * 1000
                ),
                HistoryItem(
                    id = UUID.randomUUID().toString(),
                    title = "YouTube - Android 15 Full Review",
                    url = "https://www.youtube.com",
                    timestamp = System.currentTimeMillis() - 36 * 3600 * 1000
                )
            ),
            downloadItems = emptyList()
        )
    )
    val uiState: StateFlow<BrowserUiState> = _uiState.asStateFlow()

    fun updateSearchInput(query: String) {
        _uiState.update { it.copy(searchInput = query) }
    }

    fun submitSearch(query: String = _uiState.value.searchInput) {
        val trimmed = query.trim()
        if (trimmed.isEmpty()) return
        val formattedUrl = when {
            trimmed.startsWith("http://") || trimmed.startsWith("https://") -> trimmed
            trimmed.contains(".") && !trimmed.contains(" ") -> "https://$trimmed"
            else -> "https://www.google.com/search?q=${java.net.URLEncoder.encode(trimmed, "UTF-8")}"
        }
        navigateToUrl(formattedUrl, trimmed)
    }

    fun navigateToUrl(url: String, title: String? = null) {
        _uiState.update { state ->
            val pageTitle = title ?: url.removePrefix("https://").removePrefix("http://").split("/").firstOrNull() ?: "Web"
            val newHistoryItem = HistoryItem(
                id = UUID.randomUUID().toString(),
                title = pageTitle,
                url = url,
                timestamp = System.currentTimeMillis()
            )
            val updatedHistory = if (url.isNotBlank()) {
                listOf(newHistoryItem) + state.historyItems.filterNot { it.url == url }
            } else {
                state.historyItems
            }
            state.copy(
                currentUrl = url,
                searchInput = url,
                isViewingWeb = true,
                webPageTitle = pageTitle,
                canGoBack = true,
                canGoForward = false,
                historyItems = updatedHistory
            )
        }
    }

    fun goHome() {
        _uiState.update { state ->
            state.copy(
                currentUrl = "",
                searchInput = "",
                isViewingWeb = false,
                webPageTitle = "Home",
                canGoBack = false,
                canGoForward = false
            )
        }
    }

    fun goBack() {
        if (_uiState.value.isViewingWeb) {
            goHome()
        }
    }

    fun goForward() {
        // Ready for WebView history forward
    }

    // Speed Dial actions
    fun openAddSpeedDialDialog() {
        _uiState.update { it.copy(showAddSpeedDialDialog = true) }
    }

    fun closeAddSpeedDialDialog() {
        _uiState.update { it.copy(showAddSpeedDialDialog = false) }
    }

    fun addSpeedDial(title: String, url: String) {
        if (title.isBlank() || url.isBlank()) return
        val formattedUrl = if (!url.startsWith("http://") && !url.startsWith("https://")) "https://$url" else url
        val initial = title.trim().take(2).uppercase()
        val colors = listOf(0xFFE53935, 0xFF10B981, 0xFF3B82F6, 0xFF8B5CF6, 0xFFF59E0B, 0xFFEC4899)
        val randomColor = colors[(_uiState.value.speedDials.size) % colors.size]
        val newItem = SpeedDialItem(
            id = UUID.randomUUID().toString(),
            title = title.trim(),
            url = formattedUrl,
            initial = initial,
            colorHex = randomColor
        )
        _uiState.update {
            it.copy(
                speedDials = it.speedDials + newItem,
                showAddSpeedDialDialog = false
            )
        }
    }

    fun removeSpeedDial(id: String) {
        _uiState.update { state ->
            state.copy(speedDials = state.speedDials.filterNot { it.id == id })
        }
    }

    fun openEditSpeedDialDialog(item: SpeedDialItem) {
        _uiState.update { it.copy(editingSpeedDialItem = item) }
    }

    fun closeEditSpeedDialDialog() {
        _uiState.update { it.copy(editingSpeedDialItem = null) }
    }

    fun updateSpeedDial(id: String, newTitle: String, newUrl: String) {
        if (newTitle.isBlank() || newUrl.isBlank()) return
        val formattedUrl = if (!newUrl.startsWith("http://") && !newUrl.startsWith("https://")) "https://$newUrl" else newUrl
        val initial = newTitle.trim().take(2).uppercase()
        _uiState.update { state ->
            state.copy(
                speedDials = state.speedDials.map { item ->
                    if (item.id == id) {
                        item.copy(
                            title = newTitle.trim(),
                            url = formattedUrl,
                            initial = initial
                        )
                    } else {
                        item
                    }
                },
                editingSpeedDialItem = null
            )
        }
    }

    // Bookmark actions and screen management
    fun openBookmarkScreen() {
        _uiState.update {
            it.copy(
                showBookmarkScreen = true,
                showMenuSheet = false,
                showTabsSheet = false
            )
        }
    }

    fun closeBookmarkScreen() {
        _uiState.update { it.copy(showBookmarkScreen = false) }
    }

    fun selectBookmarkFolder(folderId: String) {
        _uiState.update { it.copy(selectedBookmarkFolderId = folderId) }
    }

    fun createBookmarkFolder(name: String, colorHex: Long) {
        if (name.isBlank()) return
        val newFolder = BookmarkFolder(
            id = "folder-" + UUID.randomUUID().toString().take(8),
            name = name.trim(),
            colorHex = colorHex
        )
        _uiState.update { state ->
            state.copy(
                bookmarkFolders = state.bookmarkFolders + newFolder,
                selectedBookmarkFolderId = newFolder.id
            )
        }
    }

    fun deleteBookmarkFolder(folderId: String) {
        _uiState.update { state ->
            val fallbackFolder = state.bookmarkFolders.firstOrNull { it.id == "folder-mobile" }
                ?: state.bookmarkFolders.firstOrNull { it.id != "folder-all" }
            val fallbackId = fallbackFolder?.id ?: "folder-mobile"
            val fallbackName = fallbackFolder?.name ?: "Mobile Bookmarks"
            state.copy(
                bookmarkFolders = state.bookmarkFolders.filterNot { it.id == folderId && !it.isDefault },
                bookmarks = state.bookmarks.map { bm ->
                    if (bm.folderId == folderId) bm.copy(folderId = fallbackId, folderName = fallbackName) else bm
                },
                selectedBookmarkFolderId = if (state.selectedBookmarkFolderId == folderId) "folder-all" else state.selectedBookmarkFolderId
            )
        }
    }

    fun openAddBookmarkDialog() {
        _uiState.update { it.copy(showAddBookmarkDialog = true) }
    }

    fun closeAddBookmarkDialog() {
        _uiState.update { it.copy(showAddBookmarkDialog = false) }
    }

    fun addBookmark(
        title: String,
        url: String,
        folderId: String? = null,
        folderName: String? = null
    ) {
        if (title.isBlank() || url.isBlank()) return
        val formattedUrl = if (!url.startsWith("http://") && !url.startsWith("https://")) "https://$url" else url
        val initial = title.trim().take(2).uppercase()
        val colors = listOf(0xFF10B981, 0xFFE53935, 0xFF6366F1, 0xFF06B6D4, 0xFFF97316, 0xFF14B8A6)
        val randomColor = colors[(_uiState.value.bookmarks.size) % colors.size]

        val targetFolderId = folderId
            ?: if (_uiState.value.selectedBookmarkFolderId != "folder-all") _uiState.value.selectedBookmarkFolderId else "folder-mobile"
        val targetFolderName = folderName
            ?: _uiState.value.bookmarkFolders.firstOrNull { it.id == targetFolderId }?.name ?: "Mobile Bookmarks"

        val newItem = BookmarkItem(
            id = UUID.randomUUID().toString(),
            title = title.trim(),
            url = formattedUrl,
            initial = initial,
            colorHex = randomColor,
            folderId = targetFolderId,
            folderName = targetFolderName
        )
        _uiState.update {
            it.copy(
                bookmarks = it.bookmarks + newItem,
                showAddBookmarkDialog = false
            )
        }
    }

    fun removeBookmark(id: String) {
        _uiState.update { state ->
            state.copy(bookmarks = state.bookmarks.filterNot { it.id == id })
        }
    }

    fun deleteBookmarks(ids: Set<String>?) {
        _uiState.update { state ->
            if (ids == null) {
                // Clear all bookmarks in active folder or all if folder-all
                val activeFolder = state.selectedBookmarkFolderId
                if (activeFolder == "folder-all") {
                    state.copy(bookmarks = emptyList())
                } else {
                    state.copy(bookmarks = state.bookmarks.filterNot { it.folderId == activeFolder })
                }
            } else {
                state.copy(bookmarks = state.bookmarks.filterNot { it.id in ids })
            }
        }
    }

    fun addBookmarkToSpeedDial(bookmark: BookmarkItem): Boolean {
        val existing = _uiState.value.speedDials.any { it.url.equals(bookmark.url.trim(), ignoreCase = true) }
        if (existing) {
            return false
        }
        val newSpeedDial = SpeedDialItem(
            id = UUID.randomUUID().toString(),
            title = bookmark.title,
            url = bookmark.url,
            initial = bookmark.initial.ifBlank { bookmark.title.take(2).uppercase() },
            colorHex = bookmark.colorHex,
            customIconUrl = bookmark.customIconUrl
        )
        _uiState.update { it.copy(speedDials = it.speedDials + newSpeedDial) }
        return true
    }

    fun moveBookmarkToFolder(bookmarkId: String, newFolderId: String, newFolderName: String) {
        _uiState.update { state ->
            state.copy(
                bookmarks = state.bookmarks.map { bm ->
                    if (bm.id == bookmarkId) bm.copy(folderId = newFolderId, folderName = newFolderName) else bm
                }
            )
        }
    }

    fun exportBookmarksAsJson(): String {
        val items = _uiState.value.bookmarks.map { bm ->
            """  {
    "title": "${escapeJson(bm.title)}",
    "url": "${escapeJson(bm.url)}",
    "folder": "${escapeJson(bm.folderName)}",
    "folderId": "${escapeJson(bm.folderId)}",
    "colorHex": ${bm.colorHex}
  }"""
        }
        return "[\n" + items.joinToString(",\n") + "\n]"
    }

    private fun escapeJson(str: String): String {
        return str.replace("\\", "\\\\")
            .replace("\"", "\\\"")
            .replace("\n", "\\n")
            .replace("\r", "\\r")
            .replace("\t", "\\t")
    }

    fun exportBookmarksAsHtml(): String {
        val sb = StringBuilder()
        sb.append("<!DOCTYPE NETSCAPE-Bookmark-file-1>\n")
        sb.append("<!-- This is an automatically generated file by Browson. -->\n")
        sb.append("<META HTTP-EQUIV=\"Content-Type\" CONTENT=\"text/html; charset=UTF-8\">\n")
        sb.append("<TITLE>Browson Bookmarks</TITLE>\n")
        sb.append("<H1>Bookmarks</H1>\n")
        sb.append("<DL><p>\n")

        val grouped = _uiState.value.bookmarks.groupBy { it.folderName }
        grouped.forEach { (folderName, items) ->
            sb.append("    <DT><H3>").append(folderName).append("</H3>\n")
            sb.append("    <DL><p>\n")
            items.forEach { bm ->
                sb.append("        <DT><A HREF=\"").append(bm.url).append("\">").append(bm.title).append("</A>\n")
            }
            sb.append("    </DL><p>\n")
        }
        sb.append("</DL><p>\n")
        return sb.toString()
    }

    fun importBookmarks(content: String, targetFolderId: String, targetFolderName: String): Int {
        if (content.isBlank()) return 0
        val newBookmarks = mutableListOf<BookmarkItem>()
        val colors = listOf(0xFF10B981, 0xFFE53935, 0xFF6366F1, 0xFF06B6D4, 0xFFF97316, 0xFF14B8A6, 0xFF8B5CF6)

        val trimmed = content.trim()
        // Try JSON array first via pure regex parser
        if (trimmed.startsWith("[")) {
            val objectRegex = java.util.regex.Pattern.compile("\\{([^{}]+)\\}")
            val objMatcher = objectRegex.matcher(trimmed)
            while (objMatcher.find()) {
                val block = objMatcher.group(1) ?: ""
                val titleMatcher = java.util.regex.Pattern.compile("\"title\"\\s*:\\s*\"([^\"]*)\"").matcher(block)
                val urlMatcher = java.util.regex.Pattern.compile("\"url\"\\s*:\\s*\"([^\"]*)\"").matcher(block)
                val folderMatcher = java.util.regex.Pattern.compile("\"folder\"\\s*:\\s*\"([^\"]*)\"").matcher(block)
                val folderIdMatcher = java.util.regex.Pattern.compile("\"folderId\"\\s*:\\s*\"([^\"]*)\"").matcher(block)

                val url = if (urlMatcher.find()) urlMatcher.group(1)?.trim() ?: "" else ""
                val title = if (titleMatcher.find()) titleMatcher.group(1)?.trim() ?: "" else "Bookmark"
                val folder = if (folderMatcher.find()) folderMatcher.group(1)?.trim() ?: targetFolderName else targetFolderName
                val folderId = if (folderIdMatcher.find()) folderIdMatcher.group(1)?.trim() ?: targetFolderId else targetFolderId

                if (url.isNotBlank()) {
                    val formattedUrl = if (!url.startsWith("http://") && !url.startsWith("https://")) "https://$url" else url
                    newBookmarks.add(
                        BookmarkItem(
                            id = UUID.randomUUID().toString(),
                            title = if (title.isBlank()) url else title,
                            url = formattedUrl,
                            initial = (if (title.isBlank()) url else title).take(2).uppercase(),
                            colorHex = colors[newBookmarks.size % colors.size],
                            folderId = if (folderId.isNotBlank()) folderId else targetFolderId,
                            folderName = if (folder.isNotBlank()) folder else targetFolderName
                        )
                    )
                }
            }
        }

        // If not JSON or JSON was empty, try regex for HTML <a href="...">...</a>
        if (newBookmarks.isEmpty() && (trimmed.contains("<a", ignoreCase = true) || trimmed.contains("<A", ignoreCase = true))) {
            val pattern = java.util.regex.Pattern.compile("<a\\s+(?:[^>]*?\\s+)?href=[\"']([^\"']*)[\"'][^>]*>(.*?)<\\/a>", java.util.regex.Pattern.CASE_INSENSITIVE)
            val matcher = pattern.matcher(trimmed)
            while (matcher.find()) {
                val url = matcher.group(1)?.trim() ?: ""
                var title = matcher.group(2)?.trim() ?: ""
                title = title.replace(Regex("<.*?>"), "").trim()
                if (url.isNotBlank()) {
                    val formattedUrl = if (!url.startsWith("http://") && !url.startsWith("https://")) "https://$url" else url
                    newBookmarks.add(
                        BookmarkItem(
                            id = UUID.randomUUID().toString(),
                            title = if (title.isBlank()) url else title,
                            url = formattedUrl,
                            initial = (if (title.isBlank()) url else title).take(2).uppercase(),
                            colorHex = colors[newBookmarks.size % colors.size],
                            folderId = targetFolderId,
                            folderName = targetFolderName
                        )
                    )
                }
            }
        }

        // If still empty, parse line-by-line URLs
        if (newBookmarks.isEmpty()) {
            trimmed.lines().forEach { line ->
                val clean = line.trim()
                if (clean.isNotBlank()) {
                    val parts = clean.split(",", "|", "\t", limit = 2)
                    val urlCandidate = if (parts.size > 1) parts[1].trim() else parts[0].trim()
                    val titleCandidate = if (parts.size > 1) parts[0].trim() else clean
                    if (urlCandidate.contains(".") && !urlCandidate.contains(" ")) {
                        val formattedUrl = if (!urlCandidate.startsWith("http://") && !urlCandidate.startsWith("https://")) "https://$urlCandidate" else urlCandidate
                        newBookmarks.add(
                            BookmarkItem(
                                id = UUID.randomUUID().toString(),
                                title = titleCandidate,
                                url = formattedUrl,
                                initial = titleCandidate.take(2).uppercase(),
                                colorHex = colors[newBookmarks.size % colors.size],
                                folderId = targetFolderId,
                                folderName = targetFolderName
                            )
                        )
                    }
                }
            }
        }

        if (newBookmarks.isNotEmpty()) {
            _uiState.update { state ->
                state.copy(bookmarks = state.bookmarks + newBookmarks)
            }
        }
        return newBookmarks.size
    }

    fun removeBookmarkFromHomeScreen(id: String) {
        _uiState.update { state ->
            state.copy(
                bookmarks = state.bookmarks.map { item ->
                    if (item.id == id) item.copy(showOnHomeScreen = false) else item
                }
            )
        }
    }

    fun openEditBookmarkDialog(item: BookmarkItem) {
        _uiState.update { it.copy(editingBookmarkItem = item) }
    }

    fun closeEditBookmarkDialog() {
        _uiState.update { it.copy(editingBookmarkItem = null) }
    }

    fun updateBookmark(
        id: String,
        newTitle: String,
        newUrl: String,
        folderId: String? = null,
        folderName: String? = null
    ) {
        if (newTitle.isBlank() || newUrl.isBlank()) return
        val formattedUrl = if (!newUrl.startsWith("http://") && !newUrl.startsWith("https://")) "https://$newUrl" else newUrl
        val initial = newTitle.trim().take(2).uppercase()
        _uiState.update { state ->
            state.copy(
                bookmarks = state.bookmarks.map { item ->
                    if (item.id == id) {
                        item.copy(
                            title = newTitle.trim(),
                            url = formattedUrl,
                            initial = initial,
                            folderId = folderId ?: item.folderId,
                            folderName = folderName ?: item.folderName
                        )
                    } else {
                        item
                    }
                },
                editingBookmarkItem = null
            )
        }
    }

    // Bottom Sheet Menu
    fun openMenuSheet() {
        _uiState.update { it.copy(showMenuSheet = true, showTabsSheet = false) }
    }

    fun closeMenuSheet() {
        _uiState.update { it.copy(showMenuSheet = false) }
    }

    // Settings Screen
    fun openSettingsScreen() {
        _uiState.update { it.copy(showSettingsScreen = true, showMenuSheet = false, showTabsSheet = false) }
    }

    fun closeSettingsScreen() {
        _uiState.update { it.copy(showSettingsScreen = false) }
    }

    // History Screen
    fun openHistoryScreen() {
        _uiState.update { it.copy(showHistoryScreen = true, showMenuSheet = false, showTabsSheet = false) }
    }

    fun closeHistoryScreen() {
        _uiState.update { it.copy(showHistoryScreen = false) }
    }

    fun deleteHistory(selectedIds: Set<String>? = null) {
        _uiState.update { state ->
            if (selectedIds.isNullOrEmpty()) {
                // Delete all history
                state.copy(historyItems = emptyList())
            } else {
                // Delete only selected history
                state.copy(historyItems = state.historyItems.filterNot { it.id in selectedIds })
            }
        }
    }

    fun deleteHistoryItem(id: String) {
        _uiState.update { state ->
            state.copy(historyItems = state.historyItems.filterNot { it.id == id })
        }
    }

    // Download Screen
    fun openDownloadScreen() {
        _uiState.update { it.copy(showDownloadScreen = true, showMenuSheet = false, showTabsSheet = false) }
    }

    fun closeDownloadScreen() {
        _uiState.update { it.copy(showDownloadScreen = false) }
    }

    fun deleteDownload(id: String) {
        _uiState.update { state ->
            state.copy(downloadItems = state.downloadItems.filterNot { it.id == id })
        }
    }

    fun pauseOrResumeDownload(id: String) {
        _uiState.update { state ->
            state.copy(
                downloadItems = state.downloadItems.map { item ->
                    if (item.id == id) {
                        item.copy(isDownloading = !item.isDownloading)
                    } else {
                        item
                    }
                }
            )
        }
    }

    fun pauseAllDownloads() {
        _uiState.update { state ->
            state.copy(
                downloadItems = state.downloadItems.map { it.copy(isDownloading = false) }
            )
        }
    }

    fun resumeAllDownloads() {
        _uiState.update { state ->
            state.copy(
                downloadItems = state.downloadItems.map {
                    if (it.downloadedBytes < it.totalBytes) it.copy(isDownloading = true) else it
                }
            )
        }
    }

    fun clearCompletedDownloads() {
        _uiState.update { state ->
            state.copy(
                downloadItems = state.downloadItems.filter { it.downloadedBytes < it.totalBytes }
            )
        }
    }

    fun clearAllDownloads() {
        _uiState.update { state ->
            state.copy(downloadItems = emptyList())
        }
    }

    fun addDownloadItem(fileName: String, url: String, totalBytes: Long, extension: String): String {
        val id = UUID.randomUUID().toString()
        val item = DownloadItem(
            id = id,
            fileName = fileName,
            url = url,
            totalBytes = totalBytes,
            downloadedBytes = 0L,
            speedBytesPerSec = 1_500_000L,
            isDownloading = true,
            extension = extension
        )
        _uiState.update { state ->
            state.copy(downloadItems = listOf(item) + state.downloadItems)
        }
        return id
    }

    // Tool Menu Customization
    fun removeToolFromMenu(toolId: String) {
        _uiState.update { state ->
            if (toolId in state.activeToolMenuIds) {
                state.copy(
                    activeToolMenuIds = state.activeToolMenuIds - toolId,
                    availableToolMenuIds = (state.availableToolMenuIds - toolId) + toolId
                )
            } else {
                state
            }
        }
    }

    fun addToolToMenu(toolId: String) {
        _uiState.update { state ->
            if (toolId in state.availableToolMenuIds) {
                state.copy(
                    availableToolMenuIds = state.availableToolMenuIds - toolId,
                    activeToolMenuIds = state.activeToolMenuIds + toolId
                )
            } else {
                state
            }
        }
    }

    fun moveToolInMenu(fromIndex: Int, toIndex: Int) {
        _uiState.update { state ->
            val list = state.activeToolMenuIds.toMutableList()
            if (fromIndex in list.indices && toIndex in list.indices && fromIndex != toIndex) {
                val item = list.removeAt(fromIndex)
                list.add(toIndex, item)
                state.copy(activeToolMenuIds = list)
            } else {
                state
            }
        }
    }

    fun swapToolsInMenu(index1: Int, index2: Int) {
        _uiState.update { state ->
            val list = state.activeToolMenuIds.toMutableList()
            if (index1 in list.indices && index2 in list.indices && index1 != index2) {
                val temp = list[index1]
                list[index1] = list[index2]
                list[index2] = temp
                state.copy(activeToolMenuIds = list)
            } else {
                state
            }
        }
    }

    fun resetToolMenu() {
        _uiState.update { state ->
            state.copy(
                activeToolMenuIds = com.example.model.BrowserToolCatalog.DEFAULT_ACTIVE_IDS,
                availableToolMenuIds = com.example.model.BrowserToolCatalog.DEFAULT_AVAILABLE_IDS
            )
        }
    }

    // Toolbar Customization (Min 4, Max 6)
    fun removeToolFromToolbar(toolId: String): Boolean {
        var removed = false
        _uiState.update { state ->
            if (state.activeToolbarToolIds.size > 4 && toolId in state.activeToolbarToolIds) {
                removed = true
                state.copy(
                    activeToolbarToolIds = state.activeToolbarToolIds - toolId,
                    availableToolbarToolIds = (state.availableToolbarToolIds - toolId) + toolId
                )
            } else {
                state
            }
        }
        return removed
    }

    fun addToolToToolbar(toolId: String): Boolean {
        var added = false
        _uiState.update { state ->
            if (state.activeToolbarToolIds.size < 6 && toolId in state.availableToolbarToolIds) {
                added = true
                state.copy(
                    availableToolbarToolIds = state.availableToolbarToolIds - toolId,
                    activeToolbarToolIds = state.activeToolbarToolIds + toolId
                )
            } else {
                state
            }
        }
        return added
    }

    fun moveToolInToolbar(fromIndex: Int, toIndex: Int) {
        _uiState.update { state ->
            val list = state.activeToolbarToolIds.toMutableList()
            if (fromIndex in list.indices && toIndex in list.indices && fromIndex != toIndex) {
                val item = list.removeAt(fromIndex)
                list.add(toIndex, item)
                state.copy(activeToolbarToolIds = list)
            } else {
                state
            }
        }
    }

    fun resetToolbar() {
        _uiState.update { state ->
            state.copy(
                activeToolbarToolIds = com.example.model.BrowserToolCatalog.DEFAULT_ACTIVE_TOOLBAR_IDS,
                availableToolbarToolIds = com.example.model.BrowserToolCatalog.DEFAULT_AVAILABLE_TOOLBAR_IDS
            )
        }
    }

    // Tabs Sheet
    fun openTabsSheet() {
        _uiState.update { it.copy(showTabsSheet = true, showMenuSheet = false) }
    }

    fun closeTabsSheet() {
        _uiState.update { it.copy(showTabsSheet = false) }
    }

    fun toggleTabsSheet() {
        _uiState.update { state ->
            val nextState = !state.showTabsSheet
            state.copy(showTabsSheet = nextState, showMenuSheet = false)
        }
    }

    fun setIncognitoMode(isIncognito: Boolean) {
        _uiState.update { state ->
            val targetTabs = if (isIncognito) state.incognitoTabs else state.tabs
            val targetActiveId = targetTabs.firstOrNull()?.id ?: state.activeTabId
            state.copy(
                isIncognitoMode = isIncognito,
                activeTabId = targetActiveId
            )
        }
    }

    fun addNewTab(isIncognito: Boolean = _uiState.value.isIncognitoMode) {
        val count = if (isIncognito) _uiState.value.incognitoTabs.size + 1 else _uiState.value.tabs.size + 1
        val newId = if (isIncognito) "incog-tab-$count" else "tab-$count"
        val newTab = BrowserTab(newId, "New Tab", "", isHome = true, isIncognito = isIncognito)
        _uiState.update { state ->
            if (isIncognito) {
                state.copy(
                    incognitoTabs = state.incognitoTabs + newTab,
                    activeTabId = newId,
                    isIncognitoMode = true,
                    isViewingWeb = false,
                    currentUrl = "",
                    searchInput = "",
                    showTabsSheet = false
                )
            } else {
                state.copy(
                    tabs = state.tabs + newTab,
                    activeTabId = newId,
                    isIncognitoMode = false,
                    isViewingWeb = false,
                    currentUrl = "",
                    searchInput = "",
                    showTabsSheet = false
                )
            }
        }
    }

    fun selectTab(tabId: String) {
        val currentList = if (_uiState.value.isIncognitoMode) _uiState.value.incognitoTabs else _uiState.value.tabs
        val tab = currentList.firstOrNull { it.id == tabId }
            ?: _uiState.value.tabs.firstOrNull { it.id == tabId }
            ?: _uiState.value.incognitoTabs.firstOrNull { it.id == tabId }
            ?: return
        _uiState.update {
            it.copy(
                activeTabId = tabId,
                isIncognitoMode = tab.isIncognito,
                isViewingWeb = !tab.isHome,
                currentUrl = tab.url,
                searchInput = tab.url,
                showTabsSheet = false
            )
        }
    }

    fun closeTab(tabId: String) {
        _uiState.update { state ->
            if (state.isIncognitoMode) {
                val closed = state.incognitoTabs.firstOrNull { it.id == tabId }
                val remaining = state.incognitoTabs.filterNot { it.id == tabId }
                val nextActive = if (state.activeTabId == tabId) remaining.lastOrNull()?.id ?: "tab-1" else state.activeTabId
                state.copy(
                    incognitoTabs = remaining,
                    activeTabId = nextActive,
                    isIncognitoMode = remaining.isNotEmpty(),
                    lastClosedTab = closed,
                    showClosedTabPopup = closed != null
                )
            } else {
                val closed = state.tabs.firstOrNull { it.id == tabId }
                val remaining = state.tabs.filterNot { it.id == tabId }
                if (remaining.isEmpty()) {
                    val freshTab = BrowserTab("tab-1", "Home", "", isHome = true)
                    state.copy(
                        tabs = listOf(freshTab),
                        activeTabId = "tab-1",
                        isViewingWeb = false,
                        lastClosedTab = closed,
                        showClosedTabPopup = closed != null
                    )
                } else {
                    val nextActive = if (state.activeTabId == tabId) remaining.last().id else state.activeTabId
                    val activeTab = remaining.firstOrNull { it.id == nextActive }
                    state.copy(
                        tabs = remaining,
                        activeTabId = nextActive,
                        isViewingWeb = activeTab?.isHome == false,
                        currentUrl = activeTab?.url ?: "",
                        lastClosedTab = closed,
                        showClosedTabPopup = closed != null
                    )
                }
            }
        }
    }

    fun undoCloseTab() {
        val closed = _uiState.value.lastClosedTab ?: return
        _uiState.update { state ->
            if (closed.isIncognito) {
                state.copy(
                    incognitoTabs = state.incognitoTabs + closed,
                    activeTabId = closed.id,
                    isIncognitoMode = true,
                    lastClosedTab = null,
                    showClosedTabPopup = false
                )
            } else {
                state.copy(
                    tabs = state.tabs + closed,
                    activeTabId = closed.id,
                    isIncognitoMode = false,
                    lastClosedTab = null,
                    showClosedTabPopup = false
                )
            }
        }
    }

    fun dismissClosedTabPopup() {
        _uiState.update { it.copy(showClosedTabPopup = false) }
    }

    fun setThemeMode(mode: ThemeMode) {
        _uiState.update { it.copy(themeMode = mode) }
    }

    fun setReduceAnimations(reduce: Boolean) {
        _uiState.update { it.copy(reduceAnimations = reduce) }
    }

    fun setAnimationScale(scale: Float) {
        _uiState.update { it.copy(animationScale = scale) }
    }

    fun setDefaultSearchEngine(engine: String) {
        _uiState.update { it.copy(defaultSearchEngine = engine) }
    }

    fun setUiGradient(gradientId: String?) {
        _uiState.update { it.copy(uiGradientId = gradientId) }
    }

    fun setHomeWallpaper(mode: String, style: String = _uiState.value.wallpaperStyle) {
        _uiState.update { it.copy(wallpaperMode = mode, wallpaperStyle = style) }
    }

    fun setSingleAppColor(colorHex: Long) {
        _uiState.update {
            it.copy(
                customPrimaryHex = colorHex,
                customIconBgHex = null,
                isDualColorActive = false
            )
        }
    }

    fun setDualAppColor(darkHex: Long, lightHex: Long) {
        _uiState.update {
            it.copy(
                customPrimaryHex = darkHex,
                customIconBgHex = lightHex,
                isDualColorActive = true
            )
        }
    }

    fun resetAppColor() {
        _uiState.update {
            it.copy(
                customPrimaryHex = null,
                customIconBgHex = null,
                isDualColorActive = false
            )
        }
    }
}
