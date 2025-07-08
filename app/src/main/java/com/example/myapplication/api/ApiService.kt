
package com.example.myapplication.api

import android.os.Handler
import android.os.Looper
import android.util.Log
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONObject
import java.io.File
import java.io.IOException
import java.util.concurrent.TimeUnit

object ApiService {
    // Tạo OkHttpClient với timeout dài hơn (1 phút)
    private val client = OkHttpClient.Builder()
        .connectTimeout(90, TimeUnit.SECONDS)
        .writeTimeout(90, TimeUnit.SECONDS)
        .readTimeout(90, TimeUnit.SECONDS)  // Tăng read timeout
        .build()

    // URL ban đầu - sẽ được cập nhật bởi fetchAndSetBaseUrl
    private var BASE_URL = "https://vienvipvail-default-rtdb.firebaseio.com"
    private var USER_ID = "abcb" // Giá trị mặc định, sẽ được cập nhật khi đăng nhập

    // Hàm này gọi GET API để lấy base URL mới rồi cập nhật BASE_URL
    fun fetchAndSetBaseUrl(onComplete: ((success: Boolean) -> Unit)? = null) {
        val request = Request.Builder()
            .url("https://vienvipvail-default-rtdb.firebaseio.com/api-android-ngrok.json")
            .get()
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("ApiService", "Failed to fetch BASE_URL: ${e.message}")
                onComplete?.invoke(false)
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (!response.isSuccessful) {
                        Log.e("ApiService", "Failed to fetch BASE_URL: ${response.code}")
                        onComplete?.invoke(false)
                        return
                    }
                    try {
                        val bodyString = response.body?.string()
                        if (!bodyString.isNullOrBlank()) {
                            // Cập nhật BASE_URL với string nhận được
                            BASE_URL = bodyString.trim().trim('"') // loại bỏ dấu " nếu có
                            Log.i("ApiService", "BASE_URL updated: $BASE_URL")
                            onComplete?.invoke(true)
                        } else {
                            Log.e("ApiService", "Empty BASE_URL received")
                            onComplete?.invoke(false)
                        }
                    } catch (e: Exception) {
                        Log.e("ApiService", "Error parsing BASE_URL: ${e.message}")
                        onComplete?.invoke(false)
                    }
                }
            }
        })
    }

    // Gửi ảnh nhận diện
    fun detectObject(imageFile: File, callback: (String?) -> Unit) {
        try {
            val requestBody = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("image", imageFile.name, imageFile.asRequestBody("image/jpeg".toMediaType()))
                .build()

            val request = Request.Builder()
                .url("$BASE_URL/detect-object")
                .post(requestBody)
                .build()

            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    Log.e("ApiService", "detectObject failed: ${e.message}")
                    callback(null)
                }

                override fun onResponse(call: Call, response: Response) {
                    try {
                        callback(response.body?.string())
                    } catch (e: Exception) {
                        Log.e("ApiService", "Error processing response: ${e.message}")
                        callback(null)
                    }
                }
            })
        } catch (e: Exception) {
            Log.e("ApiService", "Exception in detectObject: ${e.message}")
            callback(null)
        }
    }

    // Đăng ký người dùng
    fun registerUser(username: String, password: String, callback: (Int, String?) -> Unit) {
        try {
            val json = """
                {
                  "username": "$username",
                  "password": "$password"
                }
            """.trimIndent()

            val body = json.toRequestBody("application/json".toMediaType())
            val request = Request.Builder()
                .url("$BASE_URL/users/register")
                .post(body)
                .build()

            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    Log.e("ApiService", "registerUser failed: ${e.message}")
                    callback(-1, e.message)
                }

                override fun onResponse(call: Call, response: Response) {
                    try {
                        callback(response.code, response.body?.string())
                    } catch (e: Exception) {
                        Log.e("ApiService", "Error processing response: ${e.message}")
                        callback(-1, e.message)
                    }
                }
            })
        } catch (e: Exception) {
            Log.e("ApiService", "Exception in registerUser: ${e.message}")
            callback(-1, e.message)
        }
    }

    // Đăng nhập người dùng (Login)
    fun loginUser(username: String, password: String, callback: (Int, String?) -> Unit) {
        try {
            val json = """
                {
                  "username": "$username",
                  "password": "$password"
                }
            """.trimIndent()

            val body = json.toRequestBody("application/json".toMediaType())
            val request = Request.Builder()
                .url("$BASE_URL/users/login")
                .post(body)
                .build()

            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    Log.e("ApiService", "loginUser failed: ${e.message}")
                    callback(-1, e.message)
                }

                override fun onResponse(call: Call, response: Response) {
                    try {
                        val responseBody = response.body?.string()
                        callback(response.code, responseBody)

                        // Lưu userId nếu đăng nhập thành công
                        if (response.code == 200 && responseBody != null) {
                            try {
                                val jsonResponse = JSONObject(responseBody)
                                if (jsonResponse.has("user_id")) {
                                    val userId = jsonResponse.getString("user_id")
                                    USER_ID = userId
                                    Log.i("ApiService", "USER_ID updated: $USER_ID")
                                }
                            } catch (e: Exception) {
                                Log.e("ApiService", "Error parsing user_id: ${e.message}")
                            }
                        }
                    } catch (e: Exception) {
                        Log.e("ApiService", "Error processing response: ${e.message}")
                        callback(-1, e.message)
                    }
                }
            })
        } catch (e: Exception) {
            Log.e("ApiService", "Exception in loginUser: ${e.message}")
            callback(-1, e.message)
        }
    }

    // Thêm từ vựng (Add Vocabulary)
    fun addVocabulary(word: String, example: String?, callback: (Int, String?) -> Unit) {
        try {
            // Kiểm tra USER_ID
            if (USER_ID.isBlank()) {
                Log.e("ApiService", "USER_ID is blank")
                Handler(Looper.getMainLooper()).post {
                    callback(-1, "Bạn cần đăng nhập để lưu từ vựng")
                }
                return
            }

            val json = buildString {
                append("{")
                append("\"user_id\":\"$USER_ID\",")
                append("\"word\":\"$word\"")
                if (!example.isNullOrEmpty()) {
                    append(",\"example\":\"$example\"")
                }
                append("}")
            }

            val body = json.toRequestBody("application/json".toMediaType())
            val request = Request.Builder()
                .url("$BASE_URL/vocab")
                .post(body)
                .build()

            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    Log.e("ApiService", "addVocabulary failed: ${e.message}")
                    Handler(Looper.getMainLooper()).post {
                        callback(-1, e.message)
                    }
                }

                override fun onResponse(call: Call, response: Response) {
                    try {
                        val responseBody = response.body?.string()
                        Handler(Looper.getMainLooper()).post {
                            callback(response.code, responseBody)
                        }
                    } catch (e: Exception) {
                        Log.e("ApiService", "Error processing response: ${e.message}")
                        Handler(Looper.getMainLooper()).post {
                            callback(-1, e.message)
                        }
                    }
                }
            })
        } catch (e: Exception) {
            Log.e("ApiService", "Exception in addVocabulary: ${e.message}")
            Handler(Looper.getMainLooper()).post {
                callback(-1, e.message)
            }
        }
    }

    // Lấy danh sách từ vựng của người dùng
    fun getVocabularyList(
        pageSize: Int = 10,
        pageIndex: Int = 0,
        callback: (Int, String?) -> Unit
    ) {
        try {
            val url = "$BASE_URL/vocab/$USER_ID?pageSize=$pageSize&pageIndex=$pageIndex"
            val request = Request.Builder()
                .url(url)
                .get()
                .build()

            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    Log.e("ApiService", "getVocabularyList failed: ${e.message}")
                    callback(-1, e.message)
                }

                override fun onResponse(call: Call, response: Response) {
                    try {
                        callback(response.code, response.body?.string())
                    } catch (e: Exception) {
                        Log.e("ApiService", "Error processing response: ${e.message}")
                        callback(-1, e.message)
                    }
                }
            })
        } catch (e: Exception) {
            Log.e("ApiService", "Exception in getVocabularyList: ${e.message}")
            callback(-1, e.message)
        }
    }

    // POST /related-word
    fun getRelatedWords(word: String, limit: Int = 5, callback: (Int, String?) -> Unit) {
        try {
            val json = JSONObject().apply {
                put("word", word)
                put("limit", limit)
            }
            val requestBody = json.toString()
                .toRequestBody("application/json".toMediaType())

            val request = Request.Builder()
                .url("$BASE_URL/related-word")
                .post(requestBody)
                .build()

            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    Log.e("ApiService", "getRelatedWords failed: ${e.message}")
                    callback(-1, e.message)
                }

                override fun onResponse(call: Call, response: Response) {
                    try {
                        callback(response.code, response.body?.string())
                    } catch (e: Exception) {
                        Log.e("ApiService", "Error processing response: ${e.message}")
                        callback(-1, e.message)
                    }
                }
            })
        } catch (e: Exception) {
            Log.e("ApiService", "Exception in getRelatedWords: ${e.message}")
            callback(-1, e.message)
        }
    }

    // POST /gen-text
    fun generateText(query: String, callback: (Int, String?) -> Unit) {
        try {
            val json = JSONObject().apply {
                put("query", query)
            }
            val requestBody = json.toString()
                .toRequestBody("application/json".toMediaType())

            val request = Request.Builder()
                .url("$BASE_URL/gen-text")
                .post(requestBody)
                .build()

            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    Log.e("ApiService", "generateText failed: ${e.message}")
                    callback(-1, e.message)
                }

                override fun onResponse(call: Call, response: Response) {
                    try {
                        callback(response.code, response.body?.string())
                    } catch (e: Exception) {
                        Log.e("ApiService", "Error processing response: ${e.message}")
                        callback(-1, e.message)
                    }
                }
            })
        } catch (e: Exception) {
            Log.e("ApiService", "Exception in generateText: ${e.message}")
            callback(-1, e.message)
        }
    }

    // POST /text-to-speech
    fun textToSpeech(
        query: String,
        voice: String = "af_heart",
        speed: Double = 1.0,
        callback: (Int, ByteArray?, String?) -> Unit
    ) {
        try {
            val json = JSONObject().apply {
                put("query", query)
                put("voice", voice)
                put("speed", speed)
            }
            val requestBody = json.toString()
                .toRequestBody("application/json".toMediaType())

            val request = Request.Builder()
                .url("$BASE_URL/text-to-speech")
                .post(requestBody)
                .build()

            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    Log.e("ApiService", "textToSpeech failed: ${e.message}")
                    callback(-1, null, e.message)
                }

                override fun onResponse(call: Call, response: Response) {
                    try {
                        if (response.isSuccessful) {
                            val bytes = response.body?.bytes()
                            callback(response.code, bytes, null)
                        } else {
                            val errorBody = response.body?.string()
                            callback(response.code, null, errorBody)
                        }
                    } catch (e: Exception) {
                        Log.e("ApiService", "Error processing response: ${e.message}")
                        callback(-1, null, e.message)
                    }
                }
            })
        } catch (e: Exception) {
            Log.e("ApiService", "Exception in textToSpeech: ${e.message}")
            callback(-1, null, e.message)
        }
    }

    // POST /speech-to-text
    fun speechToText(
        audioFile: File,
        callback: (Int, String?, String?) -> Unit
    ) {
        try {
            val mediaTypeAudio = "audio/wav".toMediaTypeOrNull()
            val fileBody = audioFile.asRequestBody(mediaTypeAudio)

            val requestBody = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("audio", audioFile.name, fileBody)
                .build()

            val request = Request.Builder()
                .url("$BASE_URL/speech-to-text")
                .post(requestBody)
                .build()

            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    Log.e("ApiService", "speechToText failed: ${e.message}")
                    callback(-1, null, e.message)
                }

                override fun onResponse(call: Call, response: Response) {
                    try {
                        if (response.isSuccessful) {
                            val jsonStr = response.body?.string()
                            callback(response.code, jsonStr, null)
                        } else {
                            val errorBody = response.body?.string()
                            callback(response.code, null, errorBody)
                        }
                    } catch (e: Exception) {
                        Log.e("ApiService", "Error processing response: ${e.message}")
                        callback(-1, null, e.message)
                    }
                }
            })
        } catch (e: Exception) {
            Log.e("ApiService", "Exception in speechToText: ${e.message}")
            callback(-1, null, e.message)
        }
    }

    // DELETE /vocab xóa từ đã lưu
    fun deleteVocabulary(word: String, callback: (Int, String?) -> Unit) {
        try {
            val json = """
            {
                "user_id": "$USER_ID",
                "word": "$word"
            }
            """.trimIndent()

            val body = json.toRequestBody("application/json".toMediaType())
            val request = Request.Builder()
                .url("$BASE_URL/vocab")
                .delete(body)
                .build()

            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    Log.e("ApiService", "deleteVocabulary failed: ${e.message}")
                    callback(-1, e.message)
                }

                override fun onResponse(call: Call, response: Response) {
                    try {
                        callback(response.code, response.body?.string())
                    } catch (e: Exception) {
                        Log.e("ApiService", "Error processing response: ${e.message}")
                        callback(-1, e.message)
                    }
                }
            })
        } catch (e: Exception) {
            Log.e("ApiService", "Exception in deleteVocabulary: ${e.message}")
            callback(-1, e.message)
        }
    }

    // Cập nhật USER_ID từ bên ngoài
    fun setUserId(userId: String) {
        if (userId.isNotBlank()) {
            USER_ID = userId
            Log.i("ApiService", "USER_ID set to: $USER_ID")
        }
    }
}



