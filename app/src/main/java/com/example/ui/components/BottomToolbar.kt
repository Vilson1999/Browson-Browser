package com.example.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Brightness4
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.LocalAppTheme

@Composable
fun BottomToolbar(
    canGoBack: Boolean,
    canGoForward: Boolean,
    tabsCount: Int,
    onBackClick: () -> Unit,
    onForwardClick: () -> Unit,
    onHomeClick: () -> Unit,
    onTabsClick: () -> Unit,
    onMenuClick: () -> Unit,
    toolbarToolIds: List<String> = com.example.model.BrowserToolCatalog.DEFAULT_ACTIVE_TOOLBAR_IDS,
    onHistoryClick: () -> Unit = {},
    onDownloadsClick: () -> Unit = {},
    onBookmarksClick: () -> Unit = {},
    onNewTabClick: () -> Unit = {},
    onShareClick: () -> Unit = {},
    onDarkModeClick: () -> Unit = {},
    onOtherToolClick: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val appTheme = LocalAppTheme.current

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = if (appTheme.isAmoled) 0.dp else 8.dp,
                shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
            )
            .border(
                width = 1.dp,
                color = appTheme.toolbarBorder,
                shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
            ),
        color = appTheme.toolbarBg,
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .height(60.dp)
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val iconColor = appTheme.onSurface
            val iconDisabledColor = appTheme.onSurfaceVariant.copy(alpha = 0.35f)
            val btnContainerBg = when {
                appTheme.isAmoled -> Color(0xFF0F0F12)
                appTheme.isDark -> Color(0xFF1E2837)
                else -> Color(0xFFF1F5F9)
            }

            toolbarToolIds.forEach { toolId ->
                when (toolId) {
                    "nav_back" -> {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (canGoBack) btnContainerBg else Color.Transparent,
                            modifier = Modifier.size(40.dp)
                        ) {
                            IconButton(
                                onClick = onBackClick,
                                enabled = canGoBack,
                                modifier = Modifier
                                    .size(40.dp)
                                    .testTag("toolbar_btn_back")
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = stringResource(R.string.nav_back),
                                    tint = if (canGoBack) iconColor else iconDisabledColor,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }
                    }

                    "nav_forward" -> {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (canGoForward) btnContainerBg else Color.Transparent,
                            modifier = Modifier.size(40.dp)
                        ) {
                            IconButton(
                                onClick = onForwardClick,
                                enabled = canGoForward,
                                modifier = Modifier
                                    .size(40.dp)
                                    .testTag("toolbar_btn_forward")
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                    contentDescription = stringResource(R.string.nav_forward),
                                    tint = if (canGoForward) iconColor else iconDisabledColor,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }
                    }

                    "nav_home" -> {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = btnContainerBg,
                            modifier = Modifier.size(40.dp)
                        ) {
                            IconButton(
                                onClick = onHomeClick,
                                modifier = Modifier
                                    .size(40.dp)
                                    .testTag("toolbar_btn_home")
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Home,
                                    contentDescription = stringResource(R.string.nav_home),
                                    tint = appTheme.primary,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }
                    }

                    "nav_tabs" -> {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = btnContainerBg,
                            modifier = Modifier.size(40.dp)
                        ) {
                            IconButton(
                                onClick = onTabsClick,
                                modifier = Modifier
                                    .size(40.dp)
                                    .testTag("toolbar_btn_tabs")
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .clip(RoundedCornerShape(6.dp))
                                        .border(
                                            width = 1.8.dp,
                                            color = iconColor,
                                            shape = RoundedCornerShape(6.dp)
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = tabsCount.toString(),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = iconColor
                                    )
                                }
                            }
                        }
                    }

                    "nav_menu" -> {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = btnContainerBg,
                            modifier = Modifier.size(40.dp)
                        ) {
                            IconButton(
                                onClick = onMenuClick,
                                modifier = Modifier
                                    .size(40.dp)
                                    .testTag("toolbar_btn_me")
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Person,
                                    contentDescription = stringResource(R.string.nav_me),
                                    tint = iconColor,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }
                    }

                    "history" -> {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = btnContainerBg,
                            modifier = Modifier.size(40.dp)
                        ) {
                            IconButton(
                                onClick = onHistoryClick,
                                modifier = Modifier
                                    .size(40.dp)
                                    .testTag("toolbar_btn_history")
                            ) {
                                val tool = com.example.model.BrowserToolCatalog.getTool("history")
                                Icon(imageVector = tool.icon, contentDescription = tool.label, tint = iconColor, modifier = Modifier.size(22.dp))
                            }
                        }
                    }

                    "downloads" -> {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = btnContainerBg,
                            modifier = Modifier.size(40.dp)
                        ) {
                            IconButton(
                                onClick = onDownloadsClick,
                                modifier = Modifier
                                    .size(40.dp)
                                    .testTag("toolbar_btn_downloads")
                            ) {
                                val tool = com.example.model.BrowserToolCatalog.getTool("downloads")
                                Icon(imageVector = tool.icon, contentDescription = tool.label, tint = iconColor, modifier = Modifier.size(22.dp))
                            }
                        }
                    }

                    "bookmarks" -> {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = btnContainerBg,
                            modifier = Modifier.size(40.dp)
                        ) {
                            IconButton(
                                onClick = onBookmarksClick,
                                modifier = Modifier
                                    .size(40.dp)
                                    .testTag("toolbar_btn_bookmarks")
                            ) {
                                val tool = com.example.model.BrowserToolCatalog.getTool("bookmarks")
                                Icon(imageVector = tool.icon, contentDescription = tool.label, tint = iconColor, modifier = Modifier.size(22.dp))
                            }
                        }
                    }

                    "new_tab" -> {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = btnContainerBg,
                            modifier = Modifier.size(40.dp)
                        ) {
                            IconButton(
                                onClick = onNewTabClick,
                                modifier = Modifier
                                    .size(40.dp)
                                    .testTag("toolbar_btn_new_tab")
                            ) {
                                val tool = com.example.model.BrowserToolCatalog.getTool("new_tab")
                                Icon(imageVector = tool.icon, contentDescription = tool.label, tint = iconColor, modifier = Modifier.size(22.dp))
                            }
                        }
                    }

                    "share" -> {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = btnContainerBg,
                            modifier = Modifier.size(40.dp)
                        ) {
                            IconButton(
                                onClick = onShareClick,
                                modifier = Modifier
                                    .size(40.dp)
                                    .testTag("toolbar_btn_share")
                            ) {
                                val tool = com.example.model.BrowserToolCatalog.getTool("share")
                                Icon(imageVector = tool.icon, contentDescription = tool.label, tint = iconColor, modifier = Modifier.size(22.dp))
                            }
                        }
                    }

                    "dark_mode" -> {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = btnContainerBg,
                            modifier = Modifier.size(40.dp)
                        ) {
                            IconButton(
                                onClick = onDarkModeClick,
                                modifier = Modifier
                                    .size(40.dp)
                                    .testTag("toolbar_btn_dark_mode")
                            ) {
                                val modeIcon = when {
                                    appTheme.isAmoled -> Icons.Filled.Brightness4
                                    appTheme.isDark -> Icons.Filled.DarkMode
                                    else -> Icons.Filled.LightMode
                                }
                                val iconTint = when {
                                    appTheme.isAmoled -> Color(0xFF67E8F9)
                                    appTheme.isDark -> Color(0xFF90CAF9)
                                    else -> Color(0xFFF59E0B)
                                }
                                Icon(
                                    imageVector = modeIcon,
                                    contentDescription = "Theme Switcher",
                                    tint = iconTint,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }
                    }

                    else -> {
                        val tool = com.example.model.BrowserToolCatalog.getTool(toolId)
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = btnContainerBg,
                            modifier = Modifier.size(40.dp)
                        ) {
                            IconButton(
                                onClick = { onOtherToolClick(toolId) },
                                modifier = Modifier
                                    .size(40.dp)
                                    .testTag("toolbar_btn_$toolId")
                            ) {
                                Icon(imageVector = tool.icon, contentDescription = tool.label, tint = iconColor, modifier = Modifier.size(22.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}
