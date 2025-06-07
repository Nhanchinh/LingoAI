package com.example.myapplication.ui.chat

import android.content.Context
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.example.myapplication.api.ApiService
import java.io.File
import java.io.FileOutputStream

class RecordingManager(private val context: Context) {
    private var recorder: MediaRecorder? = null
    private var isRecording = false
    private var audioFile: File? = null

    fun startRecording(): File? {
        if (isRecording) {
            // Tránh gọi startRecording khi đang recording
            return audioFile
        }

        try {
            val file = File(context.cacheDir, "recorded_audio.3gp")
            recorder = MediaRecorder().apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
                setOutputFile(file.absolutePath)
                setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
                prepare()
                start()
            }
            audioFile = file
            isRecording = true
            return file
        } catch (e: Exception) {
            Log.e("RecordingManager", "Start recording failed: ${e.message}")
            recorder?.release()
            recorder = null
            return null
        }
    }

    fun stopRecording(onTranscription: (String?) -> Unit) {
        if (!isRecording) {
            onTranscription(null)
            return
        }

        try {
            recorder?.stop()
        } catch (e: Exception) {
            Log.e("RecordingManager", "Stop recording failed: ${e.message}")
        } finally {
            try {
                recorder?.release()
            } catch (e: Exception) {
                Log.e("RecordingManager", "Release recorder failed: ${e.message}")
            }
            recorder = null
            isRecording = false
        }

        audioFile?.let { file ->
            if (file.exists() && file.length() > 0) {
                ApiService.speechToText(file) { code, json, error ->
                    val transcription = if (code == 200 && json != null) {
                        try {
                            org.json.JSONObject(json).getString("transcription")
                        } catch (e: Exception) {
                            Log.e("RecordingManager", "Parse transcription failed: ${e.message}")
                            null
                        }
                    } else null
                    onTranscription(transcription)
                }
            } else {
                Log.e("RecordingManager", "Audio file is empty or does not exist")
                onTranscription(null)
            }
        } ?: onTranscription(null)
    }

    fun playAudioFromText(text: String) {
        // OLD CODE (bị comment):
        /*
        if (text.isBlank()) {
            Log.e("RecordingManager", "Empty text for TTS")
            return
        }

        ApiService.textToSpeech(text) { code, byteArray, error ->
            if (code == 200 && byteArray != null) {
                val handler = Handler(Looper.getMainLooper())
                handler.post {
                    var mediaPlayer: MediaPlayer? = null
                    var tempFile: File? = null

                    try {
                        tempFile = File.createTempFile("tts_audio", ".mp3", context.cacheDir)
                        FileOutputStream(tempFile).use { it.write(byteArray) }

                        mediaPlayer = MediaPlayer().apply {
                            setDataSource(tempFile.absolutePath)
                            prepare()
                            start()
                            setOnCompletionListener {
                                tempFile.delete()
                                release()
                            }
                            setOnErrorListener { _, _, _ ->
                                tempFile.delete()
                                release()
                                true
                            }
                        }
                    } catch (e: Exception) {
                        Log.e("RecordingManager", "Playback error: ${e.message}")
                        mediaPlayer?.release()
                        tempFile?.delete()
                    }
                }
            } else {
                Log.e("RecordingManager", "TTS API error: $error")
            }
        }
        */
        
        // ✅ NEW CODE: Dùng AudioManager
        AudioManager.playAudioFromText(context, text)
    }
    
    // ✅ THÊM MỚI: Function để stop audio hiện tại
    fun stopCurrentAudio() {
        AudioManager.stopCurrentAudio()
    }
    
    // ✅ THÊM MỚI: Check audio playing
    fun isAudioPlaying(): Boolean {
        return AudioManager.isPlaying()
    }
}
