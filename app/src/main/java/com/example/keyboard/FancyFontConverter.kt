package com.example.keyboard

enum class TextStyleMode(val label: String, val sample: String) {
    NORMAL("Normal", "Abc"),
    CURSIVE("Cursive", "𝓐𝓫𝓬"),
    BOLD("Bold", "𝗔𝗯𝗰"),
    MONOSPACE("Mono", "𝙰𝚋𝚌"),
    DOUBLE_STRUCK("Outline", "𝔸𝕓𝕔")
}

object FancyFontConverter {

    fun convert(text: String, style: TextStyleMode): String {
        if (style == TextStyleMode.NORMAL) return text

        val sb = StringBuilder()
        for (ch in text) {
            sb.append(convertChar(ch, style))
        }
        return sb.toString()
    }

    private fun convertChar(ch: Char, style: TextStyleMode): String {
        return when (style) {
            TextStyleMode.NORMAL -> ch.toString()
            TextStyleMode.CURSIVE -> {
                when (ch) {
                    in 'A'..'Z' -> String(Character.toChars(0x1D4D0 + (ch - 'A')))
                    in 'a'..'z' -> String(Character.toChars(0x1D4EA + (ch - 'a')))
                    in '0'..'9' -> ch.toString()
                    else -> ch.toString()
                }
            }
            TextStyleMode.BOLD -> {
                when (ch) {
                    in 'A'..'Z' -> String(Character.toChars(0x1D5D4 + (ch - 'A')))
                    in 'a'..'z' -> String(Character.toChars(0x1D5EE + (ch - 'a')))
                    in '0'..'9' -> String(Character.toChars(0x1D7EC + (ch - '0')))
                    else -> ch.toString()
                }
            }
            TextStyleMode.MONOSPACE -> {
                when (ch) {
                    in 'A'..'Z' -> String(Character.toChars(0x1D670 + (ch - 'A')))
                    in 'a'..'z' -> String(Character.toChars(0x1D68A + (ch - 'a')))
                    in '0'..'9' -> String(Character.toChars(0x1D7F6 + (ch - '0')))
                    else -> ch.toString()
                }
            }
            TextStyleMode.DOUBLE_STRUCK -> {
                when (ch) {
                    'C' -> "ℂ"
                    'H' -> "ℍ"
                    'N' -> "ℕ"
                    'P' -> "ℙ"
                    'Q' -> "ℚ"
                    'R' -> "ℝ"
                    'Z' -> "ℤ"
                    in 'A'..'Z' -> String(Character.toChars(0x1D538 + (ch - 'A')))
                    in 'a'..'z' -> String(Character.toChars(0x1D552 + (ch - 'a')))
                    in '0'..'9' -> String(Character.toChars(0x1D7D8 + (ch - '0')))
                    else -> ch.toString()
                }
            }
        }
    }
}
