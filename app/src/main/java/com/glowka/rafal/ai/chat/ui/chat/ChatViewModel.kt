package com.glowka.rafal.ai.chat.ui.chat

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.glowka.rafal.ai.chat.models.ExecutorResult
import com.glowka.rafal.ai.chat.models.ModelCaller
import com.glowka.rafal.ai.chat.models.Models
import com.glowka.rafal.ai.chat.utils.NetworkChecker
import com.glowka.rafal.ai.chat.utils.map
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Locale
import java.util.UUID

class ChatViewModel(
    context: Context
) : ViewModel() {
    private val _uiState: MutableStateFlow<ChatViewState> = MutableStateFlow(ChatViewState())
    val uiState: StateFlow<ChatViewState> = _uiState.asStateFlow()
    private val modelCaller: StateFlow<ModelCaller?> = uiState.map(viewModelScope) { model ->
        Models.getModelCaller(model.model)
    }

    private val textToSpeech = TextToSpeech(context) { status ->
        Log.d(TAG, "TextToSpeech initialization: $status")
        if (status == TextToSpeech.SUCCESS) {
            onTTSInit()
        }
    }

    private val speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context).apply {
        setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {
                Log.d(TAG, "onReadyForSpeech")
            }

            override fun onBeginningOfSpeech() {
                _uiState.update { state ->
                    state.copy(listening = true)
                }
            }

            override fun onRmsChanged(rmsdB: Float) {
                Log.d(TAG, "onRmsChanged")
            }

            override fun onBufferReceived(buffer: ByteArray?) {
                Log.d(TAG, "onBufferReceived")
            }

            override fun onEndOfSpeech() {
                Log.d(TAG, "onEndOfSpeech")
            }

            override fun onError(error: Int) {
                Log.d(TAG, "onError")
            }

            override fun onResults(results: Bundle?) {
                val data = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION) ?: emptyList<String>()
                val message = buildString {
                    data.forEach { dataLine ->
                        append(dataLine).append("\n")
                    }
                }
                Log.d(TAG, "Message: $message")
                var prompt = ""
                _uiState.update { state ->
                    prompt = buildString {
                        state.conversation.forEach { item ->
                            append(item.message).append('\n')
                        }
                        append(message.trim())
                    }
                    state.copy(
                        listening = false,
                        conversation = state.conversation.toMutableList().apply {
                            add(MessageItem(message.trim(), false))
                        }
                    )
                }
                answer(prompt)
            }

            override fun onPartialResults(partialResults: Bundle?) {
                Log.d(TAG, "onPartialResults")
            }

            override fun onEvent(eventType: Int, params: Bundle?) {
                Log.d(TAG, "onEvent")
            }
        })
    }
    private val speechRecognizerIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
        putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
    }

    private val networkChecker = NetworkChecker(context)

    private fun onTTSInit() {
        textToSpeech.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
            override fun onStart(utteranceId: String?) {
                // nop
            }

            private fun ChatViewState.checkFinalState(): ChatViewState {
                return copy(speaking = false)
            }

            override fun onDone(utteranceId: String?) {
                _uiState.update { state -> state.checkFinalState() }
            }

            override fun onError(utteranceId: String?) {
                _uiState.update { state -> state.checkFinalState() }
            }
        })
    }

    fun onMicrophonePress() {
        speechRecognizer.startListening(speechRecognizerIntent)
    }

    fun onMicrophoneRelease() {
        speechRecognizer.stopListening()
    }

    fun answer(prompt: String) {
        if (!networkChecker.isNetworkAvailable()) {
            speak(MESSAGE_MISSING_INTERNET)
            return
        }
        _uiState.update { state ->
            state.copy(analyzing = true)
        }

        val aiModel = modelCaller.value

        viewModelScope.launch(Dispatchers.IO) {
            Log.d("ZXC", "sending prompt")
            val modelResponse = aiModel?.chatPrompt(prompt)
            val message = when (modelResponse) {
                is ExecutorResult.Success -> modelResponse.text
                is ExecutorResult.Error -> {
                    if (!networkChecker.isNetworkAvailable()) {
                        MESSAGE_MISSING_INTERNET
                    } else {
                        modelResponse.errorMessage
                    }
                }

                null -> {
                    "AI model is not picked"
                }
            }

            _uiState.update { state ->
                state.copy(
                    analyzing = false,
                    speaking = true,
                    conversation = state.conversation.toMutableList().apply {
                        add(MessageItem(message, true))
                    },
                )
            }
            speak(message)
        }
    }

    private fun speak(text: String) {
        textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, UUID.randomUUID().toString())
    }

    override fun onCleared() {
        textToSpeech.shutdown()
        speechRecognizer.destroy()
        super.onCleared()
    }

    fun onChangeModel() {
        _uiState.update { state ->
            val updatedModel = when (state.model) {
                Models.GEMINI_PRO -> Models.OPENAI_GPT_3_5_TURBO
                Models.OPENAI_GPT_3_5_TURBO -> Models.GEMINI_PRO
                else -> Models.GEMINI_PRO
            }
            state.copy(model = updatedModel)
        }
    }

    companion object {
        val TAG = ChatViewModel::class.simpleName
        private const val MESSAGE_MISSING_INTERNET = "Please connect internet first"
    }
}
