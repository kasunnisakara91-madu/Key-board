package com.example.keyboard

object SinglishTransliteration {

    // Key-to-Sinhala character mapping corresponding to the user's keyboard screenshot
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

    // Common Singlish word transliteration dictionary
    private val singlishDict = mapOf(
        "mama" to "මම",
        "oya" to "ඔයා",
        "api" to "අපි",
        "suba" to "සුබ",
        "subha" to "සුබ",
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
        "pasala" to "පාසල",
        "adarai" to "ආදරෙයි",
        "satutui" to "සතුටුයි",
        "lassana" to "ලස්සන",
        "yaluwa" to "යාළුවා",
        "bestie" to "බෙස්ටී",
        "keyboard" to "කීබෝඩ්",
        "monawada" to "මොනවද",
        "kiyanna" to "කියන්න",
        "balanna" to "බලන්න",
        "innawa" to "ඉන්නවා"
    )

    fun transliterateWord(input: String): String? {
        val lower = input.lowercase().trim()
        return singlishDict[lower]
    }

    fun getSinhalaCharForEnglish(ch: Char): String {
        return keyToSinhalaMap[ch.lowercaseChar()] ?: ch.toString()
    }
}
