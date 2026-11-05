package com.example

import org.junit.Assert.*
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
  @Test
  fun addition_isCorrect() {
    assertEquals(4, 2 + 2)
  }

  @Test
  fun testBookmarkScreenOpenAndClose() {
    val viewModel = com.example.ui.BrowserViewModel()
    assertEquals(false, viewModel.uiState.value.showBookmarkScreen)

    viewModel.openBookmarkScreen()
    assertEquals(true, viewModel.uiState.value.showBookmarkScreen)
    assertEquals(false, viewModel.uiState.value.showMenuSheet)
    assertEquals(false, viewModel.uiState.value.showTabsSheet)

    viewModel.closeBookmarkScreen()
    assertEquals(false, viewModel.uiState.value.showBookmarkScreen)
  }

  @Test
  fun testBookmarkFolderCreationAndSelection() {
    val viewModel = com.example.ui.BrowserViewModel()
    val initialFolders = viewModel.uiState.value.bookmarkFolders
    assertTrue(initialFolders.isNotEmpty())

    viewModel.createBookmarkFolder("Design Resources", 0xFF8B5CF6)
    val updatedFolders = viewModel.uiState.value.bookmarkFolders
    assertEquals(initialFolders.size + 1, updatedFolders.size)
    val newFolder = updatedFolders.find { it.name == "Design Resources" }
    assertNotNull(newFolder)
    assertEquals(newFolder!!.id, viewModel.uiState.value.selectedBookmarkFolderId)

    viewModel.selectBookmarkFolder("folder-all")
    assertEquals("folder-all", viewModel.uiState.value.selectedBookmarkFolderId)
  }

  @Test
  fun testAddBookmarkToSpeedDial() {
    val viewModel = com.example.ui.BrowserViewModel()
    val initialSpeedDials = viewModel.uiState.value.speedDials

    val testBookmark = com.example.model.BookmarkItem(
      id = "bm-test-1",
      title = "Dribbble",
      url = "https://dribbble.com",
      initial = "D"
    )

    val added = viewModel.addBookmarkToSpeedDial(testBookmark)
    assertTrue(added)
    assertTrue(viewModel.uiState.value.speedDials.any { it.url == "https://dribbble.com" })

    // Duplicate check
    val addedAgain = viewModel.addBookmarkToSpeedDial(testBookmark)
    assertFalse(addedAgain)
  }

  @Test
  fun testExportAndImportBookmarks() {
    val viewModel = com.example.ui.BrowserViewModel()
    val exportedJson = viewModel.exportBookmarksAsJson()
    assertTrue(exportedJson.contains("Android Developers"))
    assertTrue(exportedJson.contains("https://developer.android.com"))

    val exportedHtml = viewModel.exportBookmarksAsHtml()
    assertTrue(exportedHtml.contains("<!DOCTYPE NETSCAPE-Bookmark-file-1>"))
    assertTrue(exportedHtml.contains("https://developer.android.com"))

    // Import test
    val jsonToImport = """
      [
        {"title": "Kotlin Docs", "url": "https://kotlinlang.org/docs", "folder": "Work & Tools"},
        {"title": "Material 3", "url": "https://m3.material.io", "folder": "Work & Tools"}
      ]
    """.trimIndent()

    val count = viewModel.importBookmarks(jsonToImport, "folder-tools", "Work & Tools")
    assertEquals(2, count)
    assertTrue(viewModel.uiState.value.bookmarks.any { it.title == "Kotlin Docs" })
    assertTrue(viewModel.uiState.value.bookmarks.any { it.title == "Material 3" })
  }

  @Test
  fun testMoveBookmarkAndSelectiveDelete() {
    val viewModel = com.example.ui.BrowserViewModel()
    val initialBookmarks = viewModel.uiState.value.bookmarks
    assertTrue(initialBookmarks.isNotEmpty())

    val target = initialBookmarks.first()
    viewModel.moveBookmarkToFolder(target.id, "folder-favorites", "Favorites")
    val moved = viewModel.uiState.value.bookmarks.find { it.id == target.id }
    assertNotNull(moved)
    assertEquals("folder-favorites", moved!!.folderId)
    assertEquals("Favorites", moved.folderName)

    // Selective delete
    viewModel.deleteBookmarks(setOf(target.id))
    assertFalse(viewModel.uiState.value.bookmarks.any { it.id == target.id })
    assertEquals(initialBookmarks.size - 1, viewModel.uiState.value.bookmarks.size)
  }

  @Test
  fun testThemeModeTransitions() {
    val viewModel = com.example.ui.BrowserViewModel()
    assertEquals(com.example.ui.ThemeMode.SYSTEM, viewModel.uiState.value.themeMode)

    viewModel.setThemeMode(com.example.ui.ThemeMode.LIGHT)
    assertEquals(com.example.ui.ThemeMode.LIGHT, viewModel.uiState.value.themeMode)

    viewModel.setThemeMode(com.example.ui.ThemeMode.DARK)
    assertEquals(com.example.ui.ThemeMode.DARK, viewModel.uiState.value.themeMode)

    viewModel.setThemeMode(com.example.ui.ThemeMode.AMOLED)
    assertEquals(com.example.ui.ThemeMode.AMOLED, viewModel.uiState.value.themeMode)
  }
}
