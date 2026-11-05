package com.example.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.example.ui.ThemeMode

data class AppThemeColors(
    val mode: ThemeMode,
    val isDark: Boolean,
    val isAmoled: Boolean,
    val background: Color,
    val surface: Color,
    val surfaceVariant: Color,
    val cardBg: Color,
    val cardBorder: Color,
    val searchBarBg: Color,
    val searchBarBorder: Color,
    val iconBg: Color,
    val iconBorder: Color,
    val speedDialAddBg: Color,
    val bookmarkAddBg: Color,
    val toolbarBg: Color,
    val toolbarBorder: Color,
    val primary: Color,
    val secondary: Color,
    val onSurface: Color,
    val onSurfaceVariant: Color
)

val LocalAppTheme = staticCompositionLocalOf {
    AppThemeColors(
        mode = ThemeMode.SYSTEM,
        isDark = false,
        isAmoled = false,
        background = LightBackground,
        surface = LightSurface,
        surfaceVariant = LightSurfaceVariant,
        cardBg = SectionCardBgLight,
        cardBorder = SectionCardOutlineLight,
        searchBarBg = SearchBarBgLight,
        searchBarBorder = SearchBarOutlineLight,
        iconBg = SectionIconBgLight,
        iconBorder = SectionIconOutlineLight,
        speedDialAddBg = SpeedDialAddBgLight,
        bookmarkAddBg = BookmarkAddBgLight,
        toolbarBg = LightSurface,
        toolbarBorder = LightOutline,
        primary = BrowsonRed,
        secondary = BrowsonGreen,
        onSurface = LightOnSurface,
        onSurfaceVariant = LightOnSurfaceVariant
    )
}

private val DarkColorScheme: ColorScheme = darkColorScheme(
    primary = BrowsonRed,
    onPrimary = Color.White,
    primaryContainer = BrowsonRedDark,
    onPrimaryContainer = Color.White,
    secondary = BrowsonGreen,
    onSecondary = Color.White,
    secondaryContainer = BrowsonGreenDark,
    onSecondaryContainer = Color.White,
    background = DarkBackground,
    onBackground = DarkOnSurface,
    surface = DarkSurface,
    onSurface = DarkOnSurface,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = DarkOnSurfaceVariant,
    outline = DarkOutline,
    outlineVariant = DarkOutline
)

private val LightColorScheme: ColorScheme = lightColorScheme(
    primary = BrowsonRed,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFFEBEE),
    onPrimaryContainer = BrowsonRedDark,
    secondary = BrowsonGreen,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFE8F5E9),
    onSecondaryContainer = BrowsonGreenDark,
    background = LightBackground,
    onBackground = LightOnSurface,
    surface = LightSurface,
    onSurface = LightOnSurface,
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = LightOnSurfaceVariant,
    outline = LightOutline,
    outlineVariant = LightOutline
)

private val AmoledColorScheme: ColorScheme = darkColorScheme(
    primary = AmoledPrimary,
    onPrimary = Color.White,
    primaryContainer = Color(0xFF3B070C),
    onPrimaryContainer = Color(0xFFFFB3B8),
    secondary = AmoledSecondary,
    onSecondary = Color.Black,
    secondaryContainer = Color(0xFF003816),
    onSecondaryContainer = Color(0xFF76FFA7),
    background = AmoledBackground,
    onBackground = AmoledOnSurface,
    surface = AmoledSurface,
    onSurface = AmoledOnSurface,
    surfaceVariant = AmoledSurfaceVariant,
    onSurfaceVariant = AmoledOnSurfaceVariant,
    outline = AmoledOutline,
    outlineVariant = AmoledOutline
)

@Composable
fun BrowsonTheme(
    themeMode: ThemeMode = ThemeMode.SYSTEM,
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val systemInDark = isSystemInDarkTheme()
    val resolvedMode = when (themeMode) {
        ThemeMode.SYSTEM -> if (systemInDark) ThemeMode.DARK else ThemeMode.LIGHT
        else -> themeMode
    }

    val isAmoled = resolvedMode == ThemeMode.AMOLED
    val isDark = resolvedMode == ThemeMode.DARK || isAmoled

    val colorScheme = when (resolvedMode) {
        ThemeMode.AMOLED -> AmoledColorScheme
        ThemeMode.DARK -> DarkColorScheme
        else -> LightColorScheme
    }

    val appThemeColors = when (resolvedMode) {
        ThemeMode.AMOLED -> AppThemeColors(
            mode = ThemeMode.AMOLED,
            isDark = true,
            isAmoled = true,
            background = AmoledBackground,
            surface = AmoledSurface,
            surfaceVariant = AmoledSurfaceVariant,
            cardBg = SectionCardBgAmoled,
            cardBorder = SectionCardOutlineAmoled,
            searchBarBg = SearchBarBgAmoled,
            searchBarBorder = SearchBarOutlineAmoled,
            iconBg = SectionIconBgAmoled,
            iconBorder = SectionIconOutlineAmoled,
            speedDialAddBg = SpeedDialAddBgAmoled,
            bookmarkAddBg = BookmarkAddBgAmoled,
            toolbarBg = AmoledBackground,
            toolbarBorder = AmoledOutline,
            primary = AmoledPrimary,
            secondary = AmoledSecondary,
            onSurface = AmoledOnSurface,
            onSurfaceVariant = AmoledOnSurfaceVariant
        )
        ThemeMode.DARK -> AppThemeColors(
            mode = ThemeMode.DARK,
            isDark = true,
            isAmoled = false,
            background = DarkBackground,
            surface = DarkSurface,
            surfaceVariant = DarkSurfaceVariant,
            cardBg = SectionCardBgDark,
            cardBorder = SectionCardOutlineDark,
            searchBarBg = SearchBarBgDark,
            searchBarBorder = SearchBarOutlineDark,
            iconBg = SectionIconBgDark,
            iconBorder = SectionIconOutlineDark,
            speedDialAddBg = SpeedDialAddBgDark,
            bookmarkAddBg = BookmarkAddBgDark,
            toolbarBg = DarkSurface,
            toolbarBorder = DarkOutline,
            primary = BrowsonRed,
            secondary = BrowsonGreen,
            onSurface = DarkOnSurface,
            onSurfaceVariant = DarkOnSurfaceVariant
        )
        else -> AppThemeColors(
            mode = ThemeMode.LIGHT,
            isDark = false,
            isAmoled = false,
            background = LightBackground,
            surface = LightSurface,
            surfaceVariant = LightSurfaceVariant,
            cardBg = SectionCardBgLight,
            cardBorder = SectionCardOutlineLight,
            searchBarBg = SearchBarBgLight,
            searchBarBorder = SearchBarOutlineLight,
            iconBg = SectionIconBgLight,
            iconBorder = SectionIconOutlineLight,
            speedDialAddBg = SpeedDialAddBgLight,
            bookmarkAddBg = BookmarkAddBgLight,
            toolbarBg = LightSurface,
            toolbarBorder = LightOutline,
            primary = BrowsonRed,
            secondary = BrowsonGreen,
            onSurface = LightOnSurface,
            onSurfaceVariant = LightOnSurfaceVariant
        )
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                window.statusBarColor = Color.Transparent.toArgb()
                window.navigationBarColor = appThemeColors.toolbarBg.toArgb()
                val insetsController = WindowCompat.getInsetsController(window, view)
                insetsController.isAppearanceLightStatusBars = !isDark
                insetsController.isAppearanceLightNavigationBars = !isDark
            }
        }
    }

    CompositionLocalProvider(LocalAppTheme provides appThemeColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}
