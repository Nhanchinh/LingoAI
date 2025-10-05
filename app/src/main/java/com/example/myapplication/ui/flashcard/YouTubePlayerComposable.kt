package com.example.myapplication.ui.flashcard

import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import kotlinx.coroutines.*

@Composable
fun YouTubePlayerComposable(
    videoId: String,
    subtitleFileName: String? = null,
    modifier: Modifier = Modifier,
    onSubtitleUpdate: (String) -> Unit = {}, // Callback để gửi phụ đề ra ngoài
    onPlayerReady: (YouTubePlayer) -> Unit = {},
    onPlayerError: (String) -> Unit = {}
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    
    var isPlayerReady by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var subtitles by remember { mutableStateOf<List<Subtitle>>(emptyList()) }
    
    // Đọc file phụ đề
    LaunchedEffect(subtitleFileName) {
        subtitleFileName?.let { fileName ->
            try {
                val json = context.assets.open(fileName).bufferedReader().use { it.readText() }
                val gson = Gson()
                val type = object : TypeToken<List<Subtitle>>() {}.type
                subtitles = gson.fromJson(json, type)
            } catch (e: Exception) {
                // Nếu không đọc được file phụ đề, tiếp tục mà không hiển thị phụ đề
                subtitles = emptyList()
            }
        }
    }
    
    Box(modifier = modifier.fillMaxSize()) {
        AndroidView(
            factory = { context ->
                YouTubePlayerView(context).apply {
                    layoutParams = FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    )
                    
                    addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
                        override fun onReady(youTubePlayer: YouTubePlayer) {
                            super.onReady(youTubePlayer)
                            isPlayerReady = true
                            youTubePlayer.loadVideo(videoId, 0f)
                            onPlayerReady(youTubePlayer)
                        }
                        
                        override fun onCurrentSecond(youTubePlayer: YouTubePlayer, second: Float) {
                            // Cập nhật phụ đề dựa trên thời gian thực của video
                            // Điều này sẽ hoạt động đúng cả khi tua video
                            val sub = subtitles.find {
                                second >= it.start && second <= it.end
                            }
                            onSubtitleUpdate(sub?.text ?: "")
                        }
                        
                        override fun onStateChange(
                            youTubePlayer: YouTubePlayer,
                            state: PlayerConstants.PlayerState
                        ) {
                            // Xử lý trạng thái của player
                            // Ví dụ: khi video kết thúc, có thể reset phụ đề
                            if (state == PlayerConstants.PlayerState.ENDED) {
                                onSubtitleUpdate("")
                            }
                        }
                        
                        override fun onError(youTubePlayer: YouTubePlayer, error: PlayerConstants.PlayerError) {
                            super.onError(youTubePlayer, error)
                            errorMessage = "YouTube Player Error: $error"
                            onPlayerError(error.name)
                        }
                    })
                    
                    // Tự động quản lý lifecycle
                    lifecycleOwner.lifecycle.addObserver(this)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
        )
        
        
        // Hiển thị loading state
        if (!isPlayerReady && errorMessage == null) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = Color.White
                )
            }
        }
        
        // Hiển thị error state
        errorMessage?.let { error ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize()
                ) {
                    Text(
                        text = error,
                        modifier = Modifier.align(Alignment.Center),
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

