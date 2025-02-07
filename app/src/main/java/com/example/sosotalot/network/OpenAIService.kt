package com.example.sosotalot.network

import android.util.Log
import com.example.sosotalot.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject

object OpenAIService {
    private val client = OkHttpClient()

    suspend fun fetchTarotInterpretation(question: String, tarotCards: List<Pair<String, String>>): String? {
        return withContext(Dispatchers.IO) {
            try {
                val apiKey = BuildConfig.OPENAI_API_KEY

                val cardsText = tarotCards.joinToString("\n") { "${it.first} (${it.second})" }

                // 📌 **更智慧的 Prompt**
                val prompt = """
                    你是一位專業的塔羅占卜師，請根據使用者的問題 **自行決定適合的解讀方式**。
                    **步驟：**
                    1. **先分析使用者的問題類型**（是愛情、人際關係、事業、抉擇問題，還是單純的運勢占卜？）。
                    2. **根據使用者選擇的牌陣(一張牌，兩張牌，三張牌)和問題類型，選擇最適合的解讀架構**：
                       - **如果使用者選擇兩張牌的牌陣，並且詢問的是決策問題（如「該不該換工作？」），請用「問題 - 建議」的方式解讀。
                       - **如果使用者選擇兩張牌的牌陣，並且詢問的是事業問題（如「過去的經驗？」），請用「過去 - 未來」的方式解讀。
                       - **三張牌的解讀方式一樣，請分別幫三張牌做解讀，但解讀的方式請根據問題類型。
                    3. **回答問題的時候請說明使用的是怎樣的架構去解讀(如問題 - 建議，過去 - 現在 - 未來)等。
                    4. **提供深入的塔羅解析**：
                       - 逐一分析每張塔羅牌的意義。
                       - 解釋這些牌如何與問題相關，並提供最重要的提醒。
                       - 針對使用者的問題，提供具體的行動建議。
                       - 整理所有牌的牌義和架構，總結出一個結論

                    **問題**: $question
                    **抽到的塔羅牌**:
                    ${tarotCards.joinToString("\n") { "- ${it.first} (${it.second})" }}
                    
                    **請提供有邏輯性且具體的塔羅解讀！**
                """.trimIndent()

                val messagesArray = org.json.JSONArray().apply {
                    put(JSONObject().apply {
                        put("role", "system")
                        put("content", "你是一位專業的塔羅占卜師，請根據使用者問題與塔羅牌提供最適合的解讀方式。")
                    })
                    put(JSONObject().apply {
                        put("role", "user")
                        put("content", prompt)
                    })
                }

                val json = JSONObject().apply {
                    put("model", "gpt-3.5-turbo")
                    put("messages", messagesArray)
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

    suspend fun fetchRecommendedLayouts(question: String): String? {
        return withContext(Dispatchers.IO) {
            try {
                val apiKey = BuildConfig.OPENAI_API_KEY

                val prompt = """
                你是一位专业塔罗师。请根据用户的问题，推荐最合适的塔罗牌阵型，并确保以下规则：
                1. **必须推荐 1 种单张牌阵型**
                2. **必须推荐 1 种两张牌阵型**
                3. **必须推荐 1 种三张牌阵型**
                4. **具体推荐哪种阵型，由你根据塔罗知识自行决定**

                请按照以下 JSON 格式返回你的推荐，不要返回额外的文字或解释：
                {
                  "single_card": {
                    "name": "阵型名称",
                    "description": "适用于..."
                  },
                  "two_card_spread": {
                    "name": "阵型名称",
                    "description": "适用于..."
                  },
                  "three_card_spread": {
                    "name": "阵型名称",
                    "description": "适用于..."
                  }
                }

                请确保：
                - **所有阵型真实存在**，并且适用于用户的问题
                - **不返回额外解释**，仅返回 JSON 数据
            """.trimIndent()

                val messagesArray = org.json.JSONArray().apply {
                    put(JSONObject().apply {
                        put("role", "system")
                        put("content", prompt)
                    })
                    put(JSONObject().apply {
                        put("role", "user")
                        put("content", question)
                    })
                }

                val json = JSONObject().apply {
                    put("model", "gpt-3.5-turbo")
                    put("messages", messagesArray)
                    put("max_tokens", 200)
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

                if (jsonResponse.has("choices")) {
                    val choicesArray = jsonResponse.getJSONArray("choices")
                    val content = choicesArray.getJSONObject(0).getJSONObject("message").getString("content")
                    return@withContext content // 确保返回的是 JSON 字符串
                } else {
                    Log.e("OpenAI", "API Error: No choices in response")
                    return@withContext null
                }
            } catch (e: Exception) {
                Log.e("OpenAI", "API Request Error", e)
                return@withContext null
            }
        }
    }


}
