package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.LocalAppTheme

@Composable
fun TopAddressBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onSubmit: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val appTheme = LocalAppTheme.current

    // Distinctive search bar container styling adapting to Light, Dark, AMOLED
    val searchBarBg = appTheme.searchBarBg
    val searchOutline = appTheme.searchBarBorder
    val searchLensBg = when {
        appTheme.isAmoled -> Color(0xFF1E1E22)
        appTheme.isDark -> Color(0xFF283347)
        else -> Color(0xFFF1F5F9)
    }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(58.dp)
            .shadow(
                elevation = if (appTheme.isAmoled) 0.dp else 4.dp,
                shape = RoundedCornerShape(29.dp),
                spotColor = Color.Black.copy(alpha = if (appTheme.isDark) 0.35f else 0.08f)
            )
            .clip(RoundedCornerShape(29.dp))
            .border(
                width = if (appTheme.isAmoled) 1.5.dp else 1.dp,
                color = searchOutline,
                shape = RoundedCornerShape(29.dp)
            ),
        color = searchBarBg,
        shape = RoundedCornerShape(29.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left Search Lens with Stylized Container
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = searchLensBg,
                modifier = Modifier.size(38.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Filled.Search,
                        contentDescription = "Search",
                        tint = appTheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(10.dp))

            // Address / Search Input text field
            BasicTextField(
                value = query,
                onValueChange = onQueryChange,
                modifier = Modifier
                    .weight(1f)
                    .testTag("address_bar_input"),
                singleLine = true,
                textStyle = TextStyle(
                    color = appTheme.onSurface,
                    fontSize = 15.sp,
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
                        onSubmit(query)
                    }
                ),
                decorationBox = { innerTextField ->
                    if (query.isEmpty()) {
                        Text(
                            text = stringResource(R.string.search_hint),
                            color = appTheme.onSurfaceVariant,
                            fontSize = 15.sp,
                            maxLines = 1
                        )
                    }
                    innerTextField()
                }
            )

            // Right Actions with Styled Backgrounds
            if (query.isNotEmpty()) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Clear button
                    Surface(
                        shape = CircleShape,
                        color = if (appTheme.isDark) Color(0xFF2A3441) else Color(0xFFE2E8F0),
                        modifier = Modifier.size(32.dp)
                    ) {
                        IconButton(
                            onClick = { onQueryChange("") },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Clear,
                                contentDescription = "Clear input",
                                tint = appTheme.onSurfaceVariant,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    // Go button (primary accent pill)
                    Surface(
                        shape = CircleShape,
                        color = appTheme.primary,
                        modifier = Modifier.size(34.dp)
                    ) {
                        IconButton(
                            onClick = {
                                keyboardController?.hide()
                                onSubmit(query)
                            },
                            modifier = Modifier
                                .size(34.dp)
                                .testTag("address_bar_go_btn")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = "Go",
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            } else {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Voice Search Button with Amber Background
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (appTheme.isAmoled) Color(0xFF251A08)
                        else if (appTheme.isDark) Color(0xFF382915)
                        else Color(0xFFFEF3C7),
                        modifier = Modifier.size(34.dp)
                    ) {
                        IconButton(
                            onClick = { /* Voice input trigger */ },
                            modifier = Modifier
                                .size(34.dp)
                                .testTag("address_bar_voice_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Mic,
                                contentDescription = "Voice Search",
                                tint = if (appTheme.isDark) Color(0xFFFBBF24) else Color(0xFFD97706),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    // QR Code Scanner Button with Sky Blue Background
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (appTheme.isAmoled) Color(0xFF091F2C)
                        else if (appTheme.isDark) Color(0xFF162F44)
                        else Color(0xFFE0F2FE),
                        modifier = Modifier.size(34.dp)
                    ) {
                        IconButton(
                            onClick = { /* QR scan trigger */ },
                            modifier = Modifier
                                .size(34.dp)
                                .testTag("address_bar_qr_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Filled.QrCodeScanner,
                                contentDescription = "Scan QR Code",
                                tint = if (appTheme.isDark) Color(0xFF38BDF8) else Color(0xFF0284C7),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
