package com.example.myapplication.ui.flashcard

import android.content.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*

class FlashcardRepository(context: Context) {
    private val dataStore = FlashcardDataStore(context)
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    
    private val _flashcardSets = MutableStateFlow<List<FlashcardSet>>(emptyList())
    val flashcardSets: StateFlow<List<FlashcardSet>> = _flashcardSets.asStateFlow()
    
    private val _currentSet = MutableStateFlow<FlashcardSet?>(null)
    val currentSet: StateFlow<FlashcardSet?> = _currentSet.asStateFlow()

    init {
        // Load dữ liệu từ DataStore khi khởi tạo
        scope.launch {
            dataStore.flashcardSets.collect { sets ->
                _flashcardSets.value = sets
            }
        }
    }

    // Helper function để lưu dữ liệu
    private fun saveData() {
        scope.launch {
            dataStore.saveFlashcardSets(_flashcardSets.value)
        }
    }

    // Tạo bộ flashcard mới
    fun createFlashcardSet(name: String, description: String): String {
        val newSet = FlashcardSet(
            id = UUID.randomUUID().toString(),
            name = name,
            description = description
        )
        _flashcardSets.value = _flashcardSets.value + newSet
        saveData() // LƯU DỮ LIỆU
        return newSet.id
    }

    // Thêm flashcard vào bộ
    fun addFlashcard(setId: String, front: String, back: String, ipa: String = "") {
        val newFlashcard = Flashcard(
            id = UUID.randomUUID().toString(),
            front = front,
            back = back,
            ipa = ipa,
            setId = setId
        )
        
        // Cập nhật _flashcardSets
        val updatedSets = _flashcardSets.value.map { set ->
            if (set.id == setId) {
                set.copy(flashcards = set.flashcards + newFlashcard)
            } else {
                set
            }
        }
        _flashcardSets.value = updatedSets
        
        // Cập nhật currentSet nếu đang xem set này
        if (_currentSet.value?.id == setId) {
            _currentSet.value = updatedSets.find { it.id == setId }
        }
        
        saveData() // LƯU DỮ LIỆU
    }

    // Xóa flashcard
    fun deleteFlashcard(setId: String, flashcardId: String) {
        val updatedSets = _flashcardSets.value.map { set ->
            if (set.id == setId) {
                set.copy(flashcards = set.flashcards.filter { it.id != flashcardId })
            } else {
                set
            }
        }
        _flashcardSets.value = updatedSets
        
        // Cập nhật currentSet nếu đang xem set này
        if (_currentSet.value?.id == setId) {
            _currentSet.value = updatedSets.find { it.id == setId }
        }
        
        saveData() // LƯU DỮ LIỆU
    }

    // Xóa bộ flashcard
    fun deleteFlashcardSet(setId: String) {
        _flashcardSets.value = _flashcardSets.value.filter { it.id != setId }
        // Xóa currentSet nếu đang xem set này
        if (_currentSet.value?.id == setId) {
            _currentSet.value = null
        }
        
        saveData() // LƯU DỮ LIỆU
    }

    // Lấy bộ flashcard theo ID
    fun getFlashcardSet(setId: String): FlashcardSet? {
        return _flashcardSets.value.find { it.id == setId }
    }

    // Đặt bộ flashcard hiện tại
    fun setCurrentSet(setId: String) {
        _currentSet.value = getFlashcardSet(setId)
    }

    // Cập nhật trạng thái học của flashcard
    fun updateFlashcardLearnedStatus(setId: String, flashcardId: String, isLearned: Boolean) {
        val updatedSets = _flashcardSets.value.map { set ->
            if (set.id == setId) {
                set.copy(
                    flashcards = set.flashcards.map { flashcard ->
                        if (flashcard.id == flashcardId) {
                            flashcard.copy(isLearned = isLearned)
                        } else {
                            flashcard
                        }
                    }
                )
            } else {
                set
            }
        }
        _flashcardSets.value = updatedSets
        
        // Cập nhật currentSet nếu đang xem set này
        if (_currentSet.value?.id == setId) {
            _currentSet.value = updatedSets.find { it.id == setId }
        }
        
        saveData() // LƯU DỮ LIỆU
    }

    companion object {
        @Volatile
        private var INSTANCE: FlashcardRepository? = null

        fun getInstance(context: Context): FlashcardRepository {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: FlashcardRepository(context).also { INSTANCE = it }
            }
        }
    }
}