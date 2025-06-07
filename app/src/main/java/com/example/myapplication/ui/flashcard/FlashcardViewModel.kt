package com.example.myapplication.ui.flashcard

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class FlashcardViewModel(private val context: Context) : ViewModel() {
    private val repository = FlashcardRepository.getInstance(context)

    val flashcardSets: StateFlow<List<FlashcardSet>> = repository.flashcardSets
    val currentSet: StateFlow<FlashcardSet?> = repository.currentSet

    // UI State
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

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
}