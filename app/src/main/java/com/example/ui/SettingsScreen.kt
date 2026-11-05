package com.example.ui

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.zIndex
import androidx.compose.runtime.mutableFloatStateOf
import kotlin.math.roundToInt
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Brightness4
import androidx.compose.material.icons.filled.BrightnessAuto
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DarkMode
import com.example.ui.theme.LocalAppTheme
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.ViewStream
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import com.example.model.BrowserToolCatalog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R

enum class SettingsMenuPage(val titleResId: Int) {
    HOME_SCREEN(R.string.settings_home_screen),
    APPEARANCE(R.string.settings_appearance),
    TOOL_MENU(R.string.settings_tool_menu),
    TOOLBAR(R.string.settings_toolbar),
    DOWNLOAD_MANAGER(R.string.settings_download_manager),
    SECURITY(R.string.settings_security),
    ADS_BLOCKER(R.string.settings_ads_blocker)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    themeMode: ThemeMode,
    onThemeModeChange: (ThemeMode) -> Unit,
    onBack: () -> Unit,
    activeToolIds: List<String> = com.example.model.BrowserToolCatalog.DEFAULT_ACTIVE_IDS,
    availableToolIds: List<String> = com.example.model.BrowserToolCatalog.DEFAULT_AVAILABLE_IDS,
    onRemoveTool: (String) -> Unit = {},
    onAddTool: (String) -> Unit = {},
    onMoveTool: (Int, Int) -> Unit = { _, _ -> },
    onSwapTools: (Int, Int) -> Unit = { _, _ -> },
    onResetTools: () -> Unit = {},
    activeToolbarToolIds: List<String> = com.example.model.BrowserToolCatalog.DEFAULT_ACTIVE_TOOLBAR_IDS,
    availableToolbarToolIds: List<String> = com.example.model.BrowserToolCatalog.DEFAULT_AVAILABLE_TOOLBAR_IDS,
    onRemoveToolbarTool: (String) -> Boolean = { false },
    onAddToolbarTool: (String) -> Boolean = { false },
    onMoveToolbarTool: (Int, Int) -> Unit = { _, _ -> },
    onResetToolbar: () -> Unit = {},
    onOpenBookmarks: () -> Unit = {}
) {
    val context = LocalContext.current
    val appTheme = LocalAppTheme.current
    val isDark = when (themeMode) {
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
        ThemeMode.DARK -> true
        ThemeMode.LIGHT -> false
        ThemeMode.AMOLED -> true
    }
    val isAmoled = appTheme.isAmoled

    // Active sub-page state: when null, shows the 7 main menus; when set, shows that menu's new page/window
    var currentSubPage by remember { mutableStateOf<SettingsMenuPage?>(null) }

    // Intercept hardware/gesture back: if inside sub-page, return to main settings menu list; else return to browser
    BackHandler {
        if (currentSubPage != null) {
            currentSubPage = null
        } else {
            onBack()
        }
    }

    // Persistent state for all settings options inside the sub-pages
    var showSpeedDialsOnHome by remember { mutableStateOf(true) }
    var showBookmarksOnHome by remember { mutableStateOf(true) }
    var searchEngine by remember { mutableStateOf("Google") }
    var bookmarkColumns by remember { mutableStateOf(5) }
    var showWeatherWidget by remember { mutableStateOf(true) }
    var openNewTabOnHome by remember { mutableStateOf(true) }

    var forceDarkWeb by remember { mutableStateOf(false) }
    var highContrastText by remember { mutableStateOf(false) }
    var smoothTransitions by remember { mutableStateOf(true) }
    var keepScreenAwake by remember { mutableStateOf(false) }

    var toolMenuVibration by remember { mutableStateOf(true) }
    var showToolMenuLabels by remember { mutableStateOf(true) }
    var autoCloseToolMenu by remember { mutableStateOf(false) }

    var toolbarPosition by remember { mutableStateOf("Bottom") }
    var showTabCounterBadge by remember { mutableStateOf(true) }
    var showForwardButton by remember { mutableStateOf(true) }
    var matchAddressBarColor by remember { mutableStateOf(true) }

    var downloadWifiOnly by remember { mutableStateOf(false) }
    var askDownloadLocation by remember { mutableStateOf(true) }
    var autoOpenDownloads by remember { mutableStateOf(false) }
    var autoResumeDownloads by remember { mutableStateOf(true) }

    var safeBrowsingEnabled by remember { mutableStateOf(true) }
    var httpsOnlyMode by remember { mutableStateOf(true) }
    var clearDataOnExit by remember { mutableStateOf(false) }
    var dntHeaderEnabled by remember { mutableStateOf(true) }
    var blockThirdPartyCookies by remember { mutableStateOf(true) }

    var adBlockerEnabled by remember { mutableStateOf(true) }
    var blockTrackersEnabled by remember { mutableStateOf(true) }
    var blockPopupsEnabled by remember { mutableStateOf(true) }
    var blockCookieBanners by remember { mutableStateOf(true) }
    var autoUpdateFilters by remember { mutableStateOf(true) }

    // Colors matching Browson palette
    val bgColor = appTheme.background
    val cardBgColor = appTheme.cardBg
    val cardBorderColor = appTheme.cardBorder
    val accentTint = appTheme.primary
    val badgeBgColor = when {
        isAmoled -> Color(0xFF141416)
        isDark -> Color(0xFF1E293B)
        else -> Color(0xFFEAF5EE)
    }
    val primaryTextColor = appTheme.onSurface
    val secondaryTextColor = appTheme.onSurfaceVariant

    // Animated transition between main settings menu and sub-page windows
    AnimatedContent(
        targetState = currentSubPage,
        transitionSpec = {
            if (targetState != null) {
                (slideInHorizontally { width -> width } + fadeIn()).togetherWith(
                    slideOutHorizontally { width -> -width / 3 } + fadeOut()
                )
            } else {
                (slideInHorizontally { width -> -width / 3 } + fadeIn()).togetherWith(
                    slideOutHorizontally { width -> width } + fadeOut()
                )
            }
        },
        label = "settings_navigation"
    ) { targetPage ->
        if (targetPage == null) {
            // ==========================================
            // MAIN SETTINGS MENU LIST (Full screen)
            // ==========================================
            Scaffold(
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("settings_screen"),
                containerColor = bgColor,
                topBar = {
                    TopAppBar(
                        title = {
                            Text(
                                text = stringResource(R.string.settings_title),
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp,
                                color = primaryTextColor
                            )
                        },
                        navigationIcon = {
                            IconButton(
                                onClick = onBack,
                                modifier = Modifier.testTag("settings_back_button")
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = stringResource(R.string.nav_back),
                                    tint = primaryTextColor
                                )
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = bgColor,
                            navigationIconContentColor = primaryTextColor,
                            titleContentColor = primaryTextColor
                        )
                    )
                }
            ) { innerPadding ->
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .testTag("settings_list"),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Header tip
                    item {
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 6.dp),
                            shape = RoundedCornerShape(12.dp),
                            color = badgeBgColor
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 14.dp, vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Shield,
                                    contentDescription = null,
                                    tint = accentTint,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = "Select any category below to configure its options on a dedicated page.",
                                    fontSize = 12.sp,
                                    color = accentTint,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }

                    // Display Theme Quick Switcher Card
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(
                                    width = if (isAmoled) 1.5.dp else 1.dp,
                                    color = cardBorderColor,
                                    shape = RoundedCornerShape(16.dp)
                                ),
                            colors = CardDefaults.cardColors(containerColor = cardBgColor),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Surface(
                                        shape = RoundedCornerShape(10.dp),
                                        color = accentTint.copy(alpha = 0.15f),
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Icon(
                                                imageVector = Icons.Filled.Palette,
                                                contentDescription = null,
                                                tint = accentTint,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                    }
                                    Column {
                                        Text(
                                            text = "Display Theme",
                                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                            color = primaryTextColor
                                        )
                                        Text(
                                            text = "Choose Light, Dark, or true AMOLED black mode",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = secondaryTextColor
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    listOf(
                                        Triple(ThemeMode.LIGHT, "Light", Icons.Filled.LightMode),
                                        Triple(ThemeMode.DARK, "Dark", Icons.Filled.DarkMode),
                                        Triple(ThemeMode.AMOLED, "AMOLED", Icons.Filled.Brightness4),
                                        Triple(ThemeMode.SYSTEM, "Auto", Icons.Filled.BrightnessAuto)
                                    ).forEach { (mode, label, icon) ->
                                        val isSelected = themeMode == mode
                                        val chipBg = if (isSelected) {
                                            accentTint.copy(alpha = 0.18f)
                                        } else {
                                            badgeBgColor
                                        }
                                        val chipBorder = if (isSelected) accentTint else cardBorderColor

                                        Surface(
                                            shape = RoundedCornerShape(12.dp),
                                            color = chipBg,
                                            border = androidx.compose.foundation.BorderStroke(1.dp, chipBorder),
                                            modifier = Modifier
                                                .weight(1f)
                                                .height(42.dp)
                                                .clip(RoundedCornerShape(12.dp))
                                                .clickable { onThemeModeChange(mode) }
                                        ) {
                                            Row(
                                                modifier = Modifier.fillMaxSize(),
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.Center
                                            ) {
                                                Icon(
                                                    imageVector = icon,
                                                    contentDescription = label,
                                                    tint = if (isSelected) accentTint else secondaryTextColor,
                                                    modifier = Modifier.size(15.dp)
                                                )
                                                Spacer(modifier = Modifier.width(3.dp))
                                                Text(
                                                    text = label,
                                                    fontSize = 11.sp,
                                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                                    color = if (isSelected) primaryTextColor else secondaryTextColor
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // 1. Home Screen Menu Card
                    item {
                        SettingsMenuItemCard(
                            title = stringResource(R.string.settings_home_screen),
                            subtitle = stringResource(R.string.settings_home_screen_desc),
                            icon = Icons.Filled.Home,
                            onClick = { currentSubPage = SettingsMenuPage.HOME_SCREEN },
                            testTag = "settings_menu_home_screen",
                            badgeBgColor = badgeBgColor,
                            accentTint = accentTint,
                            cardBgColor = cardBgColor,
                            primaryTextColor = primaryTextColor,
                            secondaryTextColor = secondaryTextColor
                        )
                    }

                    // 2. Appearance Menu Card
                    item {
                        SettingsMenuItemCard(
                            title = stringResource(R.string.settings_appearance),
                            subtitle = stringResource(R.string.settings_appearance_desc),
                            icon = Icons.Filled.Palette,
                            onClick = { currentSubPage = SettingsMenuPage.APPEARANCE },
                            testTag = "settings_menu_appearance",
                            badgeBgColor = badgeBgColor,
                            accentTint = accentTint,
                            cardBgColor = cardBgColor,
                            primaryTextColor = primaryTextColor,
                            secondaryTextColor = secondaryTextColor
                        )
                    }

                    // 3. Tool Menu Card
                    item {
                        SettingsMenuItemCard(
                            title = stringResource(R.string.settings_tool_menu),
                            subtitle = stringResource(R.string.settings_tool_menu_desc),
                            icon = Icons.Filled.Apps,
                            onClick = { currentSubPage = SettingsMenuPage.TOOL_MENU },
                            testTag = "settings_menu_tool_menu",
                            badgeBgColor = badgeBgColor,
                            accentTint = accentTint,
                            cardBgColor = cardBgColor,
                            primaryTextColor = primaryTextColor,
                            secondaryTextColor = secondaryTextColor
                        )
                    }

                    // 4. Toolbar Card
                    item {
                        SettingsMenuItemCard(
                            title = stringResource(R.string.settings_toolbar),
                            subtitle = stringResource(R.string.settings_toolbar_desc),
                            icon = Icons.Filled.ViewStream,
                            onClick = { currentSubPage = SettingsMenuPage.TOOLBAR },
                            testTag = "settings_menu_toolbar",
                            badgeBgColor = badgeBgColor,
                            accentTint = accentTint,
                            cardBgColor = cardBgColor,
                            primaryTextColor = primaryTextColor,
                            secondaryTextColor = secondaryTextColor
                        )
                    }

                    // Bookmarks Menu Card
                    item {
                        SettingsMenuItemCard(
                            title = stringResource(R.string.settings_bookmarks),
                            subtitle = stringResource(R.string.settings_bookmarks_desc),
                            icon = Icons.Filled.Bookmark,
                            onClick = onOpenBookmarks,
                            testTag = "settings_menu_bookmarks",
                            badgeBgColor = badgeBgColor,
                            accentTint = accentTint,
                            cardBgColor = cardBgColor,
                            primaryTextColor = primaryTextColor,
                            secondaryTextColor = secondaryTextColor
                        )
                    }

                    // 5. Download Manager Card
                    item {
                        SettingsMenuItemCard(
                            title = stringResource(R.string.settings_download_manager),
                            subtitle = stringResource(R.string.settings_download_manager_desc),
                            icon = Icons.Filled.Download,
                            onClick = { currentSubPage = SettingsMenuPage.DOWNLOAD_MANAGER },
                            testTag = "settings_menu_download_manager",
                            badgeBgColor = badgeBgColor,
                            accentTint = accentTint,
                            cardBgColor = cardBgColor,
                            primaryTextColor = primaryTextColor,
                            secondaryTextColor = secondaryTextColor
                        )
                    }

                    // 6. Security Card
                    item {
                        SettingsMenuItemCard(
                            title = stringResource(R.string.settings_security),
                            subtitle = stringResource(R.string.settings_security_desc),
                            icon = Icons.Filled.Security,
                            onClick = { currentSubPage = SettingsMenuPage.SECURITY },
                            testTag = "settings_menu_security",
                            badgeBgColor = badgeBgColor,
                            accentTint = accentTint,
                            cardBgColor = cardBgColor,
                            primaryTextColor = primaryTextColor,
                            secondaryTextColor = secondaryTextColor
                        )
                    }

                    // 7. Ads Blocker Card
                    item {
                        SettingsMenuItemCard(
                            title = stringResource(R.string.settings_ads_blocker),
                            subtitle = stringResource(R.string.settings_ads_blocker_desc),
                            icon = Icons.Filled.Block,
                            onClick = { currentSubPage = SettingsMenuPage.ADS_BLOCKER },
                            testTag = "settings_menu_ads_blocker",
                            badgeBgColor = badgeBgColor,
                            accentTint = accentTint,
                            cardBgColor = cardBgColor,
                            primaryTextColor = primaryTextColor,
                            secondaryTextColor = secondaryTextColor
                        )
                    }

                    item {
                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }
            }
        } else {
            // ==========================================
            // SUB-PAGE WINDOW FOR THE SELECTED MENU
            // ==========================================
            Scaffold(
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("settings_subpage_${targetPage.name.lowercase()}"),
                containerColor = bgColor,
                topBar = {
                    TopAppBar(
                        title = {
                            Text(
                                text = stringResource(targetPage.titleResId),
                                fontWeight = FontWeight.Bold,
                                fontSize = 19.sp,
                                color = primaryTextColor
                            )
                        },
                        navigationIcon = {
                            IconButton(
                                onClick = { currentSubPage = null },
                                modifier = Modifier.testTag("settings_subpage_back_button")
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Back to Settings",
                                    tint = primaryTextColor
                                )
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = bgColor,
                            navigationIconContentColor = primaryTextColor,
                            titleContentColor = primaryTextColor
                        )
                    )
                }
            ) { innerPadding ->
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    when (targetPage) {
                        SettingsMenuPage.HOME_SCREEN -> {
                            // Option 1: Search Engine
                            item {
                                SettingsOptionGroupCard(
                                    title = "Search Engine",
                                    cardBgColor = cardBgColor,
                                    primaryTextColor = primaryTextColor,
                                    cardBorderColor = cardBorderColor
                                ) {
                                    Text(
                                        text = "Choose your preferred search provider for address bar and home search queries:",
                                        fontSize = 12.sp,
                                        color = secondaryTextColor
                                    )
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        listOf("Google", "Bing", "DuckDuckGo").forEach { engine ->
                                            FilterChip(
                                                selected = searchEngine == engine,
                                                onClick = {
                                                    searchEngine = engine
                                                    Toast.makeText(context, "Search engine set to $engine", Toast.LENGTH_SHORT).show()
                                                },
                                                label = { Text(engine, fontSize = 12.sp) },
                                                leadingIcon = if (searchEngine == engine) {
                                                    { Icon(Icons.Filled.Check, contentDescription = null, modifier = Modifier.size(14.dp)) }
                                                } else null
                                            )
                                        }
                                    }
                                }
                            }

                            // Option 2: Home Shortcuts & Grid
                            item {
                                SettingsOptionGroupCard(
                                    title = "Shortcuts & Speed Dials",
                                    cardBgColor = cardBgColor,
                                    primaryTextColor = primaryTextColor,
                                    cardBorderColor = cardBorderColor
                                ) {
                                    SettingToggleRow(
                                        title = "Show Speed Dials",
                                        description = "Display top visited web portals grid",
                                        checked = showSpeedDialsOnHome,
                                        onCheckedChange = { showSpeedDialsOnHome = it },
                                        accentTint = accentTint,
                                        primaryTextColor = primaryTextColor,
                                        secondaryTextColor = secondaryTextColor
                                    )

                                    HorizontalDivider(color = cardBorderColor)

                                    SettingToggleRow(
                                        title = "Show Bookmarks on Home",
                                        description = "Display bookmark shortcuts row with add icon",
                                        checked = showBookmarksOnHome,
                                        onCheckedChange = { showBookmarksOnHome = it },
                                        accentTint = accentTint,
                                        primaryTextColor = primaryTextColor,
                                        secondaryTextColor = secondaryTextColor
                                    )

                                    HorizontalDivider(color = cardBorderColor)

                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 6.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = "Bookmark Columns",
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.Medium,
                                                color = primaryTextColor
                                            )
                                            Text(
                                                text = "Current layout: $bookmarkColumns columns per row",
                                                fontSize = 11.sp,
                                                color = secondaryTextColor
                                            )
                                        }
                                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                            listOf(4, 5).forEach { cols ->
                                                FilterChip(
                                                    selected = bookmarkColumns == cols,
                                                    onClick = { bookmarkColumns = cols },
                                                    label = { Text("$cols", fontSize = 11.sp) }
                                                )
                                            }
                                        }
                                    }
                                }
                            }

                            // Option 3: Additional Home Preferences
                            item {
                                SettingsOptionGroupCard(
                                    title = "Home Behavior",
                                    cardBgColor = cardBgColor,
                                    primaryTextColor = primaryTextColor,
                                    cardBorderColor = cardBorderColor
                                ) {
                                    SettingToggleRow(
                                        title = "Open New Tabs to Home",
                                        description = "New tabs start on Home screen with quick shortcuts",
                                        checked = openNewTabOnHome,
                                        onCheckedChange = { openNewTabOnHome = it },
                                        accentTint = accentTint,
                                        primaryTextColor = primaryTextColor,
                                        secondaryTextColor = secondaryTextColor
                                    )

                                    HorizontalDivider(color = cardBorderColor)

                                    SettingToggleRow(
                                        title = "Show Developer Info",
                                        description = "Display Vilson Sonkar author badge on Home",
                                        checked = showWeatherWidget,
                                        onCheckedChange = { showWeatherWidget = it },
                                        accentTint = accentTint,
                                        primaryTextColor = primaryTextColor,
                                        secondaryTextColor = secondaryTextColor
                                    )
                                }
                            }
                        }

                        SettingsMenuPage.APPEARANCE -> {
                            // Option 1: Theme Wallpaper
                            item {
                                SettingsOptionGroupCard(
                                    title = "Theme Wallpaper",
                                    cardBgColor = cardBgColor,
                                    primaryTextColor = primaryTextColor,
                                    cardBorderColor = cardBorderColor
                                ) {
                                    Text(
                                        text = "Select the base color theme for the browser interface:",
                                        fontSize = 12.sp,
                                        color = secondaryTextColor
                                    )
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        FilterChip(
                                            selected = themeMode == ThemeMode.LIGHT,
                                            onClick = { onThemeModeChange(ThemeMode.LIGHT) },
                                            label = { Text("Light", fontSize = 11.sp) },
                                            leadingIcon = {
                                                Icon(Icons.Filled.LightMode, contentDescription = null, modifier = Modifier.size(14.dp))
                                            }
                                        )
                                        FilterChip(
                                            selected = themeMode == ThemeMode.DARK,
                                            onClick = { onThemeModeChange(ThemeMode.DARK) },
                                            label = { Text("Dark", fontSize = 11.sp) },
                                            leadingIcon = {
                                                Icon(Icons.Filled.DarkMode, contentDescription = null, modifier = Modifier.size(14.dp))
                                            }
                                        )
                                        FilterChip(
                                            selected = themeMode == ThemeMode.AMOLED,
                                            onClick = { onThemeModeChange(ThemeMode.AMOLED) },
                                            label = { Text("AMOLED", fontSize = 11.sp) },
                                            leadingIcon = {
                                                Icon(Icons.Filled.Brightness4, contentDescription = null, modifier = Modifier.size(14.dp))
                                            }
                                        )
                                        FilterChip(
                                            selected = themeMode == ThemeMode.SYSTEM,
                                            onClick = { onThemeModeChange(ThemeMode.SYSTEM) },
                                            label = { Text("Auto", fontSize = 11.sp) },
                                            leadingIcon = {
                                                Icon(Icons.Filled.BrightnessAuto, contentDescription = null, modifier = Modifier.size(14.dp))
                                            }
                                        )
                                    }
                                }
                            }

                            // Option 2: Web Page Display
                            item {
                                SettingsOptionGroupCard(
                                    title = "Web Page Display",
                                    cardBgColor = cardBgColor,
                                    primaryTextColor = primaryTextColor,
                                    cardBorderColor = cardBorderColor
                                ) {
                                    SettingToggleRow(
                                        title = "Force Dark Mode on Web Pages",
                                        description = "Inverts white backgrounds to dark for nighttime browsing",
                                        checked = forceDarkWeb,
                                        onCheckedChange = { forceDarkWeb = it },
                                        accentTint = accentTint,
                                        primaryTextColor = primaryTextColor,
                                        secondaryTextColor = secondaryTextColor
                                    )

                                    HorizontalDivider(color = cardBorderColor)

                                    SettingToggleRow(
                                        title = "High Contrast Text",
                                        description = "Enhance text edges and contrast for better readability",
                                        checked = highContrastText,
                                        onCheckedChange = { highContrastText = it },
                                        accentTint = accentTint,
                                        primaryTextColor = primaryTextColor,
                                        secondaryTextColor = secondaryTextColor
                                    )

                                    HorizontalDivider(color = cardBorderColor)

                                    SettingToggleRow(
                                        title = "Keep Screen Awake While Reading",
                                        description = "Prevents display timeout while reading long web articles",
                                        checked = keepScreenAwake,
                                        onCheckedChange = { keepScreenAwake = it },
                                        accentTint = accentTint,
                                        primaryTextColor = primaryTextColor,
                                        secondaryTextColor = secondaryTextColor
                                    )
                                }
                            }

                            // Option 3: Animations
                            item {
                                SettingsOptionGroupCard(
                                    title = "Motion & Animations",
                                    cardBgColor = cardBgColor,
                                    primaryTextColor = primaryTextColor,
                                    cardBorderColor = cardBorderColor
                                ) {
                                    SettingToggleRow(
                                        title = "Smooth Page Transitions",
                                        description = "Enable animated screen transitions and modal reveals",
                                        checked = smoothTransitions,
                                        onCheckedChange = { smoothTransitions = it },
                                        accentTint = accentTint,
                                        primaryTextColor = primaryTextColor,
                                        secondaryTextColor = secondaryTextColor
                                    )
                                }
                            }
                        }

                        SettingsMenuPage.TOOL_MENU -> {
                            // ==========================================
                            // 1. TOP BIG BOX: Tools in Tool Menu
                            // ==========================================
                            item {
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("top_box_tool_menu"),
                                    shape = RoundedCornerShape(20.dp),
                                    colors = CardDefaults.cardColors(containerColor = cardBgColor),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, cardBorderColor)
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp)
                                    ) {
                                        // Header: Title + count + Reset button
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                                            ) {
                                                Box(
                                                    modifier = Modifier
                                                        .size(32.dp)
                                                        .clip(CircleShape)
                                                        .background(accentTint.copy(alpha = 0.15f)),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Filled.Apps,
                                                        contentDescription = null,
                                                        tint = accentTint,
                                                        modifier = Modifier.size(18.dp)
                                                    )
                                                }
                                                Column {
                                                    Text(
                                                        text = "Tools in Tool Menu",
                                                        fontSize = 15.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = primaryTextColor
                                                    )
                                                    Text(
                                                        text = "${activeToolIds.size} tools shown (3 columns)",
                                                        fontSize = 11.sp,
                                                        color = accentTint
                                                    )
                                                }
                                            }

                                            Button(
                                                onClick = {
                                                    onResetTools()
                                                    Toast.makeText(context, "Reset to default tools", Toast.LENGTH_SHORT).show()
                                                },
                                                colors = ButtonDefaults.buttonColors(
                                                    containerColor = accentTint.copy(alpha = 0.15f),
                                                    contentColor = accentTint
                                                ),
                                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                                                shape = RoundedCornerShape(12.dp),
                                                modifier = Modifier
                                                    .height(30.dp)
                                                    .testTag("reset_tools_button")
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Filled.RestartAlt,
                                                    contentDescription = null,
                                                    modifier = Modifier.size(14.dp)
                                                )
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text("Reset", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(8.dp))

                                        Text(
                                            text = "Long press any icon to grab and reposition where you slide your finger. Tap the minus (-) icon at the top-right corner to remove.",
                                            fontSize = 11.sp,
                                            color = secondaryTextColor
                                        )

                                        Spacer(modifier = Modifier.height(14.dp))

                                        // 3 COLUMNS ICON GRID IN TOP BOX
                                        if (activeToolIds.isEmpty()) {
                                            Box(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(vertical = 20.dp),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(
                                                    text = "No tools in menu. Add tools from the box below.",
                                                    fontSize = 12.sp,
                                                    color = secondaryTextColor
                                                )
                                            }
                                        } else {
                                            ReorderableToolGrid(
                                                toolIds = activeToolIds,
                                                isTopBox = true,
                                                badgeIcon = Icons.Filled.Remove,
                                                badgeColor = Color(0xFFE53935),
                                                badgeContentDesc = "Remove tool",
                                                onBadgeClick = { toolId ->
                                                    val tool = com.example.model.BrowserToolCatalog.getTool(toolId)
                                                    onRemoveTool(toolId)
                                                    Toast.makeText(context, "Removed ${tool.label}", Toast.LENGTH_SHORT).show()
                                                },
                                                onReorder = { from, to ->
                                                    onMoveTool(from, to)
                                                },
                                                cardBgColor = if (isDark) Color(0xFF233027) else Color(0xFFEAF4EE),
                                                primaryTextColor = primaryTextColor,
                                                accentTint = accentTint,
                                                enableDragReorder = true,
                                                tagPrefix = "active_tool"
                                            )
                                        }
                                    }
                                }
                            }

                            // ==========================================
                            // 2. BELOW BIG BOX: Tools that can be added
                            // ==========================================
                            item {
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("below_box_tool_menu"),
                                    shape = RoundedCornerShape(20.dp),
                                    colors = CardDefaults.cardColors(containerColor = cardBgColor),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, cardBorderColor)
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp)
                                    ) {
                                        // Header: Title + count
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                                            ) {
                                                Box(
                                                    modifier = Modifier
                                                        .size(32.dp)
                                                        .clip(CircleShape)
                                                        .background(Color(0xFF2E7D32).copy(alpha = 0.15f)),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Filled.AddCircleOutline,
                                                        contentDescription = null,
                                                        tint = Color(0xFF2E7D32),
                                                        modifier = Modifier.size(18.dp)
                                                    )
                                                }
                                                Column {
                                                    Text(
                                                        text = "Tools you can add",
                                                        fontSize = 15.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = primaryTextColor
                                                    )
                                                    Text(
                                                        text = "${availableToolIds.size} available extra tools (3 columns)",
                                                        fontSize = 11.sp,
                                                        color = Color(0xFF2E7D32)
                                                    )
                                                }
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(8.dp))

                                        Text(
                                            text = "Useful extra tools for browsing. Tap the plus (+) icon or tap any card to add into your Tool Menu above.",
                                            fontSize = 11.sp,
                                            color = secondaryTextColor
                                        )

                                        Spacer(modifier = Modifier.height(14.dp))

                                        // 3 COLUMNS ICON GRID IN BELOW BOX
                                        if (availableToolIds.isEmpty()) {
                                            Box(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(vertical = 20.dp),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(
                                                    text = "All tools are currently added to your Tool Menu.",
                                                    fontSize = 12.sp,
                                                    color = secondaryTextColor
                                                )
                                            }
                                        } else {
                                            ReorderableToolGrid(
                                                toolIds = availableToolIds,
                                                isTopBox = false,
                                                badgeIcon = Icons.Filled.Add,
                                                badgeColor = Color(0xFF2E7D32),
                                                badgeContentDesc = "Add tool",
                                                onBadgeClick = { toolId ->
                                                    val tool = com.example.model.BrowserToolCatalog.getTool(toolId)
                                                    onAddTool(toolId)
                                                    Toast.makeText(context, "Added ${tool.label} to Tool Menu", Toast.LENGTH_SHORT).show()
                                                },
                                                onCardClick = { toolId ->
                                                    val tool = com.example.model.BrowserToolCatalog.getTool(toolId)
                                                    onAddTool(toolId)
                                                    Toast.makeText(context, "Added ${tool.label} to Tool Menu", Toast.LENGTH_SHORT).show()
                                                },
                                                onReorder = { _, _ -> },
                                                cardBgColor = if (isDark) Color(0xFF233027) else Color(0xFFEAF4EE),
                                                primaryTextColor = primaryTextColor,
                                                accentTint = accentTint,
                                                enableDragReorder = false,
                                                tagPrefix = "available_tool"
                                            )
                                        }
                                    }
                                }
                            }

                            // Optional extra settings for tool menu behavior
                            item {
                                SettingsOptionGroupCard(
                                    title = "Tool Menu Options",
                                    cardBgColor = cardBgColor,
                                    primaryTextColor = primaryTextColor,
                                    cardBorderColor = cardBorderColor
                                ) {
                                    SettingToggleRow(
                                        title = "Haptic Vibration on Tap",
                                        description = "Gentle haptic feedback when tapping menu items",
                                        checked = toolMenuVibration,
                                        onCheckedChange = { toolMenuVibration = it },
                                        accentTint = accentTint,
                                        primaryTextColor = primaryTextColor,
                                        secondaryTextColor = secondaryTextColor
                                    )

                                    HorizontalDivider(color = cardBorderColor)

                                    SettingToggleRow(
                                        title = "Auto-close on Action",
                                        description = "Dismiss the tool menu immediately after selecting an item",
                                        checked = autoCloseToolMenu,
                                        onCheckedChange = { autoCloseToolMenu = it },
                                        accentTint = accentTint,
                                        primaryTextColor = primaryTextColor,
                                        secondaryTextColor = secondaryTextColor
                                    )
                                }
                            }
                        }

                        SettingsMenuPage.TOOLBAR -> {
                            // ==========================================
                            // 1. TOP BIG BOX: Tools in Toolbar (min 4, max 6)
                            // ==========================================
                            item {
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("top_box_toolbar"),
                                    shape = RoundedCornerShape(20.dp),
                                    colors = CardDefaults.cardColors(containerColor = cardBgColor),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, cardBorderColor)
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp)
                                    ) {
                                        // Header: Title + count + Reset button
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                                            ) {
                                                Box(
                                                    modifier = Modifier
                                                        .size(32.dp)
                                                        .clip(CircleShape)
                                                        .background(accentTint.copy(alpha = 0.15f)),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Filled.ViewStream,
                                                        contentDescription = null,
                                                        tint = accentTint,
                                                        modifier = Modifier.size(18.dp)
                                                    )
                                                }
                                                Column {
                                                    Text(
                                                        text = "Tools in Toolbar",
                                                        fontSize = 15.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = primaryTextColor
                                                    )
                                                    Text(
                                                        text = "${activeToolbarToolIds.size} / 6 icons (min 4, max 6)",
                                                        fontSize = 11.sp,
                                                        color = accentTint
                                                    )
                                                }
                                            }

                                            Button(
                                                onClick = {
                                                    onResetToolbar()
                                                    Toast.makeText(context, "Reset to default toolbar icons", Toast.LENGTH_SHORT).show()
                                                },
                                                colors = ButtonDefaults.buttonColors(
                                                    containerColor = accentTint.copy(alpha = 0.15f),
                                                    contentColor = accentTint
                                                ),
                                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                                                shape = RoundedCornerShape(12.dp),
                                                modifier = Modifier
                                                    .height(30.dp)
                                                    .testTag("reset_toolbar_button")
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Filled.RestartAlt,
                                                    contentDescription = null,
                                                    modifier = Modifier.size(14.dp)
                                                )
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text("Reset", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(8.dp))

                                        Text(
                                            text = "Long press any icon to grab and slide to reposition. Tap the minus (-) icon to remove. Toolbar requires min 4 and max 6 icons.",
                                            fontSize = 11.sp,
                                            color = secondaryTextColor
                                        )

                                        Spacer(modifier = Modifier.height(14.dp))

                                        ReorderableToolGrid(
                                            toolIds = activeToolbarToolIds,
                                            isTopBox = true,
                                            badgeIcon = Icons.Filled.Remove,
                                            badgeColor = Color(0xFFE53935),
                                            badgeContentDesc = "Remove from toolbar",
                                            onBadgeClick = { toolId ->
                                                if (activeToolbarToolIds.size <= 4) {
                                                    Toast.makeText(context, "Toolbar requires at least 4 icons", Toast.LENGTH_SHORT).show()
                                                } else {
                                                    val tool = com.example.model.BrowserToolCatalog.getTool(toolId)
                                                    val removed = onRemoveToolbarTool(toolId)
                                                    if (removed) {
                                                        Toast.makeText(context, "Removed ${tool.label} from Toolbar", Toast.LENGTH_SHORT).show()
                                                    }
                                                }
                                            },
                                            onReorder = { from, to ->
                                                onMoveToolbarTool(from, to)
                                            },
                                            cardBgColor = if (isDark) Color(0xFF233027) else Color(0xFFEAF4EE),
                                            primaryTextColor = primaryTextColor,
                                            accentTint = accentTint,
                                            enableDragReorder = true,
                                            tagPrefix = "toolbar_active_tool"
                                        )
                                    }
                                }
                            }

                            // ==========================================
                            // 2. BELOW BIG BOX: Tools that can be added to Toolbar
                            // ==========================================
                            item {
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("below_box_toolbar"),
                                    shape = RoundedCornerShape(20.dp),
                                    colors = CardDefaults.cardColors(containerColor = cardBgColor),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, cardBorderColor)
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp)
                                    ) {
                                        // Header: Title + count
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                                            ) {
                                                Box(
                                                    modifier = Modifier
                                                        .size(32.dp)
                                                        .clip(CircleShape)
                                                        .background(Color(0xFF2E7D32).copy(alpha = 0.15f)),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Filled.AddCircleOutline,
                                                        contentDescription = null,
                                                        tint = Color(0xFF2E7D32),
                                                        modifier = Modifier.size(18.dp)
                                                    )
                                                }
                                                Column {
                                                    Text(
                                                        text = "Tools you can add to Toolbar",
                                                        fontSize = 15.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = primaryTextColor
                                                    )
                                                    Text(
                                                        text = "${availableToolbarToolIds.size} available tools (max 6 in toolbar)",
                                                        fontSize = 11.sp,
                                                        color = Color(0xFF2E7D32)
                                                    )
                                                }
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(8.dp))

                                        Text(
                                            text = "Tap the plus (+) icon or tap any card to add into your Toolbar above. Toolbar can hold up to 6 icons.",
                                            fontSize = 11.sp,
                                            color = secondaryTextColor
                                        )

                                        Spacer(modifier = Modifier.height(14.dp))

                                        if (availableToolbarToolIds.isEmpty()) {
                                            Box(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(vertical = 20.dp),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(
                                                    text = "All tools are currently added to your Toolbar.",
                                                    fontSize = 12.sp,
                                                    color = secondaryTextColor
                                                )
                                            }
                                        } else {
                                            ReorderableToolGrid(
                                                toolIds = availableToolbarToolIds,
                                                isTopBox = false,
                                                badgeIcon = Icons.Filled.Add,
                                                badgeColor = Color(0xFF2E7D32),
                                                badgeContentDesc = "Add to toolbar",
                                                onBadgeClick = { toolId ->
                                                    if (activeToolbarToolIds.size >= 6) {
                                                        Toast.makeText(context, "Toolbar can have at most 6 icons", Toast.LENGTH_SHORT).show()
                                                    } else {
                                                        val tool = com.example.model.BrowserToolCatalog.getTool(toolId)
                                                        val added = onAddToolbarTool(toolId)
                                                        if (added) {
                                                            Toast.makeText(context, "Added ${tool.label} to Toolbar", Toast.LENGTH_SHORT).show()
                                                        }
                                                    }
                                                },
                                                onCardClick = { toolId ->
                                                    if (activeToolbarToolIds.size >= 6) {
                                                        Toast.makeText(context, "Toolbar can have at most 6 icons", Toast.LENGTH_SHORT).show()
                                                    } else {
                                                        val tool = com.example.model.BrowserToolCatalog.getTool(toolId)
                                                        val added = onAddToolbarTool(toolId)
                                                        if (added) {
                                                            Toast.makeText(context, "Added ${tool.label} to Toolbar", Toast.LENGTH_SHORT).show()
                                                        }
                                                    }
                                                },
                                                onReorder = { _, _ -> },
                                                cardBgColor = if (isDark) Color(0xFF233027) else Color(0xFFEAF4EE),
                                                primaryTextColor = primaryTextColor,
                                                accentTint = accentTint,
                                                enableDragReorder = false,
                                                tagPrefix = "toolbar_available_tool"
                                            )
                                        }
                                    }
                                }
                            }

                            // Option 3: Toolbar Placement & Extras
                            item {
                                SettingsOptionGroupCard(
                                    title = "Toolbar Placement",
                                    cardBgColor = cardBgColor,
                                    primaryTextColor = primaryTextColor,
                                    cardBorderColor = cardBorderColor
                                ) {
                                    Text(
                                        text = "Select position of the main navigation toolbar:",
                                        fontSize = 12.sp,
                                        color = secondaryTextColor
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        listOf("Bottom", "Top").forEach { pos ->
                                            FilterChip(
                                                selected = toolbarPosition == pos,
                                                onClick = { toolbarPosition = pos },
                                                label = { Text(pos, fontSize = 12.sp) }
                                            )
                                        }
                                    }
                                }
                            }

                            item {
                                SettingsOptionGroupCard(
                                    title = "Toolbar Buttons & Indicators",
                                    cardBgColor = cardBgColor,
                                    primaryTextColor = primaryTextColor,
                                    cardBorderColor = cardBorderColor
                                ) {
                                    SettingToggleRow(
                                        title = "Show Tab Counter Badge",
                                        description = "Display number of open tabs inside the tab icon",
                                        checked = showTabCounterBadge,
                                        onCheckedChange = { showTabCounterBadge = it },
                                        accentTint = accentTint,
                                        primaryTextColor = primaryTextColor,
                                        secondaryTextColor = secondaryTextColor
                                    )

                                    HorizontalDivider(color = cardBorderColor)

                                    SettingToggleRow(
                                        title = "Always Show Forward Button",
                                        description = "Keep forward button visible even when history is empty",
                                        checked = showForwardButton,
                                        onCheckedChange = { showForwardButton = it },
                                        accentTint = accentTint,
                                        primaryTextColor = primaryTextColor,
                                        secondaryTextColor = secondaryTextColor
                                    )

                                    HorizontalDivider(color = cardBorderColor)

                                    SettingToggleRow(
                                        title = "Match Address Bar Color",
                                        description = "Blend address bar background with toolbar theme",
                                        checked = matchAddressBarColor,
                                        onCheckedChange = { matchAddressBarColor = it },
                                        accentTint = accentTint,
                                        primaryTextColor = primaryTextColor,
                                        secondaryTextColor = secondaryTextColor
                                    )
                                }
                            }
                        }

                        SettingsMenuPage.DOWNLOAD_MANAGER -> {
                            item {
                                SettingsOptionGroupCard(
                                    title = "Storage Directory",
                                    cardBgColor = cardBgColor,
                                    primaryTextColor = primaryTextColor,
                                    cardBorderColor = cardBorderColor
                                ) {
                                    Text(
                                        text = "Files are saved to your device's internal storage:",
                                        fontSize = 12.sp,
                                        color = secondaryTextColor
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = badgeBgColor,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text(
                                            text = "/storage/emulated/0/Download/Browson",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = accentTint,
                                            modifier = Modifier.padding(10.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "Free space: 28.5 GB available",
                                        fontSize = 11.sp,
                                        color = secondaryTextColor
                                    )
                                }
                            }

                            item {
                                SettingsOptionGroupCard(
                                    title = "Download Preferences",
                                    cardBgColor = cardBgColor,
                                    primaryTextColor = primaryTextColor,
                                    cardBorderColor = cardBorderColor
                                ) {
                                    SettingToggleRow(
                                        title = "Download over Wi-Fi Only",
                                        description = "Prevents downloading large files over cellular data",
                                        checked = downloadWifiOnly,
                                        onCheckedChange = { downloadWifiOnly = it },
                                        accentTint = accentTint,
                                        primaryTextColor = primaryTextColor,
                                        secondaryTextColor = secondaryTextColor
                                    )

                                    HorizontalDivider(color = cardBorderColor)

                                    SettingToggleRow(
                                        title = "Ask Where to Save Each File",
                                        description = "Prompts with file name and destination dialog",
                                        checked = askDownloadLocation,
                                        onCheckedChange = { askDownloadLocation = it },
                                        accentTint = accentTint,
                                        primaryTextColor = primaryTextColor,
                                        secondaryTextColor = secondaryTextColor
                                    )

                                    HorizontalDivider(color = cardBorderColor)

                                    SettingToggleRow(
                                        title = "Auto-Open Completed Downloads",
                                        description = "Launches PDF, image, or APK immediately upon completion",
                                        checked = autoOpenDownloads,
                                        onCheckedChange = { autoOpenDownloads = it },
                                        accentTint = accentTint,
                                        primaryTextColor = primaryTextColor,
                                        secondaryTextColor = secondaryTextColor
                                    )

                                    HorizontalDivider(color = cardBorderColor)

                                    SettingToggleRow(
                                        title = "Auto-Resume Interrupted Downloads",
                                        description = "Resumes paused downloads when network reconnects",
                                        checked = autoResumeDownloads,
                                        onCheckedChange = { autoResumeDownloads = it },
                                        accentTint = accentTint,
                                        primaryTextColor = primaryTextColor,
                                        secondaryTextColor = secondaryTextColor
                                    )
                                }
                            }
                        }

                        SettingsMenuPage.SECURITY -> {
                            item {
                                SettingsOptionGroupCard(
                                    title = "Safe Browsing & Encryption",
                                    cardBgColor = cardBgColor,
                                    primaryTextColor = primaryTextColor,
                                    cardBorderColor = cardBorderColor
                                ) {
                                    SettingToggleRow(
                                        title = "Safe Browsing Protection",
                                        description = "Alerts you before opening known malicious or deceptive websites",
                                        checked = safeBrowsingEnabled,
                                        onCheckedChange = { safeBrowsingEnabled = it },
                                        accentTint = accentTint,
                                        primaryTextColor = primaryTextColor,
                                        secondaryTextColor = secondaryTextColor
                                    )

                                    HorizontalDivider(color = cardBorderColor)

                                    SettingToggleRow(
                                        title = "Always Use HTTPS",
                                        description = "Automatically upgrades all website connections to encrypted HTTPS",
                                        checked = httpsOnlyMode,
                                        onCheckedChange = { httpsOnlyMode = it },
                                        accentTint = accentTint,
                                        primaryTextColor = primaryTextColor,
                                        secondaryTextColor = secondaryTextColor
                                    )

                                    HorizontalDivider(color = cardBorderColor)

                                    SettingToggleRow(
                                        title = "Send Do Not Track (DNT) Header",
                                        description = "Sends standard HTTP header requesting sites not to track you",
                                        checked = dntHeaderEnabled,
                                        onCheckedChange = { dntHeaderEnabled = it },
                                        accentTint = accentTint,
                                        primaryTextColor = primaryTextColor,
                                        secondaryTextColor = secondaryTextColor
                                    )
                                }
                            }

                            item {
                                SettingsOptionGroupCard(
                                    title = "Privacy & Data Cleanup",
                                    cardBgColor = cardBgColor,
                                    primaryTextColor = primaryTextColor,
                                    cardBorderColor = cardBorderColor
                                ) {
                                    SettingToggleRow(
                                        title = "Clear Browsing Data on Exit",
                                        description = "Automatically purges cache, history, and cookies when app closes",
                                        checked = clearDataOnExit,
                                        onCheckedChange = { clearDataOnExit = it },
                                        accentTint = accentTint,
                                        primaryTextColor = primaryTextColor,
                                        secondaryTextColor = secondaryTextColor
                                    )

                                    HorizontalDivider(color = cardBorderColor)

                                    SettingToggleRow(
                                        title = "Block Third-Party Tracking Cookies",
                                        description = "Stops advertisers from tracking you across multiple domains",
                                        checked = blockThirdPartyCookies,
                                        onCheckedChange = { blockThirdPartyCookies = it },
                                        accentTint = accentTint,
                                        primaryTextColor = primaryTextColor,
                                        secondaryTextColor = secondaryTextColor
                                    )

                                    Spacer(modifier = Modifier.height(10.dp))

                                    Button(
                                        onClick = {
                                            Toast.makeText(context, "Browsing data, cache & cookies cleared", Toast.LENGTH_SHORT).show()
                                        },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = Color(0xFFBA1A1A),
                                            contentColor = Color.White
                                        ),
                                        shape = RoundedCornerShape(10.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Icon(Icons.Filled.Delete, contentDescription = null, modifier = Modifier.size(18.dp))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("Clear Browsing History & Cache Now", fontSize = 13.sp)
                                    }
                                }
                            }
                        }

                        SettingsMenuPage.ADS_BLOCKER -> {
                            item {
                                SettingsOptionGroupCard(
                                    title = "Shield Master Control",
                                    cardBgColor = cardBgColor,
                                    primaryTextColor = primaryTextColor,
                                    cardBorderColor = cardBorderColor
                                ) {
                                    SettingToggleRow(
                                        title = "Ad Blocker Shield",
                                        description = "Filter intrusive banner ads, video overlays & sponsor tags",
                                        checked = adBlockerEnabled,
                                        onCheckedChange = { adBlockerEnabled = it },
                                        accentTint = accentTint,
                                        primaryTextColor = primaryTextColor,
                                        secondaryTextColor = secondaryTextColor
                                    )

                                    Spacer(modifier = Modifier.height(6.dp))

                                    Surface(
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(10.dp),
                                        color = badgeBgColor
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(horizontal = 12.dp, vertical = 10.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                imageVector = Icons.Filled.Shield,
                                                contentDescription = null,
                                                tint = accentTint,
                                                modifier = Modifier.size(18.dp)
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                text = "Browson Shield Active • 14,200+ filter rules loaded",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Medium,
                                                color = accentTint
                                            )
                                        }
                                    }
                                }
                            }

                            item {
                                SettingsOptionGroupCard(
                                    title = "Protection Filters",
                                    cardBgColor = cardBgColor,
                                    primaryTextColor = primaryTextColor,
                                    cardBorderColor = cardBorderColor
                                ) {
                                    SettingToggleRow(
                                        title = "Block Web Trackers & Telemetry",
                                        description = "Stops analytics and behavioral profiling scripts",
                                        checked = blockTrackersEnabled,
                                        onCheckedChange = { blockTrackersEnabled = it },
                                        accentTint = accentTint,
                                        primaryTextColor = primaryTextColor,
                                        secondaryTextColor = secondaryTextColor
                                    )

                                    HorizontalDivider(color = cardBorderColor)

                                    SettingToggleRow(
                                        title = "Block Deceptive Popups & Redirects",
                                        description = "Suppresses unwanted new tabs and fake system alert dialogs",
                                        checked = blockPopupsEnabled,
                                        onCheckedChange = { blockPopupsEnabled = it },
                                        accentTint = accentTint,
                                        primaryTextColor = primaryTextColor,
                                        secondaryTextColor = secondaryTextColor
                                    )

                                    HorizontalDivider(color = cardBorderColor)

                                    SettingToggleRow(
                                        title = "Block Cookie Consent Banners",
                                        description = "Hides obtrusive GDPR and cookie warning prompts",
                                        checked = blockCookieBanners,
                                        onCheckedChange = { blockCookieBanners = it },
                                        accentTint = accentTint,
                                        primaryTextColor = primaryTextColor,
                                        secondaryTextColor = secondaryTextColor
                                    )

                                    HorizontalDivider(color = cardBorderColor)

                                    SettingToggleRow(
                                        title = "Auto-Update Filter Rules",
                                        description = "Fetch latest community blocklists automatically in background",
                                        checked = autoUpdateFilters,
                                        onCheckedChange = { autoUpdateFilters = it },
                                        accentTint = accentTint,
                                        primaryTextColor = primaryTextColor,
                                        secondaryTextColor = secondaryTextColor
                                    )
                                }
                            }
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun SettingsMenuItemCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    onClick: () -> Unit,
    testTag: String,
    badgeBgColor: Color,
    accentTint: Color,
    cardBgColor: Color,
    primaryTextColor: Color,
    secondaryTextColor: Color
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .testTag(testTag),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = cardBgColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(badgeBgColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = accentTint,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = primaryTextColor
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    fontSize = 12.sp,
                    color = secondaryTextColor,
                    maxLines = 1
                )
            }

            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = "Open $title",
                tint = secondaryTextColor,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
fun SettingsOptionGroupCard(
    title: String,
    cardBgColor: Color,
    primaryTextColor: Color,
    cardBorderColor: Color,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = cardBgColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = primaryTextColor
            )
            Spacer(modifier = Modifier.height(10.dp))
            content()
        }
    }
}

@Composable
fun SettingToggleRow(
    title: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    accentTint: Color,
    primaryTextColor: Color,
    secondaryTextColor: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!checked) }
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = primaryTextColor
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = description,
                fontSize = 11.sp,
                color = secondaryTextColor
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = accentTint
            )
        )
    }
}

@Composable
fun ReorderableToolGrid(
    toolIds: List<String>,
    isTopBox: Boolean,
    badgeIcon: ImageVector,
    badgeColor: Color,
    badgeContentDesc: String,
    onBadgeClick: (String) -> Unit,
    onReorder: (fromIndex: Int, toIndex: Int) -> Unit,
    cardBgColor: Color,
    primaryTextColor: Color,
    accentTint: Color,
    onCardClick: ((String) -> Unit)? = null,
    enableDragReorder: Boolean = true,
    tagPrefix: String = if (isTopBox) "active_tool" else "available_tool"
) {
    var draggedToolId by remember { mutableStateOf<String?>(null) }
    var dragOffset by remember { mutableStateOf(Offset.Zero) }
    var cellWidthPx by remember { mutableFloatStateOf(0f) }
    var cellHeightPx by remember { mutableFloatStateOf(0f) }

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        val rows = toolIds.chunked(3)
        rows.forEachIndexed { rowIndex, rowIds ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                rowIds.forEachIndexed { colIndex, toolId ->
                    val tool = com.example.model.BrowserToolCatalog.getTool(toolId)
                    val isBeingDragged = draggedToolId == toolId

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .onGloballyPositioned { coordinates ->
                                if (coordinates.size.width > 0) {
                                    cellWidthPx = coordinates.size.width.toFloat()
                                }
                                if (coordinates.size.height > 0) {
                                    cellHeightPx = coordinates.size.height.toFloat()
                                }
                            }
                            .zIndex(if (isBeingDragged) 10f else 1f)
                            .graphicsLayer {
                                if (isBeingDragged) {
                                    translationX = dragOffset.x
                                    translationY = dragOffset.y
                                    scaleX = 1.08f
                                    scaleY = 1.08f
                                    shadowElevation = 12f
                                } else {
                                    translationX = 0f
                                    translationY = 0f
                                    scaleX = 1f
                                    scaleY = 1f
                                    shadowElevation = 0f
                                }
                            }
                            .then(
                                if (enableDragReorder) {
                                    Modifier.pointerInput(toolIds) {
                                        detectDragGesturesAfterLongPress(
                                            onDragStart = {
                                                draggedToolId = toolId
                                                dragOffset = Offset.Zero
                                            },
                                            onDrag = { change, dragAmount ->
                                                change.consume()
                                                dragOffset += dragAmount

                                                if (cellWidthPx > 0f && cellHeightPx > 0f) {
                                                    val currentIndex = toolIds.indexOf(draggedToolId)
                                                    if (currentIndex != -1) {
                                                        val curRow = currentIndex / 3
                                                        val curCol = currentIndex % 3

                                                        val colDelta = (dragOffset.x / cellWidthPx).roundToInt()
                                                        val rowDelta = (dragOffset.y / cellHeightPx).roundToInt()

                                                        if (colDelta != 0 || rowDelta != 0) {
                                                            val totalRows = (toolIds.size + 2) / 3
                                                            val targetRow = (curRow + rowDelta).coerceIn(0, totalRows - 1)
                                                            val targetCol = (curCol + colDelta).coerceIn(0, 2)
                                                            val targetIndex = (targetRow * 3 + targetCol).coerceIn(0, toolIds.size - 1)

                                                            if (targetIndex != currentIndex) {
                                                                val actualColDelta = (targetIndex % 3) - curCol
                                                                val actualRowDelta = (targetIndex / 3) - curRow

                                                                onReorder(currentIndex, targetIndex)

                                                                dragOffset = Offset(
                                                                    dragOffset.x - actualColDelta * cellWidthPx,
                                                                    dragOffset.y - actualRowDelta * cellHeightPx
                                                                )
                                                            }
                                                        }
                                                    }
                                                }
                                            },
                                            onDragEnd = {
                                                draggedToolId = null
                                                dragOffset = Offset.Zero
                                            },
                                            onDragCancel = {
                                                draggedToolId = null
                                                dragOffset = Offset.Zero
                                            }
                                        )
                                    }
                                } else Modifier
                            )
                    ) {
                        ToolBoxItemCard(
                            tool = tool,
                            isTopBox = isTopBox,
                            badgeIcon = badgeIcon,
                            badgeColor = badgeColor,
                            badgeContentDesc = badgeContentDesc,
                            onBadgeClick = { onBadgeClick(toolId) },
                            onCardClick = { onCardClick?.invoke(toolId) },
                            isBeingDragged = isBeingDragged,
                            cardBgColor = cardBgColor,
                            textColor = primaryTextColor,
                            accentTint = accentTint,
                            tagPrefix = tagPrefix,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                repeat((3 - rowIds.size).coerceAtLeast(0)) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
fun ToolBoxItemCard(
    tool: com.example.model.BrowserTool,
    isTopBox: Boolean,
    badgeIcon: ImageVector,
    badgeColor: Color,
    badgeContentDesc: String,
    onBadgeClick: () -> Unit,
    onCardClick: () -> Unit,
    isBeingDragged: Boolean,
    cardBgColor: Color,
    textColor: Color,
    accentTint: Color,
    tagPrefix: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .testTag("${tagPrefix}_${tool.id}")
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .border(
                    width = if (isBeingDragged) 2.5.dp else 1.dp,
                    color = if (isBeingDragged) Color(0xFF43A047) else accentTint.copy(alpha = 0.3f),
                    shape = RoundedCornerShape(16.dp)
                )
                .clickable(onClick = onCardClick),
            color = if (isBeingDragged) accentTint.copy(alpha = 0.25f) else cardBgColor,
            shape = RoundedCornerShape(16.dp),
            tonalElevation = if (isBeingDragged) 6.dp else 1.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 6.dp, vertical = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Tool icon
                Icon(
                    imageVector = tool.icon,
                    contentDescription = null,
                    tint = accentTint,
                    modifier = Modifier.size(26.dp)
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Tool label (no arrows, no move text)
                Text(
                    text = tool.label,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = textColor,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Center
                )
            }
        }

        // Top right corner badge: minus for top box, plus for below box
        Surface(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(4.dp)
                .size(24.dp)
                .clip(CircleShape)
                .clickable(onClick = onBadgeClick)
                .testTag(if (isTopBox) "remove_badge_${tool.id}" else "add_badge_${tool.id}"),
            color = badgeColor,
            shape = CircleShape,
            shadowElevation = 3.dp
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = badgeIcon,
                    contentDescription = badgeContentDesc,
                    tint = Color.White,
                    modifier = Modifier.size(15.dp)
                )
            }
        }
    }
}
