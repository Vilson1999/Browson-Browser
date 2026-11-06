package com.example.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsTopHeight
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.example.R
import com.example.model.BookmarkItem
import com.example.model.HistoryItem
import com.example.model.SpeedDialItem
import com.example.ui.components.BookmarkSection
import com.example.ui.components.SpeedDialSection
import com.example.ui.components.TopAddressBar
import com.example.ui.theme.LocalAppTheme
import kotlinx.coroutines.launch
import java.net.URI

enum class HomeSectionTab(val title: String) {
    SPEED_DIAL("Speed Dial"),
    BOOKMARK("Bookmark"),
    RECENT("Recent")
}

@OptIn(ExperimentalFoundationApi::class)
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

    val isLightMode = !appTheme.isDark && !appTheme.isAmoled
    val homeBgModifier = when {
        appTheme.uiGradientBrush != null -> Modifier.background(appTheme.uiGradientBrush!!)
        isLightMode -> Modifier.background(Color(0xFFF8FAFC))
        else -> Modifier.background(Brush.verticalGradient(wallpaperColors))
    }

    // Pager for horizontal swipe between Speed Dial (0), Bookmark (1), Recent (2)
    val pagerState = rememberPagerState(initialPage = 0, pageCount = { 3 })
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = modifier
            .fillMaxSize()
            .then(homeBgModifier)
    ) {
        // Status bar area: colour matches the app searchbar colour
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .windowInsetsTopHeight(WindowInsets.statusBars)
                .background(appTheme.searchBarBg)
        )

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
                    .padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(14.dp))

                // TOP PILL ADDRESS BAR WITH MIC & QR
                TopAddressBar(
                    query = searchQuery,
                    onQueryChange = onSearchQueryChange,
                    onSubmit = onSearchSubmit,
                    modifier = Modifier.testTag("home_top_address_bar")
                )

                Spacer(modifier = Modifier.height(14.dp))

                // 3 NARROW LONG PILL ICONS (Speed Dial, Bookmark, Recent)
                HomeThreePillMenuBar(
                    selectedPage = pagerState.currentPage,
                    onSelectPage = { pageIndex ->
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(pageIndex)
                        }
                    },
                    isDark = appTheme.isDark || appTheme.isAmoled
                )

                Spacer(modifier = Modifier.height(14.dp))

                // SWIPEABLE CONTENT PAGER (Scroll left or right to change menu)
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    beyondViewportPageCount = 2
                ) { page ->
                    val pageScrollState = rememberScrollState()

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(pageScrollState)
                    ) {
                        when (page) {
                            0 -> {
                                // SPEED DIAL SECTION (Card layout removed, plus icon removed)
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 4.dp, vertical = 6.dp)
                                        .testTag("speed_dial_container")
                                ) {
                                    SpeedDialSection(
                                        items = speedDials,
                                        onItemClick = onSpeedDialClick,
                                        onAddClick = onAddSpeedDialClick,
                                        onEditClick = onEditSpeedDial,
                                        onRemoveClick = onRemoveSpeedDial,
                                        showAddButton = false
                                    )
                                }
                            }
                            1 -> {
                                // BOOKMARK SECTION (Card layout removed, plus icon removed)
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 4.dp, vertical = 6.dp)
                                        .testTag("bookmark_container")
                                ) {
                                    BookmarkSection(
                                        items = bookmarks,
                                        onItemClick = onBookmarkClick,
                                        onAddClick = onAddBookmarkClick,
                                        onEditClick = onEditBookmark,
                                        onDeleteClick = onDeleteBookmark,
                                        onRemoveClick = onRemoveBookmark,
                                        showAddButton = false
                                    )
                                }
                            }
                            2 -> {
                                // RECENT VISITED WEBSITES AS ICONS (Card layout removed)
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 4.dp, vertical = 6.dp)
                                        .testTag("recent_websites_container")
                                ) {
                                    RecentVisitedIconsSection(
                                        recentHistory = recentHistory,
                                        onItemClick = onHistoryItemClick,
                                        onOpenFullHistory = onOpenHistory
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))
                    }
                }
            }
        }
    }
}

/**
 * 3 narrow long pill icons below search bar:
 * 1. Speed Dial: Red colour text & lite red colour pill icon
 * 2. Bookmark: Green colour text & light green pill icon
 * 3. Recent: Dark teal colour text & lite teal pill icon
 * If not selected: pill stays 90% white (or themed white equivalent in dark mode)
 */
@Composable
private fun HomeThreePillMenuBar(
    selectedPage: Int,
    onSelectPage: (Int) -> Unit,
    isDark: Boolean,
    modifier: Modifier = Modifier
) {
    // Exact colors specified:
    // 1. Speed Dial
    val redText = Color(0xFFDC2626)
    val redLitePill = if (isDark) Color(0xFF451111) else Color(0xFFFFE4E6)
    val redBorder = if (isDark) Color(0xFFEF4444).copy(alpha = 0.4f) else Color(0xFFFDA4AF)

    // 2. Bookmark
    val greenText = Color(0xFF16A34A)
    val greenLitePill = if (isDark) Color(0xFF0F391F) else Color(0xFFDCFCE7)
    val greenBorder = if (isDark) Color(0xFF22C55E).copy(alpha = 0.4f) else Color(0xFF86EFAC)

    // 3. Recent (Blue colour text and lite blue pill icon)
    val blueText = Color(0xFF2563EB) // Vibrant Blue
    val liteBluePill = if (isDark) Color(0xFF0C243B) else Color(0xFFDBEAFE) // Lite Blue
    val blueBorder = if (isDark) Color(0xFF3B82F6).copy(alpha = 0.4f) else Color(0xFF93C5FD)

    // Pill icon when not selected: 10% dark colour on white (e.g. #E5E7EB) on light theme, 90% black on dark or amoled theme
    val unselectedBg = if (isDark) Color(0xE6000000) else Color(0xFFE5E7EB)
    val unselectedText = if (isDark) Color(0xFFCBD5E1) else Color(0xFF475569)
    val unselectedBorder = if (isDark) Color(0x33FFFFFF) else Color(0xFFD1D5DB)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .testTag("home_three_pill_menu_bar"),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 1. SPEED DIAL PILL
        NarrowPillItem(
            title = "Speed Dial",
            icon = Icons.Filled.Speed,
            isSelected = selectedPage == 0,
            selectedTextColor = redText,
            selectedPillColor = redLitePill,
            selectedBorderColor = redBorder,
            unselectedBg = unselectedBg,
            unselectedTextColor = unselectedText,
            unselectedBorder = unselectedBorder,
            onClick = { onSelectPage(0) },
            testTag = "pill_menu_speed_dial",
            modifier = Modifier.weight(1f)
        )

        // 2. BOOKMARK PILL
        NarrowPillItem(
            title = "Bookmark",
            icon = Icons.Filled.Bookmark,
            isSelected = selectedPage == 1,
            selectedTextColor = greenText,
            selectedPillColor = greenLitePill,
            selectedBorderColor = greenBorder,
            unselectedBg = unselectedBg,
            unselectedTextColor = unselectedText,
            unselectedBorder = unselectedBorder,
            onClick = { onSelectPage(1) },
            testTag = "pill_menu_bookmark",
            modifier = Modifier.weight(1f)
        )

        // 3. RECENT PILL
        NarrowPillItem(
            title = "Recent",
            icon = Icons.Filled.History,
            isSelected = selectedPage == 2,
            selectedTextColor = blueText,
            selectedPillColor = liteBluePill,
            selectedBorderColor = blueBorder,
            unselectedBg = unselectedBg,
            unselectedTextColor = unselectedText,
            unselectedBorder = unselectedBorder,
            onClick = { onSelectPage(2) },
            testTag = "pill_menu_recent",
            modifier = Modifier.weight(1f)
        )
    }
}

/**
 * Narrow, long pill item designed with smooth animated color changes and pill icon styling
 */
@Composable
private fun NarrowPillItem(
    title: String,
    icon: ImageVector,
    isSelected: Boolean,
    selectedTextColor: Color,
    selectedPillColor: Color,
    selectedBorderColor: Color,
    unselectedBg: Color,
    unselectedTextColor: Color,
    unselectedBorder: Color,
    onClick: () -> Unit,
    testTag: String,
    modifier: Modifier = Modifier
) {
    val targetBg = if (isSelected) selectedPillColor else unselectedBg
    val targetText = if (isSelected) selectedTextColor else unselectedTextColor
    val targetBorder = if (isSelected) selectedBorderColor else unselectedBorder

    val animatedBg by animateColorAsState(targetValue = targetBg, animationSpec = tween(220), label = "pillBg")
    val animatedText by animateColorAsState(targetValue = targetText, animationSpec = tween(220), label = "pillText")
    val animatedBorder by animateColorAsState(targetValue = targetBorder, animationSpec = tween(220), label = "pillBorder")

    Surface(
        modifier = modifier
            .height(38.dp)
            .clip(RoundedCornerShape(20.dp))
            .clickable(onClick = onClick)
            .border(
                width = if (isSelected) 1.2.dp else 1.dp,
                color = animatedBorder,
                shape = RoundedCornerShape(20.dp)
            )
            .testTag(testTag),
        shape = RoundedCornerShape(20.dp),
        color = animatedBg,
        shadowElevation = if (isSelected) 2.dp else 0.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = animatedText,
                modifier = Modifier.size(16.dp)
            )

            Spacer(modifier = Modifier.width(6.dp))

            Text(
                text = title,
                fontSize = 12.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = animatedText,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

/**
 * Modern Recent Visited Websites rendered as rich square-rounded website icons in a 5-column grid.
 */
@Composable
private fun RecentVisitedIconsSection(
    recentHistory: List<HistoryItem>,
    onItemClick: (HistoryItem) -> Unit,
    onOpenFullHistory: () -> Unit,
    modifier: Modifier = Modifier
) {
    val appTheme = LocalAppTheme.current
    val isDark = appTheme.isDark || appTheme.isAmoled

    Column(modifier = modifier.fillMaxWidth()) {
        // Section Header
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
                    color = if (appTheme.isAmoled) Color(0xFF0C243B) else if (isDark) Color(0xFF1E3A8A).copy(alpha = 0.35f) else Color(0xFFDBEAFE),
                    modifier = Modifier.size(32.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Filled.History,
                            contentDescription = null,
                            tint = if (isDark) Color(0xFF60A5FA) else Color(0xFF2563EB),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "Recent Visited",
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
                            text = "${recentHistory.size}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            if (recentHistory.isNotEmpty()) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .clickable(onClick = onOpenFullHistory)
                ) {
                    Text(
                        text = "History",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        if (recentHistory.isEmpty()) {
            // Empty state
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 28.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Surface(
                        shape = CircleShape,
                        color = if (isDark) Color(0xFF1E293B) else Color(0xFFF1F5F9),
                        modifier = Modifier.size(48.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Filled.OpenInBrowser,
                                contentDescription = null,
                                tint = if (isDark) Color(0xFF64748B) else Color(0xFF94A3B8),
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "No recent websites visited yet",
                        style = MaterialTheme.typography.bodyMedium,
                        color = appTheme.onSurfaceVariant
                    )
                    Text(
                        text = "Browse pages or search to see icons here",
                        style = MaterialTheme.typography.labelSmall,
                        color = appTheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )
                }
            }
        } else {
            // Deduplicate visited websites by host/URL to give a clean icon grid
            val uniqueHistory = recentHistory.distinctBy { it.url }.take(15)
            val columns = 5
            val totalCells = uniqueHistory.size
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
                            if (index < uniqueHistory.size) {
                                val item = uniqueHistory[index]
                                RecentWebsiteIconItem(
                                    item = item,
                                    onClick = { onItemClick(item) },
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
}

/**
 * Individual recent website icon item styled cleanly with squircle container and favicon
 */
@Composable
private fun RecentWebsiteIconItem(
    item: HistoryItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val appTheme = LocalAppTheme.current
    val host = try {
        val uri = URI(item.url)
        uri.host?.removePrefix("www.") ?: item.url
    } catch (e: Exception) {
        item.url
    }
    val faviconUrl = "https://www.google.com/s2/favicons?sz=64&domain_url=${item.url}"
    val isDark = appTheme.isDark || appTheme.isAmoled
    val initial = (item.title.ifBlank { host }).firstOrNull()?.uppercaseChar()?.toString() ?: "W"

    val iconBgColor = appTheme.iconBg
    val iconOutline = appTheme.iconBorder

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
                    .clickable(onClick = onClick)
                    .border(
                        width = 1.dp,
                        color = iconOutline,
                        shape = RoundedCornerShape(14.dp)
                    )
                    .testTag("recent_icon_${host.lowercase()}"),
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
                            .data(faviconUrl)
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
                                    .background(if (isDark) Color(0xFF0F766E) else Color(0xFF14B8A6)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = initial,
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
                                    .background(if (isDark) Color(0xFF0F766E) else Color(0xFF14B8A6)),
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
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = item.title.ifBlank { host },
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center
        )
    }
}
