package com.example.myapplication.models

/**
 * Data class representing a voice option for text-to-speech
 * Based on the voice list from API documentation
 */
data class VoiceOption(
    val id: String,
    val name: String,
    val language: String,
    val gender: Gender,
    val traits: List<String> = emptyList(),
    val quality: String = "",
    val overallGrade: String = ""
) {
    enum class Gender {
        FEMALE, MALE
    }

    companion object {
        // American English Voices
        val AMERICAN_VOICES = listOf(
            VoiceOption("af_heart", "Heart", "American English", Gender.FEMALE, listOf("❤️"), quality = "A", overallGrade = "A"),
            VoiceOption("af_alloy", "Alloy", "American English", Gender.FEMALE, quality = "B", overallGrade = "C"),
            VoiceOption("af_aoede", "Aoede", "American English", Gender.FEMALE, quality = "B", overallGrade = "C+"),
            VoiceOption("af_bella", "Bella", "American English", Gender.FEMALE, listOf("🔥"), quality = "A", overallGrade = "A-"),
            VoiceOption("af_jessica", "Jessica", "American English", Gender.FEMALE, quality = "C", overallGrade = "D"),
            VoiceOption("af_kore", "Kore", "American English", Gender.FEMALE, quality = "B", overallGrade = "C+"),
            VoiceOption("af_nicole", "Nicole", "American English", Gender.FEMALE, listOf("🎧"), quality = "B", overallGrade = "B-"),
            VoiceOption("af_nova", "Nova", "American English", Gender.FEMALE, quality = "B", overallGrade = "C"),
            VoiceOption("af_river", "River", "American English", Gender.FEMALE, quality = "C", overallGrade = "D"),
            VoiceOption("af_sarah", "Sarah", "American English", Gender.FEMALE, quality = "B", overallGrade = "C+"),
            VoiceOption("af_sky", "Sky", "American English", Gender.FEMALE, quality = "B", overallGrade = "C-"),
            VoiceOption("am_adam", "Adam", "American English", Gender.MALE, quality = "D", overallGrade = "F+"),
            VoiceOption("am_echo", "Echo", "American English", Gender.MALE, quality = "C", overallGrade = "D"),
            VoiceOption("am_eric", "Eric", "American English", Gender.MALE, quality = "C", overallGrade = "D"),
            VoiceOption("am_fenrir", "Fenrir", "American English", Gender.MALE, quality = "B", overallGrade = "C+"),
            VoiceOption("am_liam", "Liam", "American English", Gender.MALE, quality = "C", overallGrade = "D"),
            VoiceOption("am_michael", "Michael", "American English", Gender.MALE, quality = "B", overallGrade = "C+"),
            VoiceOption("am_onyx", "Onyx", "American English", Gender.MALE, quality = "C", overallGrade = "D"),
            VoiceOption("am_puck", "Puck", "American English", Gender.MALE, quality = "B", overallGrade = "C+"),
            VoiceOption("am_santa", "Santa", "American English", Gender.MALE, quality = "C", overallGrade = "D-")
        )

        // British English Voices
        val BRITISH_VOICES = listOf(
            VoiceOption("bf_alice", "Alice", "British English", Gender.FEMALE, quality = "C", overallGrade = "D"),
            VoiceOption("bf_emma", "Emma", "British English", Gender.FEMALE, quality = "B", overallGrade = "B-"),
            VoiceOption("bf_isabella", "Isabella", "British English", Gender.FEMALE, quality = "B", overallGrade = "C"),
            VoiceOption("bf_lily", "Lily", "British English", Gender.FEMALE, quality = "C", overallGrade = "D"),
            VoiceOption("bm_daniel", "Daniel", "British English", Gender.MALE, quality = "C", overallGrade = "D"),
            VoiceOption("bm_fable", "Fable", "British English", Gender.MALE, quality = "B", overallGrade = "C"),
            VoiceOption("bm_george", "George", "British English", Gender.MALE, quality = "B", overallGrade = "C"),
            VoiceOption("bm_lewis", "Lewis", "British English", Gender.MALE, quality = "C", overallGrade = "D+")
        )

        // Japanese Voices
        val JAPANESE_VOICES = listOf(
            VoiceOption("jf_alpha", "Alpha", "Japanese", Gender.FEMALE, quality = "B", overallGrade = "C+"),
            VoiceOption("jf_gongitsune", "Gongitsune", "Japanese", Gender.FEMALE, quality = "B", overallGrade = "C"),
            VoiceOption("jf_nezumi", "Nezumi", "Japanese", Gender.FEMALE, quality = "B", overallGrade = "C-"),
            VoiceOption("jf_tebukuro", "Tebukuro", "Japanese", Gender.FEMALE, quality = "B", overallGrade = "C"),
            VoiceOption("jm_kumo", "Kumo", "Japanese", Gender.MALE, quality = "B", overallGrade = "C-")
        )

        // Mandarin Chinese Voices
        val CHINESE_VOICES = listOf(
            VoiceOption("zf_xiaobei", "Xiaobei", "Mandarin Chinese", Gender.FEMALE, quality = "C", overallGrade = "D"),
            VoiceOption("zf_xiaoni", "Xiaoni", "Mandarin Chinese", Gender.FEMALE, quality = "C", overallGrade = "D"),
            VoiceOption("zf_xiaoxiao", "Xiaoxiao", "Mandarin Chinese", Gender.FEMALE, quality = "C", overallGrade = "D"),
            VoiceOption("zf_xiaoyi", "Xiaoyi", "Mandarin Chinese", Gender.FEMALE, quality = "C", overallGrade = "D"),
            VoiceOption("zm_yunjian", "Yunjian", "Mandarin Chinese", Gender.MALE, quality = "C", overallGrade = "D"),
            VoiceOption("zm_yunxi", "Yunxi", "Mandarin Chinese", Gender.MALE, quality = "C", overallGrade = "D"),
            VoiceOption("zm_yunxia", "Yunxia", "Mandarin Chinese", Gender.MALE, quality = "C", overallGrade = "D"),
            VoiceOption("zm_yunyang", "Yunyang", "Mandarin Chinese", Gender.MALE, quality = "C", overallGrade = "D")
        )

        // Spanish Voices
        val SPANISH_VOICES = listOf(
            VoiceOption("ef_dora", "Dora", "Spanish", Gender.FEMALE),
            VoiceOption("em_alex", "Alex", "Spanish", Gender.MALE),
            VoiceOption("em_santa", "Santa", "Spanish", Gender.MALE)
        )

        // French Voices
        val FRENCH_VOICES = listOf(
            VoiceOption("ff_siwis", "Siwis", "French", Gender.FEMALE, quality = "B", overallGrade = "B-")
        )

        // Hindi Voices
        val HINDI_VOICES = listOf(
            VoiceOption("hf_alpha", "Alpha", "Hindi", Gender.FEMALE, quality = "B", overallGrade = "C"),
            VoiceOption("hf_beta", "Beta", "Hindi", Gender.FEMALE, quality = "B", overallGrade = "C"),
            VoiceOption("hm_omega", "Omega", "Hindi", Gender.MALE, quality = "B", overallGrade = "C"),
            VoiceOption("hm_psi", "Psi", "Hindi", Gender.MALE, quality = "B", overallGrade = "C")
        )

        // Italian Voices
        val ITALIAN_VOICES = listOf(
            VoiceOption("if_sara", "Sara", "Italian", Gender.FEMALE, quality = "B", overallGrade = "C"),
            VoiceOption("im_nicola", "Nicola", "Italian", Gender.MALE, quality = "B", overallGrade = "C")
        )

        // Portuguese Voices
        val PORTUGUESE_VOICES = listOf(
            VoiceOption("pf_dora", "Dora", "Brazilian Portuguese", Gender.FEMALE),
            VoiceOption("pm_alex", "Alex", "Brazilian Portuguese", Gender.MALE),
            VoiceOption("pm_santa", "Santa", "Brazilian Portuguese", Gender.MALE)
        )

        // All available voices (50+ voices total)
        val ALL_VOICES = AMERICAN_VOICES + BRITISH_VOICES + JAPANESE_VOICES + 
                        CHINESE_VOICES + SPANISH_VOICES + FRENCH_VOICES + 
                        HINDI_VOICES + ITALIAN_VOICES + PORTUGUESE_VOICES

        // Default voice
        val DEFAULT_VOICE = AMERICAN_VOICES.first { it.id == "af_heart" }

        // Get voice by ID
        fun getVoiceById(id: String): VoiceOption? {
            return ALL_VOICES.find { it.id == id }
        }

        // Get voices by language
        fun getVoicesByLanguage(language: String): List<VoiceOption> {
            return ALL_VOICES.filter { it.language == language }
        }

        // Get voices by gender
        fun getVoicesByGender(gender: Gender): List<VoiceOption> {
            return ALL_VOICES.filter { it.gender == gender }
        }
    }
}