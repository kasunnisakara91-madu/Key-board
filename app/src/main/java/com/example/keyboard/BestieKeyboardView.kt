package com.example.keyboard

import android.view.inputmethod.EditorInfo
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardReturn
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R

val ScriptFontFamily = FontFamily(Font(R.font.dancing_script))
val CaveatFontFamily = FontFamily(Font(R.font.caveat))

@Composable
fun BestieKeyboardView(
    settings: KeyboardSettings,
    imeOptions: Int = EditorInfo.IME_ACTION_DONE,
    suggestions: List<String> = emptyList(),
    clipboardItems: List<ClipboardItem> = emptyList(),
    onKeyPress: (String) -> Unit,
    onBackspace: () -> Unit,
    onEnter: () -> Unit,
    onSpace: () -> Unit,
    onSuggestionClick: (String) -> Unit,
    onClipboardItemClick: (String) -> Unit,
    onClipboardDelete: (String) -> Unit,
    onClipboardClearAll: () -> Unit,
    onLanguageSwitch: () -> Unit,
    onThemeSwitchRequest: () -> Unit = {},
    onSettingsOpenRequest: () -> Unit = {},
    onHideKeyboard: () -> Unit = {},
    onCursorMoveLeft: () -> Unit = {},
    onCursorMoveRight: () -> Unit = {},
    onSelectAll: () -> Unit = {},
    onCut: () -> Unit = {},
    onCopy: () -> Unit = {},
    onPaste: () -> Unit = {},
    onTextStyleChange: (TextStyleMode) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val theme = KeyboardThemes.getById(settings.themeId)

    var currentMode by remember { mutableStateOf(KeyboardMode.LETTERS) }
    var shiftState by remember { mutableStateOf(ShiftState.OFF) }
    var activeEmojiCategory by remember { mutableStateOf(EmojiCategory.RECENT) }
    var currentTextStyle by remember { mutableStateOf(settings.textStyleMode) }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .testTag("bestie_keyboard_root")
    ) {
        // BACKGROUND: Fiery Red Lightning texture or theme gradient
        if (theme.usesBackgroundDrawable) {
            Image(
                painter = painterResource(id = R.drawable.keyboard_bg_lightning),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.matchParentSize()
            )
            // Dark scrim so keys remain ultra-readable
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(Color.Black.copy(alpha = 0.35f))
            )
        } else {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(brush = theme.backgroundBrush)
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp, vertical = 4.dp)
        ) {
            // 1. TOP CIRCULAR ACTION BUTTONS ROW (EXACTLY LIKE THE SCREENSHOT!)
            TopRoundActionRibbon(
                language = settings.currentLanguage,
                activeMode = currentMode,
                textStyle = currentTextStyle,
                theme = theme,
                onLanguageToggle = onLanguageSwitch,
                onEmojiClick = {
                    currentMode = if (currentMode == KeyboardMode.EMOJIS) KeyboardMode.LETTERS else KeyboardMode.EMOJIS
                },
                onTextStyleClick = {
                    val allStyles = TextStyleMode.values()
                    val next = allStyles[(allStyles.indexOf(currentTextStyle) + 1) % allStyles.size]
                    currentTextStyle = next
                    onTextStyleChange(next)
                },
                onClipboardClick = {
                    currentMode = if (currentMode == KeyboardMode.CLIPBOARD) KeyboardMode.LETTERS else KeyboardMode.CLIPBOARD
                },
                onFontSizeClick = {
                    // Toggle Shift or caps
                    shiftState = when (shiftState) {
                        ShiftState.OFF -> ShiftState.ONCE
                        ShiftState.ONCE -> ShiftState.CAPS_LOCK
                        ShiftState.CAPS_LOCK -> ShiftState.OFF
                    }
                },
                onTextEditorClick = {
                    currentMode = if (currentMode == KeyboardMode.TEXT_EDIT) KeyboardMode.LETTERS else KeyboardMode.TEXT_EDIT
                },
                onHideClick = onHideKeyboard
            )

            // 2. QUICK EMOJI RIBBON (UNDER ACTION BUTTONS, FROM SCREENSHOT)
            QuickEmojiRibbon(
                onEmojiClick = { emoji ->
                    onKeyPress(emoji)
                }
            )

            // 3. WORD SUGGESTIONS (IF ENABLED)
            if (settings.suggestionsEnabled && suggestions.isNotEmpty() && currentMode == KeyboardMode.LETTERS) {
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(30.dp)
                        .padding(horizontal = 4.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    items(suggestions) { word ->
                        SuggestionPill(
                            word = word,
                            theme = theme,
                            onClick = { onSuggestionClick(word) }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(2.dp))

            // 4. MAIN KEYBOARD BODY
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(settings.height.dpValue.dp)
            ) {
                when (currentMode) {
                    KeyboardMode.EMOJIS -> {
                        EmojiView(
                            theme = theme,
                            recentEmojis = settings.recentEmojis,
                            activeCategory = activeEmojiCategory,
                            onCategorySelect = { activeEmojiCategory = it },
                            onEmojiSelect = { emoji -> onKeyPress(emoji) },
                            onBackToLetters = { currentMode = KeyboardMode.LETTERS },
                            onBackspace = onBackspace
                        )
                    }

                    KeyboardMode.CLIPBOARD -> {
                        ClipboardView(
                            theme = theme,
                            items = clipboardItems,
                            onItemClick = { item ->
                                onClipboardItemClick(item.text)
                                currentMode = KeyboardMode.LETTERS
                            },
                            onItemDelete = onClipboardDelete,
                            onClearAll = onClipboardClearAll,
                            onBackToLetters = { currentMode = KeyboardMode.LETTERS }
                        )
                    }

                    KeyboardMode.TEXT_EDIT -> {
                        TextEditingToolbar(
                            theme = theme,
                            onMoveLeft = onCursorMoveLeft,
                            onMoveRight = onCursorMoveRight,
                            onSelectAll = onSelectAll,
                            onCut = onCut,
                            onCopy = onCopy,
                            onPaste = onPaste,
                            onDone = { currentMode = KeyboardMode.LETTERS }
                        )
                    }

                    KeyboardMode.LETTERS -> {
                        DualScriptKeyboardView(
                            theme = theme,
                            language = settings.currentLanguage,
                            shiftState = shiftState,
                            textStyle = currentTextStyle,
                            imeOptions = imeOptions,
                            onKey = { key ->
                                val formatted = FancyFontConverter.convert(key, currentTextStyle)
                                onKeyPress(formatted)
                                if (shiftState == ShiftState.ONCE) {
                                    shiftState = ShiftState.OFF
                                }
                            },
                            onShiftToggle = {
                                shiftState = when (shiftState) {
                                    ShiftState.OFF -> ShiftState.ONCE
                                    ShiftState.ONCE -> ShiftState.CAPS_LOCK
                                    ShiftState.CAPS_LOCK -> ShiftState.OFF
                                }
                            },
                            onBackspace = onBackspace,
                            onEnter = onEnter,
                            onSpace = onSpace,
                            onSwitchToNumbers = { currentMode = KeyboardMode.NUMBERS },
                            onSwitchLanguage = onLanguageSwitch
                        )
                    }

                    KeyboardMode.NUMBERS -> {
                        SymbolKeyboardView(
                            theme = theme,
                            isExtended = false,
                            imeOptions = imeOptions,
                            onKey = onKeyPress,
                            onBackspace = onBackspace,
                            onEnter = onEnter,
                            onSpace = onSpace,
                            onSwitchToLetters = { currentMode = KeyboardMode.LETTERS },
                            onSwitchToExtSymbols = { currentMode = KeyboardMode.EXTENDED_SYMBOLS }
                        )
                    }

                    KeyboardMode.EXTENDED_SYMBOLS -> {
                        SymbolKeyboardView(
                            theme = theme,
                            isExtended = true,
                            imeOptions = imeOptions,
                            onKey = onKeyPress,
                            onBackspace = onBackspace,
                            onEnter = onEnter,
                            onSpace = onSpace,
                            onSwitchToLetters = { currentMode = KeyboardMode.LETTERS },
                            onSwitchToExtSymbols = { currentMode = KeyboardMode.NUMBERS }
                        )
                    }

                    else -> {
                        currentMode = KeyboardMode.LETTERS
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// TOP ROUND ACTION BUTTONS ROW (From User Screenshot)
// -------------------------------------------------------------
@Composable
private fun TopRoundActionRibbon(
    language: KeyboardLanguage,
    activeMode: KeyboardMode,
    textStyle: TextStyleMode,
    theme: KeyboardThemeData,
    onLanguageToggle: () -> Unit,
    onEmojiClick: () -> Unit,
    onTextStyleClick: () -> Unit,
    onClipboardClick: () -> Unit,
    onFontSizeClick: () -> Unit,
    onTextEditorClick: () -> Unit,
    onHideClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(44.dp)
            .padding(horizontal = 2.dp, vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 1. Sinhala toggle circle button: 'සිං'
        RoundActionButton(
            text = if (language == KeyboardLanguage.SINHALA) "සිං" else "EN",
            highlighted = language == KeyboardLanguage.SINHALA,
            borderColor = theme.keyBorderColor,
            onClick = onLanguageToggle,
            testTag = "btn_round_sinhala"
        )

        // 2. Emoji face button
        RoundActionButton(
            icon = Icons.Default.Mood,
            highlighted = activeMode == KeyboardMode.EMOJIS,
            borderColor = theme.keyBorderColor,
            onClick = onEmojiClick,
            testTag = "btn_round_emoji"
        )

        // 3. Fancy Font Styler: 'Ἂά'
        RoundActionButton(
            text = "Ἂά",
            highlighted = textStyle != TextStyleMode.NORMAL,
            borderColor = theme.keyBorderColor,
            onClick = onTextStyleClick,
            testTag = "btn_round_font"
        )

        // 4. Clipboard button
        RoundActionButton(
            icon = Icons.Default.ContentPaste,
            highlighted = activeMode == KeyboardMode.CLIPBOARD,
            borderColor = theme.keyBorderColor,
            onClick = onClipboardClick,
            testTag = "btn_round_clipboard"
        )

        // 5. 'Aa' Case / Font size button
        RoundActionButton(
            text = "Aa",
            highlighted = false,
            borderColor = theme.keyBorderColor,
            onClick = onFontSizeClick,
            testTag = "btn_round_case"
        )

        // 6. Text Editor / Cursor Mover: '[|]'
        RoundActionButton(
            icon = Icons.Default.VerticalDistribute,
            highlighted = activeMode == KeyboardMode.TEXT_EDIT,
            borderColor = theme.keyBorderColor,
            onClick = onTextEditorClick,
            testTag = "btn_round_editor"
        )

        // 7. Collapse / Hide Keyboard: '˅'
        RoundActionButton(
            icon = Icons.Default.KeyboardArrowDown,
            highlighted = false,
            borderColor = theme.keyBorderColor,
            onClick = onHideClick,
            testTag = "btn_round_hide"
        )
    }
}

@Composable
private fun RoundActionButton(
    text: String? = null,
    icon: ImageVector? = null,
    highlighted: Boolean = false,
    borderColor: Color,
    onClick: () -> Unit,
    testTag: String? = null
) {
    Surface(
        modifier = Modifier
            .size(38.dp)
            .border(
                width = 1.dp,
                color = if (highlighted) borderColor else borderColor.copy(alpha = 0.4f),
                shape = CircleShape
            )
            .then(if (testTag != null) Modifier.testTag(testTag) else Modifier),
        shape = CircleShape,
        color = if (highlighted) borderColor.copy(alpha = 0.25f) else Color.Black.copy(alpha = 0.55f),
        onClick = onClick
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (highlighted) borderColor else Color.White.copy(alpha = 0.9f),
                    modifier = Modifier.size(19.dp)
                )
            } else if (text != null) {
                Text(
                    text = text,
                    color = if (highlighted) borderColor else Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
            }
        }
    }
}

// -------------------------------------------------------------
// QUICK EMOJI RIBBON (Row of favorites from user screenshot)
// -------------------------------------------------------------
@Composable
private fun QuickEmojiRibbon(
    onEmojiClick: (String) -> Unit
) {
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .height(34.dp)
            .padding(horizontal = 2.dp, vertical = 1.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        items(KeyboardLayouts.quickEmojiRibbon) { emoji ->
            Box(
                modifier = Modifier
                    .size(30.dp)
                    .clip(CircleShape)
                    .clickable { onEmojiClick(emoji) },
                contentAlignment = Alignment.Center
            ) {
                Text(text = emoji, fontSize = 20.sp)
            }
        }
    }
}

// -------------------------------------------------------------
// DUAL SCRIPT KEYBOARD VIEW (English Cursive + Sinhala Corner)
// -------------------------------------------------------------
@Composable
private fun DualScriptKeyboardView(
    theme: KeyboardThemeData,
    language: KeyboardLanguage,
    shiftState: ShiftState,
    textStyle: TextStyleMode,
    imeOptions: Int,
    onKey: (String) -> Unit,
    onShiftToggle: () -> Unit,
    onBackspace: () -> Unit,
    onEnter: () -> Unit,
    onSpace: () -> Unit,
    onSwitchToNumbers: () -> Unit,
    onSwitchLanguage: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        // ROW 1: q w e r t y u i o p (with Sinhala corner hints)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            KeyboardLayouts.dualScriptRow1.forEach { keyModel ->
                DualScriptKey(
                    modifier = Modifier.weight(1f),
                    keyModel = keyModel,
                    shiftActive = shiftState != ShiftState.OFF,
                    language = language,
                    theme = theme,
                    onClick = {
                        val char = if (language == KeyboardLanguage.SINHALA) {
                            keyModel.sinhalaText
                        } else {
                            if (shiftState != ShiftState.OFF) keyModel.primaryText.uppercase() else keyModel.primaryText
                        }
                        onKey(char)
                    }
                )
            }
        }

        // ROW 2: a s d f g h j k l
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            KeyboardLayouts.dualScriptRow2.forEach { keyModel ->
                DualScriptKey(
                    modifier = Modifier.weight(1f),
                    keyModel = keyModel,
                    shiftActive = shiftState != ShiftState.OFF,
                    language = language,
                    theme = theme,
                    onClick = {
                        val char = if (language == KeyboardLanguage.SINHALA) {
                            keyModel.sinhalaText
                        } else {
                            if (shiftState != ShiftState.OFF) keyModel.primaryText.uppercase() else keyModel.primaryText
                        }
                        onKey(char)
                    }
                )
            }
        }

        // ROW 3: Shift, z x c v b n m, Backspace
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // Shift Key with hollow arrow
            Surface(
                modifier = Modifier
                    .weight(1.3f)
                    .fillMaxHeight()
                    .border(BorderStroke(1.2.dp, theme.keyBorderColor), RoundedCornerShape(12.dp))
                    .testTag("key_shift"),
                shape = RoundedCornerShape(12.dp),
                color = if (shiftState != ShiftState.OFF) theme.keyBorderColor.copy(alpha = 0.3f) else theme.keyBackgroundColor,
                shadowElevation = 2.dp,
                onClick = onShiftToggle
            ) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = if (shiftState == ShiftState.CAPS_LOCK) Icons.Default.KeyboardCapslock else Icons.Default.ArrowUpward,
                        contentDescription = "Shift",
                        tint = if (shiftState != ShiftState.OFF) theme.keyBorderColor else Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            KeyboardLayouts.dualScriptRow3.forEach { keyModel ->
                DualScriptKey(
                    modifier = Modifier.weight(1f),
                    keyModel = keyModel,
                    shiftActive = shiftState != ShiftState.OFF,
                    language = language,
                    theme = theme,
                    onClick = {
                        val char = if (language == KeyboardLanguage.SINHALA) {
                            keyModel.sinhalaText
                        } else {
                            if (shiftState != ShiftState.OFF) keyModel.primaryText.uppercase() else keyModel.primaryText
                        }
                        onKey(char)
                    }
                )
            }

            // Backspace Key
            Surface(
                modifier = Modifier
                    .weight(1.3f)
                    .fillMaxHeight()
                    .border(BorderStroke(1.2.dp, theme.keyBorderColor), RoundedCornerShape(12.dp))
                    .testTag("key_backspace"),
                shape = RoundedCornerShape(12.dp),
                color = theme.keyBackgroundColor,
                shadowElevation = 2.dp,
                onClick = onBackspace
            ) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Backspace",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        // ROW 4: 123 (Script), 🌐, Flashboard/BESTIE Spacebar (Translucent Green), ., Enter (Neon Green Pill)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // 123 Script key
            Surface(
                modifier = Modifier
                    .weight(1.2f)
                    .fillMaxHeight()
                    .border(BorderStroke(1.2.dp, theme.keyBorderColor), RoundedCornerShape(12.dp)),
                shape = RoundedCornerShape(12.dp),
                color = theme.keyBackgroundColor,
                onClick = onSwitchToNumbers
            ) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = "123",
                        fontFamily = ScriptFontFamily,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

            // Globe key
            Surface(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .border(BorderStroke(1.2.dp, theme.keyBorderColor), RoundedCornerShape(12.dp)),
                shape = RoundedCornerShape(12.dp),
                color = theme.keyBackgroundColor,
                onClick = onSwitchLanguage
            ) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.Language,
                        contentDescription = "Language",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // SPACEBAR: Translucent glass green pill with cursive 'BESTIE' text
            Surface(
                modifier = Modifier
                    .weight(3.6f)
                    .fillMaxHeight()
                    .border(
                        BorderStroke(1.5.dp, Color(0xFF00E676).copy(alpha = 0.7f)),
                        RoundedCornerShape(20.dp)
                    ),
                shape = RoundedCornerShape(20.dp),
                color = Color(0x3300C853), // translucent green tint matching screenshot
                shadowElevation = 2.dp,
                onClick = onSpace
            ) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = if (language == KeyboardLanguage.SINHALA) "සිංහල" else "BESTIE",
                        fontFamily = ScriptFontFamily,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White.copy(alpha = 0.95f)
                    )
                }
            }

            // Period . key
            Surface(
                modifier = Modifier
                    .weight(0.9f)
                    .fillMaxHeight()
                    .border(BorderStroke(1.2.dp, theme.keyBorderColor), RoundedCornerShape(12.dp)),
                shape = RoundedCornerShape(12.dp),
                color = theme.keyBackgroundColor,
                onClick = { onKey(".") }
            ) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = ".", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            }

            // ENTER KEY: Big Vibrant Neon Green Pill with white/black return arrow
            Surface(
                modifier = Modifier
                    .weight(1.5f)
                    .fillMaxHeight()
                    .border(BorderStroke(1.5.dp, Color(0xFF69F0AE)), RoundedCornerShape(20.dp)),
                shape = RoundedCornerShape(20.dp),
                color = Color(0xFF00E676), // Solid Neon Green from screenshot
                shadowElevation = 4.dp,
                onClick = onEnter
            ) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardReturn,
                        contentDescription = "Enter",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}

// -------------------------------------------------------------
// DUAL SCRIPT KEY COMPONENT (Main letter + Sinhala corner hint)
// -------------------------------------------------------------
@Composable
private fun DualScriptKey(
    modifier: Modifier = Modifier,
    keyModel: KeyModel,
    shiftActive: Boolean,
    language: KeyboardLanguage,
    theme: KeyboardThemeData,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale = if (isPressed) 0.92f else 1.0f

    val mainChar = if (language == KeyboardLanguage.SINHALA) {
        keyModel.sinhalaText
    } else {
        if (shiftActive) keyModel.primaryText.uppercase() else keyModel.primaryText
    }

    val cornerHint = if (language == KeyboardLanguage.SINHALA) {
        keyModel.primaryText
    } else {
        keyModel.cornerHint
    }

    Surface(
        modifier = modifier
            .fillMaxHeight()
            .scale(scale)
            .border(
                BorderStroke(
                    width = 1.3.dp,
                    color = if (isPressed) Color.White else theme.keyBorderColor
                ),
                shape = RoundedCornerShape(12.dp)
            ),
        shape = RoundedCornerShape(12.dp),
        color = if (isPressed) Color(0xAA111111) else theme.keyBackgroundColor,
        shadowElevation = if (isPressed) 1.dp else 2.dp,
        onClick = onClick,
        interactionSource = interactionSource
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 3.dp, vertical = 2.dp)
        ) {
            // Corner Sinhala Hint in top right
            if (cornerHint.isNotEmpty()) {
                Text(
                    text = cornerHint,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Normal,
                    color = theme.cornerHintColor,
                    modifier = Modifier.align(Alignment.TopEnd)
                )
            }

            // Center Main Letter in Cursive Script
            Text(
                text = mainChar,
                fontFamily = if (language == KeyboardLanguage.ENGLISH) ScriptFontFamily else FontFamily.Default,
                fontSize = if (language == KeyboardLanguage.ENGLISH) 20.sp else 16.sp,
                fontWeight = FontWeight.Medium,
                color = theme.keyTextColor,
                textAlign = TextAlign.Center,
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}

// -------------------------------------------------------------
// SYMBOL KEYBOARD VIEW
// -------------------------------------------------------------
@Composable
private fun SymbolKeyboardView(
    theme: KeyboardThemeData,
    isExtended: Boolean,
    imeOptions: Int,
    onKey: (String) -> Unit,
    onBackspace: () -> Unit,
    onEnter: () -> Unit,
    onSpace: () -> Unit,
    onSwitchToLetters: () -> Unit,
    onSwitchToExtSymbols: () -> Unit
) {
    val row1 = if (isExtended) KeyboardLayouts.extSymbolsRow1 else KeyboardLayouts.numbersRow1
    val row2 = if (isExtended) KeyboardLayouts.extSymbolsRow2 else KeyboardLayouts.numbersRow2
    val row3 = if (isExtended) KeyboardLayouts.extSymbolsRow3 else KeyboardLayouts.numbersRow3

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        // Row 1
        Row(modifier = Modifier.fillMaxWidth().weight(1f), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            row1.forEach { keyModel ->
                SimpleKey(modifier = Modifier.weight(1f), text = keyModel.primaryText, theme = theme, onClick = { onKey(it) })
            }
        }

        // Row 2
        Row(modifier = Modifier.fillMaxWidth().weight(1f), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            row2.forEach { keyModel ->
                SimpleKey(modifier = Modifier.weight(1f), text = keyModel.primaryText, theme = theme, onClick = { onKey(it) })
            }
        }

        // Row 3
        Row(modifier = Modifier.fillMaxWidth().weight(1f), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            Surface(
                modifier = Modifier.weight(1.3f).fillMaxHeight().border(BorderStroke(1.2.dp, theme.keyBorderColor), RoundedCornerShape(12.dp)),
                shape = RoundedCornerShape(12.dp),
                color = theme.keyBackgroundColor,
                onClick = onSwitchToExtSymbols
            ) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = if (isExtended) "123" else "=\\<", color = theme.keyBorderColor, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
            }

            row3.forEach { keyModel ->
                SimpleKey(modifier = Modifier.weight(1f), text = keyModel.primaryText, theme = theme, onClick = { onKey(it) })
            }

            Surface(
                modifier = Modifier.weight(1.3f).fillMaxHeight().border(BorderStroke(1.2.dp, theme.keyBorderColor), RoundedCornerShape(12.dp)),
                shape = RoundedCornerShape(12.dp),
                color = theme.keyBackgroundColor,
                onClick = onBackspace
            ) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Backspace", tint = Color.White, modifier = Modifier.size(20.dp))
                }
            }
        }

        // Row 4
        Row(modifier = Modifier.fillMaxWidth().weight(1f), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            Surface(
                modifier = Modifier.weight(1.3f).fillMaxHeight().border(BorderStroke(1.2.dp, theme.keyBorderColor), RoundedCornerShape(12.dp)),
                shape = RoundedCornerShape(12.dp),
                color = theme.keyBackgroundColor,
                onClick = onSwitchToLetters
            ) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("ABC", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
            }

            Surface(
                modifier = Modifier.weight(3.5f).fillMaxHeight().border(BorderStroke(1.3.dp, Color(0xFF00E676).copy(alpha = 0.7f)), RoundedCornerShape(20.dp)),
                shape = RoundedCornerShape(20.dp),
                color = Color(0x3300C853),
                onClick = onSpace
            ) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Space", fontFamily = ScriptFontFamily, fontSize = 18.sp, color = Color.White)
                }
            }

            SimpleKey(modifier = Modifier.weight(1f), text = ".", theme = theme, onClick = { onKey(".") })

            Surface(
                modifier = Modifier.weight(1.5f).fillMaxHeight().border(BorderStroke(1.3.dp, Color(0xFF69F0AE)), RoundedCornerShape(20.dp)),
                shape = RoundedCornerShape(20.dp),
                color = Color(0xFF00E676),
                onClick = onEnter
            ) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.KeyboardReturn, contentDescription = "Enter", tint = Color.White, modifier = Modifier.size(22.dp))
                }
            }
        }
    }
}

@Composable
private fun SimpleKey(
    modifier: Modifier = Modifier,
    text: String,
    theme: KeyboardThemeData,
    onClick: (String) -> Unit
) {
    Surface(
        modifier = modifier
            .fillMaxHeight()
            .border(BorderStroke(1.2.dp, theme.keyBorderColor), RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(12.dp),
        color = theme.keyBackgroundColor,
        onClick = { onClick(text) }
    ) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(text = text, color = Color.White, fontSize = 17.sp, fontWeight = FontWeight.Medium)
        }
    }
}

// -------------------------------------------------------------
// TEXT EDITING TOOLBAR (Precision cursor navigation [|])
// -------------------------------------------------------------
@Composable
private fun TextEditingToolbar(
    theme: KeyboardThemeData,
    onMoveLeft: () -> Unit,
    onMoveRight: () -> Unit,
    onSelectAll: () -> Unit,
    onCut: () -> Unit,
    onCopy: () -> Unit,
    onPaste: () -> Unit,
    onDone: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp),
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        Text(
            text = "Text Editing & Cursor Navigator",
            color = theme.keyBorderColor,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = onSelectAll,
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = theme.keyBackgroundColor),
                border = BorderStroke(1.dp, theme.keyBorderColor),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Select All", color = Color.White, fontSize = 12.sp)
            }

            Button(
                onClick = onCut,
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = theme.keyBackgroundColor),
                border = BorderStroke(1.dp, theme.keyBorderColor),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Cut", color = Color.White, fontSize = 12.sp)
            }

            Button(
                onClick = onCopy,
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = theme.keyBackgroundColor),
                border = BorderStroke(1.dp, theme.keyBorderColor),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Copy", color = Color.White, fontSize = 12.sp)
            }

            Button(
                onClick = onPaste,
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = theme.keyBackgroundColor),
                border = BorderStroke(1.dp, theme.keyBorderColor),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Paste", color = Color.White, fontSize = 12.sp)
            }
        }

        // Direction Arrows
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedButton(
                onClick = onMoveLeft,
                modifier = Modifier.weight(1f),
                border = BorderStroke(1.5.dp, theme.keyBorderColor),
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Left", tint = theme.keyBorderColor)
                Spacer(modifier = Modifier.width(4.dp))
                Text("Left", color = Color.White)
            }

            OutlinedButton(
                onClick = onMoveRight,
                modifier = Modifier.weight(1f),
                border = BorderStroke(1.5.dp, theme.keyBorderColor),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Right", color = Color.White)
                Spacer(modifier = Modifier.width(4.dp))
                Icon(Icons.Default.ArrowForward, contentDescription = "Right", tint = theme.keyBorderColor)
            }

            Button(
                onClick = onDone,
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E676)),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Done", color = Color.Black, fontWeight = FontWeight.Bold)
            }
        }
    }
}

// -------------------------------------------------------------
// EMOJI VIEW
// -------------------------------------------------------------
@Composable
private fun EmojiView(
    theme: KeyboardThemeData,
    recentEmojis: List<String>,
    activeCategory: EmojiCategory,
    onCategorySelect: (EmojiCategory) -> Unit,
    onEmojiSelect: (String) -> Unit,
    onBackToLetters: () -> Unit,
    onBackspace: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        LazyRow(
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            items(EmojiCategory.values()) { category ->
                val isSelected = category == activeCategory
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = if (isSelected) Color(0xFF00E676) else Color.Black.copy(alpha = 0.6f),
                    border = BorderStroke(1.dp, if (isSelected) Color(0xFF00E676) else theme.keyBorderColor.copy(alpha = 0.5f)),
                    modifier = Modifier.clickable { onCategorySelect(category) }
                ) {
                    Text(
                        text = "${category.icon} ${category.label}",
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                        fontSize = 12.sp,
                        color = if (isSelected) Color.Black else Color.White,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }

        val emojis = if (activeCategory == EmojiCategory.RECENT) {
            recentEmojis
        } else {
            KeyboardLayouts.emojiCategories[activeCategory] ?: emptyList()
        }

        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 42.dp),
            modifier = Modifier.fillMaxWidth().weight(1f),
            contentPadding = PaddingValues(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items(emojis) { emoji ->
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { onEmojiSelect(emoji) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = emoji, fontSize = 24.sp)
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth().height(42.dp).padding(top = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = onBackToLetters,
                modifier = Modifier.weight(1f).fillMaxHeight(),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = theme.keyBackgroundColor),
                border = BorderStroke(1.2.dp, theme.keyBorderColor)
            ) {
                Text("ABC", color = Color.White, fontWeight = FontWeight.Bold)
            }

            IconButton(
                onClick = onBackspace,
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(theme.keyBackgroundColor)
                    .border(BorderStroke(1.2.dp, theme.keyBorderColor), RoundedCornerShape(10.dp))
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Backspace", tint = Color.White)
            }
        }
    }
}

// -------------------------------------------------------------
// CLIPBOARD VIEW
// -------------------------------------------------------------
@Composable
private fun ClipboardView(
    theme: KeyboardThemeData,
    items: List<ClipboardItem>,
    onItemClick: (ClipboardItem) -> Unit,
    onItemDelete: (String) -> Unit,
    onClearAll: () -> Unit,
    onBackToLetters: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Clipboard History", color = theme.keyBorderColor, fontWeight = FontWeight.Bold, fontSize = 14.sp)

            if (items.isNotEmpty()) {
                TextButton(
                    onClick = onClearAll,
                    colors = ButtonDefaults.textButtonColors(contentColor = Color(0xFFFF5252))
                ) {
                    Icon(imageVector = Icons.Default.DeleteSweep, contentDescription = "Clear All", modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Clear All", fontSize = 12.sp)
                }
            }
        }

        if (items.isEmpty()) {
            Box(modifier = Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center) {
                Text(
                    text = "No clipboard entries.\nCopied text is safely kept only on your device.",
                    color = Color.White.copy(alpha = 0.7f),
                    textAlign = TextAlign.Center,
                    fontSize = 13.sp
                )
            }
        } else {
            LazyRow(
                modifier = Modifier.fillMaxWidth().weight(1f),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(vertical = 4.dp)
            ) {
                items(items) { item ->
                    Surface(
                        modifier = Modifier.width(180.dp).fillMaxHeight(),
                        shape = RoundedCornerShape(12.dp),
                        color = Color.Black.copy(alpha = 0.75f),
                        border = BorderStroke(1.dp, theme.keyBorderColor.copy(alpha = 0.5f))
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize().clickable { onItemClick(item) }.padding(8.dp),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = item.text, color = Color.White, fontSize = 13.sp, maxLines = 4, overflow = TextOverflow.Ellipsis)

                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                                IconButton(onClick = { onItemDelete(item.id) }, modifier = Modifier.size(24.dp)) {
                                    Icon(Icons.Default.DeleteOutline, contentDescription = "Delete Item", tint = Color.LightGray, modifier = Modifier.size(16.dp))
                                }
                            }
                        }
                    }
                }
            }
        }

        Button(
            onClick = onBackToLetters,
            modifier = Modifier.fillMaxWidth().height(40.dp).padding(top = 4.dp),
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(containerColor = theme.keyBackgroundColor),
            border = BorderStroke(1.2.dp, theme.keyBorderColor)
        ) {
            Text("Back to Keyboard", color = Color.White)
        }
    }
}

@Composable
private fun SuggestionPill(
    word: String,
    theme: KeyboardThemeData,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .padding(horizontal = 4.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color.Black.copy(alpha = 0.5f))
            .border(BorderStroke(1.dp, theme.keyBorderColor.copy(alpha = 0.6f)), RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = word,
            color = theme.suggestionTextColor,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}
