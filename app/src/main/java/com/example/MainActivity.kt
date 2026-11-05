package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.BrowserScreen
import com.example.ui.BrowserViewModel
import com.example.ui.DownloadScreen
import com.example.ui.HistoryScreen
import com.example.ui.HomeScreen
import com.example.ui.SettingsScreen
import com.example.ui.ThemeMode
import com.example.ui.components.AddBookmarkDialog
import com.example.ui.components.AddSpeedDialDialog
import com.example.ui.components.EditBookmarkDialog
import com.example.ui.components.EditSpeedDialDialog
import com.example.ui.components.BottomToolbar
import com.example.ui.components.MenuBottomSheet
import com.example.ui.components.TabsBottomSheet
import com.example.ui.theme.BrowsonTheme

class MainActivity : ComponentActivity() {

    private val viewModel: BrowserViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()

            val isDarkTheme = when (uiState.themeMode) {
                ThemeMode.SYSTEM -> isSystemInDarkTheme()
                ThemeMode.DARK -> true
                ThemeMode.LIGHT -> false
            }

            BrowsonTheme(darkTheme = isDarkTheme) {
                if (uiState.showSettingsScreen) {
                    SettingsScreen(
                        themeMode = uiState.themeMode,
                        onThemeModeChange = { viewModel.setThemeMode(it) },
                        onBack = { viewModel.closeSettingsScreen() },
                        activeToolIds = uiState.activeToolMenuIds,
                        availableToolIds = uiState.availableToolMenuIds,
                        onRemoveTool = { viewModel.removeToolFromMenu(it) },
                        onAddTool = { viewModel.addToolToMenu(it) },
                        onMoveTool = { from, to -> viewModel.moveToolInMenu(from, to) },
                        onSwapTools = { i1, i2 -> viewModel.swapToolsInMenu(i1, i2) },
                        onResetTools = { viewModel.resetToolMenu() },
                        activeToolbarToolIds = uiState.activeToolbarToolIds,
                        availableToolbarToolIds = uiState.availableToolbarToolIds,
                        onRemoveToolbarTool = { viewModel.removeToolFromToolbar(it) },
                        onAddToolbarTool = { viewModel.addToolToToolbar(it) },
                        onMoveToolbarTool = { from, to -> viewModel.moveToolInToolbar(from, to) },
                        onResetToolbar = { viewModel.resetToolbar() }
                    )
                } else if (uiState.showHistoryScreen) {
                    HistoryScreen(
                        historyItems = uiState.historyItems,
                        onNavigateToUrl = { url, title ->
                            viewModel.navigateToUrl(url, title)
                        },
                        onDeleteHistory = { ids ->
                            viewModel.deleteHistory(ids)
                        },
                        onClose = { viewModel.closeHistoryScreen() }
                    )
                } else if (uiState.showDownloadScreen) {
                    DownloadScreen(
                        downloadItems = uiState.downloadItems,
                        onPauseOrResume = { viewModel.pauseOrResumeDownload(it) },
                        onDeleteDownload = { viewModel.deleteDownload(it) },
                        onPauseAll = { viewModel.pauseAllDownloads() },
                        onResumeAll = { viewModel.resumeAllDownloads() },
                        onClearCompleted = { viewModel.clearCompletedDownloads() },
                        onOpenSettings = {
                            viewModel.closeDownloadScreen()
                            viewModel.openSettingsScreen()
                        },
                        onClose = { viewModel.closeDownloadScreen() }
                    )
                } else {
                    Scaffold(
                        modifier = Modifier.fillMaxSize(),
                        containerColor = MaterialTheme.colorScheme.background,
                        bottomBar = {
                            BottomToolbar(
                                canGoBack = uiState.canGoBack,
                                canGoForward = uiState.canGoForward,
                                tabsCount = uiState.tabs.size,
                                onBackClick = { viewModel.goBack() },
                                onForwardClick = { viewModel.goForward() },
                                onHomeClick = { viewModel.goHome() },
                                onTabsClick = { viewModel.toggleTabsSheet() },
                                onMenuClick = { viewModel.openMenuSheet() },
                                toolbarToolIds = uiState.activeToolbarToolIds,
                                onHistoryClick = { viewModel.openHistoryScreen() },
                                onDownloadsClick = { viewModel.openDownloadScreen() },
                                onBookmarksClick = { viewModel.openAddBookmarkDialog() },
                                onNewTabClick = { viewModel.addNewTab() },
                                onDarkModeClick = {
                                    val nextMode = when (uiState.themeMode) {
                                        ThemeMode.LIGHT -> ThemeMode.DARK
                                        ThemeMode.DARK -> ThemeMode.LIGHT
                                        ThemeMode.SYSTEM -> if (isDarkTheme) ThemeMode.LIGHT else ThemeMode.DARK
                                    }
                                    viewModel.setThemeMode(nextMode)
                                }
                            )
                        }
                    ) { innerPadding ->
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(bottom = innerPadding.calculateBottomPadding())
                        ) {
                            if (uiState.isViewingWeb) {
                                BrowserScreen(
                                    url = uiState.currentUrl,
                                    onUrlChange = { newUrl ->
                                        viewModel.updateSearchInput(newUrl)
                                    },
                                    onClose = { viewModel.goHome() }
                                )
                            } else {
                                HomeScreen(
                                    searchQuery = uiState.searchInput,
                                    speedDials = uiState.speedDials,
                                    bookmarks = uiState.bookmarks.filter { it.showOnHomeScreen },
                                    onSearchQueryChange = { viewModel.updateSearchInput(it) },
                                    onSearchSubmit = { viewModel.submitSearch(it) },
                                    onSpeedDialClick = { item ->
                                        viewModel.navigateToUrl(item.url, item.title)
                                    },
                                    onAddSpeedDialClick = { viewModel.openAddSpeedDialDialog() },
                                    onEditSpeedDial = { item -> viewModel.openEditSpeedDialDialog(item) },
                                    onRemoveSpeedDial = { id -> viewModel.removeSpeedDial(id) },
                                    onBookmarkClick = { item ->
                                        viewModel.navigateToUrl(item.url, item.title)
                                    },
                                    onAddBookmarkClick = { viewModel.openAddBookmarkDialog() },
                                    onEditBookmark = { item -> viewModel.openEditBookmarkDialog(item) },
                                    onDeleteBookmark = { id -> viewModel.removeBookmark(id) },
                                    onRemoveBookmark = { id -> viewModel.removeBookmarkFromHomeScreen(id) }
                                )
                            }

                            // 11. Tabs Menu anchored directly above bottom toolbar (toolbar always visible)
                            if (uiState.showTabsSheet) {
                                TabsBottomSheet(
                                    tabs = uiState.tabs,
                                    incognitoTabs = uiState.incognitoTabs,
                                    isIncognitoMode = uiState.isIncognitoMode,
                                    activeTabId = uiState.activeTabId,
                                    showClosedTabPopup = uiState.showClosedTabPopup,
                                    onSelectTab = { viewModel.selectTab(it) },
                                    onCloseTab = { viewModel.closeTab(it) },
                                    onNewTab = { isIncog -> viewModel.addNewTab(isIncog) },
                                    onToggleIncognito = { isIncog -> viewModel.setIncognitoMode(isIncog) },
                                    onUndoCloseTab = { viewModel.undoCloseTab() },
                                    onDismissClosedTabPopup = { viewModel.dismissClosedTabPopup() },
                                    onDismiss = { viewModel.closeTabsSheet() },
                                    isDarkTheme = isDarkTheme,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                        }

                        // Add Speed Dial Dialog
                        if (uiState.showAddSpeedDialDialog) {
                            AddSpeedDialDialog(
                                onDismiss = { viewModel.closeAddSpeedDialDialog() },
                                onConfirm = { title, url ->
                                    viewModel.addSpeedDial(title, url)
                                }
                            )
                        }

                        // Edit Speed Dial Dialog (with Name and Website boxes)
                        uiState.editingSpeedDialItem?.let { editingItem ->
                            EditSpeedDialDialog(
                                item = editingItem,
                                onDismiss = { viewModel.closeEditSpeedDialDialog() },
                                onConfirm = { name, website ->
                                    viewModel.updateSpeedDial(editingItem.id, name, website)
                                }
                            )
                        }

                        // Add Bookmark Dialog
                        if (uiState.showAddBookmarkDialog) {
                            AddBookmarkDialog(
                                onDismiss = { viewModel.closeAddBookmarkDialog() },
                                onConfirm = { title, url ->
                                    viewModel.addBookmark(title, url)
                                }
                            )
                        }

                        // Edit Bookmark Dialog (with Name and Website boxes)
                        uiState.editingBookmarkItem?.let { editingItem ->
                            EditBookmarkDialog(
                                item = editingItem,
                                onDismiss = { viewModel.closeEditBookmarkDialog() },
                                onConfirm = { name, website ->
                                    viewModel.updateBookmark(editingItem.id, name, website)
                                }
                            )
                        }

                        // Menu Bottom Sheet (Tool Menu)
                        if (uiState.showMenuSheet) {
                            MenuBottomSheet(
                                themeMode = uiState.themeMode,
                                onThemeModeChange = { viewModel.setThemeMode(it) },
                                onAddBookmark = {
                                    viewModel.closeMenuSheet()
                                    viewModel.openAddBookmarkDialog()
                                },
                                onAddSpeedDial = {
                                    viewModel.closeMenuSheet()
                                    viewModel.openAddSpeedDialDialog()
                                },
                                onCloseApp = {
                                    viewModel.closeMenuSheet()
                                    finish()
                                },
                                onOpenSettings = {
                                    viewModel.openSettingsScreen()
                                },
                                onOpenHistory = {
                                    viewModel.openHistoryScreen()
                                },
                                onOpenDownloads = {
                                    viewModel.openDownloadScreen()
                                },
                                activeToolIds = uiState.activeToolMenuIds,
                                currentUrl = uiState.currentUrl,
                                onDismiss = { viewModel.closeMenuSheet() }
                            )
                        }
                    }
                }
            }
        }
    }
}
