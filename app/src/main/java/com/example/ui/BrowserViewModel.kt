package com.example.ui

import androidx.lifecycle.ViewModel
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
    LIGHT
}

data class BrowserUiState(
    val currentUrl: String = "",
    val searchInput: String = "",
    val isViewingWeb: Boolean = false,
    val webPageTitle: String = "New Tab",
    val canGoBack: Boolean = false,
    val canGoForward: Boolean = false,
    val speedDials: List<SpeedDialItem> = emptyList(),
    val bookmarks: List<BookmarkItem> = emptyList(),
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
    val historyItems: List<HistoryItem> = emptyList(),
    val downloadItems: List<DownloadItem> = emptyList(),
    val activeToolMenuIds: List<String> = com.example.model.BrowserToolCatalog.DEFAULT_ACTIVE_IDS,
    val availableToolMenuIds: List<String> = com.example.model.BrowserToolCatalog.DEFAULT_AVAILABLE_IDS,
    val activeToolbarToolIds: List<String> = com.example.model.BrowserToolCatalog.DEFAULT_ACTIVE_TOOLBAR_IDS,
    val availableToolbarToolIds: List<String> = com.example.model.BrowserToolCatalog.DEFAULT_AVAILABLE_TOOLBAR_IDS
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
                BookmarkItem(UUID.randomUUID().toString(), "Android Developers", "https://developer.android.com", "AD", 0xFF3DDC84),
                BookmarkItem(UUID.randomUUID().toString(), "TechCrunch", "https://techcrunch.com", "TC", 0xFF0A9928),
                BookmarkItem(UUID.randomUUID().toString(), "Hacker News", "https://news.ycombinator.com", "HN", 0xFFFF6600),
                BookmarkItem(UUID.randomUUID().toString(), "Medium", "https://medium.com", "M", 0xFF000000),
                BookmarkItem(UUID.randomUUID().toString(), "Weather", "https://weather.com", "W", 0xFF1B73E8)
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
            downloadItems = listOf(
                DownloadItem(
                    id = "dl-1",
                    fileName = "Android_Studio_Ladybug.tar.gz",
                    url = "https://developer.android.com/studio/ladybug.tar.gz",
                    totalBytes = 980_000_000L,
                    downloadedBytes = 460_000_000L,
                    speedBytesPerSec = 4_800_000L,
                    isDownloading = true,
                    extension = "tar"
                ),
                DownloadItem(
                    id = "dl-2",
                    fileName = "Nature_4K_Wildlife_Drone.mp4",
                    url = "https://video.example.com/nature_4k.mp4",
                    totalBytes = 145_000_000L,
                    downloadedBytes = 62_000_000L,
                    speedBytesPerSec = 2_100_000L,
                    isDownloading = true,
                    extension = "video"
                ),
                DownloadItem(
                    id = "dl-3",
                    fileName = "Kotlin_Multiplatform_Guide.pdf",
                    url = "https://kotlinlang.org/docs/kmp.pdf",
                    totalBytes = 18_400_000L,
                    downloadedBytes = 18_400_000L,
                    speedBytesPerSec = 0L,
                    isDownloading = false,
                    extension = "pdf"
                ),
                DownloadItem(
                    id = "dl-4",
                    fileName = "Browson_Browser_Release.apk",
                    url = "https://download.browson.com/app-v2.apk",
                    totalBytes = 42_500_000L,
                    downloadedBytes = 42_500_000L,
                    speedBytesPerSec = 0L,
                    isDownloading = false,
                    extension = "apk"
                ),
                DownloadItem(
                    id = "dl-5",
                    fileName = "Lofi_Chill_Beats_2026.mp3",
                    url = "https://audio.example.com/lofi.mp3",
                    totalBytes = 9_200_000L,
                    downloadedBytes = 9_200_000L,
                    speedBytesPerSec = 0L,
                    isDownloading = false,
                    extension = "audio"
                ),
                DownloadItem(
                    id = "dl-6",
                    fileName = "Site_Manifest_Config.xml",
                    url = "https://example.com/manifest.xml",
                    totalBytes = 320_000L,
                    downloadedBytes = 320_000L,
                    speedBytesPerSec = 0L,
                    isDownloading = false,
                    extension = "xml"
                ),
                DownloadItem(
                    id = "dl-7",
                    fileName = "Developer_Notes.txt",
                    url = "https://notes.example.com/notes.txt",
                    totalBytes = 54_000L,
                    downloadedBytes = 54_000L,
                    speedBytesPerSec = 0L,
                    isDownloading = false,
                    extension = "txt"
                ),
                DownloadItem(
                    id = "dl-8",
                    fileName = "Material_Design_Icons.zip",
                    url = "https://design.google/icons.zip",
                    totalBytes = 28_000_000L,
                    downloadedBytes = 28_000_000L,
                    speedBytesPerSec = 0L,
                    isDownloading = false,
                    extension = "zip"
                ),
                DownloadItem(
                    id = "dl-9",
                    fileName = "Dataset_Archive_Compressed.rar",
                    url = "https://data.example.com/dataset.rar",
                    totalBytes = 64_000_000L,
                    downloadedBytes = 64_000_000L,
                    speedBytesPerSec = 0L,
                    isDownloading = false,
                    extension = "rar"
                )
            )
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

    // Bookmark actions
    fun openAddBookmarkDialog() {
        _uiState.update { it.copy(showAddBookmarkDialog = true) }
    }

    fun closeAddBookmarkDialog() {
        _uiState.update { it.copy(showAddBookmarkDialog = false) }
    }

    fun addBookmark(title: String, url: String) {
        if (title.isBlank() || url.isBlank()) return
        val formattedUrl = if (!url.startsWith("http://") && !url.startsWith("https://")) "https://$url" else url
        val initial = title.trim().take(2).uppercase()
        val colors = listOf(0xFF10B981, 0xFFE53935, 0xFF6366F1, 0xFF06B6D4, 0xFFF97316, 0xFF14B8A6)
        val randomColor = colors[(_uiState.value.bookmarks.size) % colors.size]
        val newItem = BookmarkItem(
            id = UUID.randomUUID().toString(),
            title = title.trim(),
            url = formattedUrl,
            initial = initial,
            colorHex = randomColor
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

    fun updateBookmark(id: String, newTitle: String, newUrl: String) {
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
                            initial = initial
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
}
