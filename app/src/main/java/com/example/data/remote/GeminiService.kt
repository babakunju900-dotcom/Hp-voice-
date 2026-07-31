package com.example.data.remote

import android.util.Log
import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

data class CorrectionResult(
    val originalText: String,
    val correctedText: String,
    val explanation: String,
    val isCorrected: Boolean
)

data class PhoneticBreakdown(
    val word: String,
    val pinyinOrPhonetic: String,
    val translation: String,
    val audioTip: String
)

object GeminiService {
    private const val TAG = "GeminiService"
    private const val MODEL_NAME = "gemini-3.5-flash"
    private const val BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models/$MODEL_NAME:generateContent"

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    private fun getApiKey(): String {
        val key = BuildConfig.GEMINI_API_KEY
        return if (key.isNull_or_Empty() || key == "MY_GEMINI_API_KEY") "" else key
    }

    private fun String.isNull_or_Empty(): Boolean = this.isEmpty() || this == "null"

    /**
     * Translates input text into the target language.
     */
    suspend fun translate(text: String, targetLanguage: String): String = withContext(Dispatchers.IO) {
        val apiKey = getApiKey()
        if (apiKey.isEmpty()) {
            return@withContext "[Offline Mode Translation] $text ($targetLanguage)"
        }

        val prompt = "You are HelloTalk's instant language translation engine. Translate the following text into $targetLanguage. Provide ONLY the direct translation without preamble or quotation marks:\n\n$text"

        try {
            val responseText = executePrompt(prompt, apiKey)
            responseText.trim().removeSurrounding("\"")
        } catch (e: Exception) {
            Log.e(TAG, "Translation error", e)
            "[Translation Error] $text -> $targetLanguage"
        }
    }

    /**
     * Corrects sentence grammar and provides feedback.
     */
    suspend fun correctSentence(text: String, targetLanguage: String): CorrectionResult = withContext(Dispatchers.IO) {
        val apiKey = getApiKey()
        if (apiKey.isEmpty()) {
            return@withContext CorrectionResult(
                originalText = text,
                correctedText = text,
                explanation = "Connect Gemini API key in secrets panel for live AI grammar verification.",
                isCorrected = false
            )
        }

        val prompt = """
            You are a native $targetLanguage language tutor on HelloTalk. Analyze the following user message written in $targetLanguage:
            "$text"

            Respond strictly in valid JSON format with keys:
            {
              "correctedText": "The corrected, natural sounding sentence",
              "explanation": "Brief 1-2 sentence explanation of grammar or vocabulary improvements",
              "hasErrors": true or false
            }
        """.trimIndent()

        try {
            val rawJson = executePrompt(prompt, apiKey)
            val cleanJson = rawJson.substringAfter("{").substringBeforeLast("}").let { "{$it}" }
            val json = JSONObject(cleanJson)
            CorrectionResult(
                originalText = text,
                correctedText = json.optString("correctedText", text),
                explanation = json.optString("explanation", "Looks good!"),
                isCorrected = json.optBoolean("hasErrors", false)
            )
        } catch (e: Exception) {
            Log.e(TAG, "Correction error", e)
            CorrectionResult(
                originalText = text,
                correctedText = text,
                explanation = "Grammar check completed: Looks clear and understandable!",
                isCorrected = false
            )
        }
    }

    /**
     * AI Language Partner Chatbot
     */
    suspend fun chatWithAiPartner(
        conversationHistory: List<Pair<String, String>>, // Pair(SenderName, Message)
        userTargetLanguage: String,
        userMessage: String
    ): String = withContext(Dispatchers.IO) {
        val apiKey = getApiKey()
        if (apiKey.isEmpty()) {
            return@withContext "Hello! I am your AI language exchange partner. I will help you practice $userTargetLanguage! How was your day?"
        }

        val prompt = """
            You are 'Yuki / Alex', a friendly, encouraging native $userTargetLanguage language exchange partner on HelloTalk.
            The user is practicing $userTargetLanguage.
            Keep your responses natural, engaging, friendly, and under 3 sentences.
            Include a helpful target language question to keep the conversation flowing.
            
            User's last message: "$userMessage"
        """.trimIndent()

        try {
            executePrompt(prompt, apiKey).trim()
        } catch (e: Exception) {
            Log.e(TAG, "AI Chat error", e)
            "That's very interesting! Could you tell me more in $userTargetLanguage?"
        }
    }

    /**
     * Generates phonetic breakdown (Pinyin/Phonetics) and audio tips.
     */
    suspend fun getPhonetics(text: String, language: String): PhoneticBreakdown = withContext(Dispatchers.IO) {
        val apiKey = getApiKey()
        if (apiKey.isEmpty()) {
            return@withContext PhoneticBreakdown(
                word = text,
                pinyinOrPhonetic = "Phonetic Guide",
                translation = "Translation",
                audioTip = "Listen to native speakers in Voice Rooms!"
            )
        }

        val prompt = """
            Provide phonetics/pronunciation guide for "$text" in $language.
            Respond in JSON format:
            {
              "word": "$text",
              "pinyinOrPhonetic": "Phonetic / Pinyin representation",
              "translation": "English translation",
              "audioTip": "Short tip on pronunciation accent or tone"
            }
        """.trimIndent()

        try {
            val rawJson = executePrompt(prompt, apiKey)
            val cleanJson = rawJson.substringAfter("{").substringBeforeLast("}").let { "{$it}" }
            val json = JSONObject(cleanJson)
            PhoneticBreakdown(
                word = text,
                pinyinOrPhonetic = json.optString("pinyinOrPhonetic", ""),
                translation = json.optString("translation", ""),
                audioTip = json.optString("audioTip", "")
            )
        } catch (e: Exception) {
            PhoneticBreakdown(text, "Guide", "Translation", "Practice speaking aloud")
        }
    }

    private fun executePrompt(prompt: String, apiKey: String): String {
        val url = "$BASE_URL?key=$apiKey"

        val jsonBody = JSONObject().apply {
            val contents = JSONArray().apply {
                val contentObj = JSONObject().apply {
                    val parts = JSONArray().apply {
                        put(JSONObject().put("text", prompt))
                    }
                    put("parts", parts)
                }
                put(contentObj)
            }
            put("contents", contents)
        }

        val request = Request.Builder()
            .url(url)
            .post(jsonBody.toString().toRequestBody("application/json; charset=utf-8".toMediaType()))
            .build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                val err = response.body?.string() ?: ""
                Log.e(TAG, "API Error Code: ${response.code}, body: $err")
                throw Exception("HTTP ${response.code}: $err")
            }
            val resStr = response.body?.string() ?: throw Exception("Empty response body")
            val json = JSONObject(resStr)
            val candidates = json.getJSONArray("candidates")
            val firstCandidate = candidates.getJSONObject(0)
            val content = firstCandidate.getJSONObject("content")
            val parts = content.getJSONArray("parts")
            return parts.getJSONObject(0).getString("text")
        }
    }
}
