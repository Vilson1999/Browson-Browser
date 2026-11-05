package com.example.ui

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.DriveFileMove
import androidx.compose.material.icons.automirrored.filled.ViewList
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.CreateNewFolder
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SelectAll
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
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
import com.example.model.BookmarkFolder
import com.example.model.BookmarkItem

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun BookmarkScreen(
    bookmarks: List<BookmarkItem>,
    folders: List<BookmarkFolder>,
    selectedFolderId: String,
    onSelectFolder: (String) -> Unit,
    onCreateFolder: (String, Long) -> Unit,
    onDeleteFolder: (String) -> Unit,
    onNavigateToUrl: (String, String?) -> Unit,
    onDeleteBookmarks: (Set<String>?) -> Unit,
    onAddToSpeedDial: (BookmarkItem) -> Boolean,
    onAddBookmark: (String, String, String?, String?) -> Unit,
    onEditBookmark: (BookmarkItem) -> Unit,
    onMoveBookmark: (String, String, String) -> Unit,
    onImportBookmarks: (String, String, String) -> Int,
    onExportJson: () -> String,
    onExportHtml: () -> String,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var isSelectionMode by remember { mutableStateOf(false) }
    val selectedIds = remember { mutableStateListOf<String>() }

    var isGridView by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    var isSearching by remember { mutableStateOf(false) }

    var showCreateFolderDialog by remember { mutableStateOf(false) }
    var showImportDialog by remember { mutableStateOf(false) }
    var showExportDialog by remember { mutableStateOf(false) }
    var showClearConfirmDialog by remember { mutableStateOf(false) }
    var showAddDialog by remember { mutableStateOf(false) }
    var bookmarkToMove by remember { mutableStateOf<BookmarkItem?>(null) }

    // Intercept back key
    BackHandler {
        if (isSelectionMode) {
            isSelectionMode = false
            selectedIds.clear()
        } else if (isSearching) {
            isSearching = false
            searchQuery = ""
        } else {
            onClose()
        }
    }

    // Filter bookmarks based on selected folder and search query
    val filteredBookmarks = remember(bookmarks, selectedFolderId, searchQuery) {
        bookmarks.filter { bm ->
            val matchesFolder = (selectedFolderId == "folder-all" || bm.folderId == selectedFolderId)
            val matchesSearch = if (searchQuery.isBlank()) {
                true
            } else {
                bm.title.contains(searchQuery, ignoreCase = true) ||
                    bm.url.contains(searchQuery, ignoreCase = true)
            }
            matchesFolder && matchesSearch
        }
    }

    Surface(
        modifier = modifier.fillMaxSize().testTag("bookmark_screen"),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            // ==========================================
            // TOP APP BAR
            // ==========================================
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp),
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 2.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Left: Back button & Title or Selection counter
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier.size(40.dp)
                        ) {
                            IconButton(
                                onClick = {
                                    if (isSelectionMode) {
                                        isSelectionMode = false
                                        selectedIds.clear()
                                    } else if (isSearching) {
                                        isSearching = false
                                        searchQuery = ""
                                    } else {
                                        onClose()
                                    }
                                },
                                modifier = Modifier.testTag("bookmark_back_button")
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = if (isSelectionMode) "Cancel selection" else "Back",
                                    tint = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        if (isSelectionMode) {
                            Text(
                                text = "${selectedIds.size} selected",
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                color = MaterialTheme.colorScheme.primary
                            )
                        } else if (isSearching) {
                            OutlinedTextField(
                                value = searchQuery,
                                onValueChange = { searchQuery = it },
                                placeholder = { Text("Search bookmarks...", fontSize = 14.sp) },
                                singleLine = true,
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(end = 8.dp)
                                    .testTag("bookmark_search_input"),
                                trailingIcon = {
                                    if (searchQuery.isNotEmpty()) {
                                        IconButton(onClick = { searchQuery = "" }) {
                                            Icon(Icons.Filled.Close, contentDescription = "Clear", modifier = Modifier.size(16.dp))
                                        }
                                    }
                                }
                            )
                        } else {
                            Column {
                                Text(
                                    text = stringResource(R.string.bookmark_menu_title),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 19.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "${filteredBookmarks.size} item${if (filteredBookmarks.size == 1) "" else "s"}",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    // Right: Actions with styled icon backgrounds
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        if (isSelectionMode) {
                            // Select All
                            ActionButtonWithBg(
                                icon = Icons.Filled.SelectAll,
                                contentDescription = "Select All",
                                onClick = {
                                    if (selectedIds.size == filteredBookmarks.size) {
                                        selectedIds.clear()
                                    } else {
                                        selectedIds.clear()
                                        selectedIds.addAll(filteredBookmarks.map { it.id })
                                    }
                                },
                                testTag = "bookmark_select_all"
                            )

                            // Delete selected
                            ActionButtonWithBg(
                                icon = Icons.Filled.Delete,
                                contentDescription = "Delete Selected",
                                iconTint = MaterialTheme.colorScheme.error,
                                bgColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.8f),
                                onClick = {
                                    if (selectedIds.isNotEmpty()) {
                                        showClearConfirmDialog = true
                                    }
                                },
                                testTag = "bookmark_delete_selected"
                            )
                        } else if (!isSearching) {
                            // Search toggle
                            ActionButtonWithBg(
                                icon = Icons.Filled.Search,
                                contentDescription = "Search Bookmarks",
                                onClick = { isSearching = true },
                                testTag = "bookmark_search_btn"
                            )

                            // Import action
                            ActionButtonWithBg(
                                icon = Icons.Filled.FileDownload,
                                contentDescription = stringResource(R.string.bookmark_import),
                                onClick = { showImportDialog = true },
                                testTag = "bookmark_import_btn"
                            )

                            // Export action
                            ActionButtonWithBg(
                                icon = Icons.Filled.FileUpload,
                                contentDescription = stringResource(R.string.bookmark_export),
                                onClick = { showExportDialog = true },
                                testTag = "bookmark_export_btn"
                            )

                            // Add bookmark (+)
                            ActionButtonWithBg(
                                icon = Icons.Filled.Add,
                                contentDescription = stringResource(R.string.add_bookmark),
                                iconTint = MaterialTheme.colorScheme.onPrimary,
                                bgColor = MaterialTheme.colorScheme.primary,
                                onClick = { showAddDialog = true },
                                testTag = "bookmark_add_btn"
                            )
                        }
                    }
                }
            }

            // ==========================================
            // FOLDER TABS BAR WITH ICONS & BACKGROUNDS
            // ==========================================
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 14.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Folder chips
                folders.forEach { folder ->
                    val isSelected = (folder.id == selectedFolderId)
                    val count = if (folder.id == "folder-all") {
                        bookmarks.size
                    } else {
                        bookmarks.count { it.folderId == folder.id }
                    }

                    FilterChip(
                        selected = isSelected,
                        onClick = { onSelectFolder(folder.id) },
                        label = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = folder.name,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    shape = CircleShape,
                                    color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
                                    modifier = Modifier.size(18.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(
                                            text = "$count",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        },
                        leadingIcon = {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .clip(CircleShape)
                                    .background(Color(folder.colorHex))
                            )
                        },
                        shape = RoundedCornerShape(16.dp),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                            selectedLabelColor = MaterialTheme.colorScheme.primary
                        ),
                        modifier = Modifier.testTag("folder_chip_${folder.id}")
                    )
                }

                // "+ New Folder" chip with distinct background and styling
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.6f),
                    modifier = Modifier
                        .height(32.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .clickable { showCreateFolderDialog = true }
                        .testTag("create_folder_chip")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.CreateNewFolder,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = stringResource(R.string.bookmark_create_folder),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                }
            }

            // Quick View Mode and Multi-Select bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val activeFolderName = folders.firstOrNull { it.id == selectedFolderId }?.name ?: "Bookmarks"
                Text(
                    text = activeFolderName,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Switch between List and Grid
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier.size(32.dp)
                    ) {
                        IconButton(
                            onClick = { isGridView = !isGridView },
                            modifier = Modifier.size(32.dp).testTag("bookmark_toggle_grid")
                        ) {
                            Icon(
                                imageVector = if (isGridView) Icons.AutoMirrored.Filled.ViewList else Icons.Filled.GridView,
                                contentDescription = if (isGridView) "List View" else "Grid View",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    // Multi-select toggle
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = if (isSelectionMode) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier.height(32.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .clickable {
                                    isSelectionMode = !isSelectionMode
                                    if (!isSelectionMode) selectedIds.clear()
                                }
                                .padding(horizontal = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Filled.CheckCircle,
                                contentDescription = null,
                                tint = if (isSelectionMode) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (isSelectionMode) "Done" else "Select",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = if (isSelectionMode) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            // ==========================================
            // BOOKMARKS LIST / ADAPTIVE GRID
            // ==========================================
            if (filteredBookmarks.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                            modifier = Modifier.size(72.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Filled.Bookmark,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(36.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = stringResource(R.string.bookmark_empty),
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 15.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            Button(
                                onClick = { showAddDialog = true },
                                shape = RoundedCornerShape(20.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primary
                                )
                            ) {
                                Icon(Icons.Filled.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Add Bookmark")
                            }

                            Button(
                                onClick = { showImportDialog = true },
                                shape = RoundedCornerShape(20.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                            ) {
                                Icon(Icons.Filled.FileDownload, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Import")
                            }
                        }
                    }
                }
            } else {
                BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                    val isTabletOrWide = maxWidth > 600.dp

                    if (isGridView || isTabletOrWide) {
                        val columns = if (maxWidth > 840.dp) 3 else 2
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(columns),
                            modifier = Modifier
                                .fillMaxSize()
                                .testTag("bookmark_grid"),
                            contentPadding = PaddingValues(14.dp),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            items(filteredBookmarks, key = { it.id }) { item ->
                                BookmarkCardItem(
                                    item = item,
                                    isSelectionMode = isSelectionMode,
                                    isSelected = selectedIds.contains(item.id),
                                    onItemClick = {
                                        if (isSelectionMode) {
                                            if (selectedIds.contains(item.id)) selectedIds.remove(item.id) else selectedIds.add(item.id)
                                        } else {
                                            onNavigateToUrl(item.url, item.title)
                                            onClose()
                                        }
                                    },
                                    onLongClick = {
                                        isSelectionMode = true
                                        if (!selectedIds.contains(item.id)) selectedIds.add(item.id)
                                    },
                                    onSendClick = {
                                        sendBookmark(context, item)
                                    },
                                    onAddToSpeedDial = {
                                        val added = onAddToSpeedDial(item)
                                        Toast.makeText(
                                            context,
                                            if (added) "Added '${item.title}' to Speed Dials" else "Already in Speed Dials",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    },
                                    onEditClick = { onEditBookmark(item) },
                                    onDeleteClick = { onDeleteBookmarks(setOf(item.id)) },
                                    onMoveClick = { bookmarkToMove = item }
                                )
                            }
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .testTag("bookmark_list"),
                            contentPadding = PaddingValues(14.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            items(filteredBookmarks, key = { it.id }) { item ->
                                BookmarkRowItem(
                                    item = item,
                                    isSelectionMode = isSelectionMode,
                                    isSelected = selectedIds.contains(item.id),
                                    onItemClick = {
                                        if (isSelectionMode) {
                                            if (selectedIds.contains(item.id)) selectedIds.remove(item.id) else selectedIds.add(item.id)
                                        } else {
                                            onNavigateToUrl(item.url, item.title)
                                            onClose()
                                        }
                                    },
                                    onLongClick = {
                                        isSelectionMode = true
                                        if (!selectedIds.contains(item.id)) selectedIds.add(item.id)
                                    },
                                    onSendClick = {
                                        sendBookmark(context, item)
                                    },
                                    onAddToSpeedDial = {
                                        val added = onAddToSpeedDial(item)
                                        Toast.makeText(
                                            context,
                                            if (added) "Added '${item.title}' to Speed Dials" else "Already in Speed Dials",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    },
                                    onEditClick = { onEditBookmark(item) },
                                    onDeleteClick = { onDeleteBookmarks(setOf(item.id)) },
                                    onMoveClick = { bookmarkToMove = item }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // ==========================================
    // DIALOGS
    // ==========================================

    // 1. Create Folder Dialog
    if (showCreateFolderDialog) {
        CreateFolderDialog(
            onDismiss = { showCreateFolderDialog = false },
            onConfirm = { name, colorHex ->
                onCreateFolder(name, colorHex)
                showCreateFolderDialog = false
                Toast.makeText(context, "Folder '$name' created", Toast.LENGTH_SHORT).show()
            }
        )
    }

    // 2. Import Bookmarks Dialog
    if (showImportDialog) {
        ImportBookmarksDialog(
            folders = folders.filter { it.id != "folder-all" },
            onDismiss = { showImportDialog = false },
            onImport = { content, targetFolderId, targetFolderName ->
                val count = onImportBookmarks(content, targetFolderId, targetFolderName)
                showImportDialog = false
                Toast.makeText(context, "Successfully imported $count bookmarks", Toast.LENGTH_LONG).show()
            }
        )
    }

    // 3. Export Bookmarks Dialog
    if (showExportDialog) {
        ExportBookmarksDialog(
            onDismiss = { showExportDialog = false },
            onExportJson = onExportJson,
            onExportHtml = onExportHtml,
            context = context
        )
    }

    // 4. Batch Delete Confirmation
    if (showClearConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showClearConfirmDialog = false },
            title = { Text(stringResource(R.string.bookmark_delete_confirm), fontWeight = FontWeight.Bold) },
            text = { Text("Are you sure you want to delete ${selectedIds.size} selected bookmark${if (selectedIds.size == 1) "" else "s"}?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDeleteBookmarks(selectedIds.toSet())
                        selectedIds.clear()
                        isSelectionMode = false
                        showClearConfirmDialog = false
                    },
                    modifier = Modifier.testTag("confirm_delete_bookmarks")
                ) {
                    Text(stringResource(R.string.action_delete), color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearConfirmDialog = false }) {
                    Text(stringResource(R.string.dialog_cancel))
                }
            }
        )
    }

    // 5. Add Bookmark Dialog
    if (showAddDialog) {
        AddBookmarkWithFolderDialog(
            folders = folders.filter { it.id != "folder-all" },
            initialFolderId = if (selectedFolderId != "folder-all") selectedFolderId else "folder-mobile",
            onDismiss = { showAddDialog = false },
            onConfirm = { title, url, fId, fName ->
                onAddBookmark(title, url, fId, fName)
                showAddDialog = false
                Toast.makeText(context, "Added bookmark '$title'", Toast.LENGTH_SHORT).show()
            }
        )
    }

    // 6. Move Bookmark Dialog
    bookmarkToMove?.let { bm ->
        MoveBookmarkFolderDialog(
            bookmark = bm,
            folders = folders.filter { it.id != "folder-all" },
            onDismiss = { bookmarkToMove = null },
            onMove = { newFolderId, newFolderName ->
                onMoveBookmark(bm.id, newFolderId, newFolderName)
                bookmarkToMove = null
                Toast.makeText(context, "Moved to '$newFolderName'", Toast.LENGTH_SHORT).show()
            }
        )
    }
}

// ==========================================
// ACTION BUTTON WITH STYLED ICON BACKGROUND
// ==========================================
@Composable
fun ActionButtonWithBg(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    bgColor: Color = MaterialTheme.colorScheme.surfaceVariant,
    iconTint: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    testTag: String = ""
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = bgColor,
        modifier = modifier
            .size(38.dp)
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .testTag(testTag)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                imageVector = icon,
                contentDescription = contentDescription,
                tint = iconTint,
                modifier = Modifier.size(19.dp)
            )
        }
    }
}

// ==========================================
// BOOKMARK ROW ITEM (Compact List View)
// ==========================================
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun BookmarkRowItem(
    item: BookmarkItem,
    isSelectionMode: Boolean,
    isSelected: Boolean,
    onItemClick: () -> Unit,
    onLongClick: () -> Unit,
    onSendClick: () -> Unit,
    onAddToSpeedDial: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onMoveClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showMenu by remember { mutableStateOf(false) }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f),
                shape = RoundedCornerShape(16.dp)
            )
            .combinedClickable(
                onClick = onItemClick,
                onLongClick = onLongClick
            )
            .testTag("bookmark_item_${item.id}"),
        color = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f) else MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(16.dp),
        tonalElevation = if (isSelected) 4.dp else 1.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Selection checkbox or Website Icon / Thumbnail
            if (isSelectionMode) {
                Checkbox(
                    checked = isSelected,
                    onCheckedChange = { onItemClick() },
                    colors = CheckboxDefaults.colors(checkedColor = MaterialTheme.colorScheme.primary),
                    modifier = Modifier.padding(end = 6.dp)
                )
            } else {
                WebsiteFaviconBadge(
                    item = item,
                    size = 46.dp
                )
                Spacer(modifier = Modifier.width(12.dp))
            }

            // Info (Title, URL domain, Folder pill)
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 6.dp)
            ) {
                Text(
                    text = item.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = cleanDomain(item.url),
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.primary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                // Folder pill
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                    modifier = Modifier.clip(RoundedCornerShape(8.dp))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Folder,
                            contentDescription = null,
                            tint = Color(item.colorHex),
                            modifier = Modifier.size(11.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = item.folderName,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // New Design Action Buttons with Icon Backgrounds
            if (!isSelectionMode) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // 1. Send Bookmark
                    ActionButtonWithBg(
                        icon = Icons.Filled.Share,
                        contentDescription = "Send Bookmark",
                        bgColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f),
                        iconTint = MaterialTheme.colorScheme.primary,
                        onClick = onSendClick,
                        testTag = "send_bookmark_${item.id}"
                    )

                    // 2. Add to Speed Dial
                    ActionButtonWithBg(
                        icon = Icons.Filled.Speed,
                        contentDescription = "Add to Speed Dial",
                        bgColor = Color(0xFFFEF3C7),
                        iconTint = Color(0xFFD97706),
                        onClick = onAddToSpeedDial,
                        testTag = "speeddial_bookmark_${item.id}"
                    )

                    // 3. Move / More menu
                    Box {
                        ActionButtonWithBg(
                            icon = Icons.AutoMirrored.Filled.DriveFileMove,
                            contentDescription = "Move Folder",
                            bgColor = MaterialTheme.colorScheme.surfaceVariant,
                            iconTint = MaterialTheme.colorScheme.onSurfaceVariant,
                            onClick = onMoveClick,
                            testTag = "move_bookmark_${item.id}"
                        )
                    }

                    // 4. Delete
                    ActionButtonWithBg(
                        icon = Icons.Filled.DeleteOutline,
                        contentDescription = "Delete",
                        bgColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.6f),
                        iconTint = MaterialTheme.colorScheme.error,
                        onClick = onDeleteClick,
                        testTag = "delete_bookmark_${item.id}"
                    )
                }
            }
        }
    }
}

// ==========================================
// BOOKMARK CARD ITEM (Rich Grid / Tablet View)
// ==========================================
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun BookmarkCardItem(
    item: BookmarkItem,
    isSelectionMode: Boolean,
    isSelected: Boolean,
    onItemClick: () -> Unit,
    onLongClick: () -> Unit,
    onSendClick: () -> Unit,
    onAddToSpeedDial: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onMoveClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f),
                shape = RoundedCornerShape(18.dp)
            )
            .combinedClickable(
                onClick = onItemClick,
                onLongClick = onLongClick
            )
            .testTag("bookmark_card_${item.id}"),
        color = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f) else MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(18.dp),
        tonalElevation = if (isSelected) 4.dp else 2.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            // Header: Icon + Selection / Folder
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                WebsiteFaviconBadge(item = item, size = 48.dp)

                if (isSelectionMode) {
                    Checkbox(
                        checked = isSelected,
                        onCheckedChange = { onItemClick() },
                        colors = CheckboxDefaults.colors(checkedColor = MaterialTheme.colorScheme.primary)
                    )
                } else {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier.clip(RoundedCornerShape(8.dp))
                    ) {
                        Text(
                            text = item.folderName,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Title & URL
            Text(
                text = item.title,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = cleanDomain(item.url),
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.primary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Bottom action icons row with modern background pills
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Send Bookmark
                ActionButtonWithBg(
                    icon = Icons.Filled.Share,
                    contentDescription = "Send",
                    bgColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f),
                    iconTint = MaterialTheme.colorScheme.primary,
                    onClick = onSendClick,
                    modifier = Modifier.size(34.dp)
                )

                // Add to Speed Dial
                ActionButtonWithBg(
                    icon = Icons.Filled.Speed,
                    contentDescription = "Add to Speed Dial",
                    bgColor = Color(0xFFFEF3C7),
                    iconTint = Color(0xFFD97706),
                    onClick = onAddToSpeedDial,
                    modifier = Modifier.size(34.dp)
                )

                // Move Folder
                ActionButtonWithBg(
                    icon = Icons.AutoMirrored.Filled.DriveFileMove,
                    contentDescription = "Move",
                    bgColor = MaterialTheme.colorScheme.surfaceVariant,
                    iconTint = MaterialTheme.colorScheme.onSurfaceVariant,
                    onClick = onMoveClick,
                    modifier = Modifier.size(34.dp)
                )

                // Delete
                ActionButtonWithBg(
                    icon = Icons.Filled.DeleteOutline,
                    contentDescription = "Delete",
                    bgColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.6f),
                    iconTint = MaterialTheme.colorScheme.error,
                    onClick = onDeleteClick,
                    modifier = Modifier.size(34.dp)
                )
            }
        }
    }
}

// ==========================================
// WEBSITE FAVICON / THUMBNAIL BADGE
// ==========================================
@Composable
fun WebsiteFaviconBadge(
    item: BookmarkItem,
    size: androidx.compose.ui.unit.Dp,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    Surface(
        modifier = modifier
            .size(size)
            .clip(RoundedCornerShape(14.dp)),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f),
        shape = RoundedCornerShape(14.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
    ) {
        Box(contentAlignment = Alignment.Center) {
            SubcomposeAsyncImage(
                model = ImageRequest.Builder(context)
                    .data(item.faviconUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = "${item.title} icon",
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .size(size * 0.55f)
                    .clip(RoundedCornerShape(4.dp)),
                loading = {
                    MonogramInitialBadge(item = item, size = size)
                },
                error = {
                    MonogramInitialBadge(item = item, size = size)
                }
            )
        }
    }
}

@Composable
fun MonogramInitialBadge(item: BookmarkItem, size: androidx.compose.ui.unit.Dp) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(item.colorHex)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = item.initial.ifBlank { item.title.take(1).uppercase() },
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = if (size > 40.dp) 16.sp else 12.sp
        )
    }
}

// ==========================================
// CREATE FOLDER DIALOG
// ==========================================
@Composable
fun CreateFolderDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, Long) -> Unit
) {
    var folderName by remember { mutableStateOf("") }
    val colors = listOf(
        0xFF10B981, // Emerald
        0xFF3B82F6, // Blue
        0xFF8B5CF6, // Purple
        0xFFF59E0B, // Amber
        0xFFEC4899, // Pink
        0xFF06B6D4, // Cyan
        0xFFEF4444  // Red
    )
    var selectedColor by remember { mutableStateOf(colors[0]) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.bookmark_create_folder), fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                OutlinedTextField(
                    value = folderName,
                    onValueChange = { folderName = it },
                    label = { Text(stringResource(R.string.bookmark_folder_name)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("folder_name_input")
                )

                Text("Pick Folder Color", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    colors.forEach { colorHex ->
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(Color(colorHex))
                                .border(
                                    width = if (selectedColor == colorHex) 3.dp else 0.dp,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    shape = CircleShape
                                )
                                .clickable { selectedColor = colorHex },
                            contentAlignment = Alignment.Center
                        ) {
                            if (selectedColor == colorHex) {
                                Icon(
                                    imageVector = Icons.Filled.Check,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (folderName.isNotBlank()) {
                        onConfirm(folderName, selectedColor)
                    }
                },
                enabled = folderName.isNotBlank(),
                modifier = Modifier.testTag("confirm_create_folder")
            ) {
                Text(stringResource(R.string.dialog_save))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.dialog_cancel))
            }
        }
    )
}

// ==========================================
// IMPORT BOOKMARKS DIALOG
// ==========================================
@Composable
fun ImportBookmarksDialog(
    folders: List<BookmarkFolder>,
    onDismiss: () -> Unit,
    onImport: (String, String, String) -> Unit
) {
    var importText by remember { mutableStateOf("") }
    var selectedFolderId by remember { mutableStateOf(folders.firstOrNull()?.id ?: "folder-mobile") }
    val selectedFolderName = folders.firstOrNull { it.id == selectedFolderId }?.name ?: "Mobile Bookmarks"

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.FileDownload, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.width(8.dp))
                Text(stringResource(R.string.bookmark_import_title), fontWeight = FontWeight.Bold)
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "Paste JSON, HTML bookmark format (<a href='...'>), or line-by-line URLs:",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                OutlinedTextField(
                    value = importText,
                    onValueChange = { importText = it },
                    placeholder = { Text(stringResource(R.string.bookmark_import_hint)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(130.dp)
                        .testTag("import_text_input")
                )

                // One-tap load sample bookmarks button
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .clickable {
                            importText = """
                            [
                              {"title": "Jetpack Compose Docs", "url": "https://developer.android.com/jetpack/compose", "folder": "Development & Tools"},
                              {"title": "Kotlin Language", "url": "https://kotlinlang.org", "folder": "Development & Tools"},
                              {"title": "GitHub Explore", "url": "https://github.com/explore", "folder": "Development & Tools"},
                              {"title": "Google News", "url": "https://news.google.com", "folder": "Reading List"},
                              {"title": "BBC Technology", "url": "https://www.bbc.com/news/technology", "folder": "Reading List"},
                              {"title": "MDN Web Docs", "url": "https://developer.mozilla.org", "folder": "Development & Tools"}
                            ]
                            """.trimIndent()
                        }
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Filled.FileUpload, contentDescription = null, tint = MaterialTheme.colorScheme.secondary, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = stringResource(R.string.bookmark_import_sample),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (importText.isNotBlank()) {
                        onImport(importText, selectedFolderId, selectedFolderName)
                    }
                },
                enabled = importText.isNotBlank(),
                modifier = Modifier.testTag("confirm_import_bookmarks")
            ) {
                Text(stringResource(R.string.bookmark_import))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.dialog_cancel))
            }
        }
    )
}

// ==========================================
// EXPORT BOOKMARKS DIALOG
// ==========================================
@Composable
fun ExportBookmarksDialog(
    onDismiss: () -> Unit,
    onExportJson: () -> String,
    onExportHtml: () -> String,
    context: Context
) {
    var exportFormat by remember { mutableStateOf("JSON") }
    val exportedData = remember(exportFormat) {
        if (exportFormat == "JSON") onExportJson() else onExportHtml()
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.FileUpload, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.width(8.dp))
                Text(stringResource(R.string.bookmark_export_title), fontWeight = FontWeight.Bold)
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = exportFormat == "JSON",
                        onClick = { exportFormat = "JSON" },
                        label = { Text("JSON Format") },
                        modifier = Modifier.weight(1f)
                    )
                    FilterChip(
                        selected = exportFormat == "HTML",
                        onClick = { exportFormat = "HTML" },
                        label = { Text("HTML (Netscape)") },
                        modifier = Modifier.weight(1f)
                    )
                }

                OutlinedTextField(
                    value = exportedData,
                    onValueChange = {},
                    readOnly = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                        .testTag("export_text_preview")
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            val clip = ClipData.newPlainText("Bookmarks", exportedData)
                            clipboard.setPrimaryClip(clip)
                            Toast.makeText(context, "Copied bookmarks to clipboard", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Filled.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Copy")
                    }

                    Button(
                        onClick = {
                            val sendIntent = Intent().apply {
                                action = Intent.ACTION_SEND
                                putExtra(Intent.EXTRA_SUBJECT, "Browson Bookmarks Export")
                                putExtra(Intent.EXTRA_TEXT, exportedData)
                                type = if (exportFormat == "JSON") "application/json" else "text/html"
                            }
                            context.startActivity(Intent.createChooser(sendIntent, "Export Bookmarks via"))
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Filled.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Share")
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}

// ==========================================
// ADD BOOKMARK WITH FOLDER DIALOG
// ==========================================
@Composable
fun AddBookmarkWithFolderDialog(
    folders: List<BookmarkFolder>,
    initialFolderId: String,
    onDismiss: () -> Unit,
    onConfirm: (String, String, String, String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var url by remember { mutableStateOf("") }
    var selectedFolderId by remember { mutableStateOf(initialFolderId) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.add_bookmark), fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text(stringResource(R.string.dialog_title)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("add_bookmark_title_input")
                )
                OutlinedTextField(
                    value = url,
                    onValueChange = { url = it },
                    label = { Text(stringResource(R.string.dialog_url)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("add_bookmark_url_input")
                )

                Text("Save in Folder:", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    folders.forEach { f ->
                        FilterChip(
                            selected = f.id == selectedFolderId,
                            onClick = { selectedFolderId = f.id },
                            label = { Text(f.name, fontSize = 11.sp) }
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank() && url.isNotBlank()) {
                        val folderName = folders.firstOrNull { it.id == selectedFolderId }?.name ?: "Mobile Bookmarks"
                        onConfirm(title, url, selectedFolderId, folderName)
                    }
                },
                enabled = title.isNotBlank() && url.isNotBlank(),
                modifier = Modifier.testTag("confirm_add_bookmark_btn")
            ) {
                Text(stringResource(R.string.dialog_save))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.dialog_cancel))
            }
        }
    )
}

// ==========================================
// MOVE BOOKMARK FOLDER DIALOG
// ==========================================
@Composable
fun MoveBookmarkFolderDialog(
    bookmark: BookmarkItem,
    folders: List<BookmarkFolder>,
    onDismiss: () -> Unit,
    onMove: (String, String) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Move '${bookmark.title}'", fontWeight = FontWeight.Bold, fontSize = 16.sp) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("Select destination folder:", fontSize = 13.sp)
                folders.forEach { folder ->
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .clickable { onMove(folder.id, folder.name) }
                            .padding(vertical = 4.dp),
                        color = if (bookmark.folderId == folder.id) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Filled.Folder, contentDescription = null, tint = Color(folder.colorHex), modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(folder.name, fontWeight = FontWeight.Medium, fontSize = 13.sp)
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

// Helper to send bookmark via system share intent
private fun sendBookmark(context: Context, item: BookmarkItem) {
    val sendIntent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_SUBJECT, item.title)
        putExtra(Intent.EXTRA_TEXT, "${item.title}\n${item.url}")
        type = "text/plain"
    }
    context.startActivity(Intent.createChooser(sendIntent, "Send bookmark via"))
}

// Helper to extract clean domain name
private fun cleanDomain(url: String): String {
    return url.replace("https://", "")
        .replace("http://", "")
        .replace("www.", "")
        .split("/")
        .firstOrNull() ?: url
}
