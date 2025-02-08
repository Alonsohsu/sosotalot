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
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.sosotalot.R
import com.example.sosotalot.databinding.FragmentCardDrawingBinding
import com.example.sosotalot.network.OpenAIService
import com.example.sosotalot.tarot.TarotCardManager
import com.example.sosotalot.viewmodel.TarotViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CardDrawingFragment : Fragment() {

    private var _binding: FragmentCardDrawingBinding? = null
    private val binding get() = _binding!!

    private lateinit var tarotCardManager: TarotCardManager  // 声明 tarotCardManager

    private val sharedViewModel: TarotViewModel by activityViewModels() // 使用 ViewModel 存储数据

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCardDrawingBinding.inflate(inflater, container, false)
        tarotCardManager = TarotCardManager(requireContext())

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // **检查 ViewModel 是否已有抽取的牌**
        if (sharedViewModel.drawnCards.value.isNullOrEmpty()) {
            initInitialCard()  // 只有当 ViewModel 为空时才初始化
        } else {
            updateLayoutForSelectedSpread(
                sharedViewModel.drawnCards.value!!,
                sharedViewModel.selectedIndex.value ?: 0,
                sharedViewModel.question.value ?: "未知问题",
                sharedViewModel.interpretation.value ?: "暂无解读"
            )
        }
    }

    override fun onResume() {
        super.onResume()
        // **检查 ViewModel 是否有数据**
        if (!sharedViewModel.drawnCards.value.isNullOrEmpty()) {
            updateLayoutForSelectedSpread(
                sharedViewModel.drawnCards.value!!,
                sharedViewModel.selectedIndex.value ?: 0,
                sharedViewModel.question.value ?: "未知问题",
                sharedViewModel.interpretation.value ?: "暂无解读"
            )
        }
    }

    private fun initInitialCard() {
        val initialImageView = ImageView(requireContext()).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                gravity = Gravity.CENTER
            }
            setImageResource(R.drawable.tarlot_back)
            scaleType = ImageView.ScaleType.FIT_CENTER
            setOnClickListener {
                val question = arguments?.getString("question") ?: "未知问题"
                drawCardsAndShowResult(question)
            }
        }
        binding.cardContainer.addView(initialImageView)
    }

    private fun updateLayoutForSelectedSpread(
        tarotCards: List<Pair<String, String>>,
        selectedIndex: Int,
        question: String,
        interpretation: String
    ) {
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
            val card = tarotCards[0]

            // 如果這張卡片有結果圖片，則顯示結果圖片；否則顯示塔羅牌背面
            val isCardRevealed = sharedViewModel.isCardRevealed(card.first)

            val imageView = ImageView(requireContext()).apply {
                layoutParams = FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.WRAP_CONTENT,
                    FrameLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    gravity = Gravity.CENTER
                }

                // **如果卡片已被翻開，顯示結果圖；否則顯示背面**
                val drawable = if (isCardRevealed) {
                    tarotCardManager.getCardImage(card.first)
                } else {
                    resources.getDrawable(R.drawable.tarlot_back, null)
                }
                setImageDrawable(drawable)

                // 逆位翻轉 180 度
                scaleY = if (card.second == "逆位") -1f else 1f

                // 點擊後顯示解釋
                setOnClickListener {
                    sharedViewModel.revealCard(card.first) // 記錄這張卡片已經被翻開
                    navigateToResultScreen(card, question, interpretation)
                }
            }

            binding.cardContainer.addView(imageView)
        } else {
            Toast.makeText(context, "没有可用的卡牌信息", Toast.LENGTH_SHORT).show()
        }
    }


    private fun addTwoCards(tarotCards: List<Pair<String, String>>, question: String, interpretation: String) {
        if (tarotCards.size >= 2) {
            tarotCards.take(2).forEachIndexed { index, card ->
                val isCardRevealed = sharedViewModel.isCardRevealed(card.first)

                val imageView = ImageView(requireContext()).apply {
                    layoutParams = FrameLayout.LayoutParams(
                        resources.getDimensionPixelSize(R.dimen.tarot_card_width),
                        resources.getDimensionPixelSize(R.dimen.tarot_card_height)
                    ).apply {
                        gravity = Gravity.CENTER_HORIZONTAL
                        leftMargin = if (index > 0) resources.getDimensionPixelSize(R.dimen.tarot_card_spacing) else 0
                    }

                    // **如果卡片已翻開，顯示結果圖片，否則顯示塔羅背面**
                    val drawable = if (isCardRevealed) {
                        tarotCardManager.getCardImage(card.first)
                    } else {
                        resources.getDrawable(R.drawable.tarlot_back, null)
                    }
                    setImageDrawable(drawable)

                    // 逆位翻轉
                    scaleY = if (card.second == "逆位") -1f else 1f

                    setOnClickListener {
                        sharedViewModel.revealCard(card.first)
                        navigateToResultScreen(card, question, interpretation)
                    }
                }

                binding.cardContainer.addView(imageView)
            }
        } else {
            Toast.makeText(context, "卡牌数量不足以显示两张", Toast.LENGTH_SHORT).show()
        }
    }



    private fun addThreeCards(tarotCards: List<Pair<String, String>>, question: String, interpretation: String) {
        val positions = arrayOf(Gravity.START, Gravity.CENTER, Gravity.END)

        if (tarotCards.size >= 3) {
            positions.forEachIndexed { index, position ->
                val card = tarotCards[index]
                val isCardRevealed = sharedViewModel.isCardRevealed(card.first)

                val imageView = ImageView(requireContext()).apply {
                    layoutParams = FrameLayout.LayoutParams(
                        resources.getDimensionPixelSize(R.dimen.tarot_card_width),
                        resources.getDimensionPixelSize(R.dimen.tarot_card_height)
                    ).apply {
                        gravity = position
                    }

                    // **如果卡片已翻開，顯示結果圖片，否則顯示塔羅背面**
                    val drawable = if (isCardRevealed) {
                        tarotCardManager.getCardImage(card.first)
                    } else {
                        resources.getDrawable(R.drawable.tarlot_back, null)
                    }
                    setImageDrawable(drawable)

                    // 逆位翻轉
                    scaleY = if (card.second == "逆位") -1f else 1f

                    setOnClickListener {
                        sharedViewModel.revealCard(card.first)
                        navigateToResultScreen(card, question, interpretation)
                    }
                }

                binding.cardContainer.addView(imageView)
            }
        } else {
            Toast.makeText(context, "卡牌数量不足以显示三张", Toast.LENGTH_SHORT).show()
        }
    }

    private fun navigateToResultScreen(card: Pair<String, String>, question: String, interpretation: String) {
        val bundle = Bundle().apply {
            putString("question", question)
            putString("selected_card_name", card.first)  // 卡牌名稱
            putString("selected_card_position", card.second)  // "正位" or "逆位"
            putString("interpretation", interpretation)
            putInt("selectedIndex", arguments?.getInt("selectedIndex") ?: 0) // 牌陣選擇索引
        }

        findNavController().navigate(R.id.action_cardDrawingFragment_to_tarotResultFragment, bundle)
    }


    private fun drawCardsAndShowResult(question: String) {
        showLoading(true)

        val tarotCards = tarotCardManager.drawRandomTarotCards()

        lifecycleScope.launch {
            val interpretation = OpenAIService.fetchTarotData(
                context = requireContext(),
                question = question,
                tarotCards = tarotCards,
                type = OpenAIService.TarotRequestType.INTERPRETATION
            )

            withContext(Dispatchers.Main) {
                showLoading(false)
                if (interpretation != null) {
                    val selectedIndex = arguments?.getInt("selectedIndex", -1) ?: -1
                    if (selectedIndex != -1) {
                        // **存储结果到 ViewModel**
                        sharedViewModel.setTarotData(question, tarotCards, interpretation, selectedIndex)
                        updateLayoutForSelectedSpread(tarotCards, selectedIndex, question, interpretation)
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
