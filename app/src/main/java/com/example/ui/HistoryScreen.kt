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
import androidx.compose.material.icons.automirrored.filled.ViewList
import androidx.compose.material.icons.filled.BookmarkAdd
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ClearAll
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SelectAll
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
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
import androidx.compose.ui.graphics.vector.ImageVector
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
import com.example.model.HistoryItem
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

private enum class HistoryTimeFilter(val labelResId: Int) {
    ALL(R.string.history_filter_all),
    TODAY(R.string.history_today),
    YESTERDAY(R.string.history_yesterday),
    THIS_WEEK(R.string.history_this_week),
    OLDER(R.string.history_older)
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HistoryScreen(
    historyItems: List<HistoryItem>,
    onNavigateToUrl: (String, String?) -> Unit,
    onDeleteHistory: (Set<String>?) -> Unit,
    onClose: () -> Unit,
    onAddToBookmarks: ((String, String) -> Unit)? = null,
    onAddToSpeedDial: ((String, String) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var isSelectionMode by remember { mutableStateOf(false) }
    val selectedIds = remember { mutableStateListOf<String>() }

    var searchQuery by remember { mutableStateOf("") }
    var isSearching by remember { mutableStateOf(false) }
    var selectedTimeFilter by remember { mutableStateOf(HistoryTimeFilter.ALL) }
    var selectedDomainFilter by remember { mutableStateOf<String?>(null) }
    var isGridView by remember { mutableStateOf(false) }

    var showClearRangeDialog by remember { mutableStateOf(false) }

    // Sorted latest on top
    val sortedItems = remember(historyItems) {
        historyItems.sortedByDescending { it.timestamp }
    }

    // Unique popular domains for instant filter chips
    val popularDomains = remember(sortedItems) {
        sortedItems.map { extractDomain(it.url) }
            .filter { it.isNotBlank() }
            .distinct()
            .take(8)
    }

    // Filtered items based on search query, time filter, and domain filter
    val filteredItems = remember(sortedItems, searchQuery, selectedTimeFilter, selectedDomainFilter) {
        sortedItems.filter { item ->
            // Search query filter
            val matchesSearch = if (searchQuery.isBlank()) {
                true
            } else {
                item.title.contains(searchQuery, ignoreCase = true) ||
                    item.url.contains(searchQuery, ignoreCase = true)
            }

            // Domain filter
            val matchesDomain = if (selectedDomainFilter == null) {
                true
            } else {
                extractDomain(item.url).equals(selectedDomainFilter, ignoreCase = true)
            }

            // Time filter
            val group = getHistoryTimeGroup(item.timestamp)
            val matchesTime = when (selectedTimeFilter) {
                HistoryTimeFilter.ALL -> true
                HistoryTimeFilter.TODAY -> group == HistoryTimeGroup.TODAY
                HistoryTimeFilter.YESTERDAY -> group == HistoryTimeGroup.YESTERDAY
                HistoryTimeFilter.THIS_WEEK -> group == HistoryTimeGroup.THIS_WEEK
                HistoryTimeFilter.OLDER -> group == HistoryTimeGroup.OLDER
            }

            matchesSearch && matchesDomain && matchesTime
        }
    }

    // Intercept back key when in selection mode or searching
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

    Surface(
        modifier = modifier.fillMaxSize().testTag("history_screen"),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            // ==========================================
            // TOP APP BAR WITH STYLED ICON BACKGROUNDS
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
                        .padding(horizontal = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Left: Back button & Title/Counter
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
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
                                modifier = Modifier.size(40.dp).testTag("history_back_button")
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
                            Column {
                                Text(
                                    text = "${selectedIds.size} selected",
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = "Tap cards to select",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        } else if (isSearching) {
                            OutlinedTextField(
                                value = searchQuery,
                                onValueChange = { searchQuery = it },
                                placeholder = { Text(stringResource(R.string.history_search_hint), fontSize = 13.sp) },
                                singleLine = true,
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(end = 8.dp)
                                    .testTag("history_search_input"),
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
                                    text = stringResource(R.string.history_title),
                                    fontSize = 19.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "${filteredItems.size} visited pages",
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
                            // Select All button
                            HistoryActionIcon(
                                icon = Icons.Filled.SelectAll,
                                contentDescription = "Select or deselect all",
                                onClick = {
                                    if (selectedIds.size == filteredItems.size) {
                                        selectedIds.clear()
                                    } else {
                                        selectedIds.clear()
                                        selectedIds.addAll(filteredItems.map { it.id })
                                    }
                                },
                                testTag = "history_select_all_button"
                            )

                            // Delete Selected button
                            HistoryActionIcon(
                                icon = Icons.Filled.Delete,
                                contentDescription = "Delete selected history",
                                bgColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.85f),
                                iconTint = MaterialTheme.colorScheme.error,
                                onClick = {
                                    if (selectedIds.isNotEmpty()) {
                                        onDeleteHistory(selectedIds.toSet())
                                        selectedIds.clear()
                                        isSelectionMode = false
                                        Toast.makeText(context, "Deleted selected items", Toast.LENGTH_SHORT).show()
                                    }
                                },
                                testTag = "history_delete_button"
                            )
                        } else if (!isSearching) {
                            // Search Toggle
                            HistoryActionIcon(
                                icon = Icons.Filled.Search,
                                contentDescription = "Search history",
                                onClick = { isSearching = true },
                                testTag = "history_search_toggle"
                            )

                            // Clear History (Dialog)
                            HistoryActionIcon(
                                icon = Icons.Filled.DeleteSweep,
                                contentDescription = stringResource(R.string.history_clear_all),
                                bgColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.35f),
                                iconTint = MaterialTheme.colorScheme.error,
                                onClick = {
                                    if (sortedItems.isNotEmpty()) {
                                        showClearRangeDialog = true
                                    }
                                },
                                testTag = "history_delete_button"
                            )
                        }
                    }
                }
            }

            // ==========================================
            // FILTER CHIPS ROW (Time & Domains)
            // ==========================================
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
            ) {
                // Time Range Filters
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 14.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    HistoryTimeFilter.values().forEach { filter ->
                        val isSelected = (selectedTimeFilter == filter)
                        val count = when (filter) {
                            HistoryTimeFilter.ALL -> sortedItems.size
                            HistoryTimeFilter.TODAY -> sortedItems.count { getHistoryTimeGroup(it.timestamp) == HistoryTimeGroup.TODAY }
                            HistoryTimeFilter.YESTERDAY -> sortedItems.count { getHistoryTimeGroup(it.timestamp) == HistoryTimeGroup.YESTERDAY }
                            HistoryTimeFilter.THIS_WEEK -> sortedItems.count { getHistoryTimeGroup(it.timestamp) == HistoryTimeGroup.THIS_WEEK }
                            HistoryTimeFilter.OLDER -> sortedItems.count { getHistoryTimeGroup(it.timestamp) == HistoryTimeGroup.OLDER }
                        }

                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedTimeFilter = filter },
                            label = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = stringResource(filter.labelResId),
                                        fontSize = 12.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                    )
                                    if (count > 0) {
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Surface(
                                            shape = CircleShape,
                                            color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
                                            modifier = Modifier.size(16.dp)
                                        ) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Text(
                                                    text = "$count",
                                                    fontSize = 9.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }
                                        }
                                    }
                                }
                            },
                            shape = RoundedCornerShape(16.dp),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                                selectedLabelColor = MaterialTheme.colorScheme.primary
                            )
                        )
                    }
                }

                // Domain Quick Filters (if any visited domains exist)
                if (popularDomains.isNotEmpty() && !isSearching) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState())
                            .padding(horizontal = 14.dp, vertical = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            modifier = Modifier.padding(end = 2.dp)
                        ) {
                            Text(
                                text = "Websites:",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }

                        popularDomains.forEach { domain ->
                            val isSelected = (selectedDomainFilter == domain)
                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                                border = androidx.compose.foundation.BorderStroke(
                                    width = 1.dp,
                                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
                                ),
                                modifier = Modifier
                                    .clip(RoundedCornerShape(14.dp))
                                    .clickable {
                                        selectedDomainFilter = if (isSelected) null else domain
                                    }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(6.dp)
                                            .clip(CircleShape)
                                            .background(if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant)
                                    )
                                    Text(
                                        text = domain,
                                        fontSize = 11.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Quick toolbar controls (Selection toggle & Grid toggle)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (selectedDomainFilter != null) "Filtered by $selectedDomainFilter" else "Recent Activity",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Grid / List toggle
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier.size(32.dp)
                    ) {
                        IconButton(
                            onClick = { isGridView = !isGridView },
                            modifier = Modifier.size(32.dp).testTag("history_toggle_grid")
                        ) {
                            Icon(
                                imageVector = if (isGridView) Icons.AutoMirrored.Filled.ViewList else Icons.Filled.GridView,
                                contentDescription = if (isGridView) "List View" else "Grid View",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    // Selection mode toggle
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
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (isSelectionMode) "Done" else "Select",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                color = if (isSelectionMode) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            // ==========================================
            // BODY: EMPTY STATE OR HISTORY ITEMS
            // ==========================================
            if (filteredItems.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                            modifier = Modifier.size(76.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Filled.History,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(38.dp)
                                )
                            }
                        }

                        Text(
                            text = if (searchQuery.isNotBlank() || selectedDomainFilter != null) "No matching history" else stringResource(R.string.history_empty),
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Text(
                            text = if (searchQuery.isNotBlank()) "No visited pages match '$searchQuery'" else "Pages you visit while browsing will be listed here chronologically.",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )

                        if (searchQuery.isNotBlank() || selectedDomainFilter != null) {
                            Button(
                                onClick = {
                                    searchQuery = ""
                                    selectedDomainFilter = null
                                    selectedTimeFilter = HistoryTimeFilter.ALL
                                },
                                shape = RoundedCornerShape(20.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                            ) {
                                Text("Reset Filters")
                            }
                        }
                    }
                }
            } else {
                BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                    val isTablet = maxWidth > 600.dp

                    if (isGridView || isTablet) {
                        val columns = if (maxWidth > 840.dp) 3 else 2
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(columns),
                            modifier = Modifier
                                .fillMaxSize()
                                .testTag("history_list"),
                            contentPadding = PaddingValues(14.dp),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            items(filteredItems, key = { it.id }) { item ->
                                HistoryCardItem(
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
                                    onDeleteClick = {
                                        onDeleteHistory(setOf(item.id))
                                        Toast.makeText(context, "Removed from history", Toast.LENGTH_SHORT).show()
                                    },
                                    onShareClick = {
                                        shareHistoryUrl(context, item.title, item.url)
                                    },
                                    onCopyLinkClick = {
                                        copyUrlToClipboard(context, item.url)
                                    },
                                    onBookmarkClick = {
                                        onAddToBookmarks?.invoke(item.title, item.url)
                                        Toast.makeText(context, "Saved to Bookmarks", Toast.LENGTH_SHORT).show()
                                    },
                                    onSpeedDialClick = {
                                        onAddToSpeedDial?.invoke(item.title, item.url)
                                        Toast.makeText(context, "Added to Speed Dials", Toast.LENGTH_SHORT).show()
                                    }
                                )
                            }
                        }
                    } else {
                        // Sectioned List View grouped by Date
                        val grouped = remember(filteredItems) {
                            filteredItems.groupBy { getHistoryTimeGroup(it.timestamp) }
                        }

                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .testTag("history_list"),
                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            grouped.forEach { (timeGroup, itemsInGroup) ->
                                // Section Sticky Header
                                item(key = "header_${timeGroup.name}") {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(top = 10.dp, bottom = 4.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                text = timeGroup.title,
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.primary
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Surface(
                                                shape = CircleShape,
                                                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                                                modifier = Modifier.size(18.dp)
                                            ) {
                                                Box(contentAlignment = Alignment.Center) {
                                                    Text(
                                                        text = "${itemsInGroup.size}",
                                                        fontSize = 10.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = MaterialTheme.colorScheme.primary
                                                    )
                                                }
                                            }
                                        }

                                        // Quick clear section action
                                        Surface(
                                            shape = RoundedCornerShape(8.dp),
                                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(8.dp))
                                                .clickable {
                                                    val idsToRemove = itemsInGroup.map { it.id }.toSet()
                                                    onDeleteHistory(idsToRemove)
                                                    Toast.makeText(context, "Cleared ${timeGroup.title} history", Toast.LENGTH_SHORT).show()
                                                }
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Filled.DeleteOutline,
                                                    contentDescription = null,
                                                    tint = MaterialTheme.colorScheme.error,
                                                    modifier = Modifier.size(12.dp)
                                                )
                                                Spacer(modifier = Modifier.width(3.dp))
                                                Text(
                                                    text = "Clear",
                                                    fontSize = 11.sp,
                                                    color = MaterialTheme.colorScheme.error,
                                                    fontWeight = FontWeight.Medium
                                                )
                                            }
                                        }
                                    }
                                }

                                items(itemsInGroup, key = { it.id }) { item ->
                                    HistoryItemRow(
                                        item = item,
                                        isSelectionMode = isSelectionMode,
                                        isSelected = selectedIds.contains(item.id),
                                        onClick = {
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
                                        onDeleteClick = {
                                            onDeleteHistory(setOf(item.id))
                                            Toast.makeText(context, "Removed from history", Toast.LENGTH_SHORT).show()
                                        },
                                        onShareClick = {
                                            shareHistoryUrl(context, item.title, item.url)
                                        },
                                        onCopyLinkClick = {
                                            copyUrlToClipboard(context, item.url)
                                        },
                                        onBookmarkClick = {
                                            onAddToBookmarks?.invoke(item.title, item.url)
                                            Toast.makeText(context, "Saved to Bookmarks", Toast.LENGTH_SHORT).show()
                                        },
                                        onSpeedDialClick = {
                                            onAddToSpeedDial?.invoke(item.title, item.url)
                                            Toast.makeText(context, "Added to Speed Dials", Toast.LENGTH_SHORT).show()
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // ==========================================
    // CLEAR BROWSING DATA RANGE DIALOG
    // ==========================================
    if (showClearRangeDialog) {
        var selectedClearOption by remember { mutableStateOf("ALL") }

        AlertDialog(
            onDismissRequest = { showClearRangeDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.DeleteSweep,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = stringResource(R.string.history_clear_range),
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Choose the time range of browsing history to remove:",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    // Option 1: Last 24 Hours
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .clickable { selectedClearOption = "TODAY" },
                        color = if (selectedClearOption == "TODAY") MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = selectedClearOption == "TODAY",
                                onClick = { selectedClearOption = "TODAY" }
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Last 24 Hours (Today)", fontSize = 13.sp, fontWeight = FontWeight.Medium)
                        }
                    }

                    // Option 2: Last 7 Days
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .clickable { selectedClearOption = "WEEK" },
                        color = if (selectedClearOption == "WEEK") MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = selectedClearOption == "WEEK",
                                onClick = { selectedClearOption = "WEEK" }
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Last 7 Days", fontSize = 13.sp, fontWeight = FontWeight.Medium)
                        }
                    }

                    // Option 3: All Time
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .clickable { selectedClearOption = "ALL" },
                        color = if (selectedClearOption == "ALL") MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = selectedClearOption == "ALL",
                                onClick = { selectedClearOption = "ALL" }
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("All Time", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.error)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        when (selectedClearOption) {
                            "ALL" -> onDeleteHistory(null)
                            "TODAY" -> {
                                val oneDayAgo = System.currentTimeMillis() - 24 * 3600 * 1000
                                val toDelete = sortedItems.filter { it.timestamp >= oneDayAgo }.map { it.id }.toSet()
                                onDeleteHistory(toDelete)
                            }
                            "WEEK" -> {
                                val weekAgo = System.currentTimeMillis() - 7 * 24 * 3600 * 1000
                                val toDelete = sortedItems.filter { it.timestamp >= weekAgo }.map { it.id }.toSet()
                                onDeleteHistory(toDelete)
                            }
                        }
                        showClearRangeDialog = false
                        Toast.makeText(context, "History cleared", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.testTag("confirm_clear_all_history")
                ) {
                    Text(
                        text = stringResource(R.string.action_delete),
                        color = MaterialTheme.colorScheme.error,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showClearRangeDialog = false },
                    modifier = Modifier.testTag("cancel_clear_all_history")
                ) {
                    Text(text = stringResource(R.string.dialog_cancel))
                }
            }
        )
    }
}

// ==========================================
// ACTION ICON WITH STYLED BACKGROUND
// ==========================================
@Composable
fun HistoryActionIcon(
    icon: ImageVector,
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
// REDESIGNED HISTORY ITEM ROW (List View)
// ==========================================
@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun HistoryItemRow(
    item: HistoryItem,
    isSelectionMode: Boolean,
    isSelected: Boolean,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onShareClick: () -> Unit,
    onCopyLinkClick: () -> Unit,
    onBookmarkClick: () -> Unit,
    onSpeedDialClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val faviconUrl = "https://www.google.com/s2/favicons?sz=128&domain_url=${item.url.trim()}"
    val formattedTime = remember(item.timestamp) {
        formatHistoryTime(item.timestamp)
    }
    val domain = remember(item.url) {
        extractDomain(item.url)
    }

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
                onClick = onClick,
                onLongClick = onLongClick
            )
            .testTag("history_item_${item.id}"),
        color = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f) else MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(16.dp),
        tonalElevation = if (isSelected) 4.dp else 1.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Selection Checkbox or Website Favicon with Icon Background
            if (isSelectionMode) {
                Checkbox(
                    checked = isSelected,
                    onCheckedChange = { onClick() },
                    colors = CheckboxDefaults.colors(checkedColor = MaterialTheme.colorScheme.primary),
                    modifier = Modifier.padding(end = 6.dp)
                )
            } else {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
                    modifier = Modifier.size(44.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        SubcomposeAsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(faviconUrl)
                                .crossfade(true)
                                .build(),
                            contentDescription = "${item.title} icon",
                            contentScale = ContentScale.Fit,
                            modifier = Modifier.size(24.dp).clip(RoundedCornerShape(4.dp)),
                            loading = {
                                MonogramInitialBadge(title = item.title, domain = domain)
                            },
                            error = {
                                MonogramInitialBadge(title = item.title, domain = domain)
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))
            }

            // Title, Domain Badge, and Time
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 6.dp)
            ) {
                Text(
                    text = item.title.ifBlank { item.url },
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(3.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Domain pill
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
                        modifier = Modifier.clip(RoundedCornerShape(6.dp))
                    ) {
                        Text(
                            text = domain,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }

                    // Time
                    Text(
                        text = formattedTime,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Action Buttons with Icon Backgrounds
            if (!isSelectionMode) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Send / Share
                    HistoryActionIcon(
                        icon = Icons.Filled.Share,
                        contentDescription = "Share",
                        bgColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f),
                        iconTint = MaterialTheme.colorScheme.primary,
                        onClick = onShareClick,
                        modifier = Modifier.size(32.dp)
                    )

                    // Add to Speed Dial / Bookmark
                    HistoryActionIcon(
                        icon = Icons.Filled.Speed,
                        contentDescription = "Add to Speed Dial",
                        bgColor = Color(0xFFFEF3C7),
                        iconTint = Color(0xFFD97706),
                        onClick = onSpeedDialClick,
                        modifier = Modifier.size(32.dp)
                    )

                    // Delete Entry
                    HistoryActionIcon(
                        icon = Icons.Filled.DeleteOutline,
                        contentDescription = "Delete",
                        bgColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.5f),
                        iconTint = MaterialTheme.colorScheme.error,
                        onClick = onDeleteClick,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
        }
    }
}

// ==========================================
// REDESIGNED HISTORY CARD ITEM (Grid / Tablet View)
// ==========================================
@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun HistoryCardItem(
    item: HistoryItem,
    isSelectionMode: Boolean,
    isSelected: Boolean,
    onItemClick: () -> Unit,
    onLongClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onShareClick: () -> Unit,
    onCopyLinkClick: () -> Unit,
    onBookmarkClick: () -> Unit,
    onSpeedDialClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val faviconUrl = "https://www.google.com/s2/favicons?sz=128&domain_url=${item.url.trim()}"
    val formattedTime = remember(item.timestamp) {
        formatHistoryTime(item.timestamp)
    }
    val domain = remember(item.url) {
        extractDomain(item.url)
    }

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
            .testTag("history_item_${item.id}"),
        color = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f) else MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(18.dp),
        tonalElevation = if (isSelected) 4.dp else 2.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            // Header: Favicon + Domain + Selection checkbox
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
                        modifier = Modifier.size(42.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            SubcomposeAsyncImage(
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data(faviconUrl)
                                    .crossfade(true)
                                    .build(),
                                contentDescription = "${item.title} icon",
                                contentScale = ContentScale.Fit,
                                modifier = Modifier.size(24.dp).clip(RoundedCornerShape(4.dp)),
                                loading = {
                                    MonogramInitialBadge(title = item.title, domain = domain)
                                },
                                error = {
                                    MonogramInitialBadge(title = item.title, domain = domain)
                                }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
                        modifier = Modifier.clip(RoundedCornerShape(6.dp))
                    ) {
                        Text(
                            text = domain,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                if (isSelectionMode) {
                    Checkbox(
                        checked = isSelected,
                        onCheckedChange = { onItemClick() },
                        colors = CheckboxDefaults.colors(checkedColor = MaterialTheme.colorScheme.primary)
                    )
                } else {
                    Text(
                        text = formattedTime,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Title & URL
            Text(
                text = item.title.ifBlank { item.url },
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = item.url,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Action Buttons with Icon Backgrounds
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Share
                HistoryActionIcon(
                    icon = Icons.Filled.Share,
                    contentDescription = "Share",
                    bgColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f),
                    iconTint = MaterialTheme.colorScheme.primary,
                    onClick = onShareClick,
                    modifier = Modifier.size(34.dp)
                )

                // Speed Dial
                HistoryActionIcon(
                    icon = Icons.Filled.Speed,
                    contentDescription = "Speed Dial",
                    bgColor = Color(0xFFFEF3C7),
                    iconTint = Color(0xFFD97706),
                    onClick = onSpeedDialClick,
                    modifier = Modifier.size(34.dp)
                )

                // Copy Link
                HistoryActionIcon(
                    icon = Icons.Filled.ContentCopy,
                    contentDescription = "Copy Link",
                    bgColor = MaterialTheme.colorScheme.surfaceVariant,
                    iconTint = MaterialTheme.colorScheme.onSurfaceVariant,
                    onClick = onCopyLinkClick,
                    modifier = Modifier.size(34.dp)
                )

                // Delete
                HistoryActionIcon(
                    icon = Icons.Filled.DeleteOutline,
                    contentDescription = "Delete",
                    bgColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.5f),
                    iconTint = MaterialTheme.colorScheme.error,
                    onClick = onDeleteClick,
                    modifier = Modifier.size(34.dp)
                )
            }
        }
    }
}

// Fallback Monogram Badge
@Composable
private fun MonogramInitialBadge(title: String, domain: String) {
    val initial = remember(title, domain) {
        val clean = if (title.isNotBlank()) title else domain
        clean.take(2).uppercase()
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primaryContainer),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = initial,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

// ==========================================
// TIME & DOMAIN HELPERS
// ==========================================
private enum class HistoryTimeGroup(val title: String) {
    TODAY("Today"),
    YESTERDAY("Yesterday"),
    THIS_WEEK("This Week"),
    OLDER("Older")
}

private fun getHistoryTimeGroup(timestamp: Long): HistoryTimeGroup {
    val now = System.currentTimeMillis()
    val calNow = Calendar.getInstance().apply { timeInMillis = now }
    val calItem = Calendar.getInstance().apply { timeInMillis = timestamp }

    val diffYear = calNow.get(Calendar.YEAR) - calItem.get(Calendar.YEAR)
    val diffDays = (calNow.get(Calendar.DAY_OF_YEAR) - calItem.get(Calendar.DAY_OF_YEAR))

    return when {
        diffYear == 0 && diffDays == 0 -> HistoryTimeGroup.TODAY
        diffYear == 0 && diffDays == 1 -> HistoryTimeGroup.YESTERDAY
        diffYear == 0 && diffDays in 2..7 -> HistoryTimeGroup.THIS_WEEK
        else -> HistoryTimeGroup.OLDER
    }
}

private fun formatHistoryTime(timestamp: Long): String {
    val now = System.currentTimeMillis()
    val diff = now - timestamp
    return when {
        diff < 60 * 1000 -> "Just now"
        diff < 60 * 60 * 1000 -> "${diff / (60 * 1000)}m ago"
        diff < 24 * 60 * 60 * 1000 -> SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date(timestamp))
        else -> SimpleDateFormat("MMM d, h:mm a", Locale.getDefault()).format(Date(timestamp))
    }
}

private fun extractDomain(url: String): String {
    return url.replace("https://", "")
        .replace("http://", "")
        .replace("www.", "")
        .split("/")
        .firstOrNull() ?: url
}

private fun shareHistoryUrl(context: Context, title: String, url: String) {
    val sendIntent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_SUBJECT, title)
        putExtra(Intent.EXTRA_TEXT, "$title\n$url")
        type = "text/plain"
    }
    context.startActivity(Intent.createChooser(sendIntent, "Share link via"))
}

private fun copyUrlToClipboard(context: Context, url: String) {
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip = ClipData.newPlainText("URL", url)
    clipboard.setPrimaryClip(clip)
    Toast.makeText(context, "Link copied to clipboard", Toast.LENGTH_SHORT).show()
}
