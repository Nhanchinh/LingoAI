package com.example.myapplication.ui.flashcard

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.SoundPool
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import com.example.myapplication.R

/**
 * SoundManager để phát âm thanh khi trả lời đúng/sai
 */
class SoundManager(context: Context) {
    private val soundPool: SoundPool
    private val correctSoundId: Int
    private val wrongSoundId: Int
    
    init {
        // Tạo SoundPool với attributes hiện đại
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()
            
        soundPool = SoundPool.Builder()
            .setMaxStreams(2)
            .setAudioAttributes(audioAttributes)
            .build()
            
        // Sử dụng âm thanh custom từ res/raw - nghe hay như recorder
        // Âm thanh đúng: ui_click.mp3 (tiếng "tap" nhẹ nhàng)
        // Âm thanh sai: ui_error.mp3 (tiếng "buzz" ngắn gọn)
        correctSoundId = soundPool.load(context, R.raw.ui_click, 1)
        wrongSoundId = soundPool.load(context, R.raw.ui_error, 1)
    }
    
    fun playCorrectSound() {
        // Âm thanh đúng: nhẹ nhàng, vui tai
        soundPool.play(correctSoundId, 0.3f, 0.3f, 1, 0, 1.2f)
    }
    
    fun playWrongSound() {
        // Âm thanh sai: cảnh báo nhưng không quá khó chịu
        soundPool.play(wrongSoundId, 0.25f, 0.25f, 1, 0, 0.8f)
    }
    
    fun release() {
        soundPool.release()
    }
}

/**
 * Composable để tạo SoundManager
 */
@Composable
fun rememberSoundManager(): SoundManager {
    val context = LocalContext.current
    return remember { SoundManager(context) }
}

/**
 * DisposableEffect để cleanup SoundManager khi component unmount
 */
@Composable
fun SoundManagerEffect(soundManager: SoundManager) {
    DisposableEffect(soundManager) {
        onDispose {
            soundManager.release()
        }
    }
}
