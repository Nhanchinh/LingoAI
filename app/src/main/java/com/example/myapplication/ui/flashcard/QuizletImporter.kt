package com.example.myapplication.ui.flashcard

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import java.util.regex.Pattern

class QuizletImporter {
    companion object {
        private val QUIZLET_URL_PATTERNS = listOf(
            "quizlet.com/(\\d+)/.*",
            "quizlet.com/.*/([0-9]+)",
            "quizlet.com/([0-9]+)"
        )

        suspend fun importFromUrl(url: String): List<Flashcard> = withContext(Dispatchers.IO) {
            try {
                val setId = extractSetId(url) ?: throw Exception("URL Quizlet không hợp lệ")

                // Thêm User-Agent và các headers khác để giả lập trình duyệt
                val doc = Jsoup.connect("https://quizlet.com/$setId")
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36")
                    .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8")
                    .header("Accept-Language", "en-US,en;q=0.5")
                    .header("Connection", "keep-alive")
                    .header("Upgrade-Insecure-Requests", "1")
                    .referrer("https://www.google.com")
                    .timeout(10000)
                    .get()

                val cards = mutableListOf<Flashcard>()

                // Thử nhiều selector khác nhau
                val possibleSelectors = listOf(
                    "div.SetPageTerm-content",
                    "div.SetPageTerms-term",
                    "div.TermText",
                    ".SetPageTerm-wordText",
                    ".SetPageTerm-definitionText"
                )

                var foundElements = false

                for (selector in possibleSelectors) {
                    val elements = doc.select(selector)
                    if (elements.isNotEmpty()) {
                        foundElements = true
                        if (selector == "div.TermText") {
                            // Xử lý theo cặp
                            var i = 0
                            while (i < elements.size - 1) {
                                val term = elements[i].text()
                                val definition = elements[i + 1].text()
                                if (term.isNotEmpty() && definition.isNotEmpty()) {
                                    cards.add(
                                        Flashcard(
                                            id = java.util.UUID.randomUUID().toString(),
                                            front = term,
                                            back = definition,
                                            ipa = "",
                                            setId = "",
                                            createdAt = System.currentTimeMillis()
                                        )
                                    )
                                }
                                i += 2
                            }
                        } else {
                            // Xử lý các trường hợp khác
                            elements.forEach { element ->
                                val term = element.select(".SetPageTerm-wordText, .TermText").firstOrNull()?.text() ?: ""
                                val definition = element.select(".SetPageTerm-definitionText, .TermText").lastOrNull()?.text() ?: ""

                                if (term.isNotEmpty() && definition.isNotEmpty()) {
                                    cards.add(
                                        Flashcard(
                                            id = java.util.UUID.randomUUID().toString(),
                                            front = term,
                                            back = definition,
                                            ipa = "",
                                            setId = "",
                                            createdAt = System.currentTimeMillis()
                                        )
                                    )
                                }
                            }
                        }
                        break
                    }
                }

                if (!foundElements) {
                    // Thử phương pháp cuối cùng - tìm tất cả text
                    val allText = doc.select("span.TermText")
                    var i = 0
                    while (i < allText.size - 1) {
                        val term = allText[i].text()
                        val definition = allText[i + 1].text()
                        if (term.isNotEmpty() && definition.isNotEmpty()) {
                            cards.add(
                                Flashcard(
                                    id = java.util.UUID.randomUUID().toString(),
                                    front = term,
                                    back = definition,
                                    ipa = "",
                                    setId = "",
                                    createdAt = System.currentTimeMillis()
                                )
                            )
                        }
                        i += 2
                    }
                }

                if (cards.isEmpty()) {
                    throw Exception("Không thể lấy được thẻ từ Quizlet. Vui lòng kiểm tra lại URL hoặc đảm bảo bộ thẻ không bị private")
                }

                cards
            } catch (e: Exception) {
                val errorMessage = when {
                    e.message?.contains("403") == true -> "Không thể truy cập Quizlet. Vui lòng thử lại sau"
                    e.message?.contains("404") == true -> "Không tìm thấy bộ thẻ này trên Quizlet"
                    e.message?.contains("timeout") == true -> "Kết nối đến Quizlet quá chậm. Vui lòng thử lại"
                    else -> "Lỗi khi import: ${e.message}"
                }
                throw Exception(errorMessage)
            }
        }

        private fun extractSetId(url: String): String? {
            for (pattern in QUIZLET_URL_PATTERNS) {
                val matcher = Pattern.compile(pattern).matcher(url)
                if (matcher.find()) {
                    return matcher.group(1)
                }
            }
            return null
        }
    }
}