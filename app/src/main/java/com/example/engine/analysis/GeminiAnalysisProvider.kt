package com.example.engine.analysis

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

sealed class GeminiProviderResult {
    data class Success(val aiStyleAnalysis: String, val detectedFeatures: List<String>) : GeminiProviderResult()
    data class NotConfigured(val message: String) : GeminiProviderResult()
    data class Error(val message: String) : GeminiProviderResult()
}

class GeminiAnalysisProvider {

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    fun isConfigured(): Boolean {
        val key = BuildConfig.GEMINI_API_KEY
        return !key.isNullOrBlank() && key != "MY_GEMINI_API_KEY"
    }

    suspend fun analyzeEditingAesthetic(
        referenceDescription: String,
        detectedStats: String
    ): GeminiProviderResult = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (!isConfigured()) {
            return@withContext GeminiProviderResult.NotConfigured(
                "Gemini AI API Key is not configured. The app will use the high-precision Local On-Device Analysis Engine."
            )
        }

        try {
            val prompt = """
                You are an expert video editing analysis engine for Ahmed Edits studio.
                Given the reference video technical characteristics:
                $detectedStats
                Video description/context:
                $referenceDescription
                
                Provide a JSON response with:
                {
                  "styleName": "descriptive style name",
                  "pacing": "fast/medium/cinematic",
                  "colorLookAdvice": "advice on highlights/shadows/saturation",
                  "suggestedTransitions": ["transition1", "transition2"],
                  "recommendedIntensity": 75
                }
            """.trimIndent()

            val requestJson = JSONObject().apply {
                val contentsArr = JSONArray().apply {
                    val contentObj = JSONObject().apply {
                        val partsArr = JSONArray().apply {
                            put(JSONObject().put("text", prompt))
                        }
                        put("parts", partsArr)
                    }
                    put(contentObj)
                }
                put("contents", contentsArr)
            }

            val request = Request.Builder()
                .url("https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=$apiKey")
                .post(requestJson.toString().toRequestBody("application/json".toMediaType()))
                .build()

            val response = client.newCall(request).execute()
            if (response.isSuccessful) {
                val responseBody = response.body?.string() ?: ""
                val root = JSONObject(responseBody)
                val candidates = root.optJSONArray("candidates")
                val text = candidates?.optJSONObject(0)
                    ?.optJSONObject("content")
                    ?.optJSONArray("parts")
                    ?.optJSONObject(0)
                    ?.optString("text", "") ?: ""

                GeminiProviderResult.Success(
                    aiStyleAnalysis = text,
                    detectedFeatures = listOf("Camera Motion", "Color Grading", "Beat Sync", "Punch Zooms")
                )
            } else {
                GeminiProviderResult.Error("API returned code: ${response.code} - ${response.message}")
            }
        } catch (e: Exception) {
            GeminiProviderResult.Error("Gemini API connection error: ${e.message}")
        }
    }
}
