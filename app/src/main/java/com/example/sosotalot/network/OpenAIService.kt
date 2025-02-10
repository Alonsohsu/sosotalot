package com.example.sosotalot.network

import android.content.Context
import android.util.Log
import com.example.sosotalot.BuildConfig
import com.example.sosotalot.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject

object OpenAIService {
    private val client = OkHttpClient()

    suspend fun fetchTarotData(
        context: Context,
        question: String,
        tarotCards: List<Pair<String, String>>? = null,
        type: TarotRequestType
    ): String? {
        return withContext(Dispatchers.IO) {
            try {
                val apiKey = BuildConfig.OPENAI_API_KEY

                // 获取适当的 prompt
                val prompt = when (type) {
                    TarotRequestType.INTERPRETATION -> context.getString(R.string.tarot_prompt2)
                    TarotRequestType.RECOMMENDED_LAYOUTS -> context.getString(R.string.tarot_prompt)
                }

                // 组装 API 请求参数
                val messagesArray = org.json.JSONArray().apply {
                    put(JSONObject().apply {
                        put("role", "system")
                        put("content", prompt)
                    })
                    put(JSONObject().apply {
                        put("role", "user")
                        put("content", if (tarotCards != null) {
                            val cardsText = tarotCards.joinToString("\n") { "- ${it.first} (${it.second})" }
                            "$question\n\n抽到的塔罗牌：\n$cardsText"
                        } else {
                            question
                        })
                    })
                }

                val json = JSONObject().apply {
                    put("model", "gpt-3.5-turbo")
                    put("messages", messagesArray)
                    put("max_tokens", 400)
                }

                val body = json.toString().toRequestBody("application/json".toMediaTypeOrNull())
                val request = Request.Builder()
                    .url("https://api.openai.com/v1/chat/completions")
                    .header("Authorization", "Bearer $apiKey")
                    .post(body)
                    .build()

                val response = client.newCall(request).execute()
                val responseData = response.body?.string()
                val jsonResponse = JSONObject(responseData ?: "{}")

                if (jsonResponse.has("error")) {
                    val errorMessage = jsonResponse.getJSONObject("error").getString("message")
                    Log.e("OpenAI", "API Error: $errorMessage")
                    return@withContext "API 錯誤: $errorMessage"
                }

                return@withContext jsonResponse.getJSONArray("choices")
                    .getJSONObject(0)
                    .getJSONObject("message")
                    .getString("content")
            } catch (e: Exception) {
                Log.e("OpenAI", "API 請求錯誤", e)
                return@withContext null
            }
        }
    }

    enum class TarotRequestType {
        INTERPRETATION, // 塔罗解读
        RECOMMENDED_LAYOUTS // 推荐牌阵
    }
}
