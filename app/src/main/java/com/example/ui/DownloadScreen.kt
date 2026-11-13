package com.example.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Android
import androidx.compose.material.icons.filled.AudioFile
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.FolderZip
import androidx.compose.material.icons.filled.InsertDriveFile
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.VideoFile
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.DownloadCategory
import com.example.model.DownloadItem
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun DownloadScreen(
    downloadItems: List<DownloadItem>,
    onPauseOrResume: (String) -> Unit,
    onDeleteDownload: (String) -> Unit,
    onPauseAll: () -> Unit,
    onResumeAll: () -> Unit,
    onClearCompleted: () -> Unit,
    onClearAllDownloads: () -> Unit = {},
    onOpenSettings: () -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var showOverflowMenu by remember { mutableStateOf(false) }
    var selectedCategory by remember { mutableStateOf<DownloadCategory?>(null) }

    BackHandler {
        onClose()
    }

    // Determine which extensions actually exist in downloading or downloaded files
    // User requirement: "when download or downloading the file it show that extention text if file not match the extention the text hidden."
    val existingCategories = remember(downloadItems) {
        val allCategories = listOf(
            DownloadCategory.VIDEO,
            DownloadCategory.AUDIO,
            DownloadCategory.TXT,
            DownloadCategory.PDF,
            DownloadCategory.XML,
            DownloadCategory.APK,
            DownloadCategory.ZIP,
            DownloadCategory.RAR,
            DownloadCategory.TAR,
            DownloadCategory.OTHER
        )

        allCategories.filter { cat ->
            downloadItems.any { item -> matchesCategory(item, cat) }
        }
    }

    // Filtered downloads
    val filteredItems = remember(downloadItems, selectedCategory) {
        if (selectedCategory == null) {
            downloadItems
        } else {
            downloadItems.filter { matchesCategory(it, selectedCategory!!) }
        }
    }

    // Metrics for active downloads:
    // User requirement: "Below row section and above downloading file show left time on right and downloading speed at right and total Downloading file size in center."
    val activeDownloads = remember(downloadItems) {
        downloadItems.filter { it.isDownloading && it.downloadedBytes < it.totalBytes }
    }
    val totalDownloadingSize = remember(activeDownloads) {
        activeDownloads.sumOf { it.totalBytes }
    }
    val totalSpeedBytes = remember(activeDownloads) {
        activeDownloads.sumOf { it.speedBytesPerSec }
    }
    val totalRemainingBytes = remember(activeDownloads) {
        activeDownloads.sumOf { (it.totalBytes - it.downloadedBytes).coerceAtLeast(0L) }
    }
    val remainingSeconds = remember(totalRemainingBytes, totalSpeedBytes) {
        if (totalSpeedBytes > 0) totalRemainingBytes / totalSpeedBytes else 0L
    }

    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            // 1. Top Bar with 3-dot overflow menu
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
                        .padding(horizontal = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(
                        onClick = onClose,
                        modifier = Modifier
                            .size(48.dp)
                            .testTag("download_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Text(
                        text = stringResource(R.string.downloads_title),
                        fontSize = 19.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = {
                                onClearAllDownloads()
                                Toast.makeText(context, "Downloads cleared", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier
                                .size(48.dp)
                                .testTag("download_clear_all_top_button")
                        ) {
                            Icon(
                                imageVector = Icons.Filled.DeleteSweep,
                                contentDescription = "Clear all downloads",
                                tint = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        // 3 dots on top which have some menu
                        Box {
                            IconButton(
                                onClick = { showOverflowMenu = true },
                                modifier = Modifier
                                    .size(48.dp)
                                    .testTag("download_overflow_menu_btn")
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.MoreVert,
                                    contentDescription = "More download options",
                                    tint = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.size(24.dp)
                                )
                            }

                            DropdownMenu(
                                expanded = showOverflowMenu,
                                onDismissRequest = { showOverflowMenu = false }
                            ) {
                                DropdownMenuItem(
                                    text = { Text("Pause all") },
                                    leadingIcon = {
                                        Icon(Icons.Filled.Pause, contentDescription = null, modifier = Modifier.size(18.dp))
                                    },
                                    onClick = {
                                        onPauseAll()
                                        showOverflowMenu = false
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("Resume all") },
                                    leadingIcon = {
                                        Icon(Icons.Filled.PlayArrow, contentDescription = null, modifier = Modifier.size(18.dp))
                                    },
                                    onClick = {
                                        onResumeAll()
                                        showOverflowMenu = false
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("Clear completed") },
                                    leadingIcon = {
                                        Icon(Icons.Filled.DeleteOutline, contentDescription = null, modifier = Modifier.size(18.dp))
                                    },
                                    onClick = {
                                        onClearCompleted()
                                        showOverflowMenu = false
                                        Toast.makeText(context, "Completed downloads cleared", Toast.LENGTH_SHORT).show()
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("Clear all downloads", color = MaterialTheme.colorScheme.error) },
                                    leadingIcon = {
                                        Icon(Icons.Filled.Delete, contentDescription = null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(18.dp))
                                    },
                                    onClick = {
                                        onClearAllDownloads()
                                        showOverflowMenu = false
                                        Toast.makeText(context, "All downloads cleared", Toast.LENGTH_SHORT).show()
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("Download settings") },
                                    leadingIcon = {
                                        Icon(Icons.Filled.Folder, contentDescription = null, modifier = Modifier.size(18.dp))
                                    },
                                    onClick = {
                                        showOverflowMenu = false
                                        onOpenSettings()
                                    }
                                )
                            }
                        }
                    }
                }
            }

            // 2. A ROW SECTION AT TOP OF DOWNLOAD FILE IN PILL SHAPE
            // (video, audio, txt, pdf, xml, apk, zip, rar, tar, other)
            // when download or downloading the file it show that extention text if file not match the extention the text hidden.
            if (existingCategories.isNotEmpty()) {
                val scrollState = rememberScrollState()
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 10.dp)
                        .horizontalScroll(scrollState)
                        .padding(horizontal = 14.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // "All" filter pill
                    ExtensionPill(
                        text = "all",
                        isSelected = selectedCategory == null,
                        onClick = { selectedCategory = null },
                        testTag = "pill_all"
                    )

                    // Dynamically shown matching extension pills only
                    existingCategories.forEach { category ->
                        val isSelected = selectedCategory == category
                        ExtensionPill(
                            text = category.label,
                            isSelected = isSelected,
                            onClick = {
                                selectedCategory = if (isSelected) null else category
                            },
                            testTag = "pill_${category.label}"
                        )
                    }
                }
            }

            // 3. BELOW ROW SECTION AND ABOVE DOWNLOADING FILE:
            // "Below row section and above downloading file show left time on right and downloading speed at right and total Downloading file size in center."
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 6.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f),
                        shape = RoundedCornerShape(18.dp)
                    ),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                shape = RoundedCornerShape(18.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Left: status tag / indicator
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.weight(0.9f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(
                                    if (activeDownloads.isNotEmpty()) {
                                        MaterialTheme.colorScheme.primaryContainer
                                    } else {
                                        MaterialTheme.colorScheme.surfaceVariant
                                    }
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Download,
                                contentDescription = null,
                                tint = if (activeDownloads.isNotEmpty()) {
                                    MaterialTheme.colorScheme.primary
                                } else {
                                    MaterialTheme.colorScheme.onSurfaceVariant
                                },
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Text(
                            text = if (activeDownloads.isNotEmpty()) "${activeDownloads.size} active" else "Idle",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // Center: TOTAL DOWNLOADING FILE SIZE (CENTER)
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.weight(1.3f)
                    ) {
                        Text(
                            text = if (activeDownloads.isNotEmpty()) {
                                formatBytes(totalDownloadingSize)
                            } else {
                                "0 MB"
                            },
                            fontSize = 15.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.primary,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = stringResource(R.string.download_total_size),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    }

                    // Right: DOWNLOADING SPEED AT RIGHT AND LEFT TIME ON RIGHT
                    Column(
                        horizontalAlignment = Alignment.End,
                        modifier = Modifier.weight(1.3f)
                    ) {
                        // Downloading speed at right
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Speed,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.secondary,
                                modifier = Modifier.size(13.dp)
                            )
                            Text(
                                text = if (activeDownloads.isNotEmpty() && totalSpeedBytes > 0) {
                                    "${formatSpeed(totalSpeedBytes)}"
                                } else {
                                    "0 KB/s"
                                },
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        // Left time on right
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Timer,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.outline,
                                modifier = Modifier.size(12.dp)
                            )
                            Text(
                                text = if (activeDownloads.isNotEmpty() && remainingSeconds > 0) {
                                    formatTime(remainingSeconds)
                                } else {
                                    "--"
                                },
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // 4. DOWNLOADING / DOWNLOADED FILES LIST
            if (filteredItems.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.surfaceVariant),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Download,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                        Text(
                            text = stringResource(R.string.download_empty),
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "No files match the selected category.",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .testTag("download_items_list"),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(filteredItems, key = { it.id }) { item ->
                        DownloadItemCard(
                            item = item,
                            onTogglePause = { onPauseOrResume(item.id) },
                            onDelete = { onDeleteDownload(item.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ExtensionPill(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    testTag: String
) {
    Surface(
        modifier = Modifier
            .height(36.dp)
            .clip(RoundedCornerShape(18.dp))
            .clickable(onClick = onClick)
            .border(
                width = 1.dp,
                color = if (isSelected) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.7f)
                },
                shape = RoundedCornerShape(18.dp)
            )
            .testTag(testTag),
        color = if (isSelected) {
            MaterialTheme.colorScheme.primary
        } else {
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        },
        shape = RoundedCornerShape(18.dp)
    ) {
        Box(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                fontSize = 12.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) {
                    MaterialTheme.colorScheme.onPrimary
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                }
            )
        }
    }
}

@Composable
private fun DownloadItemCard(
    item: DownloadItem,
    onTogglePause: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isComplete = item.downloadedBytes >= item.totalBytes
    val progress = if (item.totalBytes > 0) {
        (item.downloadedBytes.toFloat() / item.totalBytes.toFloat()).coerceIn(0f, 1f)
    } else 0f
    val percentInt = (progress * 100).toInt()

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.45f),
                shape = RoundedCornerShape(18.dp)
            )
            .testTag("download_card_${item.id}"),
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(18.dp),
        tonalElevation = 1.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Top Row: Extension badge + File name + Actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // File type icon badge (icon not text)
                val (fileIcon, iconTint, iconBg) = when (item.extension.lowercase()) {
                    "video", "mp4", "mkv", "webm", "avi", "mov" -> Triple(
                        Icons.Filled.VideoFile,
                        Color(0xFFE53935),
                        Color(0xFFE53935).copy(alpha = 0.15f)
                    )
                    "audio", "mp3", "wav", "flac", "aac", "ogg" -> Triple(
                        Icons.Filled.AudioFile,
                        Color(0xFF8E24AA),
                        Color(0xFF8E24AA).copy(alpha = 0.15f)
                    )
                    "pdf" -> Triple(
                        Icons.Filled.PictureAsPdf,
                        Color(0xFFD32F2F),
                        Color(0xFFD32F2F).copy(alpha = 0.15f)
                    )
                    "apk" -> Triple(
                        Icons.Filled.Android,
                        Color(0xFF2E7D32),
                        Color(0xFF2E7D32).copy(alpha = 0.15f)
                    )
                    "zip", "rar", "tar", "gz", "7z" -> Triple(
                        Icons.Filled.FolderZip,
                        Color(0xFFF57C00),
                        Color(0xFFF57C00).copy(alpha = 0.15f)
                    )
                    "txt", "xml", "json", "html", "doc", "docx" -> Triple(
                        Icons.Filled.Description,
                        Color(0xFF1976D2),
                        Color(0xFF1976D2).copy(alpha = 0.15f)
                    )
                    else -> Triple(
                        Icons.Filled.InsertDriveFile,
                        MaterialTheme.colorScheme.primary,
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                    )
                }

                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(iconBg)
                        .testTag("download_file_icon_${item.id}"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = fileIcon,
                        contentDescription = item.extension,
                        tint = iconTint,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                // File name & url
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = item.fileName,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = if (isComplete) {
                            "Completed • ${formatBytes(item.totalBytes)}"
                        } else {
                            "${formatBytes(item.downloadedBytes)} / ${formatBytes(item.totalBytes)}"
                        },
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Control buttons: Pause/Resume (if downloading) and Delete
                if (!isComplete) {
                    IconButton(
                        onClick = onTogglePause,
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("download_pause_resume_${item.id}")
                    ) {
                        Icon(
                            imageVector = if (item.isDownloading) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                            contentDescription = if (item.isDownloading) "Pause" else "Resume",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                } else {
                    Icon(
                        imageVector = Icons.Filled.CheckCircle,
                        contentDescription = "Completed",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .size(24.dp)
                            .padding(end = 4.dp)
                    )
                }

                IconButton(
                    onClick = onDelete,
                    modifier = Modifier
                        .size(36.dp)
                        .testTag("download_delete_${item.id}")
                ) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = "Delete download",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            // If not complete: show progress bar & metrics (speed & left time)
            if (!isComplete) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "$percentInt%",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )

                        val itemRemainingBytes = (item.totalBytes - item.downloadedBytes).coerceAtLeast(0L)
                        val itemRemainingSec = if (item.speedBytesPerSec > 0) itemRemainingBytes / item.speedBytesPerSec else 0L

                        Text(
                            text = if (item.isDownloading) {
                                "${formatSpeed(item.speedBytesPerSec)} • ${formatTime(itemRemainingSec)}"
                            } else {
                                "Paused"
                            },
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

private fun matchesCategory(item: DownloadItem, category: DownloadCategory): Boolean {
    val ext = item.extension.lowercase()
    val fileNameExt = item.fileName.substringAfterLast(".", "").lowercase()
    return when (category) {
        DownloadCategory.ALL -> true
        DownloadCategory.VIDEO -> ext in category.extensions || fileNameExt in category.extensions
        DownloadCategory.AUDIO -> ext in category.extensions || fileNameExt in category.extensions
        DownloadCategory.TXT -> ext in category.extensions || fileNameExt in category.extensions
        DownloadCategory.PDF -> ext in category.extensions || fileNameExt in category.extensions
        DownloadCategory.XML -> ext in category.extensions || fileNameExt in category.extensions
        DownloadCategory.APK -> ext in category.extensions || fileNameExt in category.extensions
        DownloadCategory.ZIP -> ext in category.extensions || fileNameExt in category.extensions
        DownloadCategory.RAR -> ext in category.extensions || fileNameExt in category.extensions
        DownloadCategory.TAR -> ext in category.extensions || fileNameExt in category.extensions
        DownloadCategory.OTHER -> {
            val allStandard = DownloadCategory.values().flatMap { it.extensions }
            ext !in allStandard && fileNameExt !in allStandard
        }
    }
}

private fun formatBytes(bytes: Long): String {
    if (bytes <= 0) return "0 B"
    val kb = bytes / 1024.0
    val mb = kb / 1024.0
    val gb = mb / 1024.0
    return when {
        gb >= 1.0 -> String.format(Locale.US, "%.1f GB", gb)
        mb >= 1.0 -> String.format(Locale.US, "%.1f MB", mb)
        kb >= 1.0 -> String.format(Locale.US, "%.1f KB", kb)
        else -> "$bytes B"
    }
}

private fun formatSpeed(bytesPerSec: Long): String {
    if (bytesPerSec <= 0) return "0 KB/s"
    val kb = bytesPerSec / 1024.0
    val mb = kb / 1024.0
    return if (mb >= 1.0) {
        String.format(Locale.US, "%.1f MB/s", mb)
    } else {
        String.format(Locale.US, "%.0f KB/s", kb)
    }
}

private fun formatTime(seconds: Long): String {
    if (seconds <= 0) return "--"
    val mins = seconds / 60
    val secs = seconds % 60
    val hours = mins / 60
    return when {
        hours > 0 -> "${hours}h ${mins % 60}m"
        mins > 0 -> "${mins}m ${secs}s"
        else -> "${secs}s"
    }
}
