package com.example.myapplication.ui.flashcard

// Data class cho một flashcard
data class Flashcard(
    val id: String = "",
    val front: String, // Mặt trước (từ tiếng Anh)
    val back: String,  // Mặt sau (định nghĩa tiếng Việt)
    val ipa: String = "", // Phiên âm IPA
    val setId: String, // ID của bộ flashcard
    val createdAt: Long = System.currentTimeMillis(),
    var isLearned: Boolean = false
)

// Data class cho một bộ flashcard
data class FlashcardSet(
    val id: String = "",
    val name: String,
    val description: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val flashcards: List<Flashcard> = emptyList()
)

// Enum cho trạng thái học tập
enum class StudyMode {
    FRONT_TO_BACK, // Từ tiếng Anh -> Nghĩa tiếng Việt
    BACK_TO_FRONT, // Nghĩa tiếng Việt -> Từ tiếng Anh
    MIXED          // Trộn lẫn
}

// Data class cho kết quả học tập
data class StudyResult(
    val flashcardId: String,
    val isCorrect: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)