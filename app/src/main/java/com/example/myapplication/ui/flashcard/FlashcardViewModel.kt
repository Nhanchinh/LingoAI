package com.example.myapplication.ui.flashcard

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// Thêm sealed class ImportProgress
sealed class ImportProgress {
    object Idle : ImportProgress()
    object Loading : ImportProgress()
    data class Success(val count: Int) : ImportProgress()
    data class Error(val message: String) : ImportProgress()
}

class FlashcardViewModel(private val context: Context) : ViewModel() {
    private val repository = FlashcardRepository.getInstance(context)

    val flashcardSets: StateFlow<List<FlashcardSet>> = repository.flashcardSets
    val currentSet: StateFlow<FlashcardSet?> = repository.currentSet

    // UI State
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    // Import progress state
    private val _importProgress = MutableStateFlow<ImportProgress>(ImportProgress.Idle)
    val importProgress: StateFlow<ImportProgress> = _importProgress.asStateFlow()

    // Tạo bộ flashcard mới
    fun createFlashcardSet(name: String, description: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                repository.createFlashcardSet(name, description)
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Thêm flashcard
    fun addFlashcard(setId: String, front: String, back: String, ipa: String = "") {
        viewModelScope.launch {
            try {
                repository.addFlashcard(setId, front, back, ipa)
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    // Import từ Quizlet
    fun importFromQuizlet(setId: String, url: String) {
        viewModelScope.launch {
            try {
                _importProgress.value = ImportProgress.Loading

                // Import cards từ Quizlet
                val importedCards = QuizletImporter.importFromUrl(url)

                // Thêm từng card vào set
                importedCards.forEach { card ->
                    addFlashcard(setId, card.front, card.back, card.ipa)
                }

                _importProgress.value = ImportProgress.Success(importedCards.size)
            } catch (e: Exception) {
                _importProgress.value = ImportProgress.Error(e.message ?: "Import failed")
            }
        }
    }

    // Reset import progress
    fun resetImportProgress() {
        _importProgress.value = ImportProgress.Idle
    }

    // Xóa flashcard
    fun deleteFlashcard(setId: String, flashcardId: String) {
        repository.deleteFlashcard(setId, flashcardId)
    }

    // Xóa bộ flashcard
    fun deleteFlashcardSet(setId: String) {
        repository.deleteFlashcardSet(setId)
    }

    // Đặt bộ flashcard hiện tại
    fun setCurrentSet(setId: String) {
        repository.setCurrentSet(setId)
    }

    // Cập nhật trạng thái học
    fun updateFlashcardLearnedStatus(setId: String, flashcardId: String, isLearned: Boolean) {
        repository.updateFlashcardLearnedStatus(setId, flashcardId, isLearned)
    }

    // Xóa lỗi
    fun clearError() {
        _error.value = null
    }

    fun importFromQuizletContent(setId: String, cards: List<Triple<String, String, String>>) {
        viewModelScope.launch {
            try {
                _importProgress.value = ImportProgress.Loading

                // BATCH IMPORT - chỉ gọi repository 1 lần
                withContext(Dispatchers.IO) {
                    repository.addFlashcardsBatch(setId, cards)
                }

                // Cập nhật currentSet
                setCurrentSet(setId)

                _importProgress.value = ImportProgress.Success(cards.size)
            } catch (e: Exception) {
                _importProgress.value = ImportProgress.Error(e.message ?: "Import failed")
            }
        }
    }
}
