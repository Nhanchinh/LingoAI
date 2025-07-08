package com.example.myapplication.ui.flashcard

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.first
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString
import com.example.myapplication.UserPreferences

// Extension để tạo DataStore
val Context.flashcardDataStore: DataStore<Preferences> by preferencesDataStore(name = "flashcard_preferences")

// Serializable data classes
@Serializable
data class SerializableFlashcard(
    val id: String = "",
    val front: String,
    val back: String,
    val ipa: String = "",
    val setId: String,
    val createdAt: Long = System.currentTimeMillis(),
    val isLearned: Boolean = false
)

@Serializable
data class SerializableFlashcardSet(
    val id: String = "",
    val name: String,
    val description: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val flashcards: List<SerializableFlashcard> = emptyList()
)

class FlashcardDataStore(private val context: Context) {
    private val userPreferences = UserPreferences(context)
    
    // Lấy key theo userId hiện tại
    private suspend fun getFlashcardSetsKey(): String {
        val userId = userPreferences.userId.first()
        return if (userId != null) "flashcard_sets_$userId" else "flashcard_sets"
    }

    // Lưu flashcard sets
    suspend fun saveFlashcardSets(sets: List<FlashcardSet>) {
        try {
            val serializableSets = sets.map { set ->
                SerializableFlashcardSet(
                    id = set.id,
                    name = set.name,
                    description = set.description,
                    createdAt = set.createdAt,
                    flashcards = set.flashcards.map { flashcard ->
                        SerializableFlashcard(
                            id = flashcard.id,
                            front = flashcard.front,
                            back = flashcard.back,
                            ipa = flashcard.ipa,
                            setId = flashcard.setId,
                            createdAt = flashcard.createdAt,
                            isLearned = flashcard.isLearned
                        )
                    }
                )
            }
            
            val jsonString = Json.encodeToString(serializableSets)
            val key = stringPreferencesKey(getFlashcardSetsKey())
            
            context.flashcardDataStore.edit { preferences ->
                preferences[key] = jsonString
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // Lấy flashcard sets theo userId hiện tại
    val flashcardSets: Flow<List<FlashcardSet>> = userPreferences.userId.map { userId ->
        try {
            val keyName = if (userId != null) "flashcard_sets_$userId" else "flashcard_sets"
            val key = stringPreferencesKey(keyName)
            
            val preferences = context.flashcardDataStore.data.first()
            val jsonString = preferences[key] ?: return@map emptyList()
            val serializableSets = Json.decodeFromString<List<SerializableFlashcardSet>>(jsonString)
            
            serializableSets.map { set ->
                FlashcardSet(
                    id = set.id,
                    name = set.name,
                    description = set.description,
                    createdAt = set.createdAt,
                    flashcards = set.flashcards.map { flashcard ->
                        Flashcard(
                            id = flashcard.id,
                            front = flashcard.front,
                            back = flashcard.back,
                            ipa = flashcard.ipa,
                            setId = flashcard.setId,
                            createdAt = flashcard.createdAt,
                            isLearned = flashcard.isLearned
                        )
                    }
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
} 