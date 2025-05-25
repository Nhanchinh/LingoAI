package com.example.myapplication.api

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

object ApiService {
    private val client = OkHttpClient()
    private var BASE_URL = "http://<your-server>" // đổi chỗ này
    private var USER_ID = "abcb" // đổi chỗ này

    // Hàm này gọi GET API để lấy base URL mới rồi cập nhật BASE_URL
    fun fetchAndSetBaseUrl(onComplete: ((success: Boolean) -> Unit)? = null) {
        val request = Request.Builder()
            .url("https://vienvipvail-default-rtdb.firebaseio.com/api-android-ngrok.json")
            .get()
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                // Gọi callback báo lỗi hoặc thất bại
                onComplete?.invoke(false)
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (!response.isSuccessful) {
                        onComplete?.invoke(false)
                        return
                    }
                    val bodyString = response.body?.string()
                    if (!bodyString.isNullOrBlank()) {
                        // Cập nhật BASE_URL với string nhận được
                        BASE_URL = bodyString.trim().trim('"') // loại bỏ dấu " nếu có
                        Log.i("vien", BASE_URL)
                        onComplete?.invoke(true)
                    } else {
                        onComplete?.invoke(false)
                    }
                }
            }
        })
    }

    // Ví dụ các hàm api khác sử dụng BASE_URL
    // fun someApiCall() { ... BASE_URL + "/endpoint" ... }

    //1 Gửi ảnh nhận diện
    fun detectObject(imageFile: File, callback: (String?) -> Unit) {
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
                callback(null)
            }

            override fun onResponse(call: Call, response: Response) {
                callback(response.body?.string())
            }
        })
    }

    //2 Đăng ký người dùng
    fun registerUser(username: String, password: String, callback: (Int, String?) -> Unit) {
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
                callback(-1, null)
            }

            override fun onResponse(call: Call, response: Response) {
                callback(response.code, response.body?.string())
            }
        })
    }

    // 4. Đăng nhập người dùng (Login)
    fun loginUser(username: String, password: String, callback: (Int, String?) -> Unit) {
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
                callback(-1, null)
            }

            override fun onResponse(call: Call, response: Response) {
                callback(response.code, response.body?.string())
            }
        })
    }

    // 5. Thêm từ vựng (Add Vocabulary)
    // example là optional, nếu null thì không gửi trường đó
    fun addVocabulary(word: String, example: String?, callback: (Int, String?) -> Unit) {
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
                callback(-1, null) // -1 để biểu thị lỗi không gửi được request
            }

            override fun onResponse(call: Call, response: Response) {
                callback(response.code, response.body?.string())
            }
        })
    }

    // 6. Lấy danh sách từ vựng của người dùng
    fun getVocabularyList(
        pageSize: Int = 10,
        pageIndex: Int = 0,
        callback: (Int, String?) -> Unit
    ) {
        val url = "$BASE_URL/vocab/$USER_ID?pageSize=$pageSize&pageIndex=$pageIndex"
        val request = Request.Builder()
            .url(url)
            .get()
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback(-1, null)
            }

            override fun onResponse(call: Call, response: Response) {
                callback(response.code, response.body?.string())
            }
        })
    }

    // 7. POST /related-word
    fun getRelatedWords(word: String, limit: Int = 56, callback: (Int, String?) -> Unit) {
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
                callback(-1, null)
            }

            override fun onResponse(call: Call, response: Response) {
                callback(response.code, response.body?.string())
            }
        })
    }

    // 8. POST /gen-text
    fun generateText(query: String, callback: (Int, String?) -> Unit) {
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
                callback(-1, null) // -1 để báo lỗi kết nối
            }

            override fun onResponse(call: Call, response: Response) {
                callback(response.code, response.body?.string())
            }
        })
    }

    // 9. POST /text-to-speech
    fun textToSpeech(
        query: String,
        voice: String = "af_heart",
        speed: Double = 1.0,
        callback: (Int, ByteArray?, String?) -> Unit
    ) {
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
                callback(-1, null, e.message)
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val bytes = response.body?.bytes()
                    callback(response.code, bytes, null)
                } else {
                    val errorBody = response.body?.string()
                    callback(response.code, null, errorBody)
                }
            }
        })
    }

    // 10. POST /speech-to-text
    fun speechToText(
        audioFile: File,
        callback: (Int, String?, String?) -> Unit
    ) {
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
                callback(-1, null, e.message)
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val jsonStr = response.body?.string()
                    callback(response.code, jsonStr, null)
                } else {
                    val errorBody = response.body?.string()
                    callback(response.code, null, errorBody)
                }
            }
        })
    }

    // 10. DELETE /vocab xóa từ đã lưu
    fun deleteVocabulary(word: String, callback: (Int, String?) -> Unit) {
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
                callback(-1, null) // lỗi kết nối
            }

            override fun onResponse(call: Call, response: Response) {
                callback(response.code, response.body?.string())
            }
        })
    }

    fun setUserId(
        userId: String,
    ) {
        USER_ID = userId
    }

}
