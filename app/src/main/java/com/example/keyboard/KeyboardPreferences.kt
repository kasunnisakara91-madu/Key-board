package com.example.keyboard

import android.content.Context
import android.content.SharedPreferences

enum class KeyboardHeight(val label: String, val dpValue: Int) {
    COMPACT("Compact", 220),
    NORMAL("Normal", 260),
    TALL("Tall", 300)
}

enum class KeyboardLanguage(val code: String, val displayName: String, val shortLabel: String) {
    ENGLISH("en", "English", "EN"),
    SINHALA("si", "සිංහල", "සිං")
}

data class KeyboardSettings(
    val themeId: ThemeId = ThemeId.RED_LIGHTNING,
    val height: KeyboardHeight = KeyboardHeight.NORMAL,
    val soundEnabled: Boolean = true,
    val hapticEnabled: Boolean = true,
    val suggestionsEnabled: Boolean = true,
    val autoCapEnabled: Boolean = true,
    val doubleSpacePeriodEnabled: Boolean = true,
    val keyBordersEnabled: Boolean = true,
    val currentLanguage: KeyboardLanguage = KeyboardLanguage.ENGLISH,
    val textStyleMode: TextStyleMode = TextStyleMode.NORMAL,
    val recentEmojis: List<String> = listOf("😌", "🧚", "❤️", "🌸", "🦋", "🌓", "🌎", "⌨️", "🎹")
)

class KeyboardPreferences(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREFS_NAME = "bestie_keyboard_prefs"
        private const val KEY_THEME = "pref_theme"
        private const val KEY_HEIGHT = "pref_height"
        private const val KEY_SOUND = "pref_sound"
        private const val KEY_HAPTIC = "pref_haptic"
        private const val KEY_SUGGESTIONS = "pref_suggestions"
        private const val KEY_AUTO_CAP = "pref_auto_cap"
        private const val KEY_DOUBLE_SPACE = "pref_double_space"
        private const val KEY_KEY_BORDERS = "pref_key_borders"
        private const val KEY_LANGUAGE = "pref_language"
        private const val KEY_RECENT_EMOJIS = "pref_recent_emojis"
    }

    fun loadSettings(): KeyboardSettings {
        val themeStr = prefs.getString(KEY_THEME, ThemeId.RED_LIGHTNING.name) ?: ThemeId.RED_LIGHTNING.name
        val theme = runCatching { ThemeId.valueOf(themeStr) }.getOrDefault(ThemeId.RED_LIGHTNING)

        val heightStr = prefs.getString(KEY_HEIGHT, KeyboardHeight.NORMAL.name) ?: KeyboardHeight.NORMAL.name
        val height = runCatching { KeyboardHeight.valueOf(heightStr) }.getOrDefault(KeyboardHeight.NORMAL)

        val langStr = prefs.getString(KEY_LANGUAGE, KeyboardLanguage.ENGLISH.name) ?: KeyboardLanguage.ENGLISH.name
        val lang = runCatching { KeyboardLanguage.valueOf(langStr) }.getOrDefault(KeyboardLanguage.ENGLISH)

        val styleStr = prefs.getString("pref_text_style", TextStyleMode.NORMAL.name) ?: TextStyleMode.NORMAL.name
        val textStyle = runCatching { TextStyleMode.valueOf(styleStr) }.getOrDefault(TextStyleMode.NORMAL)

        val recentEmojiRaw = prefs.getString(KEY_RECENT_EMOJIS, "😌,🧚,❤️,🌸,🦋,🌓,🌎,⌨️,🎹") ?: "😌,🧚,❤️,🌸,🦋,🌓,🌎,⌨️,🎹"
        val recentEmojis = recentEmojiRaw.split(",").filter { it.isNotBlank() }

        return KeyboardSettings(
            themeId = theme,
            height = height,
            soundEnabled = prefs.getBoolean(KEY_SOUND, true),
            hapticEnabled = prefs.getBoolean(KEY_HAPTIC, true),
            suggestionsEnabled = prefs.getBoolean(KEY_SUGGESTIONS, true),
            autoCapEnabled = prefs.getBoolean(KEY_AUTO_CAP, true),
            doubleSpacePeriodEnabled = prefs.getBoolean(KEY_DOUBLE_SPACE, true),
            keyBordersEnabled = prefs.getBoolean(KEY_KEY_BORDERS, true),
            currentLanguage = lang,
            textStyleMode = textStyle,
            recentEmojis = recentEmojis
        )
    }

    fun setTextStyleMode(mode: TextStyleMode) {
        prefs.edit().putString("pref_text_style", mode.name).apply()
    }

    fun setTheme(themeId: ThemeId) {
        prefs.edit().putString(KEY_THEME, themeId.name).apply()
    }

    fun setHeight(height: KeyboardHeight) {
        prefs.edit().putString(KEY_HEIGHT, height.name).apply()
    }

    fun setSoundEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_SOUND, enabled).apply()
    }

    fun setHapticEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_HAPTIC, enabled).apply()
    }

    fun setSuggestionsEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_SUGGESTIONS, enabled).apply()
    }

    fun setAutoCapEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_AUTO_CAP, enabled).apply()
    }

    fun setDoubleSpacePeriodEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_DOUBLE_SPACE, enabled).apply()
    }

    fun setKeyBordersEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_KEY_BORDERS, enabled).apply()
    }

    fun setLanguage(language: KeyboardLanguage) {
        prefs.edit().putString(KEY_LANGUAGE, language.name).apply()
    }

    fun addRecentEmoji(emoji: String) {
        val current = loadSettings().recentEmojis.toMutableList()
        current.remove(emoji)
        current.add(0, emoji)
        val trimmed = current.take(16)
        prefs.edit().putString(KEY_RECENT_EMOJIS, trimmed.joinToString(",")).apply()
    }
}
