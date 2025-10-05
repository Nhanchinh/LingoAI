package com.example.myapplication.ui.flashcard

import android.content.Context
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object VideoDataManager {
    
    /**
     * Đọc tất cả file JSON video từ assets và tạo danh sách video
     */
    suspend fun loadAllVideos(context: Context): List<VideoWithSubtitle> = withContext(Dispatchers.IO) {
        val videoFiles = listOf(
            "subtitle_basic_vocab.json",
            "subtitle_pronunciation.json", 
            "subtitle_toeic.json",
            "subtitle_ielts.json",
            "subtitle_conversation.json"
        )
        
        val videos = mutableListOf<VideoWithSubtitle>()
        val gson = Gson()
        
        videoFiles.forEach { fileName ->
            try {
                // Đọc file JSON từ assets
                val json = context.assets.open(fileName).bufferedReader().use { it.readText() }
                
                // Parse JSON thành VideoInfo object
                val videoInfo = gson.fromJson(json, VideoInfo::class.java)
                
                // Chuyển đổi VideoInfo thành VideoWithSubtitle
                val videoWithSubtitle = VideoWithSubtitle(
                    videoId = videoInfo.videoId,
                    title = videoInfo.title,
                    description = videoInfo.description,
                    duration = videoInfo.duration,
                    level = videoInfo.level,
                    subtitleFileName = fileName
                )
                
                videos.add(videoWithSubtitle)
                
            } catch (e: Exception) {
                // Log lỗi nhưng tiếp tục với file khác
                println("Error loading video file $fileName: ${e.message}")
            }
        }
        
        return@withContext videos
    }
    
    /**
     * Đọc phụ đề từ file JSON
     */
    suspend fun loadSubtitles(context: Context, fileName: String): List<Subtitle> = withContext(Dispatchers.IO) {
        try {
            val json = context.assets.open(fileName).bufferedReader().use { it.readText() }
            val gson = Gson()
            val videoInfo = gson.fromJson(json, VideoInfo::class.java)
            return@withContext videoInfo.subtitles
        } catch (e: Exception) {
            println("Error loading subtitles from $fileName: ${e.message}")
            return@withContext emptyList()
        }
    }
    
    /**
     * Lấy thông tin video từ file JSON
     */
    suspend fun getVideoInfo(context: Context, fileName: String): VideoInfo? = withContext(Dispatchers.IO) {
        try {
            val json = context.assets.open(fileName).bufferedReader().use { it.readText() }
            val gson = Gson()
            return@withContext gson.fromJson(json, VideoInfo::class.java)
        } catch (e: Exception) {
            println("Error loading video info from $fileName: ${e.message}")
            return@withContext null
        }
    }
}
