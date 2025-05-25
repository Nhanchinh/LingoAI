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
    }

    fun stopRecording(onTranscription: (String?) -> Unit) {
        if (!isRecording) return

        try {
            recorder?.stop()
        } catch (e: Exception) {
            Log.e("Recorder", "stop failed: ${e.message}")
        }
        recorder?.release()
        recorder = null
        isRecording = false

        audioFile?.let { file ->
            ApiService.speechToText(file) { code, json, error ->
                val transcription = if (code == 200) {
                    try {
                        org.json.JSONObject(json).getString("transcription")
                    } catch (e: Exception) {
                        null
                    }
                } else null
                onTranscription(transcription)
            }
        }
    }

    fun playAudioFromText(text: String) {
        ApiService.textToSpeech(text) { code, byteArray, error ->
            if (code == 200 && byteArray != null) {
                val handler = Handler(Looper.getMainLooper())
                handler.post {
                    try {
                        val tempFile = File.createTempFile("tts_audio", ".mp3", context.cacheDir)
                        FileOutputStream(tempFile).use { it.write(byteArray) }

                        val mediaPlayer = MediaPlayer().apply {
                            setDataSource(tempFile.absolutePath)
                            prepare()
                            start()
                            setOnCompletionListener {
                                tempFile.delete()
                                release()
                            }
                        }
                    } catch (e: Exception) {
                        Log.e("AudioPlayer", "Playback error: ${e.message}")
                    }
                }
            } else {
                Log.e("TTS", "Error: $error")
            }
        }
    }
}
