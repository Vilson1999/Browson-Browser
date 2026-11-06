package com.example.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import com.example.ui.theme.LocalAppTheme
import com.example.ui.theme.SectionIconBgDark
import com.example.ui.theme.SectionIconBgLight
import com.example.ui.theme.SectionIconOutlineDark
import com.example.ui.theme.SectionIconOutlineLight
import com.example.ui.theme.SpeedDialAddBgDark
import com.example.ui.theme.SpeedDialAddBgLight
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.ui.graphics.Brush
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.example.R
import com.example.model.SpeedDialItem

@Composable
fun SpeedDialSection(
    items: List<SpeedDialItem>,
    onItemClick: (SpeedDialItem) -> Unit,
    onAddClick: () -> Unit,
    onEditClick: (SpeedDialItem) -> Unit,
    onRemoveClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    showAddButton: Boolean = false
) {
    Column(modifier = modifier.fillMaxWidth()) {
        val appTheme = LocalAppTheme.current
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = when {
                        appTheme.isAmoled -> Color(0xFF261908)
                        appTheme.isDark -> Color(0xFF38251B)
                        else -> Color(0xFFFEF3C7)
                    },
                    modifier = Modifier.size(32.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Filled.Speed,
                            contentDescription = null,
                            tint = if (appTheme.isDark) Color(0xFFFBBF24) else Color(0xFFD97706),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = stringResource(R.string.speed_dials_title),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.width(6.dp))
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.height(20.dp)
                ) {
                    Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(horizontal = 6.dp)) {
                        Text(
                            text = "${items.size}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            if (showAddButton) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .clickable(onClick = onAddClick)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Add,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = "Add",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // 5 COLUMNS Grid with Square Round Corner Shape
        val columns = 5
        val totalCells = if (showAddButton) items.size + 1 else items.size
        val rows = (totalCells + columns - 1) / columns

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            for (row in 0 until rows) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    for (col in 0 until columns) {
                        val index = row * columns + col
                        if (index < items.size) {
                            val item = items[index]
                            SquareRoundedSpeedDialItem(
                                item = item,
                                onClick = { onItemClick(item) },
                                onEdit = { onEditClick(item) },
                                onDelete = { onRemoveClick(item.id) },
                                modifier = Modifier.weight(1f)
                            )
                        } else if (showAddButton && index == items.size) {
                            // Plus icon at the last with matching square round corner shape
                            SquareRoundedAddIconItem(
                                label = "Add",
                                onClick = onAddClick,
                                modifier = Modifier.weight(1f)
                            )
                        } else {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SquareRoundedSpeedDialItem(
    item: SpeedDialItem,
    onClick: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showContextMenu by remember { mutableStateOf(false) }
    val appTheme = LocalAppTheme.current
    val itemBaseColor = Color(item.colorHex)
    val iconOutline = if (appTheme.customIconBgColor != null) appTheme.iconBorder else itemBaseColor.copy(alpha = 0.35f)

    // Gradient mix for icon background blending item colour with theme icon background
    val iconGradientMix = Brush.linearGradient(
        colors = listOf(
            itemBaseColor.copy(alpha = if (appTheme.isDark) 0.45f else 0.30f),
            appTheme.iconBg,
            itemBaseColor.copy(alpha = if (appTheme.isDark) 0.22f else 0.12f)
        )
    )

    Column(
        modifier = modifier
            .padding(horizontal = 2.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier.size(52.dp),
            contentAlignment = Alignment.Center
        ) {
            // Square round corner shape with colour gradient mix background
            Surface(
                modifier = Modifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .combinedClickable(
                        onClick = onClick,
                        onLongClick = { showContextMenu = true }
                    )
                    .border(
                        width = 1.dp,
                        color = iconOutline,
                        shape = RoundedCornerShape(14.dp)
                    )
                    .testTag("speed_dial_${item.title.lowercase()}"),
                color = Color.Transparent,
                shape = RoundedCornerShape(14.dp),
                shadowElevation = 2.dp
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(iconGradientMix)
                        .padding(8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    // Website Logo / Favicon using Coil
                    SubcomposeAsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(item.faviconUrl)
                            .crossfade(true)
                            .build(),
                        contentDescription = "${item.title} Logo",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.size(30.dp),
                        loading = {
                            // Brand theme fallback during loading
                            FallbackLogoBadge(
                                initial = item.initial,
                                colorHex = item.colorHex
                            )
                        },
                        error = {
                            // Brand theme fallback on error
                            FallbackLogoBadge(
                                initial = item.initial,
                                colorHex = item.colorHex
                            )
                        }
                    )
                }
            }

            // Long-press Context Menu showing 2 options: Edit, Delete
            DropdownMenu(
                expanded = showContextMenu,
                onDismissRequest = { showContextMenu = false },
                modifier = Modifier.testTag("speed_dial_context_menu")
            ) {
                // 1. Edit option
                DropdownMenuItem(
                    text = {
                        Text(
                            text = stringResource(R.string.action_edit),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Filled.Edit,
                            contentDescription = "Edit",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                    },
                    onClick = {
                        showContextMenu = false
                        onEdit()
                    },
                    modifier = Modifier.testTag("menu_action_edit")
                )

                // 2. Delete option
                DropdownMenuItem(
                    text = {
                        Text(
                            text = stringResource(R.string.action_delete),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.error
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Filled.Delete,
                            contentDescription = "Delete",
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(18.dp)
                        )
                    },
                    onClick = {
                        showContextMenu = false
                        onDelete()
                    },
                    modifier = Modifier.testTag("menu_action_delete")
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Site Name Label
        Text(
            text = item.title,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun FallbackLogoBadge(
    initial: String,
    colorHex: Long,
    modifier: Modifier = Modifier
) {
    val baseColor = Color(colorHex)
    val badgeGradient = Brush.linearGradient(
        listOf(
            baseColor,
            baseColor.copy(alpha = 0.82f)
        )
    )
    Box(
        modifier = modifier
            .size(30.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(badgeGradient),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = initial,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
    }
}

@Composable
fun SquareRoundedAddIconItem(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val appTheme = LocalAppTheme.current
    val addBgColor = appTheme.speedDialAddBg

    Column(
        modifier = modifier
            .padding(horizontal = 2.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(
            modifier = Modifier
                .size(52.dp)
                .clip(RoundedCornerShape(14.dp))
                .border(
                    width = 1.5.dp,
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
                    shape = RoundedCornerShape(14.dp)
                )
                .clickable(onClick = onClick)
                .testTag("add_speed_dial_btn"),
            color = addBgColor,
            shape = RoundedCornerShape(14.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = label,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
            textAlign = TextAlign.Center
        )
    }
}
