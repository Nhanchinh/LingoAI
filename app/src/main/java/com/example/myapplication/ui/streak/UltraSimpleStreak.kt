package com.example.myapplication.ui.streak

import android.content.Context
import android.content.SharedPreferences
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 * Ultra Simple Streak - Cực kỳ đơn giản!
 * Chỉ cần vào app là tính streak 😄
 */
object UltraSimpleStreak {
    
    private const val PREFS_NAME = "ultra_simple_streak"
    private const val KEY_LAST_APP_OPEN_DATE = "last_app_open_date"
    private const val KEY_STREAK_COUNT = "streak_count"
    private const val KEY_BEST_STREAK = "best_streak"
    
    private lateinit var prefs: SharedPreferences
    
    fun initialize(context: Context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }
    
    /**
     * Gọi khi app được mở (onCreate hoặc onResume)
     */
    fun recordAppOpen() {
        if (!::prefs.isInitialized) return
        
        val today = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)
        val lastOpenDate = prefs.getString(KEY_LAST_APP_OPEN_DATE, "") ?: ""
        var currentStreak = prefs.getInt(KEY_STREAK_COUNT, 0)
        var bestStreak = prefs.getInt(KEY_BEST_STREAK, 0)
        
        when {
            // Chưa bao giờ mở app
            lastOpenDate.isEmpty() -> {
                currentStreak = 1
                bestStreak = 1
            }
            
            // Hôm qua mở app, hôm nay mở lại
            lastOpenDate == LocalDate.now().minusDays(1).format(DateTimeFormatter.ISO_LOCAL_DATE) -> {
                currentStreak++
                bestStreak = maxOf(bestStreak, currentStreak)
            }
            
            // Hôm nay đã mở rồi
            lastOpenDate == today -> {
                // Không làm gì, streak đã được tính
            }
            
            // Bỏ lỡ ngày (không mở app hôm qua)
            else -> {
                currentStreak = 1 // Reset về 1
                bestStreak = maxOf(bestStreak, 1)
            }
        }
        
        // Lưu lại
        prefs.edit()
            .putString(KEY_LAST_APP_OPEN_DATE, today)
            .putInt(KEY_STREAK_COUNT, currentStreak)
            .putInt(KEY_BEST_STREAK, bestStreak)
            .apply()
    }
    
    /**
     * Lấy thông tin streak hiện tại
     */
    fun getStreakInfo(): UltraSimpleStreakInfo {
        if (!::prefs.isInitialized) return UltraSimpleStreakInfo()
        
        val today = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)
        val lastOpenDate = prefs.getString(KEY_LAST_APP_OPEN_DATE, "") ?: ""
        val currentStreak = prefs.getInt(KEY_STREAK_COUNT, 0)
        val bestStreak = prefs.getInt(KEY_BEST_STREAK, 0)
        
        return UltraSimpleStreakInfo(
            currentStreak = currentStreak,
            bestStreak = bestStreak,
            lastOpenDate = lastOpenDate,
            isTodayOpened = lastOpenDate == today
        )
    }
    
    /**
     * Reset streak (cho testing)
     */
    fun resetStreak() {
        if (!::prefs.isInitialized) return
        
        prefs.edit()
            .putString(KEY_LAST_APP_OPEN_DATE, "")
            .putInt(KEY_STREAK_COUNT, 0)
            .putInt(KEY_BEST_STREAK, 0)
            .apply()
    }
}

/**
 * Data class đơn giản cho streak info
 */
data class UltraSimpleStreakInfo(
    val currentStreak: Int = 0,
    val bestStreak: Int = 0,
    val lastOpenDate: String = "",
    val isTodayOpened: Boolean = false
)
