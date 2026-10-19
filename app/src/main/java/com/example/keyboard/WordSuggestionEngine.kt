package com.example.keyboard

class WordSuggestionEngine {

    private val englishWords = listOf(
        "the", "and", "you", "that", "was", "for", "are", "with", "his", "they",
        "have", "this", "from", "one", "had", "word", "but", "not", "what", "all",
        "were", "when", "your", "can", "said", "there", "use", "each", "which", "she",
        "do", "how", "their", "if", "will", "up", "other", "about", "out", "many",
        "then", "them", "these", "so", "some", "her", "would", "make", "like", "him",
        "into", "time", "has", "look", "two", "more", "write", "go", "see", "number",
        "no", "way", "could", "people", "my", "than", "first", "water", "been", "call",
        "who", "oil", "its", "now", "find", "long", "down", "day", "did", "get",
        "come", "made", "may", "part", "bestie", "keyboard", "beautiful", "thanks", "hello",
        "welcome", "please", "love", "friend", "happy", "great", "awesome", "perfect",
        "good", "morning", "night", "today", "tomorrow", "tonight", "awesome", "amazing"
    )

    private val sinhalaWords = listOf(
        "ආයුබෝවන්", "කොහොමද", "ස්තූතියි", "සුබ", "දවසක්", "ලස්සන", "යාළුවා", "මම",
        "ඔබ", "අපි", "ඔයා", "හොඳයි", "එන්න", "යන්න", "කරන්න", "දන්නවා", "නෑ",
        "ඔව්", "කවද්ද", "කොහෙද", "මොකක්ද", "ඇයි", "ගොඩක්", "පස්සේ", "දැන්",
        "පොඩ්ඩක්", "ගෙදර", "වැඩ", "පාසල", "රස්සාව", "ආදරෙයි", "සතුටුයි", "කාලය"
    )

    fun getSuggestions(prefix: String, language: KeyboardLanguage): List<String> {
        val trimmed = prefix.trim()
        if (trimmed.isEmpty()) {
            return when (language) {
                KeyboardLanguage.ENGLISH -> listOf("Hello", "I", "The", "Thanks")
                KeyboardLanguage.SINHALA -> listOf("ආයුබෝවන්", "කොහොමද", "ස්තූතියි", "සුබ")
            }
        }

        // Check if prefix matches a Singlish transliteration (e.g. mama -> මම, oya -> ඔයා)
        val singlishMatch = SinglishTransliteration.transliterateWord(trimmed)

        val candidates = when (language) {
            KeyboardLanguage.ENGLISH -> {
                val lower = trimmed.lowercase()
                val matches = englishWords.filter { it.startsWith(lower) && it != lower }.take(3).toMutableList()
                if (singlishMatch != null) {
                    matches.add(0, singlishMatch)
                }
                if (matches.isEmpty()) {
                    matches.add(trimmed)
                }
                if (trimmed.first().isUpperCase()) {
                    matches.map { if (it.all { c -> c.isLetter() }) it.replaceFirstChar { c -> c.uppercase() } else it }
                } else {
                    matches
                }
            }
            KeyboardLanguage.SINHALA -> {
                val matches = sinhalaWords.filter { it.startsWith(trimmed) && it != trimmed }.take(3).toMutableList()
                if (singlishMatch != null && !matches.contains(singlishMatch)) {
                    matches.add(0, singlishMatch)
                }
                if (matches.isEmpty()) {
                    matches.add(singlishMatch ?: trimmed)
                }
                matches
            }
        }

        return candidates.distinct()
    }

    fun shouldAutoCapitalize(textBeforeCursor: CharSequence?): Boolean {
        if (textBeforeCursor.isNullOrEmpty()) return true
        val trimmed = textBeforeCursor.trimEnd()
        if (trimmed.isEmpty()) return true
        val lastChar = trimmed.last()
        return lastChar == '.' || lastChar == '!' || lastChar == '?' || lastChar == '\n'
    }
}
