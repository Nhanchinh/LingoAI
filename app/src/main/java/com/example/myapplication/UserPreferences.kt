package com.example.myapplication



import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Sử dụng DataStore thay vì SharedPreferences
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

class UserPreferences(private val context: Context) {
    companion object {
        private val USER_ID_KEY = stringPreferencesKey("user_id")
        private val USERNAME_KEY = stringPreferencesKey("username")
        private val AUTH_TOKEN_KEY = stringPreferencesKey("auth_token")
        private val AI_VOICE_KEY = stringPreferencesKey("ai_voice")
    }

    // Lưu thông tin người dùng
    suspend fun saveUserData(userId: String, username: String) {
        context.dataStore.edit { preferences ->
            preferences[USER_ID_KEY] = userId
            preferences[USERNAME_KEY] = username
        }
    }

    // Lưu token xác thực
    suspend fun saveAuthToken(token: String) {
        context.dataStore.edit { preferences ->
            preferences[AUTH_TOKEN_KEY] = token
        }
    }

    // Lấy ID người dùng
    val userId: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[USER_ID_KEY]
    }

    // Lấy tên người dùng
    val username: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[USERNAME_KEY]
    }

    // Kiểm tra đã đăng nhập hay chưa
    val isLoggedIn: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[USER_ID_KEY] != null
    }

    // Xóa dữ liệu khi đăng xuất
    suspend fun clearUserData() {
        context.dataStore.edit { preferences ->
            preferences.remove(USER_ID_KEY)
            preferences.remove(USERNAME_KEY)
            preferences.remove(AUTH_TOKEN_KEY)
            preferences.remove(AI_VOICE_KEY)
        }
    }

    // --- AI Preferences ---
    val aiVoice: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[AI_VOICE_KEY]
    }

    suspend fun saveAiVoice(voice: String) {
        context.dataStore.edit { preferences ->
            preferences[AI_VOICE_KEY] = voice
        }
    }
}