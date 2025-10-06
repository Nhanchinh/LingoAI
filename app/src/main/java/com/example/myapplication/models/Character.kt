package com.example.myapplication.models

/**
 * Data class representing a Character Profile
 * Used for managing AI personalities and conversation contexts
 */
data class Character(
    val id: String = "",
    val userId: String = "",
    val name: String,
    val personality: String,
    val description: String,
    val voiceId: String = VoiceOption.DEFAULT_VOICE.id,
    val isActive: Boolean = true,
    val createdAt: String = "",
    val updatedAt: String = ""
) {
    // Get the voice option for this character
    val voiceOption: VoiceOption?
        get() = VoiceOption.getVoiceById(voiceId)

    // Convert to JSON object for API requests
    fun toJsonString(): String {
        return """
        {
            "user_id": "$userId",
            "name": "$name",
            "personality": "$personality",
            "description": "$description",
            "voice_settings": {
                "voice": "$voiceId"
            },
            "is_active": $isActive
        }
        """.trimIndent()
    }

    companion object {
        // Default characters with different personalities
        val DEFAULT_CHARACTERS = listOf(
            Character(
                name = "Emma",
                personality = "Cheerful, friendly AI",
                description = "Talks like a lively, supportive best friend",
                voiceId = "af_heart"
            )
        )

        // Create character from API response
        fun fromApiResponse(json: org.json.JSONObject): Character {
            return Character(
                id = json.optString("_id", ""),
                userId = json.optString("user_id", ""),
                name = json.optString("name", ""),
                personality = json.optString("personality", ""),
                description = json.optString("description", ""),
                voiceId = json.optJSONObject("voice_settings")?.optString("voice") ?: VoiceOption.DEFAULT_VOICE.id,
                isActive = json.optBoolean("is_active", true),
                createdAt = json.optString("created_at", ""),
                updatedAt = json.optString("updated_at", "")
            )
        }

        // Parse list of characters from API response
        fun fromApiResponseList(jsonArray: org.json.JSONArray): List<Character> {
            val characters = mutableListOf<Character>()
            for (i in 0 until jsonArray.length()) {
                val json = jsonArray.getJSONObject(i)
                characters.add(fromApiResponse(json))
            }
            return characters
        }
    }
}

/**
 * Data class representing a Conversation
 * Used for managing chat history and context
 */
data class Conversation(
    val id: String = "",
    val userId: String = "",
    val characterId: String = "",
    val title: String,
    val messages: List<Message> = emptyList(),
    val createdAt: String = "",
    val updatedAt: String = ""
) {
    companion object {
        // Create conversation from API response
        fun fromApiResponse(json: org.json.JSONObject): Conversation {
            val messagesArray = json.optJSONArray("messages") ?: org.json.JSONArray()
            val messages = mutableListOf<Message>()
            for (i in 0 until messagesArray.length()) {
                val messageJson = messagesArray.getJSONObject(i)
                messages.add(Message.fromApiResponse(messageJson))
            }

            return Conversation(
                id = json.optString("_id", ""),
                userId = json.optString("user_id", ""),
                characterId = json.optString("character_id", ""),
                title = json.optString("title", ""),
                messages = messages,
                createdAt = json.optString("created_at", ""),
                updatedAt = json.optString("updated_at", "")
            )
        }

        // Parse list of conversations from API response
        fun fromApiResponseList(jsonArray: org.json.JSONArray): List<Conversation> {
            val conversations = mutableListOf<Conversation>()
            for (i in 0 until jsonArray.length()) {
                val json = jsonArray.getJSONObject(i)
                conversations.add(fromApiResponse(json))
            }
            return conversations
        }
    }
}

/**
 * Data class representing a Message in a conversation
 */
data class Message(
    val role: String, // "user" or "assistant"
    val content: String,
    val timestamp: String = ""
) {
    val isUser: Boolean
        get() = role == "user"

    companion object {
        // Create message from API response
        fun fromApiResponse(json: org.json.JSONObject): Message {
            return Message(
                role = json.optString("role", ""),
                content = json.optString("content", ""),
                timestamp = json.optString("timestamp", "")
            )
        }
    }
}