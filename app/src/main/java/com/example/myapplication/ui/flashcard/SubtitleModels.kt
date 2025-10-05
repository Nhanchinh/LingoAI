package com.example.myapplication.ui.flashcard

import com.google.gson.annotations.SerializedName

// Model cho một phụ đề
data class Subtitle(
    @SerializedName("start")
    val start: Double,
    @SerializedName("end")
    val end: Double,
    @SerializedName("text")
    val text: String
)

// Model cho video với phụ đề (cấu trúc cũ - để tương thích ngược)
data class VideoWithSubtitle(
    val videoId: String,
    val title: String,
    val description: String,
    val duration: String,
    val level: String,
    val subtitleFileName: String // Tên file JSON chứa phụ đề
)

// Model mới cho video info từ JSON file
data class VideoInfo(
    @SerializedName("videoId")
    val videoId: String,
    @SerializedName("title")
    val title: String,
    @SerializedName("description")
    val description: String,
    @SerializedName("duration")
    val duration: String,
    @SerializedName("level")
    val level: String,
    @SerializedName("subtitles")
    val subtitles: List<Subtitle>
)
