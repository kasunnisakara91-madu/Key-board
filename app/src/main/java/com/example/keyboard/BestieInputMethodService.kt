package com.example.keyboard

import android.content.ClipDescription
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.inputmethodservice.InputMethodService
import android.media.AudioManager
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.text.InputType
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.compose.runtime.*
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.lifecycle.*
import androidx.savedstate.SavedStateRegistry
import androidx.savedstate.SavedStateRegistryController
import androidx.savedstate.SavedStateRegistryOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import com.example.MainActivity

class BestieInputMethodService : InputMethodService(),
    LifecycleOwner,
    ViewModelStoreOwner,
    SavedStateRegistryOwner {

    private val lifecycleRegistry = LifecycleRegistry(this)
    private val appViewModelStore = ViewModelStore()
    private val savedStateRegistryController = SavedStateRegistryController.create(this)

    override val lifecycle: Lifecycle get() = lifecycleRegistry
    override val viewModelStore: ViewModelStore get() = appViewModelStore
    override val savedStateRegistry: SavedStateRegistry get() = savedStateRegistryController.savedStateRegistry

    private lateinit var keyboardPrefs: KeyboardPreferences
    private lateinit var clipboardHelper: ClipboardManagerHelper
    private val suggestionEngine = WordSuggestionEngine()

    private var audioManager: AudioManager? = null
    private var vibrator: Vibrator? = null

    // Mutable states observed by Compose
    private val settingsState = mutableStateOf(KeyboardSettings())
    private val suggestionsState = mutableStateOf<List<String>>(emptyList())
    private val clipboardListState = mutableStateOf<List<ClipboardItem>>(emptyList())
    private val imeOptionsState = mutableIntStateOf(EditorInfo.IME_ACTION_DONE)

    private var lastSpaceTime = 0L

    override fun onCreate() {
        super.onCreate()
        savedStateRegistryController.performRestore(null)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)

        keyboardPrefs = KeyboardPreferences(this)
        clipboardHelper = ClipboardManagerHelper(this)
        audioManager = getSystemService(Context.AUDIO_SERVICE) as? AudioManager
        vibrator = getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator

        settingsState.value = keyboardPrefs.loadSettings()
        clipboardListState.value = clipboardHelper.getItems()
    }

    override fun onCreateInputView(): View {
        val window = window?.window
        if (window != null) {
            val decorView = window.decorView
            decorView.setViewTreeLifecycleOwner(this)
            decorView.setViewTreeViewModelStoreOwner(this)
            decorView.setViewTreeSavedStateRegistryOwner(this)
        }

        return ComposeView(this).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnDetachedFromWindow)
            setContent {
                val currentSettings by settingsState
                val currentSuggestions by suggestionsState
                val currentClips by clipboardListState
                val currentImeOptions by imeOptionsState

                BestieKeyboardView(
                    settings = currentSettings,
                    imeOptions = currentImeOptions,
                    suggestions = currentSuggestions,
                    clipboardItems = currentClips,
                    onKeyPress = { text -> handleCharacter(text) },
                    onBackspace = { handleBackspace() },
                    onEnter = { handleEnter() },
                    onSpace = { handleSpace() },
                    onSuggestionClick = { word -> handleSuggestion(word) },
                    onClipboardItemClick = { text -> handleClipboardPaste(text) },
                    onClipboardDelete = { id ->
                        clipboardHelper.deleteItem(id)
                        clipboardListState.value = clipboardHelper.getItems()
                    },
                    onClipboardClearAll = {
                        clipboardHelper.clearAll()
                        clipboardListState.value = emptyList()
                    },
                    onLanguageSwitch = { toggleLanguage() },
                    onThemeSwitchRequest = { cycleTheme() },
                    onSettingsOpenRequest = { openSettingsActivity() },
                    onHideKeyboard = { requestHideSelf(0) },
                    onCursorMoveLeft = {
                        sendDownUpKeyEvents(KeyEvent.KEYCODE_DPAD_LEFT)
                    },
                    onCursorMoveRight = {
                        sendDownUpKeyEvents(KeyEvent.KEYCODE_DPAD_RIGHT)
                    },
                    onSelectAll = {
                        currentInputConnection?.performContextMenuAction(android.R.id.selectAll)
                    },
                    onCut = {
                        currentInputConnection?.performContextMenuAction(android.R.id.cut)
                    },
                    onCopy = {
                        currentInputConnection?.performContextMenuAction(android.R.id.copy)
                    },
                    onPaste = {
                        currentInputConnection?.performContextMenuAction(android.R.id.paste)
                    },
                    onTextStyleChange = { mode ->
                        keyboardPrefs.setTextStyleMode(mode)
                        settingsState.value = settingsState.value.copy(textStyleMode = mode)
                    }
                )
            }
        }
    }

    override fun onStartInput(attribute: EditorInfo?, restarting: Boolean) {
        super.onStartInput(attribute, restarting)
        settingsState.value = keyboardPrefs.loadSettings()
        imeOptionsState.intValue = attribute?.imeOptions ?: EditorInfo.IME_ACTION_DONE
        checkSystemClipboard()
        updateSuggestions()
    }

    override fun onStartInputView(info: EditorInfo?, restarting: Boolean) {
        super.onStartInputView(info, restarting)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_START)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
        imeOptionsState.intValue = info?.imeOptions ?: EditorInfo.IME_ACTION_DONE
        updateSuggestions()
    }

    override fun onFinishInputView(finishingInput: Boolean) {
        super.onFinishInputView(finishingInput)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_PAUSE)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_STOP)
    }

    override fun onDestroy() {
        super.onDestroy()
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        appViewModelStore.clear()
    }

    private fun handleCharacter(text: String) {
        provideFeedback()
        val ic = currentInputConnection ?: return
        ic.commitText(text, 1)

        // Record recent emoji if single emoji or emoji-like
        if (text.length in 1..4 && !text.all { it.isLetterOrDigit() || it.isWhitespace() }) {
            keyboardPrefs.addRecentEmoji(text)
            settingsState.value = keyboardPrefs.loadSettings()
        }

        updateSuggestions()
    }

    private fun handleBackspace() {
        provideFeedback()
        val ic = currentInputConnection ?: return
        val selectedText = ic.getSelectedText(0)
        if (selectedText.isNullOrEmpty()) {
            ic.deleteSurroundingText(1, 0)
        } else {
            ic.commitText("", 1)
        }
        updateSuggestions()
    }

    private fun handleEnter() {
        provideFeedback()
        val ic = currentInputConnection ?: return
        val editorInfo = currentInputEditorInfo
        val action = (editorInfo?.imeOptions ?: 0) and EditorInfo.IME_MASK_ACTION

        if (action != EditorInfo.IME_ACTION_NONE && action != EditorInfo.IME_ACTION_UNSPECIFIED) {
            ic.performEditorAction(action)
        } else {
            ic.sendKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ENTER))
            ic.sendKeyEvent(KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_ENTER))
        }
        updateSuggestions()
    }

    private fun handleSpace() {
        provideFeedback()
        val ic = currentInputConnection ?: return
        val settings = settingsState.value
        val now = System.currentTimeMillis()

        if (settings.doubleSpacePeriodEnabled && (now - lastSpaceTime) < 500) {
            // Delete previous space and insert period + space
            val before = ic.getTextBeforeCursor(2, 0)
            if (before != null && before.endsWith(" ")) {
                ic.deleteSurroundingText(1, 0)
                ic.commitText(". ", 1)
                lastSpaceTime = 0L
                updateSuggestions()
                return
            }
        }

        lastSpaceTime = now
        ic.commitText(" ", 1)
        updateSuggestions()
    }

    private fun handleSuggestion(word: String) {
        provideFeedback()
        val ic = currentInputConnection ?: return

        // Delete currently typed word prefix if applicable
        val before = ic.getTextBeforeCursor(30, 0)?.toString() ?: ""
        val lastWord = before.takeLastWhile { !it.isWhitespace() }
        if (lastWord.isNotEmpty()) {
            ic.deleteSurroundingText(lastWord.length, 0)
        }

        ic.commitText("$word ", 1)
        updateSuggestions()
    }

    private fun handleClipboardPaste(text: String) {
        provideFeedback()
        val ic = currentInputConnection ?: return
        ic.commitText(text, 1)
        updateSuggestions()
    }

    private fun toggleLanguage() {
        provideFeedback()
        val current = settingsState.value.currentLanguage
        val next = if (current == KeyboardLanguage.ENGLISH) KeyboardLanguage.SINHALA else KeyboardLanguage.ENGLISH
        keyboardPrefs.setLanguage(next)
        settingsState.value = settingsState.value.copy(currentLanguage = next)
        updateSuggestions()
    }

    private fun cycleTheme() {
        provideFeedback()
        val all = ThemeId.values()
        val currentIdx = all.indexOf(settingsState.value.themeId)
        val next = all[(currentIdx + 1) % all.size]
        keyboardPrefs.setTheme(next)
        settingsState.value = settingsState.value.copy(themeId = next)
    }

    private fun openSettingsActivity() {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("OPEN_TAB", "SETTINGS")
        }
        startActivity(intent)
    }

    private fun updateSuggestions() {
        val editorInfo = currentInputEditorInfo
        // If password field or suggestions disabled, don't show suggestions
        val isPassword = isPasswordField(editorInfo)
        if (isPassword || !settingsState.value.suggestionsEnabled) {
            suggestionsState.value = emptyList()
            return
        }

        val ic = currentInputConnection ?: return
        val before = ic.getTextBeforeCursor(25, 0)?.toString() ?: ""
        val prefix = before.takeLastWhile { !it.isWhitespace() }
        suggestionsState.value = suggestionEngine.getSuggestions(prefix, settingsState.value.currentLanguage)
    }

    private fun isPasswordField(info: EditorInfo?): Boolean {
        if (info == null) return false
        val inputType = info.inputType
        val variation = inputType and InputType.TYPE_MASK_VARIATION
        return variation == InputType.TYPE_TEXT_VARIATION_PASSWORD ||
                variation == InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD ||
                variation == InputType.TYPE_TEXT_VARIATION_WEB_PASSWORD
    }

    private fun checkSystemClipboard() {
        try {
            val cm = getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager ?: return
            if (cm.hasPrimaryClip() && (cm.primaryClipDescription?.hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN) == true ||
                        cm.primaryClipDescription?.hasMimeType(ClipDescription.MIMETYPE_TEXT_HTML) == true)) {
                val clip = cm.primaryClip
                if (clip != null && clip.itemCount > 0) {
                    val text = clip.getItemAt(0).coerceToText(this)?.toString()
                    if (!text.isNullOrBlank()) {
                        clipboardHelper.addItem(text)
                        clipboardListState.value = clipboardHelper.getItems()
                    }
                }
            }
        } catch (_: Exception) {
            // Privacy guard: safely catch security exceptions on Android 10+ background clipboard access
        }
    }

    private fun provideFeedback() {
        val settings = settingsState.value

        // Haptic feedback
        if (settings.hapticEnabled) {
            vibrator?.let { vib ->
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vib.vibrate(VibrationEffect.createOneShot(20, VibrationEffect.DEFAULT_AMPLITUDE))
                } else {
                    @Suppress("DEPRECATION")
                    vib.vibrate(20)
                }
            }
        }

        // Sound feedback
        if (settings.soundEnabled) {
            audioManager?.playSoundEffect(AudioManager.FX_KEYPRESS_STANDARD, 0.4f)
        }
    }
}
