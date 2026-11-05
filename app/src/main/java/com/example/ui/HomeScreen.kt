package com.example.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.model.BookmarkItem
import com.example.model.SpeedDialItem
import com.example.ui.components.BookmarkSection
import com.example.ui.components.SpeedDialSection
import com.example.ui.components.TopAddressBar

import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import com.example.ui.theme.SectionCardBgDark
import com.example.ui.theme.SectionCardBgLight
import com.example.ui.theme.SectionCardOutlineDark
import com.example.ui.theme.SectionCardOutlineLight

@Composable
fun HomeScreen(
    searchQuery: String,
    speedDials: List<SpeedDialItem>,
    bookmarks: List<BookmarkItem>,
    onSearchQueryChange: (String) -> Unit,
    onSearchSubmit: (String) -> Unit,
    onSpeedDialClick: (SpeedDialItem) -> Unit,
    onAddSpeedDialClick: () -> Unit,
    onEditSpeedDial: (SpeedDialItem) -> Unit,
    onRemoveSpeedDial: (String) -> Unit,
    onBookmarkClick: (BookmarkItem) -> Unit,
    onAddBookmarkClick: () -> Unit,
    onEditBookmark: (BookmarkItem) -> Unit,
    onDeleteBookmark: (String) -> Unit,
    onRemoveBookmark: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    val isDark = isSystemInDarkTheme()
    val sectionCardBg = if (isDark) SectionCardBgDark else SectionCardBgLight
    val sectionCardBorder = if (isDark) SectionCardOutlineDark else SectionCardOutlineLight

    Column(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(horizontal = 16.dp)
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(4.dp))

        // Top Pill-shaped Address Bar with Large Icon
        TopAddressBar(
            query = searchQuery,
            onQueryChange = onSearchQueryChange,
            onSubmit = onSearchSubmit,
            modifier = Modifier.testTag("home_top_address_bar")
        )

        Spacer(modifier = Modifier.height(10.dp))

        // 1. SPEED DIALS SECTION (at top, icon shape, plus icon at last)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    width = 1.dp,
                    color = sectionCardBorder,
                    shape = RoundedCornerShape(18.dp)
                ),
            colors = CardDefaults.cardColors(
                containerColor = sectionCardBg
            ),
            shape = RoundedCornerShape(18.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 10.dp)
            ) {
                SpeedDialSection(
                    items = speedDials,
                    onItemClick = onSpeedDialClick,
                    onAddClick = onAddSpeedDialClick,
                    onEditClick = onEditSpeedDial,
                    onRemoveClick = onRemoveSpeedDial
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // 2. BOOKMARKS SECTION (below speed dials, icon shape, plus icon at last)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    width = 1.dp,
                    color = sectionCardBorder,
                    shape = RoundedCornerShape(18.dp)
                ),
            colors = CardDefaults.cardColors(
                containerColor = sectionCardBg
            ),
            shape = RoundedCornerShape(18.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 10.dp)
            ) {
                BookmarkSection(
                    items = bookmarks,
                    onItemClick = onBookmarkClick,
                    onAddClick = onAddBookmarkClick,
                    onEditClick = onEditBookmark,
                    onDeleteClick = onDeleteBookmark,
                    onRemoveClick = onRemoveBookmark
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}
