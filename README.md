# 🌟 LingoAI - Ứng Dụng Học Tiếng Anh Thông Minh

![LingoAI Logo](app/src/main/res/drawable/lingo_ai.png)

LingoAI là một ứng dụng Android được thiết kế để giúp người dùng học từ vựng tiếng Anh một cách dễ dàng và tương tác thông qua các công cụ hỗ trợ bởi AI. Ứng dụng kết hợp các khả năng AI tiên tiến để mang lại trải nghiệm học tập liền mạch và hiệu quả.

## 📱 Mục Lục

- [🚀 Tính Năng Chính](#-tính-năng-chính)
- [🔄 Luồng Sử Dụng](#-luồng-sử-dụng)
- [🛠️ Công Nghệ Sử Dụng](#️-công-nghệ-sử-dụng)
- [📋 Yêu Cầu Hệ Thống](#-yêu-cầu-hệ-thống)
- [🚀 Hướng Dẫn Cài Đặt](#-hướng-dẫn-cài-đặt)
- [🏗️ Cấu Trúc Dự Án](#️-cấu-trúc-dự-án)
- [🎨 UI/UX Design](#-uiux-design)

## 🚀 Tính Năng Chính

### 1. 📚 Word Genie - Tra Cứu Từ Vựng Thông Minh

**Chức năng chính:**
- **Tra cứu từ vựng**: Nhập từ tiếng Anh để học nghĩa, phiên âm, audio
- **Thông tin chi tiết**: Nghĩa tiếng Việt, phiên âm IPA, audio phát âm
- **Tự động tạo ra**:
  - Từ đồng nghĩa và trái nghĩa
  - Cụm từ liên quan
  - Từ cùng chủ đề
- **Gợi ý từ thông minh** khi nhập
- **Thêm vào lịch sử** tự động

**Luồng sử dụng:**
1. Mở Word Genie từ Home
2. Nhập từ tiếng Anh cần tra
3. Xem kết quả chi tiết
4. Nghe phát âm (nếu có)
5. Từ tự động được lưu vào History

### 2. 💬 ChatSmart AI - Luyện Tập Giao Tiếp

**Chức năng chính:**
- **Luyện tập giao tiếp** với AI thông minh
- **Speech-to-Text**: Nhận diện giọng nói tiếng Anh
- **AI Response**: Phản hồi thông minh và phù hợp ngữ cảnh
- **Text-to-Speech**: Phát âm tự nhiên
- **Lưu cuộc trò chuyện** trong lịch sử

**Luồng sử dụng:**
1. Mở ChatSmart AI từ Home
2. Chọn nhân vật AI (nếu có nhiều option)
3. Bắt đầu trò chuyện bằng:
   - Gõ text
   - Nhấn mic để nói
4. AI phản hồi và có thể phát âm
5. Tiếp tục cuộc trò chuyện

### 3. 👁️ Visionary Words - Nhận Diện Qua Camera

**Chức năng chính:**
- **Nhận diện vật thể** qua camera realtime
- **Khoanh vùng và gắn nhãn** bằng tiếng Anh
- **Tránh trùng lặp** từ vựng thông minh
- **Nghĩa tiếng Việt** tương ứng
- **Lưu vào lịch sử** để ôn tập

**Luồng sử dụng:**
1. Mở Visionary Words từ Home
2. Cho phép quyền camera
3. Hướng camera vào vật thể
4. App tự động nhận diện và hiển thị tên tiếng Anh
5. Tap để xem nghĩa tiếng Việt
6. Từ được lưu vào History

### 4. 📖 History - Lịch Sử Học Tập

**Chức năng chính:**
- **Lịch sử từ vựng** đã tra cứu từ tất cả tính năng
- **Lazy loading** với pagination thông minh
- **Thêm từ vào flashcard** dễ dàng
- **Xóa từ** không cần thiết
- **Tìm kiếm** trong lịch sử

**Luồng sử dụng:**
1. Mở History từ Home
2. Xem danh sách từ đã học
3. Tap vào từ để xem chi tiết
4. Tap "Thêm vào Flashcard" để tạo bộ thẻ
5. Có thể xóa từ không cần thiết

### 5. 🃏 Learning - Hệ Thống Flashcard & Video

#### 5.1. Danh Sách Từ Vựng (Flashcard)

**Chức năng chính:**
- **Tạo bộ thẻ** từ vựng tùy chỉnh
- **Import từ vựng** từ History
- **3 chế độ học**:
  - **Flashcard**: Lật thẻ với flip animation
  - **Ghép cặp**: Nối từ với nghĩa
  - **Trắc nghiệm**: 4 đáp án A, B, C, D
- **Quản lý bộ thẻ** hiệu quả
- **Theo dõi tiến độ** học tập

**Luồng sử dụng:**
1. Mở Learning từ Home
2. Chọn "Danh sách từ vựng"
3. Tạo bộ thẻ mới hoặc chọn bộ có sẵn
4. Thêm từ vựng vào bộ thẻ
5. Chọn chế độ học:
   - **Flashcard**: Lật thẻ, đánh giá "Biết rồi"/"Chưa biết"
   - **Ghép cặp**: Kéo thả để nối từ với nghĩa
   - **Trắc nghiệm**: Chọn đáp án đúng
6. Xem kết quả và tiến độ

#### 5.2. Học Qua Video

**Chức năng chính:**
- **Danh sách video** học từ vựng tự động từ JSON
- **YouTube Player** tích hợp
- **Phụ đề đồng bộ** chính xác với video
- **Speech Recognition**: Luyện phát âm
- **Đánh giá phát âm** bằng AI
- **Tự động scan** file JSON trong assets

**Luồng sử dụng:**
1. Mở Learning từ Home
2. Chọn "Học qua video"
3. Chọn video từ danh sách
4. Xem video với phụ đề đồng bộ
5. Nhấn "Thu âm phát âm" khi có phụ đề
6. Nói theo phụ đề hiện tại
7. Nhận đánh giá phát âm (Excellent/Good/Fair/Poor/Very Poor)
8. Tiếp tục với câu tiếp theo

### 6. 👤 Profile - Thông Tin Cá Nhân

**Chức năng chính:**
- **Thông tin người dùng**: Tên, ID
- **Streak tracking**: Theo dõi chuỗi ngày học
- **Cài đặt AI**: Tùy chỉnh ChatSmart AI
- **Cài đặt thông báo**: Bật/tắt notifications
- **Đăng xuất**: Thoát tài khoản

**Luồng sử dụng:**
1. Mở Profile từ Home
2. Xem thông tin cá nhân và streak
3. Tap Settings để:
   - Cài đặt AI
   - Cài đặt thông báo
4. Tap "Đăng xuất" để thoát

## 🔄 Luồng Sử Dụng

### Luồng Đăng Nhập/Đăng Ký
```
Splash Screen → Login/Register → Home Dashboard
```

### Luồng Học Từ Vựng Cơ Bản
```
Home → Word Genie → Nhập từ → Xem kết quả → Tự động lưu History
```

### Luồng Luyện Giao Tiếp
```
Home → ChatSmart AI → Chọn nhân vật → Trò chuyện (Text/Voice) → AI phản hồi
```

### Luồng Nhận Diện Camera
```
Home → Visionary Words → Cho phép camera → Nhận diện vật thể → Xem nghĩa → Lưu History
```

### Luồng Học Flashcard
```
Home → Learning → Danh sách từ vựng → Tạo/Chọn bộ thẻ → Chọn chế độ học → Học → Xem kết quả
```

### Luồng Học Video
```
Home → Learning → Học qua video → Chọn video → Xem video + phụ đề → Thu âm → Đánh giá phát âm
```

### Luồng Quản Lý Lịch Sử
```
Home → History → Xem danh sách → Tìm kiếm → Thêm vào Flashcard/Xóa từ
```

## 🛠️ Công Nghệ Sử Dụng

### Frontend
- **Language**: Kotlin
- **UI Framework**: Jetpack Compose
- **Architecture**: MVVM with Repository Pattern
- **State Management**: StateFlow & Compose State
- **Navigation**: Compose Navigation
- **Animation**: Compose Animation

### Dependencies
```kotlin
// Core Android
implementation("androidx.core:core-ktx:1.12.0")
implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")

// Compose
implementation("androidx.compose.ui:ui")
implementation("androidx.compose.ui:ui-tooling-preview")
implementation("androidx.compose.material3:material3")
implementation("androidx.compose.material:material-icons-extended")

// Camera & Image Processing
implementation("androidx.camera:camera-camera2")
implementation("androidx.camera:camera-lifecycle")
implementation("androidx.camera:camera-view")

// Audio & Speech
implementation("androidx.media3:media3-exoplayer")

// YouTube Player
implementation("com.pierfrancescosoffritti.androidyoutubeplayer:core:12.1.0")

// JSON Parsing
implementation("com.google.code.gson:gson:2.10.1")

// Data Storage
implementation("androidx.datastore:datastore-preferences")

// HTTP Client
implementation("com.squareup.okhttp3:okhttp")
```

### Backend Architecture
- **Dynamic Base URL**: App tự động lấy base URL từ Firebase
- **User Authentication**: Login/Register với USER_ID tracking
- **RESTful APIs**: Tất cả endpoints được xây dựng từ base URL
- **No API Keys Required**: Chỉ cần internet connection

### AI & Speech Features
- **Speech Recognition**: Android SpeechRecognizer API
- **Text Similarity**: Levenshtein Distance algorithm
- **Pronunciation Scoring**: Threshold-based scoring system
- **Object Detection**: Custom ML model integration

## 📋 Yêu Cầu Hệ Thống

- **Android SDK**: API Level 24+ (Android 7.0)
- **Target SDK**: API Level 34 (Android 14)
- **RAM**: Tối thiểu 2GB
- **Storage**: 100MB dung lượng trống
- **Camera**: Có camera sau để sử dụng Visionary Words
- **Microphone**: Có mic để sử dụng ChatSmart AI và Video Study
- **Internet**: Kết nối internet ổn định (bắt buộc)

## 🚀 Hướng Dẫn Cài Đặt

### 1. Clone Repository
```bash
git clone https://github.com/Nhanchinh/LingoAI.git
cd LingoAI
```

### 2. Thiết Lập Android Studio
- Mở Android Studio
- Chọn "Open an existing project"
- Chọn thư mục LingoAI vừa clone

### 3. Đồng Bộ Dependencies
```bash
# Android Studio sẽ tự động đồng bộ Gradle
# Hoặc chạy lệnh:
./gradlew sync
```

### 4. Cấu Hình API (Tự Động)
App sẽ **tự động cấu hình** API endpoints:
- Base URL được lấy từ Firebase: `https://vienvipvail-default-rtdb.firebaseio.com/api-android-ngrok.json`
- Không cần thiết lập API keys
- User ID được cập nhật sau khi đăng nhập

### 5. Build & Run
```bash
# Debug build
./gradlew assembleDebug

# Release build
./gradlew assembleRelease

# Install to device
./gradlew installDebug
```

### 6. Chạy Server Backend
1. Vào link: `https://colab.research.google.com/drive/1jWw4_jh-BLSytfnp54L1BFSYd43ijOw_#scrollTo=ZQTDr5ZoUTR5`
2. Click Runtime → Run All
3. Đợi server chạy (khoảng 30 phút)
4. Server sẽ tự động cập nhật URL trong Firebase

### 7. Tạo Tài Khoản
- Khởi chạy app
- Tạo tài khoản mới hoặc đăng nhập
- App sẽ tự động kết nối với backend

## 🏗️ Cấu Trúc Dự Án

```
app/src/main/
├── java/com/example/myapplication/
│ ├── api/                    # API services & repositories
│ ├── navigation/            # Navigation & routing
│ ├── ui/
│ │ ├── auth/               # Authentication screens
│ │ ├── chat/               # ChatSmart AI feature
│ │ ├── common/             # Shared UI components
│ │ ├── flashcard/          # Flashcard & Video Study
│ │ │ ├── FlashcardScreen.kt
│ │ │ ├── VideoStudyScreen.kt
│ │ │ ├── VideoPlayerScreen.kt
│ │ │ ├── YouTubePlayerComposable.kt
│ │ │ ├── SpeechRecognitionManager.kt
│ │ │ ├── TextSimilarity.kt
│ │ │ └── SoundManager.kt
│ │ ├── history/            # History screen
│ │ ├── home/               # Home dashboard
│ │ ├── theme/              # App theming
│ │ ├── visionaryword/      # Camera & object detection
│ │ └── wordgenie/          # Word lookup feature
│ ├── MainActivity.kt       # Main activity
│ ├── SplashActivity.kt     # Splash screen
│ └── UserPreferences.kt    # User settings
├── res/
│ ├── drawable/             # Images and icons
│ ├── font/                 # Custom fonts
│ ├── raw/                  # Sound effects
│ ├── values/               # Colors, strings, themes
│ └── xml/                  # Backup rules
└── assets/
    ├── english_words.txt   # Word database
    ├── subtitle_*.json     # Video metadata & subtitles
    └── ...
```

## 🎨 UI/UX Design

### Theme System
- **Primary Color**: MainColor (#6C63FF)
- **Secondary Colors**: ButtonPrimary, ButtonSecondary
- **Typography**: Roboto font family
- **Design System**: Material Design 3

### Navigation Structure
- **Bottom Navigation**: 5 tabs với icon và animation
- **Screen Transitions**: Smooth slide/fade animations
- **Responsive Design**: Hỗ trợ các kích thước màn hình

### Key UI Components
- **Home Buttons**: Rounded cards với hover effects
- **Flashcard**: Flip animation với 3D effect
- **Video Player**: Full-screen với subtitle overlay
- **Progress Indicators**: Linear progress với percentage
- **Dialogs**: Material 3 design với smooth animations

## 🎯 Tính Năng Nổi Bật

### 1. Hệ Thống Video Study Thông Minh
- **Auto-scan JSON**: Tự động tìm và load video từ assets
- **Subtitle Sync**: Đồng bộ phụ đề chính xác với video
- **Speech Recognition**: Luyện phát âm realtime
- **Pronunciation Scoring**: Đánh giá phát âm bằng AI

### 2. Flashcard System Đa Dạng
- **3 Study Modes**: Flashcard, Matching, Multiple Choice
- **Progress Tracking**: Theo dõi tiến độ học tập
- **Sound Feedback**: Âm thanh khi đúng/sai
- **Smart Import**: Import từ History dễ dàng

### 3. AI Integration
- **ChatSmart AI**: Trò chuyện thông minh
- **Object Detection**: Nhận diện vật thể qua camera
- **Speech Processing**: Xử lý giọng nói tiên tiến
- **Text Similarity**: Thuật toán Levenshtein Distance

### 4. User Experience
- **Offline Support**: Lưu trữ local data
- **Lazy Loading**: Tối ưu performance
- **Smooth Animations**: Trải nghiệm mượt mà
- **Responsive Design**: Tương thích nhiều thiết bị

## 🐛 Known Issues & Future Improvements

### Current Limitations
1. **Backend Dependency**: Cần server chạy để hoạt động
2. **Video Quality**: Phụ thuộc vào chất lượng YouTube
3. **Speech Recognition**: Chỉ hỗ trợ tiếng Anh
4. **Offline Mode**: Một số tính năng cần internet

### Planned Features
1. **Offline Dictionary**: Từ điển offline hoàn chỉnh
2. **Multi-language Support**: Hỗ trợ nhiều ngôn ngữ
3. **Advanced Analytics**: Thống kê học tập chi tiết
4. **Social Features**: Chia sẻ tiến độ với bạn bè
5. **Gamification**: Hệ thống điểm, huy hiệu

---

**🎓 Happy Learning with LingoAI!**

*Ứng dụng được phát triển với mục tiêu mang lại trải nghiệm học tiếng Anh thú vị và hiệu quả nhất cho người dùng Việt Nam.*