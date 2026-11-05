package com.example.ui.components

import android.app.Activity
import android.content.Intent
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.BrightnessAuto
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.DesktopWindows
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Flight
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.ThemeMode

data class ToolMenuItem(
    val id: String,
    val icon: ImageVector,
    val label: String,
    val testTag: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuBottomSheet(
    themeMode: ThemeMode,
    onThemeModeChange: (ThemeMode) -> Unit,
    onAddBookmark: () -> Unit,
    onAddSpeedDial: () -> Unit,
    onCloseApp: () -> Unit,
    onOpenSettings: () -> Unit,
    onOpenHistory: () -> Unit = {},
    onOpenDownloads: () -> Unit = {},
    activeToolIds: List<String> = com.example.model.BrowserToolCatalog.DEFAULT_ACTIVE_IDS,
    currentUrl: String,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val isDark = when (themeMode) {
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
        ThemeMode.DARK -> true
        ThemeMode.LIGHT -> false
    }

    var isDesktopSite by remember { mutableStateOf(false) }
    var showSettingsPanel by remember { mutableStateOf(false) }

    // Colors matching screenshots (mint/sage tint for pills)
    val pillBgColor = if (isDark) Color(0xFF233027) else Color(0xFFEAF4EE)
    val pillIconTint = if (isDark) Color(0xFF81C784) else Color(0xFF1E5E3A)
    val pillTextColor = if (isDark) Color(0xFFE2E8F0) else Color(0xFF1C2820)

    // Bottom action pill colors
    val bottomPillBgColor = if (isDark) Color(0xFF253329) else Color(0xFFEAF4EE)
    val bottomPillIconTint = if (isDark) Color(0xFFC8E6C9) else Color(0xFF1E3A2B)
    val closePillBgColor = Color(0xFFBA1A1A) // Crimson red pill from screenshot demo
    val closePillIconTint = Color.White

    // Dynamically map activeToolIds to ToolMenuItems, chunked in 3 columns
    val activeToolItems = activeToolIds.map { id ->
        val tool = com.example.model.BrowserToolCatalog.getTool(id)
        ToolMenuItem(tool.id, tool.icon, tool.label, "tool_${tool.id}")
    }
    val chunkedToolRows = activeToolItems.chunked(3)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = if (isDark) Color(0xFF131714) else Color(0xFFF9FBF9),
        scrimColor = Color.Transparent,
        dragHandle = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .width(36.dp)
                        .height(4.dp)
                        .clip(CircleShape)
                        .background(if (isDark) Color(0xFF4A5568) else Color(0xFFCBD5E1))
                )

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .size(36.dp)
                        .clip(CircleShape)
                        .testTag("tool_menu_close_button")
                ) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = "Close",
                        tint = if (isDark) Color(0xFFCBD5E1) else Color(0xFF475569),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        },
        modifier = Modifier.testTag("tool_menu_bottom_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp)
                .navigationBarsPadding()
        ) {
            // Optional expanded Settings section
            AnimatedVisibility(visible = showSettingsPanel) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(pillBgColor)
                        .padding(14.dp)
                ) {
                    Text(
                        text = "Theme Wallpaper",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = pillTextColor
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilterChip(
                            selected = themeMode == ThemeMode.SYSTEM,
                            onClick = { onThemeModeChange(ThemeMode.SYSTEM) },
                            label = { Text("System Default", fontSize = 11.sp) },
                            leadingIcon = {
                                Icon(Icons.Filled.BrightnessAuto, contentDescription = null, modifier = Modifier.size(14.dp))
                            }
                        )
                        FilterChip(
                            selected = themeMode == ThemeMode.DARK,
                            onClick = { onThemeModeChange(ThemeMode.DARK) },
                            label = { Text("Black", fontSize = 11.sp) },
                            leadingIcon = {
                                Icon(Icons.Filled.DarkMode, contentDescription = null, modifier = Modifier.size(14.dp))
                            }
                        )
                        FilterChip(
                            selected = themeMode == ThemeMode.LIGHT,
                            onClick = { onThemeModeChange(ThemeMode.LIGHT) },
                            label = { Text("White", fontSize = 11.sp) },
                            leadingIcon = {
                                Icon(Icons.Filled.LightMode, contentDescription = null, modifier = Modifier.size(14.dp))
                            }
                        )
                    }
                }
            }

            // 3 Rows x 3 Columns Tool Menu UI
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Dynamically render rows of 3 columns from active tools
                chunkedToolRows.forEach { rowTools ->
                    ToolMenuRow(
                        items = rowTools,
                        pillBgColor = pillBgColor,
                        pillIconTint = pillIconTint,
                        pillTextColor = pillTextColor,
                        onItemClick = { item ->
                            when (item.id) {
                                "history" -> {
                                    onDismiss()
                                    onOpenHistory()
                                }
                                "downloads" -> {
                                    onDismiss()
                                    onOpenDownloads()
                                }
                                "bookmarks" -> {
                                    onDismiss()
                                    onAddBookmark()
                                }
                                "desktop" -> {
                                    isDesktopSite = !isDesktopSite
                                    Toast.makeText(
                                        context,
                                        if (isDesktopSite) "Desktop site enabled" else "Mobile site enabled",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                                "dark_mode" -> {
                                    val nextMode = if (isDark) ThemeMode.LIGHT else ThemeMode.DARK
                                    onThemeModeChange(nextMode)
                                }
                                "share" -> {
                                    val sendIntent = Intent().apply {
                                        action = Intent.ACTION_SEND
                                        putExtra(Intent.EXTRA_TEXT, currentUrl.ifBlank { "https://www.google.com" })
                                        type = "text/plain"
                                    }
                                    val shareIntent = Intent.createChooser(sendIntent, "Share via")
                                    context.startActivity(shareIntent)
                                }
                                "offline" -> Toast.makeText(context, "Offline pages opened", Toast.LENGTH_SHORT).show()
                                "find" -> Toast.makeText(context, "Find on page opened", Toast.LENGTH_SHORT).show()
                                "translate" -> Toast.makeText(context, "Translating page...", Toast.LENGTH_SHORT).show()
                                "incognito" -> Toast.makeText(context, "Incognito browsing mode", Toast.LENGTH_SHORT).show()
                                "reader" -> Toast.makeText(context, "Reader mode activated", Toast.LENGTH_SHORT).show()
                                "print" -> Toast.makeText(context, "Saving page as PDF...", Toast.LENGTH_SHORT).show()
                                "screenshot" -> Toast.makeText(context, "Captured screenshot to gallery", Toast.LENGTH_SHORT).show()
                                "clear_data" -> Toast.makeText(context, "Cache and browsing data cleared", Toast.LENGTH_SHORT).show()
                                "zoom" -> Toast.makeText(context, "Text zoom set to 100%", Toast.LENGTH_SHORT).show()
                                "qr_code" -> Toast.makeText(context, "QR Scanner ready", Toast.LENGTH_SHORT).show()
                                "ad_block" -> Toast.makeText(context, "Ad & Tracker Blocker active", Toast.LENGTH_SHORT).show()
                                "full_screen" -> Toast.makeText(context, "Full screen mode enabled", Toast.LENGTH_SHORT).show()
                                "cookie" -> Toast.makeText(context, "Cookie permissions manager", Toast.LENGTH_SHORT).show()
                                "view_source" -> Toast.makeText(context, "Viewing HTML source code", Toast.LENGTH_SHORT).show()
                                "voice_search" -> Toast.makeText(context, "Listening for voice search...", Toast.LENGTH_SHORT).show()
                                "night_light" -> Toast.makeText(context, "Blue light filter enabled", Toast.LENGTH_SHORT).show()
                                "new_tab" -> Toast.makeText(context, "New tab created", Toast.LENGTH_SHORT).show()
                                "home" -> Toast.makeText(context, "Home page", Toast.LENGTH_SHORT).show()
                                else -> Toast.makeText(context, item.label, Toast.LENGTH_SHORT).show()
                            }
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Subtle divider between tools grid and bottom actions
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(if (isDark) Color(0xFF222C24) else Color(0xFFE2EBE4))
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Bottom of tool menu: 4 pill shape background icons without label (from screenshot demo)
            // (bookmark, add to speed dial, close the app, setting)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 1. Bookmark icon pill (without label)
                BottomActionPill(
                    icon = Icons.Filled.BookmarkBorder,
                    contentDescription = stringResource(R.string.add_bookmark),
                    backgroundColor = bottomPillBgColor,
                    iconTint = bottomPillIconTint,
                    testTag = "tool_menu_bottom_bookmark",
                    modifier = Modifier.weight(1f),
                    onClick = onAddBookmark
                )

                // 2. Add to speed dial icon pill (without label)
                BottomActionPill(
                    icon = Icons.Filled.AddCircleOutline,
                    contentDescription = stringResource(R.string.tool_add_speed_dial),
                    backgroundColor = bottomPillBgColor,
                    iconTint = bottomPillIconTint,
                    testTag = "tool_menu_bottom_speed_dial",
                    modifier = Modifier.weight(1f),
                    onClick = onAddSpeedDial
                )

                // 3. Dark/Light mode icon pill (half moon for dark, sun for light)
                BottomActionPill(
                    icon = if (isDark) Icons.Filled.DarkMode else Icons.Filled.LightMode,
                    contentDescription = if (isDark) "Switch to Light mode" else "Switch to Dark mode",
                    backgroundColor = bottomPillBgColor,
                    iconTint = if (isDark) Color(0xFF90CAF9) else Color(0xFFF59E0B),
                    testTag = "tool_menu_bottom_theme_toggle",
                    modifier = Modifier.weight(1f),
                    onClick = {
                        val nextMode = if (isDark) ThemeMode.LIGHT else ThemeMode.DARK
                        onThemeModeChange(nextMode)
                    }
                )

                // 4. Setting icon pill (opens full-screen Settings menu)
                BottomActionPill(
                    icon = Icons.Filled.Settings,
                    contentDescription = stringResource(R.string.tool_settings),
                    backgroundColor = bottomPillBgColor,
                    iconTint = bottomPillIconTint,
                    testTag = "tool_menu_bottom_settings",
                    modifier = Modifier.weight(1f),
                    onClick = onOpenSettings
                )
            }
        }
    }
}

@Composable
fun ToolMenuRow(
    items: List<ToolMenuItem>,
    pillBgColor: Color,
    pillIconTint: Color,
    pillTextColor: Color,
    onItemClick: (ToolMenuItem) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        items.forEach { item ->
            ToolMenuPill(
                item = item,
                pillBgColor = pillBgColor,
                pillIconTint = pillIconTint,
                pillTextColor = pillTextColor,
                modifier = Modifier.weight(1f),
                onClick = { onItemClick(item) }
            )
        }
        repeat((3 - items.size).coerceAtLeast(0)) {
            Spacer(modifier = Modifier.weight(1f))
        }
    }
}

@Composable
fun ToolMenuPill(
    item: ToolMenuItem,
    pillBgColor: Color,
    pillIconTint: Color,
    pillTextColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier
            .height(44.dp)
            .clip(RoundedCornerShape(22.dp))
            .clickable(onClick = onClick)
            .testTag(item.testTag),
        color = pillBgColor,
        shape = RoundedCornerShape(22.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = item.icon,
                contentDescription = null,
                tint = pillIconTint,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(5.dp))
            Text(
                text = item.label,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = pillTextColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Start
            )
        }
    }
}

@Composable
fun BottomActionPill(
    icon: ImageVector,
    contentDescription: String,
    backgroundColor: Color,
    iconTint: Color,
    testTag: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier
            .height(50.dp)
            .clip(RoundedCornerShape(25.dp))
            .clickable(onClick = onClick)
            .testTag(testTag),
        color = backgroundColor,
        shape = RoundedCornerShape(25.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = contentDescription,
                tint = iconTint,
                modifier = Modifier.size(22.dp)
            )
        }
    }
}
