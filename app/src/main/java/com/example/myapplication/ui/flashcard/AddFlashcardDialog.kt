package com.example.myapplication.ui.flashcard

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.shape.RoundedCornerShape
import com.example.myapplication.ui.theme.ButtonPrimary
import com.example.myapplication.ui.theme.ButtonSecondary
import com.example.myapplication.ui.theme.TextPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddFlashcardDialog(
    onDismiss: () -> Unit,
    onAdd: (String, String, String) -> Unit
) {
    var front by remember { mutableStateOf("") }
    var back by remember { mutableStateOf("") }
    var ipa by remember { mutableStateOf("") }

    var frontError by remember { mutableStateOf(false) }
    var backError by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                "Thêm thẻ mới",
                color = TextPrimary
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                // Front (English word)
                OutlinedTextField(
                    value = front,
                    onValueChange = {
                        front = it
                        frontError = false
                    },
                    label = { Text("Từ tiếng Anh") },
                    isError = frontError,
                    supportingText = if (frontError) {
                        { Text("Vui lòng nhập từ tiếng Anh") }
                    } else null,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ButtonPrimary,
                        unfocusedBorderColor = ButtonSecondary
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                // IPA
                OutlinedTextField(
                    value = ipa,
                    onValueChange = { ipa = it },
                    label = { Text("Phiên âm IPA (không bắt buộc)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ButtonPrimary,
                        unfocusedBorderColor = ButtonSecondary
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Back (Vietnamese meaning)
                OutlinedTextField(
                    value = back,
                    onValueChange = {
                        back = it
                        backError = false
                    },
                    label = { Text("Nghĩa tiếng Việt") },
                    isError = backError,
                    supportingText = if (backError) {
                        { Text("Vui lòng nhập nghĩa tiếng Việt") }
                    } else null,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ButtonPrimary,
                        unfocusedBorderColor = ButtonSecondary
                    )
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    when {
                        front.isBlank() -> frontError = true
                        back.isBlank() -> backError = true
                        else -> {
                            onAdd(front.trim(), back.trim(), ipa.trim())
                        }
                    }
                }
            ) {
                Text(
                    "Thêm",
                    color = ButtonPrimary
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    "Hủy",
                    color = TextPrimary
                )
            }
        },
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(16.dp)
    )
}