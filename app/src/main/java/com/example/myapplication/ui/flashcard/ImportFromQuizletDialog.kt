package com.example.myapplication.ui.flashcard

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import com.example.myapplication.ui.common.KeyboardDismissWrapper
import com.example.myapplication.ui.theme.ButtonPrimary
import com.example.myapplication.ui.theme.ButtonSecondary
import com.example.myapplication.ui.theme.TextPrimary

// Giữ nguyên tên hàm parseQuizletContent nhưng thêm IPA
private fun parseQuizletContent(content: String): List<Triple<String, String, String>> {
    return try {
        val lines = content.split("\n").map { it.trim() }
        val cards = mutableListOf<Triple<String, String, String>>()

        var i = 0
        while (i < lines.size - 1) {
            val front = lines[i]
            val back = if (i + 1 < lines.size) lines[i + 1] else ""
            val ipa = if (i + 2 < lines.size) lines[i + 2] else ""

            // Chỉ thêm card khi có đủ front và back
            if (front.isNotEmpty() && back.isNotEmpty()) {
                cards.add(Triple(front, back, ipa))
            }

            i += 3 // Luôn nhảy 3 dòng để đảm bảo đúng format
        }

        cards
    } catch (e: Exception) {
        emptyList()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImportFromQuizletDialog(
    onDismiss: () -> Unit,
    onImport: (List<Triple<String, String, String>>) -> Unit // Đổi thành Triple để thêm IPA
) {
    var content by remember { mutableStateOf("") }
    var isError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                "Import danh sách từ vựng",
                color = TextPrimary
            )
        },
        text = {
            KeyboardDismissWrapper {


            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                // Phần hướng dẫn với scroll
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = ButtonSecondary.copy(alpha = 0.1f)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(12.dp)
                    ) {
                        Text(
                            "Định dạng danh sách:",
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            "• Mỗi từ phải viết trên 3 dòng riêng biệt",
                            color = TextPrimary
                        )
                        Text(
                            "• Dòng 1: từ tiếng Anh",
                            color = TextPrimary
                        )
                        Text(
                            "• Dòng 2: nghĩa tiếng Việt",
                            color = TextPrimary
                        )
                        Text(
                            "• Dòng 3: phiên âm IPA (để trống nếu không có)",
                            color = TextPrimary
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            "Ví dụ đúng:",
                            fontWeight = FontWeight.Bold,
                            color = Color.Green
                        )
                        Text(
                            """
                            hello
                            xin chào
                            həˈləʊ
                            goodbye
                            tạm biệt
                            ˈɡʊdbaɪ
                            """.trimIndent(),
                            color = Color.Green,
                            modifier = Modifier.padding(start = 8.dp)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            "Ví dụ sai:",
                            fontWeight = FontWeight.Bold,
                            color = Color.Red
                        )
                        Text(
                            """
                            hello xin chào həˈləʊ
                            goodbye, tạm biệt, ˈɡʊdbaɪ
                            """.trimIndent(),
                            color = Color.Red,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Ô nhập liệu
                OutlinedTextField(
                    value = content,
                    onValueChange = {
                        content = it
                        isError = false
                    },
                    label = { Text("Paste danh sách từ vựng") },
                    placeholder = {
                        Text(
                            "hello\nxin chào\nhəˈləʊ\ngoodbye\ntạm biệt\nˈɡʊdbaɪ",
                            color = TextPrimary.copy(alpha = 0.5f)
                        )
                    },
                    isError = isError,
                    supportingText = if (isError) {
                        { Text(errorMessage, color = MaterialTheme.colorScheme.error) }
                    } else null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ButtonPrimary,
                        unfocusedBorderColor = ButtonSecondary
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Preview kết quả trong card có scroll
                if (content.isNotEmpty()) {
                    val cards = parseQuizletContent(content)
                    if (cards.isNotEmpty()) {
                        Text(
                            "Xem trước ${cards.size} thẻ:",
                            fontWeight = FontWeight.Medium,
                            color = TextPrimary
                        )
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = ButtonSecondary.copy(alpha = 0.1f)
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .verticalScroll(rememberScrollState())
                                    .padding(8.dp)
                            ) {
                                cards.forEach { (front, back, ipa) ->
                                    Card(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 4.dp),
                                        colors = CardDefaults.cardColors(
                                            containerColor = ButtonSecondary
                                        )
                                    ) {
                                        Column(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(8.dp)
                                        ) {
                                            Text(front, fontWeight = FontWeight.Medium)
                                            Text(back, color = TextPrimary.copy(alpha = 0.7f))
                                            if (ipa.isNotEmpty()) {
                                                Text("/$ipa/", color = Color(0xFF2196F3))
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    try {
                        val cards = parseQuizletContent(content)
                        if (cards.isEmpty()) {
                            isError = true
                            errorMessage = "Không tìm thấy từ vựng nào. Vui lòng kiểm tra lại định dạng"
                        } else {
                            onImport(cards)
                        }
                    } catch (e: Exception) {
                        isError = true
                        errorMessage = "Định dạng không hợp lệ. Mỗi từ cần có 3 dòng (từ, nghĩa và IPA)"
                    }
                }
            ) {
                Text("Import", color = ButtonPrimary)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Hủy", color = TextPrimary)
            }
        },
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(16.dp)
    )
}