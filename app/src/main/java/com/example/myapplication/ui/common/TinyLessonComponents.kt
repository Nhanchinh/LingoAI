package com.example.myapplication.ui.common


import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun TopicDropdown(
    topics: List<String>,
    selectedTopic: String,
    onTopicSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxWidth()) {
        Button(onClick = { expanded = true }, modifier = Modifier.fillMaxWidth()) {
            Text(text = selectedTopic)
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            topics.forEach { topic ->
                DropdownMenuItem(
                    text = { Text(topic) },
                    onClick = {
                        onTopicSelected(topic)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun VocabularyList(vocabulary: List<String>) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text("📚 Từ vựng liên quan:", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))
        vocabulary.forEach { word ->
            Text("• $word", style = MaterialTheme.typography.bodyLarge)
            Spacer(modifier = Modifier.height(4.dp))
        }
    }
}
