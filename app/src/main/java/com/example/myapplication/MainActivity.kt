//package com.example.myapplication
//
//import android.Manifest
//import android.content.pm.PackageManager
//import android.os.Bundle
//import android.widget.Toast
//import androidx.activity.ComponentActivity
//import androidx.activity.compose.setContent
//import androidx.activity.enableEdgeToEdge
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.material3.MaterialTheme
//import androidx.compose.material3.Surface
//import androidx.compose.ui.Modifier
//import androidx.core.app.ActivityCompat
//import androidx.core.content.ContextCompat
//import androidx.navigation.compose.rememberNavController
//import com.example.myapplication.navigation.AppNavGraph
//import com.example.myapplication.ui.theme.MyApplicationTheme
//
//class MainActivity : ComponentActivity() {
//    private val PERMISSIONS_REQUEST_CODE = 123
//    private val REQUIRED_PERMISSIONS = arrayOf(
//        Manifest.permission.CAMERA,
//        Manifest.permission.RECORD_AUDIO,
//        Manifest.permission.INTERNET
//    )
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//
//        if (!allPermissionsGranted()) {
//            ActivityCompat.requestPermissions(
//                this, REQUIRED_PERMISSIONS, PERMISSIONS_REQUEST_CODE
//            )
//        } else {
//            initializeApp()
//        }
//    }
//
//    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
//        ContextCompat.checkSelfPermission(
//            baseContext, it
//        ) == PackageManager.PERMISSION_GRANTED
//    }
//
//    override fun onRequestPermissionsResult(
//        requestCode: Int,
//        permissions: Array<String>,
//        grantResults: IntArray
//    ) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//        if (requestCode == PERMISSIONS_REQUEST_CODE) {
//            if (allPermissionsGranted()) {
//                initializeApp()
//            } else {
//                Toast.makeText(
//                    this,
//                    "Cần các quyền để ứng dụng hoạt động đúng",
//                    Toast.LENGTH_LONG
//                ).show()
//            }
//        }
//    }
//
//    private fun initializeApp() {
//        enableEdgeToEdge()
//        setContent {
//            MyApplicationTheme {
//                Surface(
//                    modifier = Modifier.fillMaxSize(),
//                    color = MaterialTheme.colorScheme.background
//                ) {
//                    val navController = rememberNavController()
//                    AppNavGraph(navController = navController)
//                }
//            }
//        }
//    }
//}




//package com.example.myapplication
//
//import android.Manifest
//import android.content.pm.PackageManager
//import android.os.Bundle
//import android.widget.Toast
//import androidx.activity.ComponentActivity
//import androidx.activity.compose.setContent
//import androidx.activity.enableEdgeToEdge
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.material3.MaterialTheme
//import androidx.compose.material3.Surface
//import androidx.compose.runtime.collectAsState
//import androidx.compose.runtime.getValue
//import androidx.compose.ui.Modifier
//import androidx.core.app.ActivityCompat
//import androidx.core.content.ContextCompat
//import androidx.lifecycle.lifecycleScope
//import androidx.navigation.compose.rememberNavController
//import com.example.myapplication.api.ApiService
//import com.example.myapplication.navigation.AppNavGraph
//import com.example.myapplication.ui.theme.MyApplicationTheme
//import kotlinx.coroutines.flow.first
//import kotlinx.coroutines.launch
//
//class MainActivity : ComponentActivity() {
//    private val PERMISSIONS_REQUEST_CODE = 123
//    private val REQUIRED_PERMISSIONS = arrayOf(
//        Manifest.permission.CAMERA,
//        Manifest.permission.RECORD_AUDIO,
//        Manifest.permission.INTERNET
//    )
//
//    // Biến để lưu trạng thái đăng nhập
//    private var isUserLoggedIn = false
//    private var userId = ""
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//
//        // Kiểm tra trạng thái đăng nhập trước
//        checkLoginStatus()
//
//        if (!allPermissionsGranted()) {
//            ActivityCompat.requestPermissions(
//                this, REQUIRED_PERMISSIONS, PERMISSIONS_REQUEST_CODE
//            )
//        } else {
//            initializeApp()
//        }
//    }
//
//    private fun checkLoginStatus() {
//        // Sử dụng lifecycleScope để truy cập flow từ DataStore
//        lifecycleScope.launch {
//            val userPreferences = UserPreferences(applicationContext)
//
//            // Lấy trạng thái đăng nhập và userId
//            isUserLoggedIn = userPreferences.isLoggedIn.first()
//            userId = userPreferences.userId.first() ?: ""
//
//            // Cập nhật userId trong ApiService nếu đã đăng nhập
//            if (isUserLoggedIn && userId.isNotBlank()) {
//                ApiService.setUserId(userId)
//            }
//        }
//    }
//
//    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
//        ContextCompat.checkSelfPermission(
//            baseContext, it
//        ) == PackageManager.PERMISSION_GRANTED
//    }
//
//    override fun onRequestPermissionsResult(
//        requestCode: Int,
//        permissions: Array<String>,
//        grantResults: IntArray
//    ) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//        if (requestCode == PERMISSIONS_REQUEST_CODE) {
//            if (allPermissionsGranted()) {
//                initializeApp()
//            } else {
//                Toast.makeText(
//                    this,
//                    "Cần các quyền để ứng dụng hoạt động đúng",
//                    Toast.LENGTH_LONG
//                ).show()
//            }
//        }
//    }
//
//    private fun initializeApp() {
//        enableEdgeToEdge()
//        setContent {
//            MyApplicationTheme {
//                Surface(
//                    modifier = Modifier.fillMaxSize(),
//                    color = MaterialTheme.colorScheme.background
//                ) {
//                    val navController = rememberNavController()
//                    val userPreferences = UserPreferences(applicationContext)
//
//                    // Sử dụng collectAsState để theo dõi trạng thái đăng nhập trong Compose
//                    val isLoggedIn by userPreferences.isLoggedIn.collectAsState(initial = isUserLoggedIn)
//
//                    // Truyền trạng thái đăng nhập vào AppNavGraph
//                    AppNavGraph(
//                        navController = navController,
//                        isLoggedIn = isLoggedIn
//                    )
//                }
//            }
//        }
//    }
//}




package com.example.myapplication

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.rememberNavController
import com.example.myapplication.api.ApiService
import com.example.myapplication.navigation.AppNavGraph
import com.example.myapplication.ui.theme.MyApplicationTheme
import com.example.myapplication.UserPreferences
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private val PERMISSIONS_REQUEST_CODE = 123
    private val REQUIRED_PERMISSIONS = arrayOf(
        Manifest.permission.CAMERA,
        Manifest.permission.RECORD_AUDIO,
        Manifest.permission.INTERNET
    )

    // Biến để lưu trạng thái đăng nhập
    private var isUserLoggedIn = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Kiểm tra trạng thái đăng nhập trước
        lifecycleScope.launch {
            val userPreferences = UserPreferences(applicationContext)
            isUserLoggedIn = userPreferences.isLoggedIn.first()
            val userId = userPreferences.userId.first()
            val username = userPreferences.username.first()

            Log.d("MainActivity", "Login status: isLoggedIn=$isUserLoggedIn, userId=$userId, username=$username")

            if (isUserLoggedIn && userId != null) {
                ApiService.setUserId(userId)
                Log.d("MainActivity", "Set userId in ApiService: $userId")
            }

            // Kiểm tra quyền
            if (!allPermissionsGranted()) {
                ActivityCompat.requestPermissions(
                    this@MainActivity, REQUIRED_PERMISSIONS, PERMISSIONS_REQUEST_CODE
                )
            } else {
                initializeApp(isUserLoggedIn)
            }
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            baseContext, it
        ) == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSIONS_REQUEST_CODE) {
            if (allPermissionsGranted()) {
                initializeApp(isUserLoggedIn)
            } else {
                Toast.makeText(
                    this,
                    "Cần các quyền để ứng dụng hoạt động đúng",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun initializeApp(isLoggedIn: Boolean) {
        Log.d("MainActivity", "Initializing app with isLoggedIn=$isLoggedIn")
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    AppNavGraph(
                        navController = navController,
                        isLoggedIn = isLoggedIn
                    )
                }
            }
        }
    }
}