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
import androidx.compose.ui.graphics.Brush
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
    val topBarBg: Color,
    val topBarBorder: Color,
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
    val onSurfaceVariant: Color,
    val reduceAnimations: Boolean = false,
    val animationScale: Float = 0.75f,
    val uiGradientId: String? = null,
    val uiGradientColors: List<Color>? = null,
    val wallpaperMode: String = "SYSTEM",
    val wallpaperStyle: String = "mesh_prism",
    val customPrimaryColor: Color? = null,
    val customIconBgColor: Color? = null
) {
    val uiGradientBrush: Brush?
        get() = uiGradientColors?.let { Brush.verticalGradient(it) }
}

val LocalAppTheme = staticCompositionLocalOf {
    AppThemeColors(
        mode = ThemeMode.SYSTEM,
        isDark = false,
        isAmoled = false,
        background = LightBackground,
        surface = LightSurface,
        surfaceVariant = LightSurfaceVariant,
        topBarBg = LightSurface,
        topBarBorder = LightOutline,
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
        onSurfaceVariant = LightOnSurfaceVariant,
        reduceAnimations = false,
        animationScale = 0.75f,
        uiGradientId = null,
        uiGradientColors = null,
        wallpaperMode = "SYSTEM",
        wallpaperStyle = "mesh_prism",
        customPrimaryColor = null,
        customIconBgColor = null
    )
}

@Composable
fun BrowsonTheme(
    themeMode: ThemeMode = ThemeMode.SYSTEM,
    darkTheme: Boolean = isSystemInDarkTheme(),
    reduceAnimations: Boolean = false,
    animationScale: Float = 0.75f,
    uiGradientId: String? = null,
    wallpaperMode: String = "SYSTEM",
    wallpaperStyle: String = "mesh_prism",
    customPrimaryHex: Long? = null,
    customIconBgHex: Long? = null,
    content: @Composable () -> Unit
) {
    val systemInDark = isSystemInDarkTheme()
    val resolvedMode = when (themeMode) {
        ThemeMode.SYSTEM -> if (systemInDark) ThemeMode.DARK else ThemeMode.LIGHT
        else -> themeMode
    }

    val isAmoled = resolvedMode == ThemeMode.AMOLED
    val isDark = resolvedMode == ThemeMode.DARK || isAmoled

    // UI Gradient support
    val resolvedGradient = UiGradientPresets.find { it.id == uiGradientId }?.colors

    // Base colors by mode
    val baseBackground = when (resolvedMode) {
        ThemeMode.AMOLED -> AmoledBackground
        ThemeMode.DARK -> DarkBackground
        else -> LightBackground // 80% white (0xFFCCCCCC)
    }
    val baseSurface = when (resolvedMode) {
        ThemeMode.AMOLED -> AmoledSurface
        ThemeMode.DARK -> DarkSurface
        else -> LightSurface
    }
    val baseSurfaceVariant = when (resolvedMode) {
        ThemeMode.AMOLED -> AmoledSurfaceVariant
        ThemeMode.DARK -> DarkSurfaceVariant
        else -> LightSurfaceVariant
    }
    val baseTopBarBg = when (resolvedMode) {
        ThemeMode.AMOLED -> AmoledBackground
        ThemeMode.DARK -> DarkSurface
        else -> LightSurface
    }
    val baseTopBarBorder = when (resolvedMode) {
        ThemeMode.AMOLED -> AmoledOutline
        ThemeMode.DARK -> DarkOutline
        else -> LightOutline
    }
    val baseCardBg = when (resolvedMode) {
        ThemeMode.AMOLED -> SectionCardBgAmoled
        ThemeMode.DARK -> SectionCardBgDark
        else -> SectionCardBgLight
    }
    val baseCardBorder = when (resolvedMode) {
        ThemeMode.AMOLED -> SectionCardOutlineAmoled
        ThemeMode.DARK -> SectionCardOutlineDark
        else -> SectionCardOutlineLight
    }
    val baseSearchBarBg = when (resolvedMode) {
        ThemeMode.AMOLED -> SearchBarBgAmoled
        ThemeMode.DARK -> SearchBarBgDark
        else -> SearchBarBgLight
    }
    val baseSearchBarBorder = when (resolvedMode) {
        ThemeMode.AMOLED -> SearchBarOutlineAmoled
        ThemeMode.DARK -> SearchBarOutlineDark
        else -> SearchBarOutlineLight
    }
    val baseIconBg = when (resolvedMode) {
        ThemeMode.AMOLED -> SectionIconBgAmoled
        ThemeMode.DARK -> SectionIconBgDark
        else -> SectionIconBgLight
    }
    val baseIconBorder = when (resolvedMode) {
        ThemeMode.AMOLED -> SectionIconOutlineAmoled
        ThemeMode.DARK -> SectionIconOutlineDark
        else -> SectionIconOutlineLight
    }
    val baseToolbarBg = when (resolvedMode) {
        ThemeMode.AMOLED -> AmoledBackground
        ThemeMode.DARK -> DarkSurface
        else -> LightSurface
    }
    val baseToolbarBorder = when (resolvedMode) {
        ThemeMode.AMOLED -> AmoledOutline
        ThemeMode.DARK -> DarkOutline
        else -> LightOutline
    }
    val basePrimary = when (resolvedMode) {
        ThemeMode.AMOLED -> AmoledPrimary
        ThemeMode.DARK -> BrowsonRed
        else -> BrowsonRed
    }
    val baseSecondary = when (resolvedMode) {
        ThemeMode.AMOLED -> AmoledSecondary
        ThemeMode.DARK -> BrowsonGreen
        else -> BrowsonGreen
    }
    val baseOnSurface = when (resolvedMode) {
        ThemeMode.AMOLED -> AmoledOnSurface
        ThemeMode.DARK -> DarkOnSurface
        else -> LightOnSurface
    }
    val baseOnSurfaceVariant = when (resolvedMode) {
        ThemeMode.AMOLED -> AmoledOnSurfaceVariant
        ThemeMode.DARK -> DarkOnSurfaceVariant
        else -> LightOnSurfaceVariant
    }

    // Custom Primary & Icon Background colors (from Normal or Dual Colour options)
    val customPrimary = customPrimaryHex?.let { Color(it) }
    val customIconBg = customIconBgHex?.let { Color(it) }

    val effectivePrimary = customPrimary ?: basePrimary
    val effectiveIconBg = customIconBg ?: baseIconBg
    val effectiveIconBorder = if (customIconBg != null) {
        effectivePrimary.copy(alpha = 0.35f)
    } else {
        baseIconBorder
    }

    val effectiveBackground = if (resolvedGradient != null) resolvedGradient.first() else baseBackground
    val effectiveTopBarBg = if (resolvedGradient != null) resolvedGradient.first() else baseTopBarBg
    val effectiveToolbarBg = if (resolvedGradient != null) resolvedGradient.last() else baseToolbarBg

    val effectiveScale = if (reduceAnimations) (animationScale * 0.5f).coerceAtLeast(0.1f) else animationScale

    val appThemeColors = AppThemeColors(
        mode = resolvedMode,
        isDark = isDark,
        isAmoled = isAmoled,
        background = effectiveBackground,
        surface = baseSurface,
        surfaceVariant = baseSurfaceVariant,
        topBarBg = effectiveTopBarBg,
        topBarBorder = baseTopBarBorder,
        cardBg = baseCardBg,
        cardBorder = baseCardBorder,
        searchBarBg = baseSearchBarBg,
        searchBarBorder = baseSearchBarBorder,
        iconBg = effectiveIconBg,
        iconBorder = effectiveIconBorder,
        speedDialAddBg = if (resolvedMode == ThemeMode.AMOLED) SpeedDialAddBgAmoled else if (isDark) SpeedDialAddBgDark else SpeedDialAddBgLight,
        bookmarkAddBg = if (resolvedMode == ThemeMode.AMOLED) BookmarkAddBgAmoled else if (isDark) BookmarkAddBgDark else BookmarkAddBgLight,
        toolbarBg = effectiveToolbarBg,
        toolbarBorder = baseToolbarBorder,
        primary = effectivePrimary,
        secondary = baseSecondary,
        onSurface = baseOnSurface,
        onSurfaceVariant = baseOnSurfaceVariant,
        reduceAnimations = reduceAnimations,
        animationScale = effectiveScale,
        uiGradientId = uiGradientId,
        uiGradientColors = resolvedGradient,
        wallpaperMode = wallpaperMode,
        wallpaperStyle = wallpaperStyle,
        customPrimaryColor = customPrimary,
        customIconBgColor = customIconBg
    )

    val colorScheme = when {
        isAmoled -> darkColorScheme(
            primary = effectivePrimary,
            onPrimary = Color.White,
            background = AmoledBackground,
            surface = AmoledSurface,
            onSurface = AmoledOnSurface,
            surfaceVariant = effectiveIconBg
        )
        isDark -> darkColorScheme(
            primary = effectivePrimary,
            onPrimary = Color.White,
            background = DarkBackground,
            surface = DarkSurface,
            onSurface = DarkOnSurface,
            surfaceVariant = effectiveIconBg
        )
        else -> lightColorScheme(
            primary = effectivePrimary,
            onPrimary = Color.White,
            background = LightBackground,
            surface = LightSurface,
            onSurface = LightOnSurface,
            surfaceVariant = effectiveIconBg
        )
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                // Status bar colour matches app searchbar colour
                window.statusBarColor = appThemeColors.searchBarBg.toArgb()
                // Navigation bar colour matches bottom toolbar colour
                window.navigationBarColor = appThemeColors.toolbarBg.toArgb()
                val insetsController = WindowCompat.getInsetsController(window, view)
                val lightThemeContent = !isDark && resolvedGradient == null
                insetsController.isAppearanceLightStatusBars = lightThemeContent
                insetsController.isAppearanceLightNavigationBars = lightThemeContent
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
