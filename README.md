# 🌟 LingoAI - Ứng Dụng Học Tiếng Anh Thông Minh

![LingoAI Logo](app/src/main/res/drawable/lingo_ai.png)

LingoAI là một ứng dụng Android được thiết kế để giúp người dùng học từ vựng tiếng Anh một cách dễ dàng và tương tác thông qua các công cụ hỗ trợ bởi AI. Ứng dụng kết hợp các khả năng AI tiên tiến để mang lại trải nghiệm học tập liền mạch và hiệu quả.

## 📱 Screenshots

*Thêm screenshots của ứng dụng ở đây*

## 🚀 Tính Năng Chính

### 1. 📚 Word Genie
- **Tra cứu từ vựng thông minh**: Nhập từ tiếng Anh để học
- **Tự động tạo ra**:
  - Từ đồng nghĩa và trái nghĩa
  - Cụm từ liên quan
  - Từ cùng chủ đề
- **Thông tin đầy đủ**:
  - Nghĩa tiếng Việt
  - Phiên âm IPA
  - Audio phát âm
- **Gợi ý từ thông minh** khi nhập

### 2. 💬 ChatSmart AI
- **Luyện tập giao tiếp** với AI thông minh
- **Speech-to-Text**: Nhận diện giọng nói
- **AI Response**: Phản hồi thông minh và phù hợp
- **Text-to-Speech**: Phát âm tự nhiên

### 3. 👁️ Visionary Words
- **Nhận diện vật thể** qua camera
- **Khoanh vùng và gắn nhãn** bằng tiếng Anh
- **Tránh trùng lặp** từ vựng thông minh
- **Nghĩa tiếng Việt** tương ứng

### 4. 📖 History
- **Lịch sử từ vựng** đã tra cứu
- **Lazy loading** với pagination thông minh
- **Thêm từ vào flashcard** dễ dàng
- **Xóa từ** không cần thiết

### 5. 🃏 Flashcard
- **Tạo bộ thẻ** từ vựng tùy chỉnh
- **Import từ Quizlet**
- **Chế độ học** với flip animation
- **Quản lý bộ thẻ** hiệu quả

## 🛠️ Công Nghệ Sử Dụng

### Frontend
- **Language**: Kotlin
- **UI Framework**: Jetpack Compose
- **Architecture**: MVVM with Repository Pattern
- **State Management**: StateFlow & Compose State
- **Navigation**: Compose Navigation

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

## 📋 Yêu Cầu Hệ Thống

- **Android SDK**: API Level 24+ (Android 7.0)
- **Target SDK**: API Level 34 (Android 14)
- **RAM**: Tối thiểu 2GB
- **Storage**: 100MB dung lượng trống
- **Camera**: Có camera sau để sử dụng Visionary Words
- **Microphone**: Có mic để sử dụng ChatSmart AI
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

### 4. Cấu Hình API (đã Tự Động khi kéo dự án về nên không cần phải làm )
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
### 6. chạy server 
vào link `https://colab.research.google.com/drive/1jWw4_jh-BLSytfnp54L1BFSYd43ijOw_#scrollTo=ZQTDr5ZoUTR5`
click runtime  chọn run all sau đó đợi server chạy (khoảng 30p ^^')

### 6. Tạo Tài Khoản
- Khởi chạy app
- Tạo tài khoản mới hoặc đăng nhập
- App sẽ tự động kết nối với backend

## 🏗️ Cấu Trúc Dự Án

app/src/main/
├── java/com/example/myapplication/
│ ├── api/ # API services
│ ├── navigation/ # Navigation & ViewModels
│ ├── ui/
│ │ ├── auth/ # Authentication screens
│ │ ├── chat/ # ChatSmart AI feature
│ │ ├── common/ # Shared UI components
│ │ ├── flashcard/ # Flashcard management
│ │ ├── history/ # History screen
│ │ ├── home/ # Home dashboard
│ │ ├── theme/ # App theming
│ │ ├── visionaryword/ # Camera & object detection
│ │ └── wordgenie/ # Word lookup feature
│ ├── MainActivity.kt # Main activity
│ ├── SplashActivity.kt # Splash screen
│ └── UserPreferences.kt # User settings
├── res/
│ ├── drawable/ # Images and icons
│ ├── font/ # Custom fonts
│ ├── values/ # Colors, strings, themes
│ └── xml/ # Backup rules
└── assets/
└── english_words.txt # Word database


## 🎨 UI/UX Design

### Theme
- **Primary Color**: MainColor (#6C63FF)
- **Secondary Colors**: ButtonPrimary, ButtonSecondary
- **Typography**: Roboto font family
- **Design System**: Material Design 3

### Navigation
- **Bottom Navigation**: 5 tabs với icon và animation
- **Screen Transitions**: Smooth animations
- **Responsive Design**: Hỗ trợ các kích thước màn hình

## 📝 Development Notes

### State Management
- Sử dụng `remember` cho local state
- `StateFlow` cho data repositories
- `LaunchedEffect` cho side effects

### API Integration
- Repository pattern cho data access
- Error handling với try-catch
- Loading states cho UX tốt hơn

### Performance Optimization
- Lazy loading cho History
- Image caching cho Visionary Words
- Efficient Compose recomposition

## 🐛 Known Issues

1. **Lazy Loading**: Backend chưa hỗ trợ pagination hoàn chỉnh
2. **Audio Playback**: Có thể delay trên một số thiết bị
3. **Camera Focus**: Tự động focus chưa hoàn hảo




Hãy bắt đầu học tiếng Anh một cách thông minh với LingoAI!



