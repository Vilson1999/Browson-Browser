package com.example.ui.theme

import androidx.compose.ui.graphics.Color

// Accent colors inspired by the Browson brand
val BrowsonRed = Color(0xFFE53935)
val BrowsonRedDark = Color(0xFFB71C1C)
val BrowsonGreen = Color(0xFF10B981)
val BrowsonGreenDark = Color(0xFF047857)
val BrowsonWhite = Color(0xFFFFFFFF)

// --- 95% OF WHITE BASE PALETTE (255 * 0.95 = 242.25 -> 0xFFF2F2F2) ---
val NinetyFivePercentWhite = Color(0xFFF2F2F2)
val NinetyFivePercentWhiteSurface = Color(0xFFE6E8EC)
val NinetyFivePercentWhiteCard = Color(0xFFEAECEF)
val NinetyFivePercentWhiteOutline = Color(0xFFD0D4DC)

// Backward compatibility alias for sheets and presets
val EightyPercentWhite = NinetyFivePercentWhite

// --- PURE DARK PALETTE (Slate/Navy Dark) ---
val DarkBackground = Color(0xFF0F1117)
val DarkSurface = Color(0xFF161922)
val DarkSurfaceVariant = Color(0xFF1E2330)
val DarkOnSurface = Color(0xFFF1F3F5)
val DarkOnSurfaceVariant = Color(0xFFA0A5B5)
val DarkOutline = Color(0xFF262C3A)

// --- PURE LIGHT PALETTE (Clean, bright, modern light) ---
val LightBackground = Color(0xFFF8FAFC)
val LightSurface = Color(0xFFFFFFFF)
val LightSurfaceVariant = Color(0xFFF1F5F9)
val LightOnSurface = Color(0xFF0F172A)
val LightOnSurfaceVariant = Color(0xFF334155)
val LightOutline = Color(0xFFE2E8F0)

// --- AMOLED PALETTE (Pitch Black 0x000000 for True OLED) ---
val AmoledBackground = Color(0xFF000000)
val AmoledSurface = Color(0xFF0A0A0A)
val AmoledSurfaceVariant = Color(0xFF141414)
val AmoledOnSurface = Color(0xFFFFFFFF)
val AmoledOnSurfaceVariant = Color(0xFFA1A1AA)
val AmoledOutline = Color(0xFF27272A)
val AmoledPrimary = Color(0xFFFF334B)
val AmoledSecondary = Color(0xFF00E676)

// --- Component-specific theme colors ---
// 80% white in light mode (0.80 * 255 = 204 -> 0xCC) and 90% black in dark/amoled mode
val SearchBarBgLight = Color(0xCCFFFFFF)
val SearchBarBgDark = Color(0xE6000000)
val SearchBarBgAmoled = Color(0xE6000000)

val SearchBarOutlineLight = Color(0xFFE2E8F0)
val SearchBarOutlineDark = Color(0xFF2C3342)
val SearchBarOutlineAmoled = Color(0xFF27272A)

val SectionCardBgLight = Color(0xFFFFFFFF)
val SectionCardBgDark = Color(0xFF161922)
val SectionCardBgAmoled = Color(0xFF090909)

val SectionCardOutlineLight = Color(0xFFE2E8F0)
val SectionCardOutlineDark = Color(0xFF262C3A)
val SectionCardOutlineAmoled = Color(0xFF1F1F23)

val SectionIconBgLight = Color(0xFFF8FAFC)
val SectionIconBgDark = Color(0xFF242938)
val SectionIconBgAmoled = Color(0xFF161618)

val SectionIconOutlineLight = Color(0xFFE2E8F0)
val SectionIconOutlineDark = Color(0xFF333B4E)
val SectionIconOutlineAmoled = Color(0xFF27272A)

val SpeedDialAddBgLight = Color(0xFFFEE2E2)
val SpeedDialAddBgDark = Color(0xFF2D1E22)
val SpeedDialAddBgAmoled = Color(0xFF260D10)

val BookmarkAddBgLight = Color(0xFFD1FAE5)
val BookmarkAddBgDark = Color(0xFF1B2D24)
val BookmarkAddBgAmoled = Color(0xFF082215)

// ============================================================
// 10 UI BACKGROUND GRADIENT PRESETS
// When selected, sets whole UI background colour
// ============================================================
data class UiGradientPreset(
    val id: String,
    val name: String,
    val colors: List<Color>
)

val UiGradientPresets = listOf(
    UiGradientPreset("sunset_glow", "Sunset Glow", listOf(Color(0xFFFF512F), Color(0xFFDD2476))),
    UiGradientPreset("ocean_breeze", "Ocean Breeze", listOf(Color(0xFF2E3192), Color(0xFF1BFFFF))),
    UiGradientPreset("aurora_sky", "Aurora Sky", listOf(Color(0xFF00C9FF), Color(0xFF92FE9D))),
    UiGradientPreset("cosmic_violet", "Cosmic Violet", listOf(Color(0xFF654EA3), Color(0xFFEAAFC8))),
    UiGradientPreset("emerald_mint", "Emerald Mint", listOf(Color(0xFF11998E), Color(0xFF38EF7D))),
    UiGradientPreset("midnight_neon", "Midnight Neon", listOf(Color(0xFF0F2027), Color(0xFF203A43), Color(0xFF2C5364))),
    UiGradientPreset("amber_sunrise", "Amber Sunrise", listOf(Color(0xFFFF8008), Color(0xFFFFC837))),
    UiGradientPreset("rose_quartz", "Rose Quartz", listOf(Color(0xFFFC5C7D), Color(0xFF6A82FB))),
    UiGradientPreset("cyberpunk_blaze", "Cyberpunk", listOf(Color(0xFF8A2387), Color(0xFFE94057), Color(0xFFF27121))),
    UiGradientPreset("slate_mystic", "Slate Mystic", listOf(Color(0xFF243B55), Color(0xFF141E30)))
)

// ============================================================
// 10 NORMAL COLOUR PRESETS
// When selected, sets app UI colour (primary)
// ============================================================
data class NormalColorPreset(
    val id: String,
    val name: String,
    val color: Color,
    val colorHex: Long
)

val NormalColorPresets = listOf(
    NormalColorPreset("red", "Browson Red", Color(0xFFE53935), 0xFFE53935),
    NormalColorPreset("blue", "Royal Blue", Color(0xFF2563EB), 0xFF2563EB),
    NormalColorPreset("green", "Emerald Green", Color(0xFF10B981), 0xFF10B981),
    NormalColorPreset("purple", "Vivid Purple", Color(0xFF8B5CF6), 0xFF8B5CF6),
    NormalColorPreset("amber", "Amber Gold", Color(0xFFF59E0B), 0xFFF59E0B),
    NormalColorPreset("cyan", "Cyan Teal", Color(0xFF06B6D4), 0xFF06B6D4),
    NormalColorPreset("pink", "Magenta Rose", Color(0xFFEC4899), 0xFFEC4899),
    NormalColorPreset("orange", "Deep Orange", Color(0xFFEA580C), 0xFFEA580C),
    NormalColorPreset("indigo", "Electric Indigo", Color(0xFF4F46E5), 0xFF4F46E5),
    NormalColorPreset("lime", "Lime Mint", Color(0xFF84CC16), 0xFF84CC16)
)

// ============================================================
// 10 DUAL-COLOUR PRESETS (Half dark, half light of same color)
// Dark colour used as main UI colour; light colour used as icon background
// ============================================================
data class DualColorPreset(
    val id: String,
    val name: String,
    val darkColor: Color,
    val darkHex: Long,
    val lightColor: Color,
    val lightHex: Long
)

val DualColorPresets = listOf(
    DualColorPreset("crimson_blush", "Crimson & Blush", Color(0xFFB71C1C), 0xFFB71C1C, Color(0xFFFFCDD2), 0xFFFFCDD2),
    DualColorPreset("navy_sky", "Navy & Sky", Color(0xFF1E3A8A), 0xFF1E3A8A, Color(0xFFBFDBFE), 0xFFBFDBFE),
    DualColorPreset("forest_mint", "Forest & Mint", Color(0xFF065F46), 0xFF065F46, Color(0xFFA7F3D0), 0xFFA7F3D0),
    DualColorPreset("violet_lavender", "Violet & Lavender", Color(0xFF581C87), 0xFF581C87, Color(0xFFDDD6FE), 0xFFDDD6FE),
    DualColorPreset("bronze_amber", "Bronze & Amber", Color(0xFFB45309), 0xFFB45309, Color(0xFFFDE68A), 0xFFFDE68A),
    DualColorPreset("dark_teal", "Teal & Aqua", Color(0xFF115E59), 0xFF115E59, Color(0xFF99F6E4), 0xFF99F6E4),
    DualColorPreset("wine_rose", "Wine & Rose", Color(0xFF831843), 0xFF831843, Color(0xFFFBCFE8), 0xFFFBCFE8),
    DualColorPreset("burnt_peach", "Orange & Peach", Color(0xFF9A3412), 0xFF9A3412, Color(0xFFFED7AA), 0xFFFED7AA),
    DualColorPreset("indigo_periwinkle", "Indigo & Periwinkle", Color(0xFF312E81), 0xFF312E81, Color(0xFFC7D2FE), 0xFFC7D2FE),
    DualColorPreset("moss_lime", "Moss & Lime", Color(0xFF365314), 0xFF365314, Color(0xFFD9F99D), 0xFFD9F99D)
)

// ============================================================
// WALLPAPER STYLE PRESETS FOR HOME SCREEN
// Modes: SYSTEM (Auto), LIGHT, DARK
// ============================================================
data class WallpaperStylePreset(
    val id: String,
    val name: String,
    val lightColors: List<Color>,
    val darkColors: List<Color>
)

val WallpaperStylePresets = listOf(
    WallpaperStylePreset(
        id = "mesh_prism",
        name = "Prism Glow",
        lightColors = listOf(Color(0xFFE2E8F0), Color(0xFFD1D8E2), NinetyFivePercentWhite),
        darkColors = listOf(Color(0xFF0F172A), Color(0xFF1E293B), Color(0xFF020617))
    ),
    WallpaperStylePreset(
        id = "aurora_flow",
        name = "Aurora Flow",
        lightColors = listOf(Color(0xFFCFE8E0), Color(0xFFCAD7E5), NinetyFivePercentWhite),
        darkColors = listOf(Color(0xFF064E3B), Color(0xFF0F172A), Color(0xFF020617))
    ),
    WallpaperStylePreset(
        id = "sunset_horizon",
        name = "Horizon Glow",
        lightColors = listOf(Color(0xFFFFDFE2), Color(0xFFE2E4EC), NinetyFivePercentWhite),
        darkColors = listOf(Color(0xFF4C0519), Color(0xFF18181B), Color(0xFF09090B))
    ),
    WallpaperStylePreset(
        id = "cosmic_nebula",
        name = "Cosmic Nebula",
        lightColors = listOf(Color(0xFFDFE6FD), Color(0xFFECE6FD), NinetyFivePercentWhite),
        darkColors = listOf(Color(0xFF1E1B4B), Color(0xFF311042), Color(0xFF0F0F1A))
    )
)
