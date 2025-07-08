package com.example.myapplication.ui.flashcard

import android.content.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import com.example.myapplication.UserPreferences

class FlashcardRepository(context: Context) {
    private val dataStore = FlashcardDataStore(context)
    private val userPreferences = UserPreferences(context)
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    
    private val saveMutex = Mutex()
    
    private val _flashcardSets = MutableStateFlow<List<FlashcardSet>>(emptyList())
    val flashcardSets: StateFlow<List<FlashcardSet>> = _flashcardSets.asStateFlow()
    
    private val _currentSet = MutableStateFlow<FlashcardSet?>(null)
    val currentSet: StateFlow<FlashcardSet?> = _currentSet.asStateFlow()

    private var currentUserId: String? = null

    init {
        // Load dữ liệu từ DataStore khi khởi tạo và khi userId thay đổi
        scope.launch {
            userPreferences.userId.collect { userId ->
                if (currentUserId != userId) {
                    currentUserId = userId
                    // Reset current set khi chuyển user
                    _currentSet.value = null
                    // Load dữ liệu của user mới
                    loadFlashcardSets()
                }
            }
        }
    }

    private fun loadFlashcardSets() {
        scope.launch {
            dataStore.flashcardSets.collect { sets ->
                _flashcardSets.value = sets
            }
        }
    }

    // Helper function để lưu dữ liệu
    private fun saveData() {
        scope.launch {
            saveMutex.withLock {
                try {
                    dataStore.saveFlashcardSets(_flashcardSets.value)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
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
        saveData()
        return newSet.id
    }

    // Thêm flashcard
    fun addFlashcard(setId: String, front: String, back: String, ipa: String = "") {
        val newFlashcard = Flashcard(
            id = UUID.randomUUID().toString(),
            front = front,
            back = back,
            ipa = ipa,
            setId = setId
        )
        
        val updatedSets = _flashcardSets.value.map { set ->
            if (set.id == setId) {
                set.copy(flashcards = set.flashcards + newFlashcard)
            } else {
                set
            }
        }
        _flashcardSets.value = updatedSets
        
        // Cập nhật currentSet nếu cần
        if (_currentSet.value?.id == setId) {
            _currentSet.value = updatedSets.find { it.id == setId }
        }
        
        saveData()
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
        
        // Cập nhật currentSet nếu cần
        if (_currentSet.value?.id == setId) {
            _currentSet.value = updatedSets.find { it.id == setId }
        }
        
        saveData()
    }

    // Xóa bộ flashcard
    fun deleteFlashcardSet(setId: String) {
        _flashcardSets.value = _flashcardSets.value.filter { it.id != setId }
        
        // Nếu set hiện tại bị xóa, clear currentSet
        if (_currentSet.value?.id == setId) {
            _currentSet.value = null
        }
        
        saveData()
    }

    // Đặt bộ flashcard hiện tại
    fun setCurrentSet(setId: String) {
        _currentSet.value = _flashcardSets.value.find { it.id == setId }
    }

    // Cập nhật trạng thái học
    fun updateFlashcardLearnedStatus(setId: String, flashcardId: String, isLearned: Boolean) {
        val updatedSets = _flashcardSets.value.map { set ->
            if (set.id == setId) {
                set.copy(flashcards = set.flashcards.map { flashcard ->
                    if (flashcard.id == flashcardId) {
                        flashcard.copy(isLearned = isLearned)
                    } else {
                        flashcard
                    }
                })
            } else {
                set
            }
        }
        _flashcardSets.value = updatedSets
        
        // Cập nhật currentSet nếu cần
        if (_currentSet.value?.id == setId) {
            _currentSet.value = updatedSets.find { it.id == setId }
        }
        
        saveData()
    }

    // Thêm hàm batch import
    fun addFlashcardsBatch(setId: String, cards: List<Triple<String, String, String>>) {
        val newFlashcards = cards.map { (front, back, ipa) ->
            Flashcard(
                id = UUID.randomUUID().toString(),
                front = front,
                back = back,
                ipa = ipa,
                setId = setId
            )
        }
        
        val updatedSets = _flashcardSets.value.map { set ->
            if (set.id == setId) {
                set.copy(flashcards = set.flashcards + newFlashcards)
            } else {
                set
            }
        }
        _flashcardSets.value = updatedSets
        
        // Cập nhật currentSet nếu cần
        if (_currentSet.value?.id == setId) {
            _currentSet.value = updatedSets.find { it.id == setId }
        }
        
        saveData()
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