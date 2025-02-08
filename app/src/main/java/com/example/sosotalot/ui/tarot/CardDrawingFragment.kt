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
            setImageResource(R.drawable.tarlot_back)  // 使用默认图标或塔罗牌占位图
            scaleType = ImageView.ScaleType.FIT_CENTER
            setOnClickListener {
                val question = arguments?.getString("question") ?: "未知问题"

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
            // 假設卡牌要水平排列，並且我們使用水平間隔來避免重疊
            val cardWidth = resources.getDimensionPixelSize(R.dimen.tarot_card_width)  // 假設每張卡片寬度120dp
            val cardHeight = resources.getDimensionPixelSize(R.dimen.tarot_card_height) // 假設每張卡片高度180dp
            val cardSpacing = resources.getDimensionPixelSize(R.dimen.tarot_card_spacing) // 假設卡片間隔為20dp

            tarotCards.take(2).forEachIndexed { index, card ->
                val imageView = createImageView(card, question, interpretation, findNavController())
                binding.cardContainer.addView(imageView)
                imageView.layoutParams = FrameLayout.LayoutParams(
                    cardWidth,
                    cardHeight
                ).apply {
                    gravity = Gravity.CENTER_HORIZONTAL
                    // 第一張卡片不設置邊距，第二張卡片設置左邊距
                    leftMargin = if (index > 0) cardSpacing else 0
                    topMargin = if (index == 0) 0 else 200  // 如果有需要，调整顶部边距以垂直分隔卡片
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
                    resources.getDimensionPixelSize(R.dimen.tarot_card_width), // 定义在dimens.xml中的固定宽度
                    resources.getDimensionPixelSize(R.dimen.tarot_card_height) // 定义在dimens.xml中的固定高度
                ).apply {
                    gravity = position
                    leftMargin = if (position == Gravity.START) 0 else 20 // 根据位置调整左边距
                    rightMargin = if (position == Gravity.END) 0 else 20 // 根据位置调整右边距
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
