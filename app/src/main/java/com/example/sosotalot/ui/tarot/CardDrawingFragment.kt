package com.example.sosotalot.ui.tarot

import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.sosotalot.R
import com.example.sosotalot.databinding.FragmentCardDrawingBinding
import com.example.sosotalot.navigation.TarotNavigator
import com.example.sosotalot.network.OpenAIService
import com.example.sosotalot.tarot.TarotCardManager
import com.example.sosotalot.ui.helper.createImageView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CardDrawingFragment : Fragment() {

    private var _binding: FragmentCardDrawingBinding? = null
    private val binding get() = _binding!!

    private lateinit var tarotCardManager: TarotCardManager  // 声明 tarotCardManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCardDrawingBinding.inflate(inflater, container, false)
        tarotCardManager = TarotCardManager(requireContext())  // 实例化 TarotCardManager
        initInitialCard()
        return binding.root
    }

    private fun initInitialCard() {
        val initialImageView = ImageView(requireContext()).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                gravity = Gravity.CENTER
            }
            setImageResource(R.drawable.ic_notifications_black_24dp)  // 使用默认图标或塔罗牌占位图
            scaleType = ImageView.ScaleType.FIT_CENTER
            setOnClickListener {
                val question = arguments?.getString("question") ?: "未知问题"
                val selectedIndex = arguments?.getInt("selectedIndex", -1) ?: -1  // 确保这里处理为非空

                drawCardsAndShowResult(question)  // 响应点击，抽牌并显示结果
            }
        }
        binding.cardContainer.addView(initialImageView)
    }

    private fun updateLayoutForSelectedSpread(tarotCards: List<Pair<String, String>>, selectedIndex: Int, question: String, interpretation: String) {
        binding.cardContainer.removeAllViews()  // 清空现有布局

        when (selectedIndex) {
            0 -> addSingleCard(tarotCards, question, interpretation)
            1 -> addTwoCards(tarotCards, question, interpretation)
            2 -> addThreeCards(tarotCards, question, interpretation)
            else -> Toast.makeText(context, "无效的牌阵选择", Toast.LENGTH_SHORT).show()
        }
    }


    private fun addSingleCard(tarotCards: List<Pair<String, String>>, question: String, interpretation: String) {
        if (tarotCards.isNotEmpty()) {
            // 选择列表中的第一张卡牌
            val card = tarotCards[0]

            // 创建ImageView，传递单张卡牌信息
            val imageView = createImageView(card, question, interpretation, findNavController())

            binding.cardContainer.addView(imageView)
            imageView.layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                gravity = Gravity.CENTER
            }
        } else {
            Toast.makeText(context, "没有可用的卡牌信息", Toast.LENGTH_SHORT).show()
        }
    }

    private fun addTwoCards(tarotCards: List<Pair<String, String>>, question: String, interpretation: String) {
        if (tarotCards.size >= 2) {
            repeat(2) { index ->
                val card = tarotCards[index] // 获取第 index 张卡牌
                val imageView = createImageView(card, question, interpretation, findNavController())
                binding.cardContainer.addView(imageView)
                imageView.layoutParams = FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.WRAP_CONTENT,
                    FrameLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    topMargin = if (index == 0) 0 else 200 // Adjust this value based on your UI
                    gravity = Gravity.CENTER_HORIZONTAL
                }
            }
        } else {
            Toast.makeText(context, "卡牌数量不足以显示两张", Toast.LENGTH_SHORT).show()
        }
    }


    private fun addThreeCards(tarotCards: List<Pair<String, String>>, question: String, interpretation: String) {
        val positions = arrayOf(Gravity.START, Gravity.CENTER, Gravity.END)
        if (tarotCards.size >= 3) {
            positions.forEachIndexed { index, position ->
                val card = tarotCards[index] // 获取第 index 张卡牌
                val imageView = createImageView(card, question, interpretation, findNavController())
                binding.cardContainer.addView(imageView)
                imageView.layoutParams = FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.WRAP_CONTENT,
                    FrameLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    gravity = position
                }
            }
        } else {
            Toast.makeText(context, "卡牌数量不足以显示三张", Toast.LENGTH_SHORT).show()
        }
    }


    private fun drawCardsAndShowResult(question: String) {
        showLoading(true)  // 显示加载动画

        val tarotCards = tarotCardManager.drawRandomTarotCards()  // 抽牌

        lifecycleScope.launch {
            val interpretation = OpenAIService.fetchTarotData(
                context = requireContext(),
                question = question,
                tarotCards = tarotCards,
                type = OpenAIService.TarotRequestType.INTERPRETATION
            )

            withContext(Dispatchers.Main) {
                showLoading(false)  // 隐藏加载动画
                if (interpretation != null) {
                    val selectedIndex = arguments?.getInt("selectedIndex", -1) ?: -1  // 确保这里处理为非空
                    if (selectedIndex != -1) {
                        updateLayoutForSelectedSpread(tarotCards, selectedIndex, question, interpretation)  // 更新布局
                    } else {
                        Toast.makeText(context, "请选择一个牌阵再继续", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(context, "解释获取失败，请重试", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
