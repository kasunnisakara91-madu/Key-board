package com.example.keyboard

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

sealed class VoiceState {
    object Idle : VoiceState()
    data class Listening(val language: KeyboardLanguage, val rms: Float = 0f) : VoiceState()
    data class Recognized(val text: String) : VoiceState()
    data class Error(val message: String) : VoiceState()
}

class VoiceInputManager(private val context: Context) {

    private var speechRecognizer: SpeechRecognizer? = null

    private val _voiceState = MutableStateFlow<VoiceState>(VoiceState.Idle)
    val voiceState: StateFlow<VoiceState> = _voiceState.asStateFlow()

    fun isAvailable(): Boolean {
        return SpeechRecognizer.isRecognitionAvailable(context)
    }

    fun startListening(
        language: KeyboardLanguage,
        onResult: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        stopListening()

        if (!isAvailable()) {
            _voiceState.value = VoiceState.Error("Voice recognition not available on device")
            onError("Voice recognition not available on device")
            return
        }

        try {
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context).apply {
                setRecognitionListener(object : RecognitionListener {
                    override fun onReadyForSpeech(params: Bundle?) {
                        _voiceState.value = VoiceState.Listening(language, 0f)
                    }

                    override fun onBeginningOfSpeech() {
                        _voiceState.value = VoiceState.Listening(language, 0.5f)
                    }

                    override fun onRmsChanged(rmsdB: Float) {
                        _voiceState.value = VoiceState.Listening(language, (rmsdB / 10f).coerceIn(0f, 1f))
                    }

                    override fun onBufferReceived(buffer: ByteArray?) {}

                    override fun onEndOfSpeech() {
                        _voiceState.value = VoiceState.Idle
                    }

                    override fun onError(error: Int) {
                        val message = when (error) {
                            SpeechRecognizer.ERROR_NO_MATCH -> "No speech recognized. Try speaking again."
                            SpeechRecognizer.ERROR_NETWORK -> "Network issue. Connect internet for voice typing."
                            SpeechRecognizer.ERROR_AUDIO -> "Audio recording error."
                            SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "Microphone permission required."
                            else -> "Could not hear clearly. Please tap mic and speak again."
                        }
                        _voiceState.value = VoiceState.Error(message)
                        onError(message)
                    }

                    override fun onResults(results: Bundle?) {
                        val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                        val text = matches?.firstOrNull() ?: ""
                        if (text.isNotBlank()) {
                            _voiceState.value = VoiceState.Recognized(text)
                            onResult(text)
                        } else {
                            _voiceState.value = VoiceState.Idle
                        }
                    }

                    override fun onPartialResults(partialResults: Bundle?) {
                        val matches = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                        val text = matches?.firstOrNull() ?: ""
                        if (text.isNotBlank()) {
                            _voiceState.value = VoiceState.Recognized(text)
                        }
                    }

                    override fun onEvent(eventType: Int, params: Bundle?) {}
                })
            }

            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
                putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3)

                val langCode = when (language) {
                    KeyboardLanguage.SINHALA -> "si-LK"
                    KeyboardLanguage.ENGLISH -> "en-US"
                }
                putExtra(RecognizerIntent.EXTRA_LANGUAGE, langCode)
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, langCode)
                putExtra("android.speech.extra.EXTRA_ADDITIONAL_LANGUAGES", arrayOf(langCode, "si", "en"))
            }

            speechRecognizer?.startListening(intent)
            _voiceState.value = VoiceState.Listening(language, 0f)

        } catch (e: Exception) {
            _voiceState.value = VoiceState.Error("Error initializing voice: ${e.message}")
            onError("Error initializing voice input")
        }
    }

    fun stopListening() {
        try {
            speechRecognizer?.stopListening()
            speechRecognizer?.cancel()
            speechRecognizer?.destroy()
        } catch (_: Exception) {}
        speechRecognizer = null
        _voiceState.value = VoiceState.Idle
    }
}
