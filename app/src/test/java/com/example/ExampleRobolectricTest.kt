package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Browson", appName)
  }

  @Test
  fun `verify settings menu strings match requirements`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    assertEquals("Settings", context.getString(R.string.settings_title))
    assertEquals("Home Screen", context.getString(R.string.settings_home_screen))
    assertEquals("Appearance", context.getString(R.string.settings_appearance))
    assertEquals("Tool Menu", context.getString(R.string.settings_tool_menu))
    assertEquals("Toolbar", context.getString(R.string.settings_toolbar))
    assertEquals("Download Manager", context.getString(R.string.settings_download_manager))
    assertEquals("Security", context.getString(R.string.settings_security))
    assertEquals("Ads Blocker", context.getString(R.string.settings_ads_blocker))
  }

  @Test
  fun `verify settings screen open and close state`() {
    val viewModel = com.example.ui.BrowserViewModel()
    assertEquals(false, viewModel.uiState.value.showSettingsScreen)

    viewModel.openMenuSheet()
    assertEquals(true, viewModel.uiState.value.showMenuSheet)

    viewModel.openSettingsScreen()
    assertEquals(true, viewModel.uiState.value.showSettingsScreen)
    assertEquals(false, viewModel.uiState.value.showMenuSheet)

    viewModel.closeSettingsScreen()
    assertEquals(false, viewModel.uiState.value.showSettingsScreen)
  }

  @Test
  fun `verify mutual exclusion between tool menu and tabs menu`() {
    val viewModel = com.example.ui.BrowserViewModel()

    // 1. Open tabs menu
    viewModel.openTabsSheet()
    assertEquals(true, viewModel.uiState.value.showTabsSheet)
    assertEquals(false, viewModel.uiState.value.showMenuSheet)

    // 2. Open tool menu -> tabs menu MUST close
    viewModel.openMenuSheet()
    assertEquals(true, viewModel.uiState.value.showMenuSheet)
    assertEquals(false, viewModel.uiState.value.showTabsSheet)

    // 3. Open tabs menu -> tool menu MUST close
    viewModel.openTabsSheet()
    assertEquals(true, viewModel.uiState.value.showTabsSheet)
    assertEquals(false, viewModel.uiState.value.showMenuSheet)

    // 4. Toggle tabs menu when tool menu is open
    viewModel.openMenuSheet()
    assertEquals(true, viewModel.uiState.value.showMenuSheet)
    viewModel.toggleTabsSheet()
    assertEquals(true, viewModel.uiState.value.showTabsSheet)
    assertEquals(false, viewModel.uiState.value.showMenuSheet)
  }

  @Test
  fun `verify history operations selective delete and clear all`() {
    val viewModel = com.example.ui.BrowserViewModel()
    val initialHistory = viewModel.uiState.value.historyItems
    assert(initialHistory.isNotEmpty())

    // Latest items are on top
    val sorted = initialHistory.sortedByDescending { it.timestamp }
    assertEquals(initialHistory.first().id, sorted.first().id)

    // Selective delete
    val targetId = initialHistory.first().id
    viewModel.deleteHistory(setOf(targetId))
    assert(viewModel.uiState.value.historyItems.none { it.id == targetId })
    assertEquals(initialHistory.size - 1, viewModel.uiState.value.historyItems.size)

    // Clear all history
    viewModel.deleteHistory(null)
    assert(viewModel.uiState.value.historyItems.isEmpty())
  }

  @Test
  fun `verify download operations pause resume and clear completed`() {
    val viewModel = com.example.ui.BrowserViewModel()
    // Initial download list is empty as requested
    assert(viewModel.uiState.value.downloadItems.isEmpty())

    // Add a download item for operation testing
    val dlId = viewModel.addDownloadItem("Test_Document.pdf", "https://example.com/doc.pdf", 10_000_000L, "pdf")
    assertEquals(1, viewModel.uiState.value.downloadItems.size)

    // Pause all
    viewModel.pauseAllDownloads()
    assert(viewModel.uiState.value.downloadItems.none { it.isDownloading })

    // Resume all
    viewModel.resumeAllDownloads()
    assert(viewModel.uiState.value.downloadItems.any { it.isDownloading })

    // Clear all downloads
    viewModel.clearAllDownloads()
    assert(viewModel.uiState.value.downloadItems.isEmpty())
  }

  @Test
  fun `verify tool menu customization remove add move and reset`() {
    val viewModel = com.example.ui.BrowserViewModel()
    val initialActive = viewModel.uiState.value.activeToolMenuIds
    val initialAvailable = viewModel.uiState.value.availableToolMenuIds
    assertEquals(9, initialActive.size)
    assert(initialAvailable.isNotEmpty())

    // 1. Remove a tool from active menu
    val removedToolId = initialActive.first()
    viewModel.removeToolFromMenu(removedToolId)
    assert(removedToolId !in viewModel.uiState.value.activeToolMenuIds)
    assert(removedToolId in viewModel.uiState.value.availableToolMenuIds)
    assertEquals(initialActive.size - 1, viewModel.uiState.value.activeToolMenuIds.size)

    // 2. Add an extra tool to active menu
    val addedToolId = viewModel.uiState.value.availableToolMenuIds.first()
    viewModel.addToolToMenu(addedToolId)
    assert(addedToolId in viewModel.uiState.value.activeToolMenuIds)
    assert(addedToolId !in viewModel.uiState.value.availableToolMenuIds)

    // 3. Move/reposition a tool
    val firstTool = viewModel.uiState.value.activeToolMenuIds[0]
    val secondTool = viewModel.uiState.value.activeToolMenuIds[1]
    viewModel.moveToolInMenu(0, 1)
    assertEquals(secondTool, viewModel.uiState.value.activeToolMenuIds[0])
    assertEquals(firstTool, viewModel.uiState.value.activeToolMenuIds[1])

    // 4. Swap two tools
    viewModel.swapToolsInMenu(0, 1)
    assertEquals(firstTool, viewModel.uiState.value.activeToolMenuIds[0])
    assertEquals(secondTool, viewModel.uiState.value.activeToolMenuIds[1])

    // 5. Reset to default
    viewModel.resetToolMenu()
    assertEquals(com.example.model.BrowserToolCatalog.DEFAULT_ACTIVE_IDS, viewModel.uiState.value.activeToolMenuIds)
  }

  @Test
  fun `verify toolbar customization constraints min 4 and max 6 and reordering`() {
    val viewModel = com.example.ui.BrowserViewModel()
    val initialActive = viewModel.uiState.value.activeToolbarToolIds
    val initialAvailable = viewModel.uiState.value.availableToolbarToolIds
    // Default toolbar has 5 tools: nav_back, nav_forward, nav_home, nav_tabs, nav_menu
    assertEquals(5, initialActive.size)
    assert(initialAvailable.isNotEmpty())

    // 1. Reorder tools in toolbar
    val tool0 = initialActive[0]
    val tool1 = initialActive[1]
    viewModel.moveToolInToolbar(0, 1)
    assertEquals(tool1, viewModel.uiState.value.activeToolbarToolIds[0])
    assertEquals(tool0, viewModel.uiState.value.activeToolbarToolIds[1])

    // 2. Remove one tool -> size becomes 4 (the minimum allowed)
    val toolToRemove = viewModel.uiState.value.activeToolbarToolIds.first()
    val removedSuccessfully = viewModel.removeToolFromToolbar(toolToRemove)
    assertEquals(true, removedSuccessfully)
    assertEquals(4, viewModel.uiState.value.activeToolbarToolIds.size)
    assert(toolToRemove in viewModel.uiState.value.availableToolbarToolIds)

    // 3. Attempt to remove another tool when at min 4 -> MUST fail and preserve size 4
    val nextToolToRemove = viewModel.uiState.value.activeToolbarToolIds.first()
    val blockedRemoval = viewModel.removeToolFromToolbar(nextToolToRemove)
    assertEquals(false, blockedRemoval)
    assertEquals(4, viewModel.uiState.value.activeToolbarToolIds.size)

    // 4. Add tools up to max 6
    val toolToAdd1 = viewModel.uiState.value.availableToolbarToolIds[0]
    val toolToAdd2 = viewModel.uiState.value.availableToolbarToolIds[1]
    assertEquals(true, viewModel.addToolToToolbar(toolToAdd1))
    assertEquals(5, viewModel.uiState.value.activeToolbarToolIds.size)
    assertEquals(true, viewModel.addToolToToolbar(toolToAdd2))
    assertEquals(6, viewModel.uiState.value.activeToolbarToolIds.size)

    // 5. Attempt to add a 7th tool when at max 6 -> MUST fail and preserve size 6
    val extraTool = viewModel.uiState.value.availableToolbarToolIds.firstOrNull()
    if (extraTool != null) {
      val blockedAdd = viewModel.addToolToToolbar(extraTool)
      assertEquals(false, blockedAdd)
      assertEquals(6, viewModel.uiState.value.activeToolbarToolIds.size)
    }

    // 6. Reset toolbar to defaults
    viewModel.resetToolbar()
    assertEquals(com.example.model.BrowserToolCatalog.DEFAULT_ACTIVE_TOOLBAR_IDS, viewModel.uiState.value.activeToolbarToolIds)
    assertEquals(5, viewModel.uiState.value.activeToolbarToolIds.size)
  }
}
