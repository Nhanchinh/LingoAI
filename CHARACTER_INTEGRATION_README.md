# Character Profiles & Voice Selection Integration

## Tổng quan
Dự án đã được cập nhật để hỗ trợ:
- ✅ Character Profiles với tính cách tùy chỉnh
- ✅ Voice Selection với 20+ giọng nói khác nhau
- ✅ Conversation Context và Memory
- ✅ API Integration đầy đủ

## Các tính năng mới

### 1. Character Profiles
- **Default Characters**: 5 nhân vật có sẵn với tính cách khác nhau (Emma, Heart, Bella, Michael, Alpha)
- **Custom Characters**: Tạo nhân vật mới với tính cách tùy chỉnh
- **API Integration**: Đồng bộ với server qua Character Profiles API

### 2. Voice Selection
- **American English**: 11 giọng nữ, 9 giọng nam (af_heart, af_bella, am_michael, etc.)
- **British English**: 4 giọng nữ, 4 giọng nam (bf_emma, bm_daniel, etc.)
- **Japanese**: 4 giọng nữ, 1 giọng nam (jf_alpha, jm_kumo, etc.)
- **Quality Grades**: Từ A đến F+ với thông tin chi tiết

### 3. Enhanced API
```kotlin
// API generateText đã được cập nhật
ApiService.generateText(
    query = "Hello",
    characterId = "character_id",      // Tính cách nhân vật
    conversationId = "conversation_id", // Context hội thoại  
    userId = "user_id"                 // ID người dùng
) { code, response ->
    // Xử lý phản hồi
}
```

## Cách sử dụng

### 1. Basic Integration
```kotlin
@Composable  
fun YourChatScreen() {
    var selectedCharacter by remember { mutableStateOf(Character.DEFAULT_CHARACTERS[0]) }
    
    ChatSmartAiChatScreen(
        sentence = "Hello!",
        selectedCharacter = selectedCharacter,
        onBack = { /* handle back */ }
    )
}
```

### 2. With Character Selection
```kotlin
@Composable
fun FullFeatureDemo() {
    var showCharacterSelection by remember { mutableStateOf(true) }
    var selectedCharacter by remember { mutableStateOf<Character?>(null) }
    
    if (showCharacterSelection) {
        CharacterSelectionScreen(
            onBack = { /* handle back */ },
            onCharacterSelected = { character ->
                selectedCharacter = character
                showCharacterSelection = false
            }
        )
    } else {
        selectedCharacter?.let { character ->
            ChatSmartAiChatScreen(
                sentence = "Hello!",
                selectedCharacter = character,
                onBack = { showCharacterSelection = true }
            )
        }
    }
}
```

### 3. Custom Character Creation
```kotlin
// Tạo nhân vật mới
val newCharacter = Character(
    name = "Alex",
    personality = "You are Alex, a helpful coding assistant. You provide clear, concise answers about programming.",
    description = "Programming assistant",
    voiceId = "af_heart"
)

// Sử dụng API để lưu
ApiService.createCharacter(
    name = newCharacter.name,
    personality = newCharacter.personality,
    description = newCharacter.description,
    voice = newCharacter.voiceId
) { code, response ->
    // Character đã được tạo
}
```

## Cấu trúc Files

### Models
- `VoiceOption.kt`: Quản lý các giọng nói có sẵn
- `Character.kt`: Quản lý nhân vật, conversation, và messages

### UI Components  
- `CharacterSelectionScreen.kt`: Màn hình chọn/tạo nhân vật
- `ChatSmartAiChatScreen.kt`: Màn hình chat đã được nâng cấp
- `CharacterChatDemo.kt`: Example integration

### API Updates
- `ApiService.kt`: Enhanced với character & conversation context
- `AudioManager.kt`: Hỗ trợ voice parameter cho TTS

## API Endpoints được sử dụng

### Character Management
```
POST /characters           - Tạo nhân vật mới
GET /characters/<user_id>  - Lấy danh sách nhân vật
PUT /characters           - Cập nhật nhân vật
DELETE /characters        - Xóa nhân vật
```

### Conversation Management  
```
POST /conversations              - Tạo hội thoại mới
POST /conversations/message      - Thêm message
GET /conversations/<id>          - Lấy lịch sử
GET /conversations/user/<id>     - Lấy danh sách hội thoại
```

### Enhanced Text Generation
```
POST /gen-text
{
    "query": "Hello",
    "character_id": "character_id",     // Optional: Personality context
    "conversation_id": "conv_id",       // Optional: Conversation memory  
    "user_id": "user_id"               // Required: User identification
}
```

## Voice Options Available

### American English (High Quality)
- `af_heart` ❤️ - Grade A (Recommended)
- `af_bella` 🔥 - Grade A- (Energetic)
- `af_nicole` 🎧 - Grade B- (Tech-friendly)
- `am_michael` - Grade C+ (Professional male)
- `am_fenrir` - Grade C+ (Strong male)

### British English  
- `bf_emma` - Grade B- (Elegant female)
- `bm_george` - Grade C (Classic male)
- `bm_daniel` - Grade D (Casual male)

### Japanese
- `jf_alpha` - Grade C+ (Calm female)
- `jm_kumo` - Grade C- (Male)

## Demo và Testing

Chạy `CharacterChatDemo.kt` để test đầy đủ tính năng:
1. Character selection UI
2. Custom character creation  
3. Voice selection
4. Chat với character context
5. TTS với voice của character

## Troubleshooting

### Lỗi thường gặp:
1. **API timeout**: Đã tăng timeout lên 90s
2. **Character không load**: Fallback về DEFAULT_CHARACTERS  
3. **Voice không play**: Kiểm tra AudioManager screenId
4. **Conversation không save**: API tự tạo conversation_id mới

### Debug tips:
- Check LogCat với tag "ApiService", "AudioManager", "ChatSmartAI"
- Verify BASE_URL đã được set đúng
- Ensure USER_ID đã được set trong ApiService

## Kết luận

Tất cả tính năng đã được implement và ready để sử dụng:
✅ Character profiles với personality tùy chỉnh
✅ 20+ voice options với quality rating  
✅ API integration đầy đủ
✅ Conversation context và memory
✅ UI components hoàn chỉnh
✅ Demo và documentation

Bạn có thể bắt đầu sử dụng ngay bằng cách gọi `CharacterSelectionScreen` hoặc tích hợp trực tiếp `selectedCharacter` param vào `ChatSmartAiChatScreen`.