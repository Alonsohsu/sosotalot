package com.example.sosotalot.tarot

import android.content.Context
import com.example.sosotalot.R

class TarotCardManager(private val context: Context) {
    fun drawRandomTarotCards(): List<Pair<String, String>> {
        val tarotDeck = context.resources.getStringArray(R.array.all_tarot_cards).toList()
        val positionOptions = listOf("正位", "逆位")  // 定义正位和逆位

        // 随机洗牌并从不重复的牌中抽取三张
        return tarotDeck.shuffled().take(3).map { it to positionOptions.random() }
    }
}


