package com.example.myapplication.ui.flashcard

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.State
import kotlin.math.min

class SpeechRecognitionManager(private val context: Context) {
    
    private var speechRecognizer: SpeechRecognizer? = null
    private var isRecording = mutableStateOf(false)
    private var recordingError = mutableStateOf<String?>(null)
    
    val isRecordingState: State<Boolean> = isRecording
    val recordingErrorState: State<String?> = recordingError
    
    fun startListening(
        onResult: (String) -> Unit,
        onError: (String) -> Unit = {}
    ) {
        if (!SpeechRecognizer.isRecognitionAvailable(context)) {
            onError("Speech recognition not available")
            return
        }
        
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context)
        
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-US")
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
            putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)
        }
        
        speechRecognizer?.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {
                isRecording.value = true
                recordingError.value = null
            }
            
            override fun onBeginningOfSpeech() {}
            
            override fun onRmsChanged(rmsdB: Float) {}
            
            override fun onBufferReceived(buffer: ByteArray?) {}
            
            override fun onEndOfSpeech() {
                isRecording.value = false
            }
            
            override fun onError(error: Int) {
                isRecording.value = false
                val errorMessage = when (error) {
                    SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "Network timeout"
                    SpeechRecognizer.ERROR_NETWORK -> "Network error"
                    SpeechRecognizer.ERROR_AUDIO -> "Audio error"
                    SpeechRecognizer.ERROR_SERVER -> "Server error"
                    SpeechRecognizer.ERROR_CLIENT -> "Client error"
                    SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "No speech input"
                    SpeechRecognizer.ERROR_NO_MATCH -> "No speech recognized"
                    SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "Recognizer busy"
                    SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "Insufficient permissions"
                    else -> "Unknown error: $error"
                }
                recordingError.value = errorMessage
                onError(errorMessage)
            }
            
            override fun onResults(results: Bundle?) {
                isRecording.value = false
                val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                val spokenText = matches?.firstOrNull() ?: ""
                onResult(spokenText)
            }
            
            override fun onPartialResults(partialResults: Bundle?) {}
            
            override fun onEvent(eventType: Int, params: Bundle?) {}
        })
        
        speechRecognizer?.startListening(intent)
    }
    
    fun stopListening() {
        speechRecognizer?.stopListening()
        isRecording.value = false
    }
    
    fun destroy() {
        speechRecognizer?.destroy()
        speechRecognizer = null
    }
}

// Hàm tính độ giống nhau giữa 2 chuỗi (Levenshtein Distance)
object TextSimilarity {
    
    fun calculateSimilarity(text1: String, text2: String): Double {
        val normalizedText1 = text1.lowercase().trim()
        val normalizedText2 = text2.lowercase().trim()
        
        if (normalizedText1.isEmpty() && normalizedText2.isEmpty()) return 1.0
        if (normalizedText1.isEmpty() || normalizedText2.isEmpty()) return 0.0
        
        val longer = if (normalizedText1.length > normalizedText2.length) normalizedText1 else normalizedText2
        val shorter = if (normalizedText1.length > normalizedText2.length) normalizedText2 else normalizedText1
        
        val editDistance = levenshteinDistance(longer, shorter)
        return (longer.length - editDistance).toDouble() / longer.length
    }
    
    private fun levenshteinDistance(s1: String, s2: String): Int {
        val costs = IntArray(s2.length + 1)
        
        for (i in 0..s1.length) {
            var lastValue = i
            for (j in 0..s2.length) {
                if (i == 0) {
                    costs[j] = j
                } else if (j > 0) {
                    val newValue = if (s1[i - 1] == s2[j - 1]) {
                        costs[j - 1]
                    } else {
                        1 + minOf(costs[j - 1], lastValue, costs[j])
                    }
                    costs[j - 1] = lastValue
                    lastValue = newValue
                }
            }
            if (i > 0) costs[s2.length] = lastValue
        }
        return costs[s2.length]
    }
    
    // Tính điểm phát âm dựa trên độ giống nhau
    fun getPronunciationScore(similarity: Double): PronunciationScore {
        return when {
            similarity >= 0.9 -> PronunciationScore.EXCELLENT
            similarity >= 0.8 -> PronunciationScore.GOOD
            similarity >= 0.6 -> PronunciationScore.FAIR
            similarity >= 0.4 -> PronunciationScore.POOR
            else -> PronunciationScore.VERY_POOR
        }
    }
}

enum class PronunciationScore(val displayName: String, val emoji: String, val color: String) {
    EXCELLENT("Excellent", "🎉", "Green"),
    GOOD("Good", "👍", "LightGreen"),
    FAIR("Fair", "👌", "Orange"),
    POOR("Poor", "👎", "Red"),
    VERY_POOR("Very Poor", "❌", "DarkRed")
}
