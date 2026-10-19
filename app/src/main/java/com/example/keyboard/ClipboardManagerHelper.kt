package com.example.keyboard

import android.content.Context
import android.content.SharedPreferences

data class ClipboardItem(
    val id: String,
    val text: String,
    val timestamp: Long
)

class ClipboardManagerHelper(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREFS_NAME = "bestie_clipboard_history"
        private const val KEY_ITEMS = "clipboard_entries"
        private const val DELIMITER = "###BESTIE_CLIP_SEP###"
        private const val MAX_ITEMS = 25
    }

    init {
        // Seed default helpful clips if empty
        if (getItems().isEmpty()) {
            addItem("Welcome to BESTIE Keyboard! ❤️")
            addItem("ආයුබෝවන් (Ayubowan)")
            addItem("Thank you so much! ✨")
        }
    }

    fun getItems(): List<ClipboardItem> {
        val raw = prefs.getString(KEY_ITEMS, null) ?: return emptyList()
        if (raw.isBlank()) return emptyList()

        return raw.split(DELIMITER).mapNotNull { entry ->
            val parts = entry.split(":::", limit = 3)
            if (parts.size == 3) {
                ClipboardItem(
                    id = parts[0],
                    timestamp = parts[1].toLongOrNull() ?: System.currentTimeMillis(),
                    text = parts[2]
                )
            } else null
        }
    }

    @Synchronized
    fun addItem(text: String) {
        val trimmed = text.trim()
        if (trimmed.isEmpty()) return

        val current = getItems().toMutableList()
        // Deduplicate text if already exists
        current.removeAll { it.text == trimmed }

        val newItem = ClipboardItem(
            id = System.currentTimeMillis().toString(),
            text = trimmed,
            timestamp = System.currentTimeMillis()
        )
        current.add(0, newItem)

        val limited = current.take(MAX_ITEMS)
        saveList(limited)
    }

    @Synchronized
    fun deleteItem(id: String) {
        val current = getItems().filterNot { it.id == id }
        saveList(current)
    }

    @Synchronized
    fun clearAll() {
        prefs.edit().remove(KEY_ITEMS).apply()
    }

    private fun saveList(items: List<ClipboardItem>) {
        val encoded = items.joinToString(DELIMITER) { item ->
            "${item.id}:::${item.timestamp}:::${item.text}"
        }
        prefs.edit().putString(KEY_ITEMS, encoded).apply()
    }
}
