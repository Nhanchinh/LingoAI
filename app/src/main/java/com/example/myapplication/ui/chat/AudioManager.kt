package com.example.myapplication.ui.chat

import android.content.Context
import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.example.myapplication.api.ApiService
import com.example.myapplication.UserPreferences
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream

object AudioManager {
    private var currentMediaPlayer: MediaPlayer? = null
    private var currentTempFile: File? = null
    private var currentScreenId: String? = null

    fun playAudioFromText(
        context: Context,
        text: String,
        screenId: String,
        onStateChange: ((Boolean) -> Unit)? = null
    ) {
        if (text.isBlank()) {
            Log.e("AudioManager", "Empty text for TTS")
            return
        }

        currentScreenId = screenId
        stopCurrentAudio()

        onStateChange?.invoke(true)

        // Read preferred voice from DataStore (default to af_heart)
        val userPrefs = UserPreferences(context)
        var preferredVoice = "af_heart"
        try {
            // Blocking fetch on IO thread
            val scope = CoroutineScope(Dispatchers.IO)
            scope.launch {
                preferredVoice = userPrefs.aiVoice.first() ?: "af_heart"
                ApiService.textToSpeech(text, voice = preferredVoice) { code, byteArray, error ->
                    handleTtsResponse(context, text, screenId, code, byteArray, error, onStateChange)
                }
            }
        } catch (e: Exception) {
            ApiService.textToSpeech(text, voice = preferredVoice) { code, byteArray, error ->
                handleTtsResponse(context, text, screenId, code, byteArray, error, onStateChange)
            }
        }
    }

    private fun handleTtsResponse(
        context: Context,
        text: String,
        screenId: String,
        code: Int,
        byteArray: ByteArray?,
        error: String?,
        onStateChange: ((Boolean) -> Unit)?
    ) {
            if (currentScreenId != screenId) {
                Log.d("AudioManager", "Screen changed - audio cancelled")
                onStateChange?.invoke(false)
            return
            }

            if (code == 200 && byteArray != null) {
                val handler = Handler(Looper.getMainLooper())
                handler.post {
                    if (currentScreenId != screenId) {
                        Log.d("AudioManager", "Screen changed - playback cancelled")
                        onStateChange?.invoke(false)
                        return@post
                    }

                    try {
                        stopCurrentAudio()

                        currentTempFile = File.createTempFile("tts_audio", ".mp3", context.cacheDir)
                        FileOutputStream(currentTempFile!!).use { it.write(byteArray) }

                        currentMediaPlayer = MediaPlayer().apply {
                            setDataSource(currentTempFile!!.absolutePath)
                            prepare()

                            setOnCompletionListener {
                                Log.d("AudioManager", "Audio completed")
                                stopCurrentAudio()
                                onStateChange?.invoke(false)
                            }

                            setOnErrorListener { _, what, extra ->
                                Log.e("AudioManager", "MediaPlayer error: what=$what, extra=$extra")
                                stopCurrentAudio()
                                onStateChange?.invoke(false)
                                true
                            }

                            if (currentScreenId == screenId) {
                                start()
                                Log.d("AudioManager", "Started playing audio for: $text")
                            } else {
                                Log.d("AudioManager", "Screen changed - start cancelled")
                                release()
                                onStateChange?.invoke(false)
                            }
                        }

                    } catch (e: Exception) {
                        Log.e("AudioManager", "Playback error: ${e.message}")
                        stopCurrentAudio()
                        onStateChange?.invoke(false)
                    }
                }
            } else {
                Log.e("AudioManager", "TTS API error: $error")
                onStateChange?.invoke(false)
            }
    }

    fun stopCurrentAudio() {
        try {
            currentMediaPlayer?.let { player ->
                if (player.isPlaying) {
                    player.stop()
                    Log.d("AudioManager", "Stopped current audio")
                }
                player.release()
            }
        } catch (e: Exception) {
            Log.e("AudioManager", "Error stopping audio: ${e.message}")
        } finally {
            currentMediaPlayer = null
        }

        try {
            currentTempFile?.let { file ->
                if (file.exists()) {
                    file.delete()
                    Log.d("AudioManager", "Deleted temp file")
                }
            }
        } catch (e: Exception) {
            Log.e("AudioManager", "Error deleting temp file: ${e.message}")
        } finally {
            currentTempFile = null
        }
    }

    fun isPlaying(): Boolean {
        return try {
            currentMediaPlayer?.isPlaying == true
        } catch (e: Exception) {
            false
        }
    }

    fun onScreenExit(screenId: String) {
        if (currentScreenId == screenId) {
            Log.d("AudioManager", "Screen $screenId exit - stopping audio")
            currentScreenId = null
            stopCurrentAudio()
        }
    }

    // Backward compatibility
    fun playAudioFromText(context: Context, text: String, onStateChange: ((Boolean) -> Unit)? = null) {
        playAudioFromText(context, text, "default", onStateChange)
    }
}