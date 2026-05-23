package com.example.api

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@JsonClass(generateAdapter = true)
data class Part(
    val text: String? = null
)

@JsonClass(generateAdapter = true)
data class Content(
    val parts: List<Part>
)

@JsonClass(generateAdapter = true)
data class GenerateContentRequest(
    val contents: List<Content>,
    val systemInstruction: Content? = null
)

@JsonClass(generateAdapter = true)
data class Candidate(
    val content: Content
)

@JsonClass(generateAdapter = true)
data class GenerateContentResponse(
    val candidates: List<Candidate>?
)

interface GeminiApiService {
    @POST("v1beta/models/gemini-3.5-flash:generateContent")
    suspend fun generateContent(
        @Query("key") apiKey: String,
        @Body request: GenerateContentRequest
    ): GenerateContentResponse
}

object RetrofitClient {
    private const val BASE_URL = "https://generativelanguage.googleapis.com/"

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    val service: GeminiApiService by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
        retrofit.create(GeminiApiService::class.java)
    }
}

object AegisBrain {
    suspend fun askAegis(prompt: String, apiKey: String, chatHistory: List<com.example.api.Content> = emptyList()): String = withContext(Dispatchers.IO) {
        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext getMockResponse(prompt)
        }

        val request = GenerateContentRequest(
            contents = chatHistory + Content(parts = listOf(Part(text = prompt))),
            systemInstruction = Content(parts = listOf(Part(text = "You are AEGIS, a premium, motivating, highly intelligent student life AI operating system companion. You help students optimize schedules, plan studies, answer questions, and stay highly disciplined. Keep instructions futuristic, direct, motivational, and extremely practical.")))
        )

        try {
            val response = RetrofitClient.service.generateContent(apiKey, request)
            response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text 
                ?: "AEGIS diagnostics offline. No response received."
        } catch (e: Exception) {
            "AEGIS Network bypass initiated. Error details: ${e.localizedMessage}. Using offline predictive model...\n\n" + getMockResponse(prompt)
        }
    }

    private fun getMockResponse(prompt: String): String {
        val lowercase = prompt.lowercase()
        return when {
            lowercase.contains("hello") || lowercase.contains("hi") || lowercase.contains("hey") -> {
                "System Online. Welcome, Scholar. This is AEGIS—your digital intelligence companion. Today looks prime for maximizing efficiency. What module shall we optimize first?"
            }
            lowercase.contains("schedule") || lowercase.contains("routine") || lowercase.contains("timetable") -> {
                "Based on optimized cognitive intervals,. I recommend grouping intense problem solving in the morning (10 AM to 1 PM) and reserving creative indexing or note summarization for early evening (5 PM). Run a 45m Pomodoro block to clear any backlogs."
            }
            lowercase.contains("exam") || lowercase.contains("midterm") || lowercase.contains("study") || lowercase.contains("test") -> {
                "Midterm preparation matrix configured. Focus on high-weight conceptual frameworks first. Break down your exam guidelines, then execute a 50/10 focus split. Avoid multi-tasking during academic countdown peaks."
            }
            lowercase.contains("calculate") || lowercase.contains("cgpa") || lowercase.contains("gpa") -> {
                "CGPA calculation engine ready. Maintain an active semester weighted average above 8.5 to bypass standard recruitment restrictions and secure top research tier indexes."
            }
            else -> {
                "AEGIS Intelligence online: Directing efforts toward optimization. To excel, structure this study task into 3 clean parts, draft active summary sheets (Cornell method), and test yourself in 15 minutes. How else can I assist your student workflow?"
            }
        }
    }
}
