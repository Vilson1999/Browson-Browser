package com.example.ui.components

import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.Home
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R

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
    // Surface background matches address bar color (surfaceVariant) and extends through the navigation bar
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surfaceVariant,
        tonalElevation = 6.dp,
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .height(56.dp)
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val iconColor = MaterialTheme.colorScheme.onSurfaceVariant
            val iconDisabledColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.38f)

            toolbarToolIds.forEach { toolId ->
                when (toolId) {
                    "nav_back" -> {
                        IconButton(
                            onClick = onBackClick,
                            enabled = true,
                            modifier = Modifier
                                .minimumInteractiveComponentSize()
                                .testTag("toolbar_btn_back")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = stringResource(R.string.nav_back),
                                tint = if (canGoBack) iconColor else iconDisabledColor,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                    "nav_forward" -> {
                        IconButton(
                            onClick = onForwardClick,
                            enabled = true,
                            modifier = Modifier
                                .minimumInteractiveComponentSize()
                                .testTag("toolbar_btn_forward")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = stringResource(R.string.nav_forward),
                                tint = if (canGoForward) iconColor else iconDisabledColor,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                    "nav_home" -> {
                        IconButton(
                            onClick = onHomeClick,
                            modifier = Modifier
                                .minimumInteractiveComponentSize()
                                .testTag("toolbar_btn_home")
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Home,
                                contentDescription = stringResource(R.string.nav_home),
                                tint = iconColor,
                                modifier = Modifier.size(26.dp)
                            )
                        }
                    }
                    "nav_tabs" -> {
                        IconButton(
                            onClick = onTabsClick,
                            modifier = Modifier
                                .minimumInteractiveComponentSize()
                                .testTag("toolbar_btn_tabs")
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(RoundedCornerShape(6.dp))
                                    .border(
                                        width = 2.dp,
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
                    "nav_menu" -> {
                        IconButton(
                            onClick = onMenuClick,
                            modifier = Modifier
                                .minimumInteractiveComponentSize()
                                .testTag("toolbar_btn_me")
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Person,
                                contentDescription = stringResource(R.string.nav_me),
                                tint = iconColor,
                                modifier = Modifier.size(26.dp)
                            )
                        }
                    }
                    "history" -> {
                        IconButton(
                            onClick = onHistoryClick,
                            modifier = Modifier
                                .minimumInteractiveComponentSize()
                                .testTag("toolbar_btn_history")
                        ) {
                            val tool = com.example.model.BrowserToolCatalog.getTool("history")
                            Icon(imageVector = tool.icon, contentDescription = tool.label, tint = iconColor, modifier = Modifier.size(24.dp))
                        }
                    }
                    "downloads" -> {
                        IconButton(
                            onClick = onDownloadsClick,
                            modifier = Modifier
                                .minimumInteractiveComponentSize()
                                .testTag("toolbar_btn_downloads")
                        ) {
                            val tool = com.example.model.BrowserToolCatalog.getTool("downloads")
                            Icon(imageVector = tool.icon, contentDescription = tool.label, tint = iconColor, modifier = Modifier.size(24.dp))
                        }
                    }
                    "bookmarks" -> {
                        IconButton(
                            onClick = onBookmarksClick,
                            modifier = Modifier
                                .minimumInteractiveComponentSize()
                                .testTag("toolbar_btn_bookmarks")
                        ) {
                            val tool = com.example.model.BrowserToolCatalog.getTool("bookmarks")
                            Icon(imageVector = tool.icon, contentDescription = tool.label, tint = iconColor, modifier = Modifier.size(24.dp))
                        }
                    }
                    "new_tab" -> {
                        IconButton(
                            onClick = onNewTabClick,
                            modifier = Modifier
                                .minimumInteractiveComponentSize()
                                .testTag("toolbar_btn_new_tab")
                        ) {
                            val tool = com.example.model.BrowserToolCatalog.getTool("new_tab")
                            Icon(imageVector = tool.icon, contentDescription = tool.label, tint = iconColor, modifier = Modifier.size(24.dp))
                        }
                    }
                    "share" -> {
                        IconButton(
                            onClick = onShareClick,
                            modifier = Modifier
                                .minimumInteractiveComponentSize()
                                .testTag("toolbar_btn_share")
                        ) {
                            val tool = com.example.model.BrowserToolCatalog.getTool("share")
                            Icon(imageVector = tool.icon, contentDescription = tool.label, tint = iconColor, modifier = Modifier.size(24.dp))
                        }
                    }
                    "dark_mode" -> {
                        IconButton(
                            onClick = onDarkModeClick,
                            modifier = Modifier
                                .minimumInteractiveComponentSize()
                                .testTag("toolbar_btn_dark_mode")
                        ) {
                            val tool = com.example.model.BrowserToolCatalog.getTool("dark_mode")
                            Icon(imageVector = tool.icon, contentDescription = tool.label, tint = iconColor, modifier = Modifier.size(24.dp))
                        }
                    }
                    else -> {
                        val tool = com.example.model.BrowserToolCatalog.getTool(toolId)
                        IconButton(
                            onClick = { onOtherToolClick(toolId) },
                            modifier = Modifier
                                .minimumInteractiveComponentSize()
                                .testTag("toolbar_btn_$toolId")
                        ) {
                            Icon(imageVector = tool.icon, contentDescription = tool.label, tint = iconColor, modifier = Modifier.size(24.dp))
                        }
                    }
                }
            }
        }
    }
}
