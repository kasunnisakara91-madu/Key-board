package com.example.keyboard

enum class KeyType {
    CHARACTER,
    SHIFT,
    BACKSPACE,
    ENTER,
    SPACE,
    SWITCH_NUMBERS,
    SWITCH_LETTERS,
    SWITCH_EXT_SYMBOLS,
    SWITCH_LANGUAGE,
    SWITCH_EMOJI,
    CLIPBOARD
}

data class KeyModel(
    val primaryText: String,
    val cornerHint: String = "",
    val sinhalaText: String = cornerHint.ifEmpty { primaryText },
    val type: KeyType = KeyType.CHARACTER,
    val weight: Float = 1.0f,
    val outputText: String = primaryText
)

enum class KeyboardMode {
    LETTERS,
    NUMBERS,
    EXTENDED_SYMBOLS,
    EMOJIS,
    CLIPBOARD,
    TEXT_EDIT,
    FONT_STYLES
}

enum class ShiftState {
    OFF,
    ONCE,
    CAPS_LOCK
}

enum class EmojiCategory(val label: String, val icon: String) {
    RECENT("Recent", "🕒"),
    SMILEYS("Smileys", "😀"),
    GESTURES("Gestures", "👍"),
    HEARTS("Hearts", "❤️"),
    FOOD("Food", "🍕"),
    ANIMALS("Nature", "🐱"),
    OBJECTS("Objects", "💡")
}

object KeyboardLayouts {

    // Quick emoji ribbon matching the user's screenshot
    val quickEmojiRibbon = listOf("😌", "🧚", "❤️", "🌸", "🦋", "🌓", "🌎", "⌨️", "🎹", "✨", "🔥", "😂", "👍")

    // English Rows with exact Sinhala corner letters from the user's image!
    val dualScriptRow1 = listOf(
        KeyModel(primaryText = "q", cornerHint = "ද"),
        KeyModel(primaryText = "w", cornerHint = "ව"),
        KeyModel(primaryText = "e", cornerHint = "එ"),
        KeyModel(primaryText = "r", cornerHint = "ර"),
        KeyModel(primaryText = "t", cornerHint = "ට"),
        KeyModel(primaryText = "y", cornerHint = "ය"),
        KeyModel(primaryText = "u", cornerHint = "උ"),
        KeyModel(primaryText = "i", cornerHint = "ඉ"),
        KeyModel(primaryText = "o", cornerHint = "ඔ"),
        KeyModel(primaryText = "p", cornerHint = "ප")
    )

    val dualScriptRow2 = listOf(
        KeyModel(primaryText = "a", cornerHint = "අ"),
        KeyModel(primaryText = "s", cornerHint = "ස"),
        KeyModel(primaryText = "d", cornerHint = "ඩ"),
        KeyModel(primaryText = "f", cornerHint = "ෆ"),
        KeyModel(primaryText = "g", cornerHint = "ග"),
        KeyModel(primaryText = "h", cornerHint = "හ"),
        KeyModel(primaryText = "j", cornerHint = "ජ"),
        KeyModel(primaryText = "k", cornerHint = "ක"),
        KeyModel(primaryText = "l", cornerHint = "ල")
    )

    val dualScriptRow3 = listOf(
        KeyModel(primaryText = "z", cornerHint = "ඥ"),
        KeyModel(primaryText = "x", cornerHint = "ං"),
        KeyModel(primaryText = "c", cornerHint = "ච"),
        KeyModel(primaryText = "v", cornerHint = "ළු"),
        KeyModel(primaryText = "b", cornerHint = "බ"),
        KeyModel(primaryText = "n", cornerHint = "න"),
        KeyModel(primaryText = "m", cornerHint = "ම")
    )

    // Numbers & Basic Symbols
    val numbersRow1 = listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "0").map { KeyModel(primaryText = it) }
    val numbersRow2 = listOf("@", "#", "$", "%", "&", "-", "+", "(", ")", "/").map { KeyModel(primaryText = it) }
    val numbersRow3 = listOf("*", "\"", "'", ":", ";", "!", "?").map { KeyModel(primaryText = it) }

    // Extended Symbols
    val extSymbolsRow1 = listOf("~", "`", "|", "^", "=", "{", "}", "[", "]", "\\").map { KeyModel(primaryText = it) }
    val extSymbolsRow2 = listOf("_", "<", ">", "€", "£", "¥", "©", "®", "°", "•").map { KeyModel(primaryText = it) }
    val extSymbolsRow3 = listOf("«", "»", "§", "±", "×", "÷", "%").map { KeyModel(primaryText = it) }

    // Categorized Emojis
    val emojiCategories = mapOf(
        EmojiCategory.SMILEYS to listOf(
            "😀", "😃", "😄", "😁", "😆", "😅", "😂", "🤣", "🥲", "☺️", "😊", "😇",
            "🙂", "🙃", "😉", "😌", "😍", "🥰", "😘", "😗", "😙", "😚", "😋", "😛",
            "😝", "😜", "🤪", "🤨", "🧐", "🤓", "😎", "🥳", "😏", "😒", "😞", "😔",
            "😟", "😕", "🙁", "😣", "😖", "😫", "😩", "🥺", "😢", "😭", "😤", "😠",
            "😡", "🤬", "🤯", "😳", "🥵", "🥶", "😱", "😨", "😰", "😥", "😓", "🤗"
        ),
        EmojiCategory.GESTURES to listOf(
            "👍", "👎", "👏", "🙌", "👐", "🤲", "🤝", "🙏", "✍️", "💅", "🤳", "💪",
            "🦾", "🦿", "🦵", "🦶", "👂", "👃", "🧠", "🫀", "🫁", "🦷", "🦴", "👀",
            "👁️", "👅", "👄", "👋", "🤚", "🖐️", "✋", "🖖", "👌", "🤌", "🤏", "✌️"
        ),
        EmojiCategory.HEARTS to listOf(
            "❤️", "🧡", "💛", "💚", "💙", "💜", "🖤", "🤍", "🤎", "💔", "❣️", "💕",
            "💞", "💓", "💗", "💖", "💘", "💝", "💟", "🔥", "✨", "⭐", "🌟", "💫",
            "💥", "💯", "💢", "💬", "👁️‍🗨️", "🗨️", "🗯️", "💭", "💤", "👑", "💎"
        ),
        EmojiCategory.FOOD to listOf(
            "🍕", "🍔", "🍟", "🌭", "🍿", "🧈", "🧇", "🥞", "🧀", "🥩", "🍗", "🍖",
            "🍏", "🍎", "🍐", "🍊", "🍋", "🍌", "🍉", "🍇", "🍓", "🫐", "🍈", "🍒",
            "🍑", "🥭", "🍍", "🥥", "🥝", "🍅", "🥑", "🥦", "🍦", "🍧", "🍨", "🍩"
        ),
        EmojiCategory.ANIMALS to listOf(
            "🐶", "🐱", "🐭", "🐹", "🐰", "🦊", "🐻", "🐼", "🐨", "🐯", "🦁", "🐮",
            "🐷", "🐸", "🐵", "🐔", "🐧", "🐦", "🐤", "🦆", "🦅", "🦉", "🦇", "🐺",
            "🐗", "🐴", "🦄", "🐝", "🪱", "🐛", "🦋", "🐌", "🐞", "🐜", "🪰"
        ),
        EmojiCategory.OBJECTS to listOf(
            "📱", "💻", "⌨️", "🖥️", "🖨️", "🖱️", "🕹️", "📷", "📸", "📹", "🎥", "📽️",
            "📞", "☎️", "📟", "📠", "📺", "📻", "🎙️", "🎚️", "🧭", "⏰", "⏱️", "⏲️",
            "💡", "🔦", "🕯️", "🪔", "🔌", "🔋", "🔍", "🔎", "🔑", "🗝️", "🚪", "🪑"
        )
    )
}
