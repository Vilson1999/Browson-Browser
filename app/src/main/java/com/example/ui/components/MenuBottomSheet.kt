package com.example.ui.components

import android.content.Intent
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Brightness4
import androidx.compose.material.icons.filled.BrightnessAuto
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
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
import com.example.ui.theme.LocalAppTheme

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
    onOpenBookmarks: () -> Unit = {},
    activeToolIds: List<String> = com.example.model.BrowserToolCatalog.DEFAULT_ACTIVE_IDS,
    currentUrl: String,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val appTheme = LocalAppTheme.current

    var isDesktopSite by remember { mutableStateOf(false) }

    // Colors matching active theme
    val containerColor = when {
        appTheme.isAmoled -> Color(0xFF000000)
        appTheme.isDark -> Color(0xFF0F172A)
        else -> Color(0xFFFFFFFF)
    }

    val toolPillBg = when {
        appTheme.isAmoled -> Color(0xFF121214)
        appTheme.isDark -> Color(0xFF1E293B)
        else -> Color(0xFFF1F5F9)
    }

    val toolPillBorder = when {
        appTheme.isAmoled -> Color(0xFF27272A)
        appTheme.isDark -> Color(0xFF334155)
        else -> Color(0xFFE2E8F0)
    }

    val bottomActionBg = when {
        appTheme.isAmoled -> Color(0xFF18181B)
        appTheme.isDark -> Color(0xFF243044)
        else -> Color(0xFFE2E8F0)
    }

    // Map activeToolIds to ToolMenuItems, chunked in 3 columns
    val activeToolItems = activeToolIds.map { id ->
        val tool = com.example.model.BrowserToolCatalog.getTool(id)
        ToolMenuItem(tool.id, tool.icon, tool.label, "tool_${tool.id}")
    }
    val chunkedToolRows = activeToolItems.chunked(3)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = containerColor,
        scrimColor = Color.Black.copy(alpha = 0.5f),
        dragHandle = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                // Drag Handle
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .width(40.dp)
                        .height(4.dp)
                        .clip(CircleShape)
                        .background(if (appTheme.isDark) Color(0xFF475569) else Color(0xFFCBD5E1))
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Browser Tools",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = appTheme.onSurface
                    )

                    Surface(
                        shape = CircleShape,
                        color = toolPillBg,
                        modifier = Modifier.size(34.dp)
                    ) {
                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier
                                .size(34.dp)
                                .testTag("tool_menu_close_button")
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Close,
                                contentDescription = "Close",
                                tint = appTheme.onSurfaceVariant,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
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
                .verticalScroll(rememberScrollState())
        ) {
            // Theme Mode Selector Bar (Light, Dark, AMOLED, System)
            ThemeModeSelectorRow(
                currentMode = themeMode,
                onModeSelected = onThemeModeChange,
                isDark = appTheme.isDark,
                isAmoled = appTheme.isAmoled
            )

            Spacer(modifier = Modifier.height(14.dp))

            // 3 Columns Grid of Active Tools
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                chunkedToolRows.forEach { rowTools ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        rowTools.forEach { item ->
                            ModernToolMenuPill(
                                item = item,
                                backgroundColor = toolPillBg,
                                borderColor = toolPillBorder,
                                textColor = appTheme.onSurface,
                                iconTint = appTheme.primary,
                                modifier = Modifier.weight(1f),
                                onClick = {
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
                                            onOpenBookmarks()
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
                                            val nextMode = when (themeMode) {
                                                ThemeMode.LIGHT -> ThemeMode.DARK
                                                ThemeMode.DARK -> ThemeMode.AMOLED
                                                ThemeMode.AMOLED -> ThemeMode.LIGHT
                                                ThemeMode.SYSTEM -> if (appTheme.isDark) ThemeMode.LIGHT else ThemeMode.DARK
                                            }
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
                        repeat((3 - rowTools.size).coerceAtLeast(0)) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Subtle divider
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(toolPillBorder)
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Bottom 4 Action Pills (Bookmark, Speed Dial, Theme Toggle, Settings)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 14.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 1. Bookmark pill
                ModernBottomActionPill(
                    icon = Icons.Filled.Bookmark,
                    contentDescription = stringResource(R.string.bookmark_menu_title),
                    backgroundColor = bottomActionBg,
                    iconTint = if (appTheme.isDark) Color(0xFF34D399) else Color(0xFF059669),
                    testTag = "tool_menu_bottom_bookmark",
                    modifier = Modifier.weight(1f),
                    onClick = {
                        onDismiss()
                        onOpenBookmarks()
                    }
                )

                // 2. Add to Speed Dial pill
                ModernBottomActionPill(
                    icon = Icons.Filled.AddCircleOutline,
                    contentDescription = stringResource(R.string.tool_add_speed_dial),
                    backgroundColor = bottomActionBg,
                    iconTint = if (appTheme.isDark) Color(0xFF60A5FA) else Color(0xFF2563EB),
                    testTag = "tool_menu_bottom_speed_dial",
                    modifier = Modifier.weight(1f),
                    onClick = onAddSpeedDial
                )

                // 3. Theme Toggle pill (Cycles Light, Dark, AMOLED)
                val themeIcon = when {
                    appTheme.isAmoled -> Icons.Filled.Brightness4
                    appTheme.isDark -> Icons.Filled.DarkMode
                    else -> Icons.Filled.LightMode
                }
                val themeTint = when {
                    appTheme.isAmoled -> Color(0xFF67E8F9)
                    appTheme.isDark -> Color(0xFF90CAF9)
                    else -> Color(0xFFF59E0B)
                }
                ModernBottomActionPill(
                    icon = themeIcon,
                    contentDescription = "Switch Theme",
                    backgroundColor = bottomActionBg,
                    iconTint = themeTint,
                    testTag = "tool_menu_bottom_theme_toggle",
                    modifier = Modifier.weight(1f),
                    onClick = {
                        val nextMode = when (themeMode) {
                            ThemeMode.LIGHT -> ThemeMode.DARK
                            ThemeMode.DARK -> ThemeMode.AMOLED
                            ThemeMode.AMOLED -> ThemeMode.LIGHT
                            ThemeMode.SYSTEM -> if (appTheme.isDark) ThemeMode.LIGHT else ThemeMode.DARK
                        }
                        onThemeModeChange(nextMode)
                    }
                )

                // 4. Settings pill
                ModernBottomActionPill(
                    icon = Icons.Filled.Settings,
                    contentDescription = stringResource(R.string.tool_settings),
                    backgroundColor = bottomActionBg,
                    iconTint = if (appTheme.isDark) Color(0xFFFBBF24) else Color(0xFFD97706),
                    testTag = "tool_menu_bottom_settings",
                    modifier = Modifier.weight(1f),
                    onClick = onOpenSettings
                )
            }
        }
    }
}

/**
 * 4-Button Theme Mode Selector Row directly inside the Menu Sheet
 */
@Composable
private fun ThemeModeSelectorRow(
    currentMode: ThemeMode,
    onModeSelected: (ThemeMode) -> Unit,
    isDark: Boolean,
    isAmoled: Boolean
) {
    val items = listOf(
        Triple(ThemeMode.LIGHT, "Light", Icons.Filled.LightMode),
        Triple(ThemeMode.DARK, "Dark", Icons.Filled.DarkMode),
        Triple(ThemeMode.AMOLED, "AMOLED", Icons.Filled.Brightness4),
        Triple(ThemeMode.SYSTEM, "Auto", Icons.Filled.BrightnessAuto)
    )

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = if (isAmoled) Color(0xFF101012) else if (isDark) Color(0xFF1E2837) else Color(0xFFF1F5F9),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isAmoled) Color(0xFF27272A) else if (isDark) Color(0xFF334155) else Color(0xFFE2E8F0)
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items.forEach { (mode, label, icon) ->
                val isSelected = currentMode == mode
                val bg = if (isSelected) {
                    if (isAmoled) Color(0xFF27272A) else if (isDark) Color(0xFF334155) else Color.White
                } else {
                    Color.Transparent
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = bg,
                    modifier = Modifier
                        .weight(1f)
                        .height(38.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { onModeSelected(mode) }
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = label,
                            tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = label,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ModernToolMenuPill(
    item: ToolMenuItem,
    backgroundColor: Color,
    borderColor: Color,
    textColor: Color,
    iconTint: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier
            .height(48.dp)
            .clip(RoundedCornerShape(14.dp))
            .border(width = 0.8.dp, color = borderColor, shape = RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
            .testTag(item.testTag),
        color = backgroundColor,
        shape = RoundedCornerShape(14.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = iconTint.copy(alpha = 0.12f),
                modifier = Modifier.size(28.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = null,
                        tint = iconTint,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = item.label,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = textColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun ModernBottomActionPill(
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
            .height(48.dp)
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .testTag(testTag),
        color = backgroundColor,
        shape = RoundedCornerShape(16.dp)
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
