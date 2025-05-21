
package com.example.myapplication.navigation

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class AuthViewModel : ViewModel() {
    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn

    fun setLoggedIn(value: Boolean) {
        _isLoggedIn.value = value
    }

    // Kiểm tra xem người dùng đã đăng nhập chưa từ SharedPreferences hoặc DataStore
    fun checkLoginStatus() {
        // Implement code để kiểm tra trạng thái đăng nhập từ SharedPreferences hoặc DataStore
        // _isLoggedIn.value = sharedPreferences.getBoolean("is_logged_in", false)
    }
}