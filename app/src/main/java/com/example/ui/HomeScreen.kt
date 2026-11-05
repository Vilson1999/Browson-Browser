package com.example.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.ui.graphics.Brush
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SmartDisplay
import androidx.compose.material.icons.filled.Tag
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.R
import com.example.model.BookmarkItem
import com.example.model.HistoryItem
import com.example.model.SpeedDialItem
import com.example.ui.components.BookmarkSection
import com.example.ui.components.SpeedDialSection
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.IconButton
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.ui.components.TopAddressBar
import com.example.ui.theme.LocalAppTheme
import java.net.URI

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
    modifier: Modifier = Modifier,
    recentHistory: List<HistoryItem> = emptyList(),
    onHistoryItemClick: (HistoryItem) -> Unit = {},
    onOpenBookmarkMenu: () -> Unit = {},
    onOpenHistory: () -> Unit = {},
    onOpenDownloads: () -> Unit = {},
    onOpenSettings: () -> Unit = {}
) {
    val scrollState = rememberScrollState()
    val appTheme = LocalAppTheme.current
    val sectionCardBg = appTheme.cardBg
    val sectionCardBorder = appTheme.cardBorder
    val isSystemDark = isSystemInDarkTheme()
    val isDarkWallpaper = when (appTheme.wallpaperMode) {
        "LIGHT" -> false
        "DARK" -> true
        else -> appTheme.isDark || isSystemDark
    }

    val wallpaperPreset = com.example.ui.theme.WallpaperStylePresets.find { it.id == appTheme.wallpaperStyle }
        ?: com.example.ui.theme.WallpaperStylePresets.first()
    val wallpaperColors = if (isDarkWallpaper) wallpaperPreset.darkColors else wallpaperPreset.lightColors

    val homeBackgroundBrush = appTheme.uiGradientBrush ?: Brush.verticalGradient(wallpaperColors)
    var selectedCategoryFilter by remember { mutableStateOf("All") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(homeBackgroundBrush)
    ) {
        // TOP APP BAR - Matching BookmarkScreen & HistoryScreen design language
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = appTheme.topBarBg,
            tonalElevation = 2.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                        .padding(horizontal = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Left: Logo in circular badge + "Browson" + "Vilson Sonkar"
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier.size(40.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Image(
                                    painter = painterResource(id = R.drawable.ic_browson_logo_with_blue_center),
                                    contentDescription = "Browson",
                                    modifier = Modifier.size(26.dp).clip(CircleShape)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Column {
                            Text(
                                text = "Browson",
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                color = appTheme.onSurface
                            )
                            Text(
                                text = "Vilson Sonkar",
                                fontWeight = FontWeight.Medium,
                                fontSize = 10.sp,
                                color = appTheme.primary
                            )
                        }
                    }

                    // Right: Circular action buttons for Bookmarks, History, Downloads, Settings
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier.size(38.dp)
                        ) {
                            IconButton(onClick = onOpenBookmarkMenu) {
                                Icon(Icons.Filled.Bookmark, contentDescription = "Bookmarks", tint = appTheme.onSurface, modifier = Modifier.size(18.dp))
                            }
                        }
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier.size(38.dp)
                        ) {
                            IconButton(onClick = onOpenHistory) {
                                Icon(Icons.Filled.History, contentDescription = "History", tint = appTheme.onSurface, modifier = Modifier.size(18.dp))
                            }
                        }
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier.size(38.dp)
                        ) {
                            IconButton(onClick = onOpenDownloads) {
                                Icon(Icons.Filled.Download, contentDescription = "Downloads", tint = appTheme.onSurface, modifier = Modifier.size(18.dp))
                            }
                        }
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier.size(38.dp)
                        ) {
                            IconButton(onClick = onOpenSettings) {
                                Icon(Icons.Filled.Settings, contentDescription = "Settings", tint = appTheme.onSurface, modifier = Modifier.size(18.dp))
                            }
                        }
                    }
                }

                // Filter chips row matching BookmarkScreen & HistoryScreen
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    listOf(
                        Triple("All", Icons.Filled.Explore, speedDials.size + bookmarks.size),
                        Triple("Speed Dials", Icons.Filled.Speed, speedDials.size),
                        Triple("Bookmarks", Icons.Filled.Bookmark, bookmarks.size),
                        Triple("Recent History", Icons.Filled.History, recentHistory.size)
                    ).forEach { (label, icon, count) ->
                        val isSelected = selectedCategoryFilter == label
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedCategoryFilter = label },
                            label = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(label, fontSize = 12.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal)
                                    if (count > 0) {
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "$count",
                                            fontSize = 10.sp,
                                            color = if (isSelected) appTheme.primary else appTheme.onSurfaceVariant.copy(alpha = 0.7f),
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            },
                            leadingIcon = {
                                Icon(icon, contentDescription = null, modifier = Modifier.size(14.dp))
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = appTheme.primary.copy(alpha = 0.15f),
                                selectedLabelColor = appTheme.primary,
                                selectedLeadingIconColor = appTheme.primary
                            )
                        )
                    }
                }
            }
        }

        // Responsive container for Mobile & Tablet (Tab)
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f),
            contentAlignment = Alignment.TopCenter
        ) {
            val maxWidth = maxWidth
            val contentMaxWidth = if (maxWidth > 600.dp) 680.dp else maxWidth

            Column(
                modifier = Modifier
                    .widthIn(max = contentMaxWidth)
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
                    .verticalScroll(scrollState),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(10.dp))

            // 2. TOP PILL ADDRESS BAR WITH MIC & QR
            TopAddressBar(
                query = searchQuery,
                onQueryChange = onSearchQueryChange,
                onSubmit = onSearchSubmit,
                modifier = Modifier.testTag("home_top_address_bar")
            )

            Spacer(modifier = Modifier.height(12.dp))

            // 3. SEARCH ENGINE SHORTCUTS (Google, YouTube, Reddit, Wikipedia, GitHub)
            SearchEnginePills(
                onEngineSelected = { urlPrefix ->
                    if (searchQuery.isNotBlank()) {
                        onSearchSubmit(searchQuery)
                    } else {
                        onSearchSubmit(urlPrefix)
                    }
                },
                appTheme = appTheme
            )

            Spacer(modifier = Modifier.height(14.dp))

            // 4. QUICK NAVIGATION HUB (Bookmarks, History, Downloads, Settings)
            HomeQuickHub(
                bookmarksCount = bookmarks.size,
                historyCount = recentHistory.size,
                onOpenBookmarks = onOpenBookmarkMenu,
                onOpenHistory = onOpenHistory,
                onOpenDownloads = onOpenDownloads,
                onOpenSettings = onOpenSettings,
                appTheme = appTheme
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 5. SPEED DIALS CARD (Modernized squircle grid with add button)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        width = if (appTheme.isAmoled) 1.5.dp else 1.dp,
                        color = sectionCardBorder,
                        shape = RoundedCornerShape(22.dp)
                    )
                    .shadow(
                        elevation = if (appTheme.isAmoled) 0.dp else 2.dp,
                        shape = RoundedCornerShape(22.dp),
                        ambientColor = Color.Black.copy(alpha = 0.05f),
                        spotColor = Color.Black.copy(alpha = 0.08f)
                    ),
                colors = CardDefaults.cardColors(containerColor = sectionCardBg),
                shape = RoundedCornerShape(22.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 12.dp)
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

            Spacer(modifier = Modifier.height(14.dp))

            // 6. BOOKMARKS CARD (Modernized squircle grid)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        width = if (appTheme.isAmoled) 1.5.dp else 1.dp,
                        color = sectionCardBorder,
                        shape = RoundedCornerShape(22.dp)
                    )
                    .shadow(
                        elevation = if (appTheme.isAmoled) 0.dp else 2.dp,
                        shape = RoundedCornerShape(22.dp),
                        ambientColor = Color.Black.copy(alpha = 0.05f),
                        spotColor = Color.Black.copy(alpha = 0.08f)
                    ),
                colors = CardDefaults.cardColors(containerColor = sectionCardBg),
                shape = RoundedCornerShape(22.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 12.dp)
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

            // 7. JUMP BACK IN / RECENT ACTIVITY SECTION (If user has browsing history)
            if (recentHistory.isNotEmpty()) {
                Spacer(modifier = Modifier.height(14.dp))
                RecentActivityCard(
                    recentHistory = recentHistory,
                    onItemClick = onHistoryItemClick,
                    onViewAllClick = onOpenHistory,
                    cardBg = sectionCardBg,
                    cardBorder = sectionCardBorder,
                    appTheme = appTheme
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 8. PRIVACY & SECURITY FOOTER PILL
            SecurityStatusPill(appTheme = appTheme)

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}
}

/**
 * Modern Brand Hero Header displaying Browson logo, app name, and status.
 */
@Composable
private fun HomeBrandHeader(
    appTheme: com.example.ui.theme.AppThemeColors,
    modifier: Modifier = Modifier
) {
    val logoContainerBg = when {
        appTheme.isAmoled -> Color(0xFF141416)
        appTheme.isDark -> Color(0xFF1E293B)
        else -> Color(0xFFE2E8F0)
    }

    val shieldBg = when {
        appTheme.isAmoled -> Color(0xFF04180A)
        appTheme.isDark -> Color(0xFF0F2E1E)
        else -> Color(0xFFDCFCE7)
    }

    val shieldBorder = when {
        appTheme.isAmoled -> Color(0xFF0D421D)
        appTheme.isDark -> Color(0xFF166534)
        else -> Color(0xFF86EFAC)
    }

    val shieldText = when {
        appTheme.isAmoled -> Color(0xFF00E676)
        appTheme.isDark -> Color(0xFF4ADE80)
        else -> Color(0xFF15803D)
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = logoContainerBg,
                modifier = Modifier
                    .size(38.dp)
                    .shadow(if (appTheme.isAmoled) 0.dp else 3.dp, RoundedCornerShape(12.dp))
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_browson_logo_blue_center),
                        contentDescription = "Browson Logo",
                        modifier = Modifier.size(28.dp)
                    )
                }
            }

            Column {
                Text(
                    text = "Browson",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    ),
                    color = appTheme.onSurface
                )
                Text(
                    text = "Smart & Private Browser",
                    style = MaterialTheme.typography.labelSmall,
                    color = appTheme.onSurfaceVariant
                )
            }
        }

        // Live Shield Protection Badge
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = shieldBg,
            border = androidx.compose.foundation.BorderStroke(1.dp, shieldBorder)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Surface(
                    shape = CircleShape,
                    color = Color(0xFF10B981),
                    modifier = Modifier.size(7.dp)
                ) {}
                Text(
                    text = "Shield Active",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = shieldText
                )
            }
        }
    }
}

/**
 * 4 Quick Feature Hub cards (Bookmarks, History, Downloads, Settings)
 */
@Composable
private fun HomeQuickHub(
    bookmarksCount: Int,
    historyCount: Int,
    onOpenBookmarks: () -> Unit,
    onOpenHistory: () -> Unit,
    onOpenDownloads: () -> Unit,
    onOpenSettings: () -> Unit,
    appTheme: com.example.ui.theme.AppThemeColors,
    modifier: Modifier = Modifier
) {
    val isAmoled = appTheme.isAmoled
    val isDark = appTheme.isDark

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        QuickHubItem(
            title = "Bookmarks",
            subtitle = if (bookmarksCount > 0) "$bookmarksCount saved" else "Manage",
            icon = Icons.Filled.Bookmark,
            iconTint = if (isAmoled) Color(0xFF00E676) else if (isDark) Color(0xFF34D399) else Color(0xFF059669),
            iconBg = if (isAmoled) Color(0xFF062B14) else if (isDark) Color(0xFF064E3B) else Color(0xFFD1FAE5),
            testTag = "quick_hub_bookmarks",
            onClick = onOpenBookmarks,
            modifier = Modifier.weight(1f),
            appTheme = appTheme
        )

        QuickHubItem(
            title = "History",
            subtitle = if (historyCount > 0) "$historyCount items" else "Recent",
            icon = Icons.Filled.History,
            iconTint = if (isAmoled) Color(0xFF38BDF8) else if (isDark) Color(0xFF60A5FA) else Color(0xFF2563EB),
            iconBg = if (isAmoled) Color(0xFF0A2338) else if (isDark) Color(0xFF1E3A8A) else Color(0xFFDBEAFE),
            testTag = "quick_hub_history",
            onClick = onOpenHistory,
            modifier = Modifier.weight(1f),
            appTheme = appTheme
        )

        QuickHubItem(
            title = "Downloads",
            subtitle = "Files",
            icon = Icons.Filled.Download,
            iconTint = if (isAmoled) Color(0xFFC084FC) else if (isDark) Color(0xFFA78BFA) else Color(0xFF7C3AED),
            iconBg = if (isAmoled) Color(0xFF2A0D47) else if (isDark) Color(0xFF4C1D95) else Color(0xFFEDE9FE),
            testTag = "quick_hub_downloads",
            onClick = onOpenDownloads,
            modifier = Modifier.weight(1f),
            appTheme = appTheme
        )

        QuickHubItem(
            title = "Settings",
            subtitle = "Options",
            icon = Icons.Filled.Settings,
            iconTint = if (isAmoled) Color(0xFFFBBF24) else if (isDark) Color(0xFFFBBF24) else Color(0xFFD97706),
            iconBg = if (isAmoled) Color(0xFF2B1605) else if (isDark) Color(0xFF78350F) else Color(0xFFFEF3C7),
            testTag = "quick_hub_settings",
            onClick = onOpenSettings,
            modifier = Modifier.weight(1f),
            appTheme = appTheme
        )
    }
}

@Composable
private fun QuickHubItem(
    title: String,
    subtitle: String,
    icon: ImageVector,
    iconTint: Color,
    iconBg: Color,
    testTag: String,
    onClick: () -> Unit,
    appTheme: com.example.ui.theme.AppThemeColors,
    modifier: Modifier = Modifier
) {
    val cardBg = when {
        appTheme.isAmoled -> Color(0xFF0B0B0D)
        appTheme.isDark -> Color(0xFF1E242B)
        else -> Color(0xFFF8FAFC)
    }

    val cardBorder = when {
        appTheme.isAmoled -> Color(0xFF222226)
        appTheme.isDark -> Color(0xFF334155)
        else -> Color(0xFFE2E8F0)
    }

    Surface(
        modifier = modifier
            .testTag(testTag)
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        color = cardBg,
        border = androidx.compose.foundation.BorderStroke(1.dp, cardBorder)
    ) {
        Column(
            modifier = Modifier
                .padding(vertical = 10.dp, horizontal = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = iconBg,
                modifier = Modifier.size(36.dp)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = iconTint,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                color = appTheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                color = appTheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

/**
 * Search engine and quick hub pills
 */
@Composable
private fun SearchEnginePills(
    onEngineSelected: (String) -> Unit,
    appTheme: com.example.ui.theme.AppThemeColors,
    modifier: Modifier = Modifier
) {
    val isAmoled = appTheme.isAmoled
    val isDark = appTheme.isDark

    val engines = listOf(
        SearchEngineItem(
            name = "Google",
            url = "https://www.google.com",
            icon = Icons.Filled.Language,
            color = Color(0xFF4285F4),
            bgColor = if (isAmoled) Color(0xFF091C30) else if (isDark) Color(0xFF1E3A8A) else Color(0xFFDBEAFE)
        ),
        SearchEngineItem(
            name = "YouTube",
            url = "https://www.youtube.com",
            icon = Icons.Filled.SmartDisplay,
            color = Color(0xFFEF4444),
            bgColor = if (isAmoled) Color(0xFF260808) else if (isDark) Color(0xFF7F1D1D) else Color(0xFFFEE2E2)
        ),
        SearchEngineItem(
            name = "Wikipedia",
            url = "https://www.wikipedia.org",
            icon = Icons.Filled.Explore,
            color = Color(0xFF64748B),
            bgColor = if (isAmoled) Color(0xFF18181B) else if (isDark) Color(0xFF334155) else Color(0xFFF1F5F9)
        ),
        SearchEngineItem(
            name = "Reddit",
            url = "https://www.reddit.com",
            icon = Icons.Filled.Tag,
            color = Color(0xFFFF5722),
            bgColor = if (isAmoled) Color(0xFF2E0F05) else if (isDark) Color(0xFF7C2D12) else Color(0xFFFFEDD5)
        ),
        SearchEngineItem(
            name = "GitHub",
            url = "https://github.com",
            icon = Icons.Filled.Code,
            color = if (isDark) Color(0xFFF8FAFC) else Color(0xFF1E293B),
            bgColor = if (isAmoled) Color(0xFF18181B) else if (isDark) Color(0xFF334155) else Color(0xFFE2E8F0)
        )
    )

    val pillContainerBg = when {
        isAmoled -> Color(0xFF0E0E10)
        isDark -> Color(0xFF1E242B)
        else -> Color(0xFFF1F5F9)
    }

    val pillContainerBorder = when {
        isAmoled -> Color(0xFF222226)
        isDark -> Color(0xFF334155)
        else -> Color(0xFFE2E8F0)
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        engines.forEach { engine ->
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = pillContainerBg,
                border = androidx.compose.foundation.BorderStroke(1.dp, pillContainerBorder),
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .clickable { onEngineSelected(engine.url) }
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Surface(
                        shape = CircleShape,
                        color = engine.bgColor,
                        modifier = Modifier.size(20.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = engine.icon,
                                contentDescription = engine.name,
                                tint = engine.color,
                                modifier = Modifier.size(13.dp)
                            )
                        }
                    }

                    Text(
                        text = engine.name,
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium),
                        color = appTheme.onSurface
                    )
                }
            }
        }
    }
}

private data class SearchEngineItem(
    val name: String,
    val url: String,
    val icon: ImageVector,
    val color: Color,
    val bgColor: Color
)

/**
 * Recent Activity ("Jump Back In") card
 */
@Composable
private fun RecentActivityCard(
    recentHistory: List<HistoryItem>,
    onItemClick: (HistoryItem) -> Unit,
    onViewAllClick: () -> Unit,
    cardBg: Color,
    cardBorder: Color,
    appTheme: com.example.ui.theme.AppThemeColors,
    modifier: Modifier = Modifier
) {
    val isAmoled = appTheme.isAmoled
    val isDark = appTheme.isDark

    Card(
        modifier = modifier
            .fillMaxWidth()
            .border(
                width = if (isAmoled) 1.5.dp else 1.dp,
                color = cardBorder,
                shape = RoundedCornerShape(22.dp)
            )
            .shadow(if (isAmoled) 0.dp else 2.dp, RoundedCornerShape(22.dp)),
        colors = CardDefaults.cardColors(containerColor = cardBg),
        shape = RoundedCornerShape(22.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        shape = CircleShape,
                        color = if (isAmoled) Color(0xFF0C243B) else if (isDark) Color(0xFF1E3A8A) else Color(0xFFDBEAFE),
                        modifier = Modifier.size(28.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Filled.History,
                                contentDescription = "Recent History",
                                tint = if (isDark) Color(0xFF60A5FA) else Color(0xFF2563EB),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    Text(
                        text = "Jump Back In",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = appTheme.onSurface
                    )
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier.clip(RoundedCornerShape(12.dp)).clickable(onClick = onViewAllClick)
                ) {
                    Text(
                        text = "View All",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                        color = appTheme.primary,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Recent visits list
            recentHistory.take(4).forEach { item ->
                val host = try {
                    val uri = URI(item.url)
                    uri.host ?: item.url
                } catch (e: Exception) {
                    item.url
                }
                val faviconUrl = "https://www.google.com/s2/favicons?sz=64&domain_url=${item.url}"

                val itemBg = when {
                    isAmoled -> Color(0xFF101012)
                    isDark -> Color(0xFF1A222D)
                    else -> Color(0xFFF8FAFC)
                }

                val itemBorder = when {
                    isAmoled -> Color(0xFF222226)
                    isDark -> Color(0xFF2D3748)
                    else -> Color(0xFFE2E8F0)
                }

                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 3.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { onItemClick(item) },
                    shape = RoundedCornerShape(12.dp),
                    color = itemBg,
                    border = androidx.compose.foundation.BorderStroke(0.6.dp, itemBorder)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 10.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (isDark) Color(0xFF2D3748) else Color(0xFFE2E8F0),
                            modifier = Modifier.size(28.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                AsyncImage(
                                    model = faviconUrl,
                                    contentDescription = item.title,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = item.title.ifBlank { host },
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                                color = appTheme.onSurface,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = host,
                                style = MaterialTheme.typography.labelSmall,
                                color = appTheme.onSurfaceVariant,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "Open",
                            tint = appTheme.onSurfaceVariant,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}

/**
 * Security Status footer pill showing ad/tracker protection status.
 */
@Composable
private fun SecurityStatusPill(
    appTheme: com.example.ui.theme.AppThemeColors,
    modifier: Modifier = Modifier
) {
    val pillBg = when {
        appTheme.isAmoled -> Color(0xFF051209)
        appTheme.isDark -> Color(0xFF151E28)
        else -> Color(0xFFF0FDF4)
    }

    val pillBorder = when {
        appTheme.isAmoled -> Color(0xFF0F361C)
        appTheme.isDark -> Color(0xFF1E293B)
        else -> Color(0xFFDCFCE7)
    }

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = pillBg,
        border = androidx.compose.foundation.BorderStroke(1.dp, pillBorder)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Surface(
                shape = CircleShape,
                color = if (appTheme.isDark) Color(0xFF064E3B) else Color(0xFFBBF7D0),
                modifier = Modifier.size(24.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Filled.Security,
                        contentDescription = "Security Shield",
                        tint = if (appTheme.isDark) Color(0xFF34D399) else Color(0xFF16A34A),
                        modifier = Modifier.size(14.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = "Browson Privacy Shield Active • Ad Blocking & HTTPS",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium),
                color = if (appTheme.isAmoled) Color(0xFF00E676) else if (appTheme.isDark) Color(0xFF94A3B8) else Color(0xFF15803D),
                textAlign = TextAlign.Center
            )
        }
    }
}
