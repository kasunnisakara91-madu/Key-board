package com.example.ui

import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.view.inputmethod.InputMethodManager
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.keyboard.*

enum class AppNavTab(val title: String, val icon: androidx.compose.ui.graphics.vector.ImageVector) {
    HOME("Home", Icons.Default.Home),
    THEMES("Themes", Icons.Default.Palette),
    PREVIEW("Preview", Icons.Default.Keyboard),
    SETTINGS("Settings", Icons.Default.Tune),
    LANGUAGES("Languages", Icons.Default.Language),
    CLIPBOARD("Clipboard", Icons.Default.ContentPaste),
    ABOUT("About", Icons.Default.Info)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    initialTab: AppNavTab = AppNavTab.HOME
) {
    val context = LocalContext.current
    val keyboardPrefs = remember { KeyboardPreferences(context) }
    val clipboardHelper = remember { ClipboardManagerHelper(context) }

    var selectedTab by remember { mutableStateOf(initialTab) }
    var currentSettings by remember { mutableStateOf(keyboardPrefs.loadSettings()) }
    var clipboardItems by remember { mutableStateOf(clipboardHelper.getItems()) }

    var isEnabled by remember { mutableStateOf(checkIsKeyboardEnabled(context)) }
    var isSelected by remember { mutableStateOf(checkIsKeyboardSelected(context)) }

    // Periodically or on resume re-check status
    LaunchedEffect(Unit) {
        isEnabled = checkIsKeyboardEnabled(context)
        isSelected = checkIsKeyboardSelected(context)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primaryContainer,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text("B", fontWeight = FontWeight.Black, fontSize = 20.sp, color = MaterialTheme.colorScheme.primary)
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "BESTIE Keyboard",
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp
            ) {
                AppNavTab.values().forEach { tab ->
                    NavigationBarItem(
                        icon = { Icon(tab.icon, contentDescription = tab.title) },
                        label = { Text(tab.title, fontSize = 11.sp) },
                        selected = selectedTab == tab,
                        onClick = {
                            selectedTab = tab
                            isEnabled = checkIsKeyboardEnabled(context)
                            isSelected = checkIsKeyboardSelected(context)
                        },
                        modifier = Modifier.testTag("nav_tab_${tab.name.lowercase()}")
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (selectedTab) {
                AppNavTab.HOME -> HomeScreen(
                    isEnabled = isEnabled,
                    isSelected = isSelected,
                    onEnableClick = {
                        openInputMethodSettings(context)
                    },
                    onSelectClick = {
                        openInputMethodPicker(context)
                    },
                    onRefreshStatus = {
                        isEnabled = checkIsKeyboardEnabled(context)
                        isSelected = checkIsKeyboardSelected(context)
                    },
                    onGoToPreview = { selectedTab = AppNavTab.PREVIEW },
                    onGoToThemes = { selectedTab = AppNavTab.THEMES }
                )

                AppNavTab.THEMES -> ThemesScreen(
                    currentThemeId = currentSettings.themeId,
                    onSelectTheme = { themeId ->
                        keyboardPrefs.setTheme(themeId)
                        currentSettings = currentSettings.copy(themeId = themeId)
                    }
                )

                AppNavTab.PREVIEW -> PreviewScreen(
                    settings = currentSettings,
                    clipboardItems = clipboardItems,
                    onLanguageSwitch = {
                        val next = if (currentSettings.currentLanguage == KeyboardLanguage.ENGLISH)
                            KeyboardLanguage.SINHALA else KeyboardLanguage.ENGLISH
                        keyboardPrefs.setLanguage(next)
                        currentSettings = currentSettings.copy(currentLanguage = next)
                    },
                    onThemeSwitch = {
                        val all = ThemeId.values()
                        val next = all[(all.indexOf(currentSettings.themeId) + 1) % all.size]
                        keyboardPrefs.setTheme(next)
                        currentSettings = currentSettings.copy(themeId = next)
                    }
                )

                AppNavTab.SETTINGS -> SettingsScreen(
                    settings = currentSettings,
                    onUpdateSettings = { updated ->
                        currentSettings = updated
                        keyboardPrefs.setSoundEnabled(updated.soundEnabled)
                        keyboardPrefs.setHapticEnabled(updated.hapticEnabled)
                        keyboardPrefs.setSuggestionsEnabled(updated.suggestionsEnabled)
                        keyboardPrefs.setAutoCapEnabled(updated.autoCapEnabled)
                        keyboardPrefs.setDoubleSpacePeriodEnabled(updated.doubleSpacePeriodEnabled)
                        keyboardPrefs.setKeyBordersEnabled(updated.keyBordersEnabled)
                        keyboardPrefs.setHeight(updated.height)
                    }
                )

                AppNavTab.LANGUAGES -> LanguagesScreen(
                    currentLanguage = currentSettings.currentLanguage,
                    onSelectLanguage = { lang ->
                        keyboardPrefs.setLanguage(lang)
                        currentSettings = currentSettings.copy(currentLanguage = lang)
                    }
                )

                AppNavTab.CLIPBOARD -> ClipboardScreen(
                    items = clipboardItems,
                    onDeleteItem = { id ->
                        clipboardHelper.deleteItem(id)
                        clipboardItems = clipboardHelper.getItems()
                    },
                    onClearAll = {
                        clipboardHelper.clearAll()
                        clipboardItems = emptyList()
                    },
                    onAddSnippet = { text ->
                        clipboardHelper.addItem(text)
                        clipboardItems = clipboardHelper.getItems()
                    }
                )

                AppNavTab.ABOUT -> AboutScreen()
            }
        }
    }
}

// -------------------------------------------------------------
// 1. HOME SCREEN (Onboarding & Activation Steps)
// -------------------------------------------------------------
@Composable
private fun HomeScreen(
    isEnabled: Boolean,
    isSelected: Boolean,
    onEnableClick: () -> Unit,
    onSelectClick: () -> Unit,
    onRefreshStatus: () -> Unit,
    onGoToPreview: () -> Unit,
    onGoToThemes: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hero Header
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(Color(0xFF4F46E5), Color(0xFF7C3AED), Color(0xFFDB2777))
                            )
                        )
                        .padding(24.dp)
                ) {
                    Column {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color.White.copy(alpha = 0.2f),
                            modifier = Modifier.padding(bottom = 12.dp)
                        ) {
                            Text(
                                text = "BESTIE KEYBOARD",
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                        Text(
                            text = "Type beautifully in English & Sinhala",
                            color = Color.White,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "A modern, private keyboard with smooth themes, emojis, offline clipboard, and tactile feedback.",
                            color = Color.White.copy(alpha = 0.9f),
                            fontSize = 14.sp,
                            lineHeight = 20.sp
                        )
                    }
                }
            }
        }

        // Setup Steps Section
        item {
            Text(
                text = "Setup Checklist",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        // Step 1: Enable
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("card_step_enable"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isEnabled) MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f)
                    else MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (isEnabled) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                        contentDescription = "Status",
                        tint = if (isEnabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "1. Enable BESTIE Keyboard",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Text(
                            text = if (isEnabled) "Active in system input methods" else "Turn on BESTIE in Android Language & Input settings",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    if (!isEnabled) {
                        Button(
                            onClick = onEnableClick,
                            modifier = Modifier.testTag("btn_enable_settings")
                        ) {
                            Text("Enable")
                        }
                    }
                }
            }
        }

        // Step 2: Select
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("card_step_select"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isSelected) MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f)
                    else MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (isSelected) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                        contentDescription = "Status",
                        tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "2. Select Default Keyboard",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Text(
                            text = if (isSelected) "BESTIE is your current default keyboard" else "Switch your current input method to BESTIE",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    if (!isSelected) {
                        Button(
                            onClick = onSelectClick,
                            enabled = isEnabled,
                            modifier = Modifier.testTag("btn_select_default")
                        ) {
                            Text("Select")
                        }
                    }
                }
            }
        }

        // Quick Actions
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onGoToPreview,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Keyboard, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Try Preview")
                }

                Button(
                    onClick = onGoToThemes,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Palette, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Themes")
                }
            }
        }

        // Privacy Guarantee Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Security,
                            contentDescription = "Security",
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "100% Offline & Private",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "BESTIE Keyboard never records your keystrokes, passwords, or messages, and never connects to the internet. Clipboard history remains strictly on your device.",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 18.sp
                    )
                }
            }
        }
    }
}

// -------------------------------------------------------------
// 2. THEMES SCREEN
// -------------------------------------------------------------
@Composable
private fun ThemesScreen(
    currentThemeId: ThemeId,
    onSelectTheme: (ThemeId) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Select Theme",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Choose from curated styles with smooth gradients and custom key colors.",
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(16.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(KeyboardThemes.allThemes) { theme ->
                val isSelected = theme.id == currentThemeId
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .clickable { onSelectTheme(theme.id) }
                        .then(
                            if (isSelected) {
                                Modifier.border(2.5.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(16.dp))
                            } else Modifier
                        ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(brush = theme.backgroundBrush)
                            .padding(12.dp)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = theme.name,
                                    color = theme.keyTextColor,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                                if (isSelected) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = "Selected",
                                        tint = theme.enterKeyBackgroundColor,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }

                            // Miniature preview keys
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                listOf("Q", "W", "E", "R").forEach { char ->
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(26.dp)
                                            .background(theme.keyBackgroundColor, RoundedCornerShape(4.dp)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(text = char, color = theme.keyTextColor, fontSize = 10.sp)
                                    }
                                }
                                Box(
                                    modifier = Modifier
                                        .weight(1.2f)
                                        .height(26.dp)
                                        .background(theme.enterKeyBackgroundColor, RoundedCornerShape(4.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("↵", color = theme.enterKeyTextColor, fontSize = 11.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// 3. PREVIEW SCREEN
// -------------------------------------------------------------
@Composable
private fun PreviewScreen(
    settings: KeyboardSettings,
    clipboardItems: List<ClipboardItem>,
    onLanguageSwitch: () -> Unit,
    onThemeSwitch: () -> Unit
) {
    var previewText by remember { mutableStateOf("Testing BESTIE Keyboard! ❤️ ආයුබෝවන් ") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(
                text = "Interactive Keyboard Sandbox",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Type below using the live interactive preview. Test typing in English, Sinhala, symbols, emojis, and clipboard.",
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = previewText,
                onValueChange = { previewText = it },
                label = { Text("Sandbox Text Field") },
                placeholder = { Text("Tap keyboard keys below...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
                    .testTag("preview_text_field"),
                trailingIcon = {
                    if (previewText.isNotEmpty()) {
                        IconButton(onClick = { previewText = "" }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear")
                        }
                    }
                }
            )
        }

        // Embedded Live Keyboard Component
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
        ) {
            BestieKeyboardView(
                settings = settings,
                imeOptions = android.view.inputmethod.EditorInfo.IME_ACTION_DONE,
                suggestions = listOf("hello", "bestie", "great", "ආයුබෝවන්"),
                clipboardItems = clipboardItems,
                onKeyPress = { previewText += it },
                onBackspace = {
                    if (previewText.isNotEmpty()) {
                        previewText = previewText.dropLast(1)
                    }
                },
                onEnter = { previewText += "\n" },
                onSpace = { previewText += " " },
                onSuggestionClick = { previewText += "$it " },
                onClipboardItemClick = { previewText += it },
                onClipboardDelete = {},
                onClipboardClearAll = {},
                onLanguageSwitch = onLanguageSwitch,
                onThemeSwitchRequest = onThemeSwitch,
                onSettingsOpenRequest = {},
                onSelectAll = {},
                onCut = {
                    previewText = ""
                },
                onCopy = {},
                onPaste = {
                    previewText += clipboardItems.firstOrNull()?.text ?: ""
                }
            )
        }
    }
}

// -------------------------------------------------------------
// 4. SETTINGS SCREEN
// -------------------------------------------------------------
@Composable
private fun SettingsScreen(
    settings: KeyboardSettings,
    onUpdateSettings: (KeyboardSettings) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Preferences & Feedback",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Tune typing sounds, vibration haptics, layout height, and auto-correct.",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Feedback Section
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    SettingToggleRow(
                        title = "Keypress Vibration",
                        subtitle = "Tactile haptic feedback on every tap",
                        checked = settings.hapticEnabled,
                        onCheckedChange = { onUpdateSettings(settings.copy(hapticEnabled = it)) }
                    )
                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
                    SettingToggleRow(
                        title = "Keypress Sound",
                        subtitle = "Audible click on key press",
                        checked = settings.soundEnabled,
                        onCheckedChange = { onUpdateSettings(settings.copy(soundEnabled = it)) }
                    )
                }
            }
        }

        // Typing Intelligence Section
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    SettingToggleRow(
                        title = "Word Suggestions",
                        subtitle = "Display candidate word completions in top bar",
                        checked = settings.suggestionsEnabled,
                        onCheckedChange = { onUpdateSettings(settings.copy(suggestionsEnabled = it)) }
                    )
                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
                    SettingToggleRow(
                        title = "Auto-Capitalization",
                        subtitle = "Capitalize first word of sentences",
                        checked = settings.autoCapEnabled,
                        onCheckedChange = { onUpdateSettings(settings.copy(autoCapEnabled = it)) }
                    )
                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
                    SettingToggleRow(
                        title = "Double-Space Period",
                        subtitle = "Double tap spacebar to insert a period and space",
                        checked = settings.doubleSpacePeriodEnabled,
                        onCheckedChange = { onUpdateSettings(settings.copy(doubleSpacePeriodEnabled = it)) }
                    )
                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
                    SettingToggleRow(
                        title = "Key Borders",
                        subtitle = "Display subtle outline borders on individual keys",
                        checked = settings.keyBordersEnabled,
                        onCheckedChange = { onUpdateSettings(settings.copy(keyBordersEnabled = it)) }
                    )
                }
            }
        }

        // Height Selection
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Keyboard Height",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Text(
                        text = "Adjust the vertical height of the keyboard keys",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        KeyboardHeight.values().forEach { height ->
                            val isSelected = height == settings.height
                            FilterChip(
                                selected = isSelected,
                                onClick = { onUpdateSettings(settings.copy(height = height)) },
                                label = { Text(height.label) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SettingToggleRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, fontWeight = FontWeight.Bold, fontSize = 15.sp)
            Text(text = subtitle, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}

// -------------------------------------------------------------
// 5. LANGUAGES SCREEN
// -------------------------------------------------------------
@Composable
private fun LanguagesScreen(
    currentLanguage: KeyboardLanguage,
    onSelectLanguage: (KeyboardLanguage) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Languages & Layouts",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Switch default input language or learn about the Sinhala typing layout.",
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(16.dp))

        KeyboardLanguage.values().forEach { lang ->
            val isSelected = lang == currentLanguage
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .clickable { onSelectLanguage(lang) },
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer
                    else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = lang.displayName,
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp,
                            color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = if (lang == KeyboardLanguage.ENGLISH) "Standard QWERTY layout" else "Sinhala character & vowel-modifier layout",
                            fontSize = 13.sp,
                            color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f) else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    RadioButton(
                        selected = isSelected,
                        onClick = { onSelectLanguage(lang) }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Sinhala Typing Guide
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Sinhala Typing Guide",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "• Consonants: Tap letters like ක, ග, ත, ද, ප, බ.\n" +
                            "• Vowel Signs (පිල්ලම්): Tap any consonant followed by ා (කා), ැ (කැ), ි (කි), ී (කී), ු (කු), ෙ (කෙ).\n" +
                            "• Hal Lakuna (්): Tap a consonant followed by ් to form pure consonants (e.g. ක + ් = ක්).\n" +
                            "• Shift Key: Press Shift to switch between primary consonants and independent vowels (අ, ආ, ඇ, ඉ, උ, එ, ඔ) and aspirated letters (ඝ, ඡ, ඨ, ඪ, ණ, ථ, ධ, භ, ළ, ෆ).\n" +
                            "• Language Switch: Tap the EN / සිං key on the bottom row to instantly switch languages.",
                    fontSize = 13.sp,
                    lineHeight = 20.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

// -------------------------------------------------------------
// 6. CLIPBOARD SCREEN
// -------------------------------------------------------------
@Composable
private fun ClipboardScreen(
    items: List<ClipboardItem>,
    onDeleteItem: (String) -> Unit,
    onClearAll: () -> Unit,
    onAddSnippet: (String) -> Unit
) {
    var newSnippetText by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Local Clipboard",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Stored 100% on device for fast pasting",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (items.isNotEmpty()) {
                FilledTonalButton(onClick = onClearAll) {
                    Text("Clear All")
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Add custom quick snippet
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = newSnippetText,
                onValueChange = { newSnippetText = it },
                placeholder = { Text("Add custom snippet...") },
                modifier = Modifier.weight(1f),
                singleLine = true
            )
            Button(
                onClick = {
                    if (newSnippetText.isNotBlank()) {
                        onAddSnippet(newSnippetText.trim())
                        newSnippetText = ""
                    }
                }
            ) {
                Text("Add")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (items.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No clipboard items yet.\nWhen you copy text or add snippets, they will appear here.",
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(items) { item ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = item.text,
                                fontSize = 14.sp,
                                modifier = Modifier.weight(1f)
                            )
                            IconButton(onClick = { onDeleteItem(item.id) }) {
                                Icon(
                                    imageVector = Icons.Default.DeleteOutline,
                                    contentDescription = "Delete",
                                    tint = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// 7. ABOUT SCREEN
// -------------------------------------------------------------
@Composable
private fun AboutScreen() {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(64.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text("B", color = MaterialTheme.colorScheme.onPrimary, fontSize = 32.sp, fontWeight = FontWeight.Black)
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "BESTIE Keyboard",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = "Version 1.0.0",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                    )
                }
            }
        }

        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Privacy & Security Declaration", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "• Zero Keystroke Logging: Your inputs never leave the keyboard service.\n" +
                                "• Zero Network Transmission: BESTIE runs completely offline. No internet permission is requested.\n" +
                                "• Password Field Safety: Word suggestions and caching are automatically silenced in password fields.\n" +
                                "• Local Clipboard Storage: All clips are stored on-device with single-tap deletion and clear-all capabilities.",
                        fontSize = 13.sp,
                        lineHeight = 20.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Architecture & Technical Details", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "• Built with Android InputMethodService API\n" +
                                "• Modern Jetpack Compose UI with Material Design 3\n" +
                                "• Sinhala Unicode character & vowel sign system\n" +
                                "• GitHub Actions automated CI/CD pipeline",
                        fontSize = 13.sp,
                        lineHeight = 20.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

// -------------------------------------------------------------
// HELPER FUNCTIONS FOR INPUT METHOD MANAGER
// -------------------------------------------------------------
private fun checkIsKeyboardEnabled(context: Context): Boolean {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager ?: return false
    val enabledList = imm.enabledInputMethodList
    return enabledList.any { it.packageName == context.packageName }
}

private fun checkIsKeyboardSelected(context: Context): Boolean {
    val defaultIme = Settings.Secure.getString(context.contentResolver, Settings.Secure.DEFAULT_INPUT_METHOD)
    return defaultIme?.contains(context.packageName) == true
}

private fun openInputMethodSettings(context: Context) {
    try {
        val intent = Intent(Settings.ACTION_INPUT_METHOD_SETTINGS).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(intent)
    } catch (_: Exception) {
        val intent = Intent(Settings.ACTION_SETTINGS).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(intent)
    }
}

private fun openInputMethodPicker(context: Context) {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
    imm?.showInputMethodPicker()
}
