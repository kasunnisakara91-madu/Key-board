package com.example.keyboard

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

enum class ThemeId {
    RED_LIGHTNING,
    BESTIE_VIOLET,
    MIDNIGHT_DARK,
    PURE_LIGHT,
    SUNSET_CORAL,
    OCEAN_BREEZE,
    CYBERPUNK
}

data class KeyboardThemeData(
    val id: ThemeId,
    val name: String,
    val backgroundBrush: Brush,
    val surfaceColor: Color,
    val keyBackgroundColor: Color,
    val actionKeyBackgroundColor: Color,
    val enterKeyBackgroundColor: Color,
    val keyTextColor: Color,
    val actionKeyTextColor: Color,
    val enterKeyTextColor: Color,
    val keyBorderColor: Color,
    val cornerHintColor: Color,
    val suggestionTextColor: Color,
    val suggestionBgColor: Color,
    val usesBackgroundDrawable: Boolean = false,
    val isDark: Boolean = true
)

object KeyboardThemes {

    // EXACT MATCH to user's screenshot: Fiery Red Lightning, Neon Green Outline, Black Pill Keys
    val RedLightning = KeyboardThemeData(
        id = ThemeId.RED_LIGHTNING,
        name = "Red Lightning (Signature)",
        backgroundBrush = Brush.verticalGradient(
            colors = listOf(Color(0xFF3B0000), Color(0xFF1A0000), Color(0xFF000000))
        ),
        surfaceColor = Color(0xFF100000),
        keyBackgroundColor = Color(0xDD0D0D0D),
        actionKeyBackgroundColor = Color(0xEE1A1A1A),
        enterKeyBackgroundColor = Color(0xFF00E676),
        keyTextColor = Color.White,
        actionKeyTextColor = Color(0xFFB0BEC5),
        enterKeyTextColor = Color.Black,
        keyBorderColor = Color(0xFF00E676), // Vibrant Neon Green
        cornerHintColor = Color(0xFFB9F6CA), // Light pastel green for Sinhala corner letters
        suggestionTextColor = Color(0xFF00E676),
        suggestionBgColor = Color(0x33000000),
        usesBackgroundDrawable = true,
        isDark = true
    )

    val BestieViolet = KeyboardThemeData(
        id = ThemeId.BESTIE_VIOLET,
        name = "Bestie Violet",
        backgroundBrush = Brush.verticalGradient(
            colors = listOf(Color(0xFF2E1065), Color(0xFF4C1D95), Color(0xFF5B21B6))
        ),
        surfaceColor = Color(0xFF240E4A),
        keyBackgroundColor = Color(0xFF432874),
        actionKeyBackgroundColor = Color(0xFF583594),
        enterKeyBackgroundColor = Color(0xFFEC4899),
        keyTextColor = Color(0xFFF5F3FF),
        actionKeyTextColor = Color(0xFFDDD6FE),
        enterKeyTextColor = Color.White,
        keyBorderColor = Color(0x33A78BFA),
        cornerHintColor = Color(0xFFDDD6FE),
        suggestionTextColor = Color(0xFFF3E8FF),
        suggestionBgColor = Color(0x33583594),
        isDark = true
    )

    val MidnightDark = KeyboardThemeData(
        id = ThemeId.MIDNIGHT_DARK,
        name = "Midnight Dark",
        backgroundBrush = Brush.verticalGradient(
            colors = listOf(Color(0xFF121212), Color(0xFF1E1E1E))
        ),
        surfaceColor = Color(0xFF181818),
        keyBackgroundColor = Color(0xFF2C2C2C),
        actionKeyBackgroundColor = Color(0xFF383838),
        enterKeyBackgroundColor = Color(0xFF3B82F6),
        keyTextColor = Color(0xFFE5E7EB),
        actionKeyTextColor = Color(0xFF9CA3AF),
        enterKeyTextColor = Color.White,
        keyBorderColor = Color(0x22FFFFFF),
        cornerHintColor = Color(0xFF9CA3AF),
        suggestionTextColor = Color(0xFFE5E7EB),
        suggestionBgColor = Color(0x33383838),
        isDark = true
    )

    val PureLight = KeyboardThemeData(
        id = ThemeId.PURE_LIGHT,
        name = "Pure Light",
        backgroundBrush = Brush.verticalGradient(
            colors = listOf(Color(0xFFF3F4F6), Color(0xFFE5E7EB))
        ),
        surfaceColor = Color(0xFFE5E7EB),
        keyBackgroundColor = Color(0xFFFFFFFF),
        actionKeyBackgroundColor = Color(0xFFD1D5DB),
        enterKeyBackgroundColor = Color(0xFF2563EB),
        keyTextColor = Color(0xFF1F2937),
        actionKeyTextColor = Color(0xFF374151),
        enterKeyTextColor = Color.White,
        keyBorderColor = Color(0x339CA3AF),
        cornerHintColor = Color(0xFF4B5563),
        suggestionTextColor = Color(0xFF1F2937),
        suggestionBgColor = Color(0x229CA3AF),
        isDark = false
    )

    val SunsetCoral = KeyboardThemeData(
        id = ThemeId.SUNSET_CORAL,
        name = "Sunset Coral",
        backgroundBrush = Brush.verticalGradient(
            colors = listOf(Color(0xFF4A1525), Color(0xFF831843), Color(0xFF9D174D))
        ),
        surfaceColor = Color(0xFF3B0716),
        keyBackgroundColor = Color(0xFF9F2856),
        actionKeyBackgroundColor = Color(0xFFB83267),
        enterKeyBackgroundColor = Color(0xFFF97316),
        keyTextColor = Color(0xFFFFF1F2),
        actionKeyTextColor = Color(0xFFFECDD3),
        enterKeyTextColor = Color.White,
        keyBorderColor = Color(0x44FDA4AF),
        cornerHintColor = Color(0xFFFECDD3),
        suggestionTextColor = Color(0xFFFFF1F2),
        suggestionBgColor = Color(0x33B83267),
        isDark = true
    )

    val OceanBreeze = KeyboardThemeData(
        id = ThemeId.OCEAN_BREEZE,
        name = "Ocean Breeze",
        backgroundBrush = Brush.verticalGradient(
            colors = listOf(Color(0xFF0F172A), Color(0xFF0C4A6E), Color(0xFF0369A1))
        ),
        surfaceColor = Color(0xFF082F49),
        keyBackgroundColor = Color(0xFF0E7490),
        actionKeyBackgroundColor = Color(0xFF155E75),
        enterKeyBackgroundColor = Color(0xFF06B6D4),
        keyTextColor = Color(0xFFF0FDF4),
        actionKeyTextColor = Color(0xFFBAE6FD),
        enterKeyTextColor = Color(0xFF082F49),
        keyBorderColor = Color(0x3367E8F9),
        cornerHintColor = Color(0xFFBAE6FD),
        suggestionTextColor = Color(0xFFF0FDF4),
        suggestionBgColor = Color(0x33155E75),
        isDark = true
    )

    val Cyberpunk = KeyboardThemeData(
        id = ThemeId.CYBERPUNK,
        name = "Neon Cyberpunk",
        backgroundBrush = Brush.verticalGradient(
            colors = listOf(Color(0xFF09090B), Color(0xFF18181B))
        ),
        surfaceColor = Color(0xFF09090B),
        keyBackgroundColor = Color(0xFF1C1917),
        actionKeyBackgroundColor = Color(0xFF27272A),
        enterKeyBackgroundColor = Color(0xFF06B6D4),
        keyTextColor = Color(0xFFF43F5E),
        actionKeyTextColor = Color(0xFF22D3EE),
        enterKeyTextColor = Color.Black,
        keyBorderColor = Color(0x88F43F5E),
        cornerHintColor = Color(0xFF22D3EE),
        suggestionTextColor = Color(0xFF22D3EE),
        suggestionBgColor = Color(0x3327272A),
        isDark = true
    )

    val allThemes = listOf(
        RedLightning,
        BestieViolet,
        MidnightDark,
        PureLight,
        SunsetCoral,
        OceanBreeze,
        Cyberpunk
    )

    fun getById(id: ThemeId): KeyboardThemeData {
        return allThemes.find { it.id == id } ?: RedLightning
    }
}
