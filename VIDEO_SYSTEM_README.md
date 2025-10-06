# Hệ thống Video Study với JSON Configuration

## Tổng quan
Hệ thống này cho phép bạn quản lý video học từ vựng thông qua các file JSON trong thư mục `assets`. App sẽ **tự động scan** tất cả file `subtitle_*.json` và hiển thị video tương ứng.

## 🚀 Tính năng mới (Cập nhật)

- ✅ **Tự động scan**: App tự động tìm tất cả file `subtitle_*.json` trong assets
- ✅ **Không cần hardcode**: Không cần sửa code khi thêm video mới
- ✅ **Dynamic loading**: Chỉ cần thêm file JSON là video sẽ xuất hiện
- ✅ **Layout ổn định**: Subtitle hiển thị với chiều cao cố định, không nhấp nháy
- ✅ **Speech recognition**: Luyện phát âm với AI tích hợp

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

## 📁 Các file JSON hiện có

1. **subtitle_basic_vocab.json** - Hội thoại về công việc
2. **subtitle_pronunciation.json** - Phát âm chuẩn  
3. **subtitle_toeic.json** - Thảo luận về Internet và công nghệ
4. **subtitle_ielts.json** - Birthday Gift Conversation
5. **subtitle_conversation.json** - Từ vựng giao tiếp

## 🎬 Cách thêm video mới (Đơn giản hóa)

### Bước 1: Tạo file JSON
Tạo file mới trong `app/src/main/assets/` với tên: `subtitle_[tên].json`

### Bước 2: Điền thông tin
```json
{
  "videoId": "YOUTUBE_VIDEO_ID",
  "title": "Tên video của bạn",
  "description": "Mô tả video",
  "duration": "Thời lượng",
  "level": "Beginner/Intermediate/Advanced",
  "subtitles": [
    { "start": 0.0, "end": 3.5, "text": "Nội dung phụ đề đầu tiên" },
    { "start": 3.6, "end": 7.0, "text": "Nội dung phụ đề thứ hai" }
  ]
}
```

### Bước 3: Build app
Chạy lại ứng dụng - video sẽ tự động xuất hiện!

## 📝 Ví dụ hoàn chỉnh

```json
{
  "videoId": "NdWVF945mHI",
  "title": "Internet and Technology Discussion",
  "description": "Jack and Evelyn discuss how the internet and technology have changed the world",
  "duration": "4 phút 29 giây",
  "level": "Advanced",
  "subtitles": [
    { "start": 0.19, "end": 15.53, "text": "What are you up to Evelyn?" },
    { "start": 15.53, "end": 19.49, "text": "Just sending a quick email to my boss." },
    { "start": 19.49, "end": 24.1, "text": "I need to clarify something that she wants me to complete by the end of the day." }
  ]
}
```

## 🔗 Lấy YouTube Video ID

Từ link YouTube: `https://www.youtube.com/watch?v=dQw4w9WgXcQ`
→ Video ID: `dQw4w9WgXcQ`

## ⏰ Tạo phụ đề từ format SRT

Nếu bạn có file SRT:
```
1
00:00:00,000 --> 00:00:03,500
Hello everyone, welcome to our lesson!
```

Chuyển thành JSON:
```json
{ "start": 0.0, "end": 3.5, "text": "Hello everyone, welcome to our lesson!" }
```

**Lưu ý**: Thời gian được chuyển từ `mm:ss,mmm` sang giây.

## 🎯 Quy tắc đặt tên file

- ✅ **Đúng**: `subtitle_basic_vocab.json`
- ✅ **Đúng**: `subtitle_toeic.json`
- ✅ **Đúng**: `subtitle_my_new_video.json`
- ❌ **Sai**: `basic_vocab.json` (thiếu prefix `subtitle_`)
- ❌ **Sai**: `video_toeic.json` (sai prefix)

## 💡 Tips viết phụ đề hiệu quả

- **Thời gian**: Mỗi subtitle nên 2-5 giây
- **Nội dung**: Ngắn gọn, dễ hiểu
- **Khoảng cách**: Có 0.1-0.5 giây nghỉ giữa các subtitle
- **Text**: Viết bình thường, không viết hoa toàn bộ
- **Độ dài**: Mỗi dòng tối đa 50-60 ký tự

## 🔧 Các file chính trong hệ thống

- **`VideoDataManager.kt`**: Tự động scan và load file JSON
- **`SubtitleModels.kt`**: Data classes cho video và phụ đề
- **`VideoStudyContent.kt`**: Hiển thị danh sách video
- **`YouTubePlayerComposable.kt`**: Player video với phụ đề đồng bộ
- **`VideoPlayerScreen.kt`**: Màn hình phát video với speech recognition

## ⚠️ Lưu ý quan trọng

- File JSON phải có cú pháp đúng
- Video ID phải tồn tại trên YouTube
- Thời gian subtitle phải chính xác
- App chỉ đọc file có prefix `subtitle_`
- Không cần restart app, chỉ cần rebuild

## 🚨 Troubleshooting

**Video không hiển thị?**
- Kiểm tra tên file có đúng format `subtitle_*.json`
- Kiểm tra cú pháp JSON có đúng không
- Kiểm tra Video ID có tồn tại không

**Subtitle không hiển thị?**
- Kiểm tra thời gian start/end có đúng không
- Kiểm tra video có đang phát không
- Kiểm tra nội dung subtitle có hợp lệ không

**App crash khi load video?**
- Kiểm tra cú pháp JSON
- Kiểm tra file có bị corrupt không
- Xem log error trong Android Studio

## 🎉 Lợi ích của hệ thống mới

1. **Hoàn toàn tự động**: Không cần sửa code khi thêm video
2. **Dễ quản lý**: Tất cả thông tin video trong file JSON
3. **Linh hoạt**: Dễ dàng thêm/sửa/xóa video
4. **Layout ổn định**: Subtitle không bị nhấp nháy
5. **Tích hợp AI**: Speech recognition cho luyện phát âm
6. **User-friendly**: Giao diện đẹp, dễ sử dụng

---

**Happy Learning! 🎓**