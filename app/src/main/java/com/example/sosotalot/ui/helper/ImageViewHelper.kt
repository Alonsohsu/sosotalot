package com.example.sosotalot.ui.helper

import android.widget.ImageView
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import com.example.sosotalot.R
import com.example.sosotalot.navigation.TarotNavigator

fun Fragment.createImageView(card: Pair<String, String>, question: String, interpretation: String, navController: NavController): ImageView {
    val tarotNavigator = TarotNavigator(navController)  // 创建 TarotNavigator 实例

    return ImageView(requireContext()).apply {
        layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        setImageResource(R.drawable.ic_notifications_black_24dp)  // 假设你有一个占位符图像或实际的塔罗牌图像资源
        scaleType = ImageView.ScaleType.FIT_CENTER
        setTag(card)  // 将牌的信息存储在视图的标记中

        setOnClickListener {
            // 从视图标记获取牌的信息
            val selectedCard = it.tag as Pair<String, String>
            // 使用 TarotNavigator 来跳转到结果页面
            tarotNavigator.navigateToResultScreen(question, selectedCard, interpretation)
        }
    }
}





