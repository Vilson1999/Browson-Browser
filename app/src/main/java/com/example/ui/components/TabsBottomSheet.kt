package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tab
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.example.model.BrowserTab
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun TabsBottomSheet(
    tabs: List<BrowserTab>,
    incognitoTabs: List<BrowserTab>,
    isIncognitoMode: Boolean,
    activeTabId: String,
    showClosedTabPopup: Boolean,
    onSelectTab: (String) -> Unit,
    onCloseTab: (String) -> Unit,
    onNewTab: (Boolean) -> Unit,
    onToggleIncognito: (Boolean) -> Unit,
    onUndoCloseTab: () -> Unit,
    onDismissClosedTabPopup: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    isDarkTheme: Boolean = isSystemInDarkTheme()
) {
    val activeList = if (isIncognitoMode) incognitoTabs else tabs
    val listState = rememberLazyListState()

    // Dynamic color tokens for dark/light mode (80% of white for tab menu background, never pure white)
    val panelBgColor = if (isDarkTheme) Color(0xFF101411) else com.example.ui.theme.EightyPercentWhite
    val dragHandleColor = if (isDarkTheme) Color.White.copy(alpha = 0.35f) else Color(0xFF94A3B8)
    val pillContainerBg = if (isDarkTheme) Color(0xFF1E1E1E) else Color(0xFFE2E8F0)
    val pillContainerBorder = if (isDarkTheme) Color.White.copy(alpha = 0.18f) else Color(0xFFCBD5E1)
    val pillActiveBg = if (isDarkTheme) Color(0xFF2E2E2E) else Color(0xFFCBD5E1)
    val iconTintActive = if (isDarkTheme) Color.White else Color(0xFF0F172A)
    val iconTintInactive = if (isDarkTheme) Color(0xFF888888) else Color(0xFF64748B)
    val actionBtnBg = if (isDarkTheme) Color(0xFF1E1E1E) else Color(0xFFE2E8F0)
    val actionBtnBorder = if (isDarkTheme) Color.White.copy(alpha = 0.18f) else Color(0xFFCBD5E1)
    val actionBtnTint = if (isDarkTheme) Color.White else Color(0xFF1E293B)

    // 8. Auto-scroll so selected tab stays in center
    LaunchedEffect(activeTabId, isIncognitoMode, activeList.size) {
        val selectedIndex = activeList.indexOfFirst { it.id == activeTabId }
        if (selectedIndex >= 0) {
            listState.animateScrollToItem(index = selectedIndex)
        }
    }

    // Auto-dismiss undo popup after 4 seconds
    LaunchedEffect(showClosedTabPopup) {
        if (showClosedTabPopup) {
            delay(4000)
            onDismissClosedTabPopup()
        }
    }

    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val screenHeight = maxHeight
        val screenWidth = maxWidth
        val panelHeight = screenHeight * 0.40f // 2. Tab menu open in 40% of screen

        // Transparent dismiss area above 40% panel - clicking closes menu without altering home background UI
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) { onDismiss() }
        )

        // 40% Bottom Panel positioned right above the toolbar (Toolbar always visible - Req 11)
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(panelHeight)
                .align(Alignment.BottomCenter)
                .shadow(elevation = 16.dp, shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                // 10. Swipe down anywhere on tabs menu closes the tab menu
                .pointerInput(Unit) {
                    detectVerticalDragGestures { _, dragAmount ->
                        if (dragAmount > 20) {
                            onDismiss()
                        }
                    }
                }
                .testTag("tabs_menu_panel"),
            color = panelBgColor,
            tonalElevation = 6.dp
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 8.dp, bottom = 6.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    // Top Drag Handle
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .width(38.dp)
                                .height(4.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(dragHandleColor)
                        )
                    }

                    // 1. TABS POSTER AT TOP (Takes main vertical space)
                    // 9. 2 tabs show full but the 3rd tab shows only 25%
                    val cardWidth = if (screenWidth > 100.dp) (screenWidth - 36.dp) / 2.25f else 140.dp
                    val isSingleTab = activeList.size <= 1

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = if (isSingleTab) Alignment.Center else Alignment.CenterStart
                    ) {
                        LazyRow(
                            state = listState,
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = if (isSingleTab) Arrangement.Center else Arrangement.spacedBy(12.dp),
                            contentPadding = PaddingValues(horizontal = 14.dp)
                        ) {
                            itemsIndexed(activeList, key = { _, item -> item.id }) { index, tab ->
                                val isSelected = tab.id == activeTabId
                                TabPosterView(
                                    tab = tab,
                                    isSelected = isSelected,
                                    cardWidth = cardWidth,
                                    onClick = { onSelectTab(tab.id) },
                                    onClose = { onCloseTab(tab.id) },
                                    modifier = Modifier.testTag("tab_poster_${tab.id}")
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // 1. BOTTOM MENU BAR
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // 5. Left side: SHOW ICONS ONLY for normal tab and incognito tab
                        Row(
                            modifier = Modifier
                                .height(40.dp)
                                .clip(RoundedCornerShape(20.dp))
                                .background(pillContainerBg)
                                .border(
                                    width = 1.dp,
                                    color = pillContainerBorder,
                                    shape = RoundedCornerShape(20.dp)
                                )
                                .padding(2.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Normal tab icon only
                            Surface(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .clickable { onToggleIncognito(false) },
                                color = if (!isIncognitoMode) pillActiveBg else Color.Transparent,
                                shape = CircleShape
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Filled.Tab,
                                        contentDescription = "Normal Tabs",
                                        tint = if (!isIncognitoMode) iconTintActive else iconTintInactive,
                                        modifier = Modifier.size(19.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(2.dp))

                            // Incognito tab icon only
                            Surface(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .clickable { onToggleIncognito(true) },
                                color = if (isIncognitoMode) pillActiveBg else Color.Transparent,
                                shape = CircleShape
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Filled.VisibilityOff,
                                        contentDescription = "Incognito Tabs",
                                        tint = if (isIncognitoMode) Color(0xFFCE93D8) else iconTintInactive,
                                        modifier = Modifier.size(19.dp)
                                    )
                                }
                            }
                        }

                        // 4. Move plus icon in center of the tab menu screen
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.linearGradient(
                                        listOf(
                                            Color(0xFF9C27B0),
                                            Color(0xFF673AB7)
                                        )
                                    )
                                )
                                .clickable { onNewTab(isIncognitoMode) }
                                .testTag("tab_menu_add_btn"),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Add,
                                contentDescription = "New Tab",
                                tint = Color.White,
                                modifier = Modifier.size(28.dp)
                            )
                        }

                        // 6. Right side: Undo icon with 3-dot icon
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Undo icon
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(actionBtnBg)
                                    .border(
                                        width = 1.dp,
                                        color = actionBtnBorder,
                                        shape = CircleShape
                                    )
                                    .clickable { onUndoCloseTab() }
                                    .testTag("tab_menu_undo_btn"),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.Undo,
                                    contentDescription = "Undo Closed Tab",
                                    tint = actionBtnTint,
                                    modifier = Modifier.size(19.dp)
                                )
                            }

                            // 3-dot icon
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(actionBtnBg)
                                    .border(
                                        width = 1.dp,
                                        color = actionBtnBorder,
                                        shape = CircleShape
                                    )
                                    .clickable { onDismiss() },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.MoreVert,
                                    contentDescription = "Options",
                                    tint = actionBtnTint,
                                    modifier = Modifier.size(19.dp)
                                )
                            }
                        }
                    }
                }

                // 7. Show small popup when close tabs with undo text
                AnimatedVisibility(
                    visible = showClosedTabPopup,
                    enter = slideInVertically { it } + fadeIn(),
                    exit = slideOutVertically { it } + fadeOut(),
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 60.dp)
                ) {
                    Surface(
                        modifier = Modifier
                            .shadow(8.dp, shape = RoundedCornerShape(20.dp))
                            .clip(RoundedCornerShape(20.dp)),
                        color = MaterialTheme.colorScheme.inverseSurface,
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(horizontal = 14.dp, vertical = 6.dp),
                            horizontalArrangement = Arrangement.spacedBy(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Tab closed",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.inverseOnSurface
                            )

                            Button(
                                onClick = onUndoCloseTab,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF9C27B0),
                                    contentColor = Color.White
                                ),
                                shape = RoundedCornerShape(14.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 2.dp),
                                modifier = Modifier.height(28.dp)
                            ) {
                                Text(
                                    text = "Undo",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TabPosterView(
    tab: BrowserTab,
    isSelected: Boolean,
    cardWidth: androidx.compose.ui.unit.Dp,
    onClick: () -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    val offsetY = remember { Animatable(0f) }
    var isDismissed by remember { mutableStateOf(false) }

    val faviconUrl = "https://www.google.com/s2/favicons?sz=128&domain_url=${tab.url.ifEmpty { "google.com" }}"

    // Glow gradient border for selected tab
    val selectedBorderBrush = Brush.sweepGradient(
        listOf(
            Color(0xFF9C27B0), // Purple
            Color(0xFF3F51B5), // Indigo
            Color(0xFF2196F3), // Blue
            Color(0xFF9C27B0)
        )
    )

    if (isDismissed) return

    Box(
        modifier = modifier
            .width(cardWidth)
            .fillMaxHeight()
            .offset { IntOffset(0, offsetY.value.roundToInt()) }
            // 3. Swipe up to remove tabs
            .pointerInput(tab.id) {
                detectVerticalDragGestures(
                    onDragEnd = {
                        if (offsetY.value < -80f) {
                            coroutineScope.launch {
                                offsetY.animateTo(-400f, animationSpec = tween(180))
                                isDismissed = true
                                onClose()
                            }
                        } else {
                            coroutineScope.launch {
                                offsetY.animateTo(0f, animationSpec = tween(120))
                            }
                        }
                    },
                    onDragCancel = {
                        coroutineScope.launch {
                            offsetY.animateTo(0f)
                        }
                    },
                    onVerticalDrag = { _, dragAmount ->
                        if (dragAmount < 0 || offsetY.value < 0) {
                            coroutineScope.launch {
                                offsetY.snapTo(offsetY.value + dragAmount)
                            }
                        }
                    }
                )
            }
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(18.dp))
                .border(
                    width = if (isSelected) 2.5.dp else 1.dp,
                    brush = if (isSelected) selectedBorderBrush else Brush.linearGradient(
                        listOf(
                            MaterialTheme.colorScheme.outline.copy(alpha = 0.35f),
                            MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)
                        )
                    ),
                    shape = RoundedCornerShape(18.dp)
                )
                .clickable(onClick = onClick),
            color = MaterialTheme.colorScheme.surfaceVariant,
            shape = RoundedCornerShape(18.dp),
            shadowElevation = if (isSelected) 6.dp else 2.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(6.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Top Row: Website icon on left, Website name in center (NO home text!), Close 'X' icon on right corner
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp, vertical = 2.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Website icon / favicon
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        SubcomposeAsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(faviconUrl)
                                .crossfade(true)
                                .build(),
                            contentDescription = "${tab.title} Icon",
                            contentScale = ContentScale.Fit,
                            modifier = Modifier
                                .size(18.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            loading = {
                                Icon(
                                    imageVector = Icons.Filled.Language,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(16.dp)
                                )
                            },
                            error = {
                                Icon(
                                    imageVector = Icons.Filled.Language,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        )

                        Spacer(modifier = Modifier.width(6.dp))

                        // Website name show on tab poster top (remove home text on it)
                        val displayName = if (tab.isHome || tab.url.isBlank()) {
                            "New Tab"
                        } else {
                            tab.url.removePrefix("https://").removePrefix("http://").removePrefix("www.").split("/").firstOrNull() ?: tab.title
                        }

                        Text(
                            text = displayName,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    // Close 'X' icon at right corner
                    Box(
                        modifier = Modifier
                            .size(22.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.8f))
                            .clickable {
                                coroutineScope.launch {
                                    offsetY.animateTo(-400f, animationSpec = tween(180))
                                    isDismissed = true
                                    onClose()
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = "Close Tab",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Website preview show on tab poster:
                // If on home screen show home screen preview; if any website show website preview!
                // (Tab poster bottom text removed completely)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    if (tab.isHome || tab.url.isBlank()) {
                        HomeScreenPreview()
                    } else {
                        WebsitePreview(tab = tab)
                    }
                }
            }
        }
    }
}

@Composable
fun HomeScreenPreview(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(10.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(6.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Mini Pill Address Bar
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(16.dp),
            shape = RoundedCornerShape(8.dp),
            color = MaterialTheme.colorScheme.surfaceVariant
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Filled.Search,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(9.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.55f)
                        .height(3.dp)
                        .background(
                            MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.35f),
                            RoundedCornerShape(2.dp)
                        )
                )
            }
        }

        // Mini 5 Speed Dials Row
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            Box(
                modifier = Modifier
                    .width(36.dp)
                    .height(3.dp)
                    .background(
                        MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.45f),
                        RoundedCornerShape(2.dp)
                    )
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                val speedColors = listOf(0xFF4285F4, 0xFFFF0000, 0xFF2D3748, 0xFF181717, 0xFFFF4500)
                speedColors.forEach { color ->
                    Box(
                        modifier = Modifier
                            .size(13.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color(color))
                    )
                }
            }
        }

        // Mini 5 Bookmarks Row
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            Box(
                modifier = Modifier
                    .width(30.dp)
                    .height(3.dp)
                    .background(
                        MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.45f),
                        RoundedCornerShape(2.dp)
                    )
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                val bookmarkColors = listOf(0xFF3DDC84, 0xFF0A9928, 0xFFFF6600, 0xFF6366F1, 0xFF06B6D4)
                bookmarkColors.forEach { color ->
                    Box(
                        modifier = Modifier
                            .size(13.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color(color))
                    )
                }
            }
        }
    }
}

@Composable
fun WebsitePreview(tab: BrowserTab, modifier: Modifier = Modifier) {
    val faviconUrl = "https://www.google.com/s2/favicons?sz=128&domain_url=${tab.url.ifEmpty { "google.com" }}"
    val domain = tab.url.removePrefix("https://").removePrefix("http://").removePrefix("www.").split("/").firstOrNull() ?: tab.title

    Column(
        modifier = modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(10.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(6.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Mini Browser Address Bar with lock icon
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(15.dp),
            shape = RoundedCornerShape(7.dp),
            color = MaterialTheme.colorScheme.surfaceVariant
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Filled.Lock,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.size(8.dp)
                )
                Spacer(modifier = Modifier.width(3.dp))
                Text(
                    text = domain,
                    fontSize = 7.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        // Central Web Page Content Preview
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(vertical = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            SubcomposeAsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(faviconUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .size(24.dp)
                    .clip(RoundedCornerShape(6.dp)),
                loading = {
                    Icon(
                        imageVector = Icons.Filled.Language,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                },
                error = {
                    Icon(
                        imageVector = Icons.Filled.Language,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            )

            Spacer(modifier = Modifier.height(3.dp))

            Text(
                text = tab.title.ifEmpty { domain },
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center
            )
        }

        // Simulated webpage content lines
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .height(3.dp)
                    .background(
                        MaterialTheme.colorScheme.outline.copy(alpha = 0.4f),
                        RoundedCornerShape(2.dp)
                    )
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.6f)
                    .height(3.dp)
                    .background(
                        MaterialTheme.colorScheme.outline.copy(alpha = 0.25f),
                        RoundedCornerShape(2.dp)
                    )
            )
        }
    }
}
