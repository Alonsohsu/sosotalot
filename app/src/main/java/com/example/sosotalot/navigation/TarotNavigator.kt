package com.example.sosotalot.navigation

import android.os.Bundle
import androidx.navigation.NavController
import com.example.sosotalot.R

class TarotNavigator(private val navController: NavController) {
    fun navigateToResultScreen(question: String, card: Pair<String, String>, interpretation: String) {
        val bundle = Bundle().apply {
            putString("question", question)
            // 将单个卡牌作为列表传递，以适应可能需要接受列表的接收页面
            putSerializable("tarot_cards", arrayListOf(card))
            putString("interpretation", interpretation)
        }
        navController.navigate(R.id.action_cardDrawingFragment_to_tarotResultFragment, bundle)
    }
}



