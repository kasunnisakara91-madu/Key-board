package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.keyboard.*
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("BESTIE Keyboard", appName)
    }

    @Test
    fun `test keyboard preferences persistence`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val prefs = KeyboardPreferences(context)

        prefs.setTheme(ThemeId.CYBERPUNK)
        prefs.setHeight(KeyboardHeight.TALL)
        prefs.setSoundEnabled(false)
        prefs.setHapticEnabled(true)
        prefs.setLanguage(KeyboardLanguage.SINHALA)

        val settings = prefs.loadSettings()
        assertEquals(ThemeId.CYBERPUNK, settings.themeId)
        assertEquals(KeyboardHeight.TALL, settings.height)
        assertEquals(false, settings.soundEnabled)
        assertEquals(true, settings.hapticEnabled)
        assertEquals(KeyboardLanguage.SINHALA, settings.currentLanguage)
    }

    @Test
    fun `test clipboard helper functionality`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val clipboardHelper = ClipboardManagerHelper(context)

        clipboardHelper.clearAll()
        assertTrue(clipboardHelper.getItems().isEmpty())

        clipboardHelper.addItem("Hello BESTIE")
        clipboardHelper.addItem("ආයුබෝවන්")

        val items = clipboardHelper.getItems()
        assertEquals(2, items.size)
        assertEquals("ආයුබෝවන්", items[0].text)

        clipboardHelper.deleteItem(items[0].id)
        val remaining = clipboardHelper.getItems()
        assertEquals(1, remaining.size)
        assertEquals("Hello BESTIE", remaining[0].text)

        clipboardHelper.clearAll()
        assertTrue(clipboardHelper.getItems().isEmpty())
    }

    @Test
    fun `test word suggestion engine`() {
        val engine = WordSuggestionEngine()

        val englishSuggestions = engine.getSuggestions("hel", KeyboardLanguage.ENGLISH)
        assertTrue(englishSuggestions.any { it.startsWith("hel", ignoreCase = true) })

        val sinhalaSuggestions = engine.getSuggestions("ආයු", KeyboardLanguage.SINHALA)
        assertTrue(sinhalaSuggestions.any { it.startsWith("ආයු") })

        assertTrue(engine.shouldAutoCapitalize("Hello world. "))
        assertTrue(engine.shouldAutoCapitalize(null))
    }

    @Test
    fun `test dual script keyboard layouts`() {
        assertTrue(KeyboardLayouts.dualScriptRow1.any { it.primaryText == "q" && it.cornerHint == "ද" })
        assertEquals("ද", SinglishTransliteration.getSinhalaCharForEnglish('q'))
        assertEquals("ක", SinglishTransliteration.getSinhalaCharForEnglish('k'))
        assertEquals("මම", SinglishTransliteration.transliterateWord("mama"))
    }

    @Test
    fun `test singlish transliteration and voice manager`() {
        assertEquals("සිංහල", SinglishTransliteration.transliterateWord("sinhala"))
        assertEquals("කියද්දි", SinglishTransliteration.transliterateWord("kiyaddi"))
        assertEquals("ඔයා", SinglishTransliteration.transliterateWord("oya"))

        val context = ApplicationProvider.getApplicationContext<Context>()
        val voiceManager = VoiceInputManager(context)
        assertEquals(VoiceState.Idle, voiceManager.voiceState.value)
    }
}
