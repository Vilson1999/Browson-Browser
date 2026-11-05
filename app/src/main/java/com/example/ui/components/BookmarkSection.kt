package com.example.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.isSystemInDarkTheme
import com.example.ui.theme.SectionIconBgDark
import com.example.ui.theme.SectionIconBgLight
import com.example.ui.theme.SectionIconOutlineDark
import com.example.ui.theme.SectionIconOutlineLight
import com.example.ui.theme.BookmarkAddBgDark
import com.example.ui.theme.BookmarkAddBgLight
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
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
import com.example.model.BookmarkItem

@Composable
fun BookmarkSection(
    items: List<BookmarkItem>,
    onItemClick: (BookmarkItem) -> Unit,
    onAddClick: () -> Unit,
    onEditClick: (BookmarkItem) -> Unit,
    onDeleteClick: (String) -> Unit,
    onRemoveClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Filled.Bookmark,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = stringResource(R.string.bookmarks_title),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "(${items.size})",
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // 5 COLUMNS Grid of Bookmarks with plus icon at the end
        val columns = 5
        val totalCells = items.size + 1
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
                            BookmarkIconItem(
                                item = item,
                                onClick = { onItemClick(item) },
                                onEdit = { onEditClick(item) },
                                onDelete = { onDeleteClick(item.id) },
                                onRemove = { onRemoveClick(item.id) },
                                modifier = Modifier.weight(1f)
                            )
                        } else if (index == items.size) {
                            // Plus icon at the last with matching 5-column square round shape
                            AddBookmarkIconItem(
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
fun BookmarkIconItem(
    item: BookmarkItem,
    onClick: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onRemove: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showContextMenu by remember { mutableStateOf(false) }
    val isDark = isSystemInDarkTheme()
    val iconBgColor = if (isDark) SectionIconBgDark else SectionIconBgLight
    val iconOutline = if (isDark) SectionIconOutlineDark else SectionIconOutlineLight

    Column(
        modifier = modifier
            .padding(horizontal = 2.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier.size(52.dp),
            contentAlignment = Alignment.Center
        ) {
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
                    .testTag("bookmark_${item.title.lowercase()}"),
                color = iconBgColor,
                shape = RoundedCornerShape(14.dp),
                shadowElevation = 2.dp
            ) {
                Box(
                    modifier = Modifier.padding(8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    SubcomposeAsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(item.faviconUrl)
                            .crossfade(true)
                            .build(),
                        contentDescription = "${item.title} Logo",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.size(28.dp),
                        loading = {
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(item.colorHex)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = item.initial,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        },
                        error = {
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(item.colorHex)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = item.initial,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                    )
                }
            }

            // Long-press Context Menu showing 3 options: Edit, Delete, Remove
            DropdownMenu(
                expanded = showContextMenu,
                onDismissRequest = { showContextMenu = false },
                modifier = Modifier.testTag("bookmark_context_menu")
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
                    modifier = Modifier.testTag("menu_action_edit_bookmark")
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
                    modifier = Modifier.testTag("menu_action_delete_bookmark")
                )

                // 3. Remove option
                DropdownMenuItem(
                    text = {
                        Text(
                            text = stringResource(R.string.action_remove),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = "Remove from Home",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )
                    },
                    onClick = {
                        showContextMenu = false
                        onRemove()
                    },
                    modifier = Modifier.testTag("menu_action_remove_bookmark")
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

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
fun AddBookmarkIconItem(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isDark = isSystemInDarkTheme()
    val addBgColor = if (isDark) BookmarkAddBgDark else BookmarkAddBgLight

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
                    color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.6f),
                    shape = RoundedCornerShape(14.dp)
                )
                .clickable(onClick = onClick)
                .testTag("add_bookmark_btn"),
            color = addBgColor,
            shape = RoundedCornerShape(14.dp)
        ) {
            Box(
                modifier = Modifier.size(52.dp),
                contentAlignment = Alignment.Center
            ) {
                // Outer Bookmark Icon
                Icon(
                    imageVector = Icons.Filled.Bookmark,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.secondary.copy(alpha = 0.28f),
                    modifier = Modifier.size(30.dp)
                )
                // Small + inside bookmark
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = label,
                    tint = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.size(15.dp)
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
