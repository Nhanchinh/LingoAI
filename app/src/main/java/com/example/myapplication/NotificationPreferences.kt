package com.example.myapplication

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

val Context.notificationDataStore: DataStore<Preferences> by preferencesDataStore(name = "notification_preferences")

class NotificationPreferences(private val context: Context) {
    
    companion object {
        private val NOTIFICATION_ENABLED_KEY = booleanPreferencesKey("notification_enabled")
        private val NOTIFICATION_HOUR_KEY = intPreferencesKey("notification_hour")
        private val NOTIFICATION_MINUTE_KEY = intPreferencesKey("notification_minute")
    }
    
    val isEnabled: Flow<Boolean> = context.notificationDataStore.data
        .map { preferences ->
            preferences[NOTIFICATION_ENABLED_KEY] ?: false
        }
    
    val hour: Flow<Int> = context.notificationDataStore.data
        .map { preferences ->
            preferences[NOTIFICATION_HOUR_KEY] ?: 8
        }
    
    val minute: Flow<Int> = context.notificationDataStore.data
        .map { preferences ->
            preferences[NOTIFICATION_MINUTE_KEY] ?: 0
        }
    
    suspend fun setNotificationEnabled(enabled: Boolean) {
        context.notificationDataStore.edit { preferences ->
            preferences[NOTIFICATION_ENABLED_KEY] = enabled
        }
    }
    
    suspend fun setNotificationTime(hour: Int, minute: Int) {
        context.notificationDataStore.edit { preferences ->
            preferences[NOTIFICATION_HOUR_KEY] = hour
            preferences[NOTIFICATION_MINUTE_KEY] = minute
        }
    }
    
    suspend fun getCurrentSettings(): Triple<Boolean, Int, Int> {
        val enabled = isEnabled
        val hourValue = hour
        val minuteValue = minute
        
        return Triple(
            enabled.first() ?: false,
            hourValue.first() ?: 8,
            minuteValue.first() ?: 0
        )
    }
}
