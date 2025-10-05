# Hệ thống Video Study với JSON Configuration

## Tổng quan
Hệ thống này cho phép bạn quản lý video học từ vựng thông qua các file JSON trong thư mục `assets`. Mỗi file JSON chứa thông tin video và phụ đề tương ứng.

## Cấu trúc JSON

Mỗi file JSON video có cấu trúc như sau:

```json
{
  "videoId": "YouTube_Video_ID",
  "title": "Tiêu đề video",
  "description": "Mô tả video",
  "duration": "Thời lượng",
  "level": "Cấp độ (Beginner/Intermediate/Advanced)",
  "subtitles": [
    {
      "start": 0.0,
      "end": 3.5,
      "text": "Nội dung phụ đề"
    }
  ]
}
```

## Các file JSON hiện có

1. **subtitle_basic_vocab.json** - Từ vựng cơ bản
2. **subtitle_pronunciation.json** - Phát âm chuẩn  
3. **subtitle_toeic.json** - Từ vựng TOEIC
4. **subtitle_ielts.json** - Từ vựng IELTS
5. **subtitle_conversation.json** - Từ vựng giao tiếp

## Cách thêm video mới

1. Tạo file JSON mới trong thư mục `app/src/main/assets/`
2. Đặt tên file theo format: `subtitle_[tên].json`
3. Thêm file vào danh sách trong `VideoDataManager.kt`:
   ```kotlin
   val videoFiles = listOf(
       "subtitle_basic_vocab.json",
       "subtitle_pronunciation.json", 
       "subtitle_toeic.json",
       "subtitle_ielts.json",
       "subtitle_conversation.json",
       "subtitle_[tên_mới].json"  // Thêm dòng này
   )
   ```

## Lấy YouTube Video ID

Để lấy Video ID từ YouTube:
1. Mở video trên YouTube
2. Copy URL (ví dụ: `https://www.youtube.com/watch?v=dQw4w9WgXcQ`)
3. Video ID là phần sau `v=` (trong ví dụ này là `dQw4w9WgXcQ`)

## Tạo phụ đề

Phụ đề được tạo theo format:
- `start`: Thời điểm bắt đầu (giây)
- `end`: Thời điểm kết thúc (giây)  
- `text`: Nội dung phụ đề

Ví dụ:
```json
{
  "start": 10.5,
  "end": 15.0,
  "text": "Hello everyone, welcome to our lesson!"
}
```

## Lợi ích

1. **Dễ quản lý**: Tất cả thông tin video trong file JSON
2. **Linh hoạt**: Dễ dàng thêm/sửa/xóa video
3. **Tự động**: Hệ thống tự động load danh sách video từ JSON
4. **Không cần hardcode**: Không cần sửa code khi thêm video mới
5. **Tương thích ngược**: Vẫn hỗ trợ format cũ nếu cần

## Các file chính

- `VideoDataManager.kt`: Quản lý việc đọc và parse JSON
- `SubtitleModels.kt`: Data classes cho video và phụ đề
- `VideoStudyContent.kt`: Hiển thị danh sách video
- `YouTubePlayerComposable.kt`: Player video với phụ đề

## Lưu ý

- Đảm bảo format JSON đúng cú pháp
- Video ID phải hợp lệ từ YouTube
- Thời gian phụ đề phải chính xác với video
- Tên file JSON phải được thêm vào danh sách trong VideoDataManager
