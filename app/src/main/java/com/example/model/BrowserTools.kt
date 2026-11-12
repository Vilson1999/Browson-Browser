package com.example.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddBox
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Cookie
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.DesktopWindows
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Flight
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Screenshot
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.WifiTethering
import androidx.compose.material.icons.filled.ZoomIn
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.vector.ImageVector

data class BrowserTool(
    val id: String,
    val icon: ImageVector,
    val label: String,
    val description: String = ""
)

object BrowserToolCatalog {
    val ALL_TOOLS: List<BrowserTool> = listOf(
        // Default tools
        BrowserTool("history", Icons.Filled.History, "History", "Browse visited web pages"),
        BrowserTool("offline", Icons.Filled.Flight, "Offline pages", "Saved pages for reading offline"),
        BrowserTool("downloads", Icons.Filled.Download, "Downloads", "Manage downloaded files"),
        BrowserTool("bookmarks", Icons.Filled.BookmarkBorder, "Bookmarks", "Quick access to saved bookmarks"),
        BrowserTool("p2p_share", Icons.Filled.WifiTethering, "P2P Share", "Offline Wi-Fi Direct file & page transfer"),
        BrowserTool("desktop", Icons.Filled.DesktopWindows, "Desktop site", "Request desktop version of site"),
        BrowserTool("find", Icons.Filled.Search, "Find in page", "Search text on current page"),
        BrowserTool("share", Icons.Filled.Share, "Share", "Share page link or content"),
        BrowserTool("dark_mode", Icons.Filled.DarkMode, "Dark mode", "Toggle dark or light theme"),
        BrowserTool("translate", Icons.Filled.Translate, "Translate", "Translate page to preferred language"),
        // Extra useful tools for browsing
        BrowserTool("incognito", Icons.Filled.VisibilityOff, "Incognito", "Browse privately without history"),
        BrowserTool("reader", Icons.Filled.MenuBook, "Reader mode", "Distraction-free reading layout"),
        BrowserTool("print", Icons.Filled.Print, "Save as PDF", "Export page to PDF or print"),
        BrowserTool("screenshot", Icons.Filled.Screenshot, "Screenshot", "Capture page screenshot"),
        BrowserTool("clear_data", Icons.Filled.DeleteSweep, "Clear data", "Clear cookies, history & cache"),
        BrowserTool("zoom", Icons.Filled.ZoomIn, "Text zoom", "Adjust page font size and zoom"),
        BrowserTool("qr_code", Icons.Filled.QrCodeScanner, "QR Scanner", "Scan QR codes to open URLs"),
        BrowserTool("ad_block", Icons.Filled.Shield, "Ad Blocker", "Toggle ad & tracker protection"),
        BrowserTool("full_screen", Icons.Filled.Fullscreen, "Full screen", "Enter immersive fullscreen mode"),
        BrowserTool("cookie", Icons.Filled.Cookie, "Cookies", "Manage page permissions & cookies"),
        BrowserTool("view_source", Icons.Filled.Code, "View source", "Inspect HTML page source code"),
        BrowserTool("voice_search", Icons.Filled.Mic, "Voice search", "Search the web using your voice"),
        BrowserTool("night_light", Icons.Filled.Bedtime, "Blue light filter", "Warm colors for night reading"),
        BrowserTool("new_tab", Icons.Filled.AddBox, "New tab", "Open a fresh browser tab"),
        BrowserTool("home", Icons.Filled.Home, "Home page", "Return to Browson start page"),
        // Toolbar specific navigation tools
        BrowserTool("nav_back", Icons.AutoMirrored.Filled.ArrowBack, "Back", "Go back to previous page"),
        BrowserTool("nav_forward", Icons.AutoMirrored.Filled.ArrowForward, "Forward", "Go forward to next page"),
        BrowserTool("nav_home", Icons.Filled.Home, "Home", "Return to home page"),
        BrowserTool("nav_tabs", Icons.Filled.AddBox, "Tabs", "View and manage open tabs"),
        BrowserTool("nav_menu", Icons.Filled.Person, "Me / Menu", "Open tool menu")
    )

    val DEFAULT_ACTIVE_IDS: List<String> = listOf(
        "history", "offline", "downloads",
        "bookmarks", "desktop", "find",
        "share", "dark_mode", "translate"
    )

    val DEFAULT_AVAILABLE_IDS: List<String> = ALL_TOOLS
        .map { it.id }
        .filterNot { it in DEFAULT_ACTIVE_IDS || it.startsWith("nav_") }

    val DEFAULT_ACTIVE_TOOLBAR_IDS: List<String> = listOf(
        "nav_back", "nav_forward", "nav_home", "nav_tabs", "nav_menu"
    )

    val DEFAULT_AVAILABLE_TOOLBAR_IDS: List<String> = listOf(
        "history", "downloads", "bookmarks", "new_tab", "share",
        "dark_mode", "desktop", "find", "reader", "qr_code",
        "screenshot", "ad_block", "voice_search"
    )

    fun getTool(id: String): BrowserTool {
        return ALL_TOOLS.firstOrNull { it.id == id }
            ?: BrowserTool(id, Icons.Filled.Search, id.replace("_", " ").replaceFirstChar { it.uppercase() })
    }
}
