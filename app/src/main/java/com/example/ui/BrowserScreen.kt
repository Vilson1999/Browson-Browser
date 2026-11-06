package com.example.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.FindInPage
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Speed
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
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.model.BookmarkItem
import com.example.model.SpeedDialItem
import com.example.ui.theme.LocalAppTheme

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun BrowserScreen(
    url: String,
    onUrlChange: (String) -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
    onAddToBookmarks: (String, String) -> Unit = { _, _ -> },
    onAddToSpeedDial: (String, String) -> Unit = { _, _ -> }
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val appTheme = LocalAppTheme.current

    var webViewInstance by remember { mutableStateOf<WebView?>(null) }
    var pageTitle by remember { mutableStateOf(url) }
    var currentDisplayUrl by remember { mutableStateOf(url) }
    var progress by remember { mutableFloatStateOf(0f) }
    var isLoading by remember { mutableStateOf(true) }

    // 3-dot dropdown menu state
    var showMenu by remember { mutableStateOf(false) }

    // Find in page state
    var showFindInPage by remember { mutableStateOf(false) }
    var findQuery by remember { mutableStateOf("") }

    // Editing URL mode state
    var isEditingUrl by remember { mutableStateOf(false) }
    var editedUrlText by remember { mutableStateOf(url) }

    BackHandler {
        if (showFindInPage) {
            showFindInPage = false
            webViewInstance?.clearMatches()
        } else if (webViewInstance?.canGoBack() == true) {
            webViewInstance?.goBack()
        } else {
            onClose()
        }
    }

    // Classic style address bar container styling
    val barBg = when {
        appTheme.isAmoled -> Color(0xE6000000)
        appTheme.isDark -> Color(0xE6000000)
        else -> Color(0xE6FFFFFF)
    }
    val barBorder = when {
        appTheme.isAmoled -> Color(0xFF27272A)
        appTheme.isDark -> Color(0xFF334155)
        else -> Color(0xFFE2E8F0)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
    ) {
        // CLASSIC STYLE ADDRESS BAR: Full length (edge-to-edge / fillMaxWidth) and little narrow (42dp height)
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(42.dp)
                .testTag("classic_top_address_bar"),
            color = barBg,
            border = androidx.compose.foundation.BorderStroke(1.dp, barBorder)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // SSL Lock indicator
                Box(
                    modifier = Modifier
                        .size(26.dp)
                        .clip(CircleShape)
                        .background(
                            if (currentDisplayUrl.startsWith("https://")) {
                                Color(0xFF10B981).copy(alpha = 0.15f)
                            } else {
                                MaterialTheme.colorScheme.surfaceVariant
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Lock,
                        contentDescription = "Secure Connection",
                        tint = if (currentDisplayUrl.startsWith("https://")) Color(0xFF10B981) else appTheme.onSurfaceVariant,
                        modifier = Modifier.size(14.dp)
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                // ADDRESS BAR: Show ONLY URL (not url name / title)
                if (isEditingUrl) {
                    BasicTextField(
                        value = editedUrlText,
                        onValueChange = { editedUrlText = it },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("browser_address_input"),
                        singleLine = true,
                        textStyle = TextStyle(
                            color = appTheme.onSurface,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Normal
                        ),
                        cursorBrush = SolidColor(appTheme.primary),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Uri,
                            imeAction = ImeAction.Go
                        ),
                        keyboardActions = KeyboardActions(
                            onGo = {
                                keyboardController?.hide()
                                isEditingUrl = false
                                val target = editedUrlText.trim()
                                if (target.isNotEmpty()) {
                                    val finalUrl = when {
                                        target.startsWith("http://") || target.startsWith("https://") -> target
                                        target.contains(".") && !target.contains(" ") -> "https://$target"
                                        else -> "https://www.google.com/search?q=${java.net.URLEncoder.encode(target, "UTF-8")}"
                                    }
                                    webViewInstance?.loadUrl(finalUrl)
                                }
                            }
                        )
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                editedUrlText = currentDisplayUrl
                                isEditingUrl = true
                            },
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Text(
                            text = currentDisplayUrl,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Normal,
                            color = appTheme.onSurface,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                // Reload button (compact narrow icon)
                IconButton(
                    onClick = {
                        if (isLoading) {
                            webViewInstance?.stopLoading()
                            isLoading = false
                        } else {
                            webViewInstance?.reload()
                        }
                    },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = if (isLoading) Icons.Filled.Close else Icons.Filled.Refresh,
                        contentDescription = if (isLoading) "Stop Loading" else "Reload Page",
                        tint = appTheme.onSurfaceVariant,
                        modifier = Modifier.size(18.dp)
                    )
                }

                // 3-DOT MORE MENU ICON (replacing the previous 'X' close button)
                Box {
                    IconButton(
                        onClick = { showMenu = true },
                        modifier = Modifier
                            .size(32.dp)
                            .testTag("browser_more_options_button")
                    ) {
                        Icon(
                            imageVector = Icons.Filled.MoreVert,
                            contentDescription = "More Options",
                            tint = appTheme.onSurfaceVariant,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // 3-Dot Dropdown Options
                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false },
                        modifier = Modifier.testTag("browser_options_dropdown")
                    ) {
                        // 1. Share Option
                        DropdownMenuItem(
                            text = { Text("Share", fontSize = 14.sp) },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Filled.Share,
                                    contentDescription = "Share",
                                    tint = appTheme.primary,
                                    modifier = Modifier.size(18.dp)
                                )
                            },
                            onClick = {
                                showMenu = false
                                val shareIntent = Intent().apply {
                                    action = Intent.ACTION_SEND
                                    putExtra(Intent.EXTRA_TEXT, currentDisplayUrl)
                                    putExtra(Intent.EXTRA_SUBJECT, pageTitle)
                                    type = "text/plain"
                                }
                                context.startActivity(Intent.createChooser(shareIntent, "Share URL"))
                            },
                            modifier = Modifier.testTag("menu_option_share")
                        )

                        // 2. Add to Bookmark
                        DropdownMenuItem(
                            text = { Text("Add to Bookmark", fontSize = 14.sp) },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Filled.Bookmark,
                                    contentDescription = "Bookmark",
                                    tint = Color(0xFF16A34A),
                                    modifier = Modifier.size(18.dp)
                                )
                            },
                            onClick = {
                                showMenu = false
                                onAddToBookmarks(pageTitle.ifBlank { currentDisplayUrl }, currentDisplayUrl)
                                Toast.makeText(context, "Added to Bookmarks", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier.testTag("menu_option_add_bookmark")
                        )

                        // 3. Add to Speed Dial
                        DropdownMenuItem(
                            text = { Text("Add to Speed Dial", fontSize = 14.sp) },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Filled.Speed,
                                    contentDescription = "Speed Dial",
                                    tint = Color(0xFFDC2626),
                                    modifier = Modifier.size(18.dp)
                                )
                            },
                            onClick = {
                                showMenu = false
                                onAddToSpeedDial(pageTitle.ifBlank { currentDisplayUrl }, currentDisplayUrl)
                                Toast.makeText(context, "Added to Speed Dial", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier.testTag("menu_option_add_speed_dial")
                        )

                        // 4. Find in Page
                        DropdownMenuItem(
                            text = { Text("Find in Page", fontSize = 14.sp) },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Filled.FindInPage,
                                    contentDescription = "Find",
                                    tint = Color(0xFF2563EB),
                                    modifier = Modifier.size(18.dp)
                                )
                            },
                            onClick = {
                                showMenu = false
                                showFindInPage = true
                            },
                            modifier = Modifier.testTag("menu_option_find_in_page")
                        )

                        // 5. Copy Link
                        DropdownMenuItem(
                            text = { Text("Copy Link", fontSize = 14.sp) },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Filled.ContentCopy,
                                    contentDescription = "Copy",
                                    tint = appTheme.onSurfaceVariant,
                                    modifier = Modifier.size(18.dp)
                                )
                            },
                            onClick = {
                                showMenu = false
                                clipboardManager.setText(AnnotatedString(currentDisplayUrl))
                                Toast.makeText(context, "URL copied to clipboard", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier.testTag("menu_option_copy_link")
                        )

                        // 6. Close to Home
                        DropdownMenuItem(
                            text = { Text("Close Page", fontSize = 14.sp) },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Filled.Close,
                                    contentDescription = "Close",
                                    tint = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.size(18.dp)
                                )
                            },
                            onClick = {
                                showMenu = false
                                onClose()
                            },
                            modifier = Modifier.testTag("menu_option_close")
                        )
                    }
                }
            }
        }

        // FIND IN PAGE FLOATING TOOLBAR
        AnimatedVisibility(
            visible = showFindInPage,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp),
                color = barBg,
                border = androidx.compose.foundation.BorderStroke(1.dp, barBorder)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Filled.FindInPage,
                        contentDescription = null,
                        tint = appTheme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    BasicTextField(
                        value = findQuery,
                        onValueChange = {
                            findQuery = it
                            if (it.isNotEmpty()) {
                                webViewInstance?.findAllAsync(it)
                            } else {
                                webViewInstance?.clearMatches()
                            }
                        },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        textStyle = TextStyle(
                            color = appTheme.onSurface,
                            fontSize = 13.sp
                        ),
                        cursorBrush = SolidColor(appTheme.primary),
                        decorationBox = { innerTextField ->
                            if (findQuery.isEmpty()) {
                                Text(
                                    text = "Find in page...",
                                    fontSize = 13.sp,
                                    color = appTheme.onSurfaceVariant
                                )
                            }
                            innerTextField()
                        }
                    )
                    IconButton(
                        onClick = { webViewInstance?.findNext(false) },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Text("▲", fontSize = 12.sp, color = appTheme.onSurface)
                    }
                    IconButton(
                        onClick = { webViewInstance?.findNext(true) },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Text("▼", fontSize = 12.sp, color = appTheme.onSurface)
                    }
                    IconButton(
                        onClick = {
                            showFindInPage = false
                            findQuery = ""
                            webViewInstance?.clearMatches()
                        },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = "Close Find",
                            tint = appTheme.onSurfaceVariant,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }

        // Web Loading Progress
        if (isLoading && progress < 1f) {
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(2.5.dp),
                color = appTheme.primary,
                trackColor = barBg
            )
        }

        // Android System WebView integration
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .background(MaterialTheme.colorScheme.background)
        ) {
            AndroidView(
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("system_webview_engine"),
                factory = { ctx ->
                    WebView(ctx).apply {
                        settings.javaScriptEnabled = true
                        settings.domStorageEnabled = true
                        settings.loadWithOverviewMode = true
                        settings.useWideViewPort = true
                        settings.cacheMode = WebSettings.LOAD_DEFAULT
                        webViewClient = object : WebViewClient() {
                            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                                super.onPageStarted(view, url, favicon)
                                isLoading = true
                                url?.let {
                                    currentDisplayUrl = it
                                    onUrlChange(it)
                                }
                            }

                            override fun onPageFinished(view: WebView?, url: String?) {
                                super.onPageFinished(view, url)
                                isLoading = false
                                url?.let {
                                    currentDisplayUrl = it
                                    onUrlChange(it)
                                }
                                view?.title?.let { pageTitle = it }
                            }

                            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                                return false
                            }
                        }
                        webChromeClient = object : WebChromeClient() {
                            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                                progress = newProgress / 100f
                                if (newProgress == 100) {
                                    isLoading = false
                                }
                            }

                            override fun onReceivedTitle(view: WebView?, title: String?) {
                                super.onReceivedTitle(view, title)
                                title?.let { pageTitle = it }
                            }
                        }
                        loadUrl(url)
                        webViewInstance = this
                    }
                },
                update = { webView ->
                    if (webView.url != url && url.isNotBlank()) {
                        webView.loadUrl(url)
                    }
                }
            )
        }
    }
}
