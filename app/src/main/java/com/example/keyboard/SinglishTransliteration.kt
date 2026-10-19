package com.example.keyboard

object SinglishTransliteration {

    val keyToSinhalaMap = mapOf(
        'q' to "ද",
        'w' to "ව",
        'e' to "එ",
        'r' to "ර",
        't' to "ට",
        'y' to "ය",
        'u' to "උ",
        'i' to "ඉ",
        'o' to "ඔ",
        'p' to "ප",
        'a' to "අ",
        's' to "ස",
        'd' to "ඩ",
        'f' to "ෆ",
        'g' to "ග",
        'h' to "හ",
        'j' to "ජ",
        'k' to "ක",
        'l' to "ල",
        'z' to "ඥ",
        'x' to "ං",
        'c' to "ච",
        'v' to "ව",
        'b' to "බ",
        'n' to "න",
        'm' to "ම"
    )

    // Comprehensive dictionary of common Singlish words
    private val singlishDict = mapOf(
        "mama" to "මම",
        "oya" to "ඔයා",
        "oyata" to "ඔයාට",
        "mata" to "මට",
        "api" to "අපි",
        "apita" to "අපිට",
        "suba" to "සුබ",
        "subha" to "සුබ",
        "dawasak" to "දවසක්",
        "kohomada" to "කොහොමද",
        "aayubowan" to "ආයුබෝවන්",
        "ayubowan" to "ආයුබෝවන්",
        "sthuthi" to "ස්තූතියි",
        "sthutiyi" to "ස්තූතියි",
        "hondai" to "හොඳයි",
        "hodayi" to "හොඳයි",
        "enna" to "එන්න",
        "yanna" to "යන්න",
        "karanna" to "කරන්න",
        "dannawa" to "දන්නවා",
        "naa" to "නෑ",
        "ne" to "නේ",
        "ow" to "ඔව්",
        "kawadda" to "කවද්ද",
        "koheda" to "කොහෙද",
        "mokakda" to "මොකක්ද",
        "mokada" to "මොකද",
        "ai" to "ඇයි",
        "godak" to "ගොඩක්",
        "passe" to "පස්සේ",
        "dan" to "දැන්",
        "poddak" to "පොඩ්ඩක්",
        "gedara" to "ගෙදර",
        "wada" to "වැඩ",
        "veda" to "වැඩ",
        "pasala" to "පාසල",
        "adarai" to "ආදරෙයි",
        "satutui" to "සතුටුයි",
        "sathutui" to "සතුටුයි",
        "lassana" to "ලස්සන",
        "yaluwa" to "යාළුවා",
        "bestie" to "බෙස්ටී",
        "keyboard" to "කීබෝඩ්",
        "monawada" to "මොනවද",
        "kiyanna" to "කියන්න",
        "balanna" to "බලන්න",
        "innawa" to "ඉන්නවා",
        "hithanna" to "හිතන්න",
        "kaala" to "කාලා",
        "beela" to "බීලා",
        "nidi" to "නිදි",
        "harima" to "හරිම",
        "puluwan" to "පුළුවන්",
        "bae" to "බෑ",
        "ba" to "බෑ",
        "ehemai" to "එහෙමයි",
        "oyath" to "ඔයාත්",
        "math" to "මාත්",
        "kiyaddi" to "කියද්දි",
        "sinhala" to "සිංහල",
        "english" to "ඉංග්‍රීසි"
    )

    fun transliterateWord(input: String): String? {
        val lower = input.lowercase().trim()
        val dictMatch = singlishDict[lower]
        if (dictMatch != null) return dictMatch

        // Algorithmic phonetic transliteration for unlisted words
        return phoneticTransliterate(lower)
    }

    private fun phoneticTransliterate(input: String): String? {
        if (input.length < 2) return null
        val sb = StringBuilder()
        var i = 0
        while (i < input.length) {
            // Check two-letter consonants first: th, ch, sh
            var consumed = false
            if (i + 1 < input.length) {
                val pair = input.substring(i, i + 2)
                val cSinhala = when (pair) {
                    "th" -> "ත"
                    "ch" -> "ච"
                    "sh" -> "ශ"
                    "dh" -> "ධ"
                    "ph" -> "ඵ"
                    "bh" -> "භ"
                    else -> null
                }
                if (cSinhala != null) {
                    sb.append(cSinhala)
                    i += 2
                    consumed = true
                }
            }

            if (!consumed) {
                val ch = input[i]
                val sChar = keyToSinhalaMap[ch]
                if (sChar != null) {
                    sb.append(sChar)
                } else {
                    sb.append(ch)
                }
                i++
            }
        }
        return if (sb.isNotEmpty()) sb.toString() else null
    }

    fun getSinhalaCharForEnglish(ch: Char): String {
        return keyToSinhalaMap[ch.lowercaseChar()] ?: ch.toString()
    }
}
