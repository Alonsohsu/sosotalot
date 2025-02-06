package com.example.sosotalot.ui.result

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.sosotalot.databinding.ActivityTarotResultBinding
import com.example.sosotalot.network.OpenAIService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import android.view.View

class TarotResultActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTarotResultBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTarotResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.progressBar.visibility = View.VISIBLE  // 顯示 Loading
        binding.textViewResult.visibility = View.GONE  // 隱藏結果

        // 從 Intent 接收問題 & 卡片數據
        val question = intent.getStringExtra("question") ?: "未知問題"
        val tarotCards = intent.getSerializableExtra("tarot_cards") as? ArrayList<Pair<String, String>> ?: arrayListOf()

        // 顯示所有抽到的卡片名稱
        val cardsText = tarotCards.joinToString("\n") { "${it.first} (${it.second})" }
        binding.textViewCardName.text = cardsText

        // 設定對應的塔羅牌圖片
        if (tarotCards.isNotEmpty()) {
            val imageResId = getTarotImageResource(tarotCards[0].first)
            binding.imageViewCard.setImageResource(imageResId)
        }

        // **發送 API 請求 (傳遞完整塔羅牌清單)**
        CoroutineScope(Dispatchers.IO).launch {
            val interpretation = OpenAIService.fetchTarotInterpretation(question, tarotCards) // ✅ 傳遞完整的卡片清單

            // 回到主執行緒更新 UI
            CoroutineScope(Dispatchers.Main).launch {
                binding.progressBar.visibility = View.GONE  // 隱藏 Loading
                binding.textViewResult.visibility = View.VISIBLE  // 顯示結果
                binding.textViewResult.text = interpretation ?: "無解讀結果"
            }
        }

        // 返回按鈕
        binding.buttonBack.setOnClickListener {
            finish()
        }
    }

    // 依據牌名返回對應的圖片資源
    private fun getTarotImageResource(cardName: String): Int {
        return when (cardName) {
            "愚者" -> com.example.sosotalot.R.drawable.ic_notifications_black_24dp
            "魔術師" -> com.example.sosotalot.R.drawable.ic_notifications_black_24dp
            "女祭司" -> com.example.sosotalot.R.drawable.ic_notifications_black_24dp
            "女皇" -> com.example.sosotalot.R.drawable.ic_notifications_black_24dp
            "皇帝" -> com.example.sosotalot.R.drawable.ic_notifications_black_24dp
            "教皇" -> com.example.sosotalot.R.drawable.ic_notifications_black_24dp
            "戀人" -> com.example.sosotalot.R.drawable.ic_notifications_black_24dp
            "戰車" -> com.example.sosotalot.R.drawable.ic_notifications_black_24dp
            else -> com.example.sosotalot.R.drawable.ic_notifications_black_24dp // 預設圖片
        }
    }
}
