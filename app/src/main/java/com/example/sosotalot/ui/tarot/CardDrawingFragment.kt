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
import androidx.activity.OnBackPressedCallback
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
import org.json.JSONObject
import org.tensorflow.lite.Interpreter

class CardDrawingFragment : Fragment() {

    private var _binding: FragmentCardDrawingBinding? = null
    private val binding get() = _binding!!

    private lateinit var tarotCardManager: TarotCardManager
    private val sharedViewModel: TarotViewModel by activityViewModels()

    // 儲存從 arguments 取得的數據
    private var selectedLayoutId: Int = -1
    private var selectedMeaning: String = "未知牌阵"
    private var question: String = "未知问题"
    private var selectedImageResId: Int = R.drawable.tarlot_back

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCardDrawingBinding.inflate(inflater, container, false)
        tarotCardManager = TarotCardManager(requireContext())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 讀取 arguments
        arguments?.let {
            selectedLayoutId = it.getInt("selectedLayoutId", -1)
            selectedMeaning = it.getString("selectedMeaning", "未知牌阵")
            question = it.getString("question", "未知问题")
            selectedImageResId = it.getInt("selectedImageResId", R.drawable.tarlot_back)
        }
        Log.e("CardDrawingFragment", "牌阵ID: $selectedLayoutId, 描述: $selectedMeaning, 图片ID: $selectedImageResId, 问题: $question")

        // 初始化畫面
        if (sharedViewModel.drawnCards.value.isNullOrEmpty()) {
            initInitialCard()
        } else {
            updateLayoutForSelectedSpread("", "", "", "")
        }

        // 設定查看解釋按鈕的點擊事件
        binding.viewExplanationButton.setOnClickListener {
            sharedViewModel.clearTarotData()
            findNavController().navigate(R.id.action_cardDrawingFragment_to_tarotMasterSelectionFragment)
        }

        // **攔截返回鍵，防止返回 `LayoutSelectionFragment`**
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // 禁止返回 `LayoutSelectionFragment`
            }
        })
    }

    override fun onResume() {
        super.onResume()
        if (!sharedViewModel.drawnCards.value.isNullOrEmpty()) {
            val summary = sharedViewModel.tarotInterpretation.value ?: ""
            val meaning1 = sharedViewModel.meaning1.value ?: ""
            val meaning2 = sharedViewModel.meaning2.value ?: ""
            val meaning3 = sharedViewModel.meaning3.value ?: ""

            updateLayoutForSelectedSpread(summary, meaning1, meaning2, meaning3)
        }
    }

    /**
     * 初始化抽牌畫面
     */
    private fun initInitialCard() {
        binding.viewExplanationButton.visibility = View.GONE  // 隱藏按鈕
        val initialImageView = createTarotImageView(selectedImageResId)
        initialImageView.setOnClickListener {
            drawCardsAndShowResult()
        }
        binding.cardContainer.addView(initialImageView)
    }

    /**
     * 根據選擇的牌陣更新畫面
     */
    private fun updateLayoutForSelectedSpread(interpretation: String, meaning1: String, meaning2: String, meaning3: String) {
        binding.cardContainer.removeAllViews()
        binding.tarotResultText.text = interpretation // 顯示解析結果

        val tarotCards = sharedViewModel.drawnCards.value ?: emptyList()
        Log.e("Tarot Debug", "選擇的 Layout ID: $selectedLayoutId, 牌數: ${tarotCards.size}")

        val layoutId = when (selectedLayoutId) {
            0 -> R.layout.single_card_layout
            1 -> R.layout.two_card_layout
            2 -> R.layout.three_card_layout
            else -> return showToast("無效的牌陣")
        }

        val view = layoutInflater.inflate(layoutId, binding.cardContainer, false)
        binding.cardContainer.addView(view)

        // **設置牌的圖片與點擊監聽**
        tarotCards.forEachIndexed { index, card ->
            val cardView = when (index) {
                0 -> view.findViewById<ImageView>(R.id.card1)
                1 -> view.findViewById<ImageView>(R.id.card2)
                2 -> view.findViewById<ImageView>(R.id.card3)
                else -> null
            }

            if (cardView != null) {
                cardView.setImageDrawable(tarotCardManager.getCardImage(card.first))
                cardView.scaleY = if (card.second == "逆位") -1f else 1f

                // **點擊後導向結果畫面，傳遞對應的解釋**
                val meaningText = when (index) {
                    0 -> meaning1
                    1 -> meaning2
                    2 -> meaning3
                    else -> ""
                }

                cardView.setOnClickListener {
                    navigateToResultScreen(card, meaningText)
                }
            }
        }

        binding.viewExplanationButton.visibility = View.VISIBLE
    }

    /**
     * 創建塔羅牌 `ImageView`
     */
    private fun createTarotImageView(imageResId: Int): ImageView {
        return ImageView(requireContext()).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                gravity = Gravity.CENTER
            }
            setImageResource(imageResId)
            scaleType = ImageView.ScaleType.FIT_CENTER
        }
    }

    /**
     * 創建根據 `tarotCards` 設定的 `ImageView`
     */
    /**
     * 創建並顯示塔羅牌的 ImageView，直接顯示正面
     */
    private fun createTarotImageViewForCard(card: Pair<String, String>): ImageView {
        return ImageView(requireContext()).apply {
            layoutParams = FrameLayout.LayoutParams(
                resources.getDimensionPixelSize(R.dimen.tarot_card_width),
                resources.getDimensionPixelSize(R.dimen.tarot_card_height)
            ).apply {
                gravity = Gravity.CENTER
            }

            // **直接顯示塔羅牌正面**
            setImageDrawable(tarotCardManager.getCardImage(card.first))

            // 逆位翻轉
            scaleY = if (card.second == "逆位") -1f else 1f
        }
    }


    /**
     * 抽取塔羅牌並顯示結果
     */
    private fun drawCardsAndShowResult() {
        showLoading(true)

        val tarotCards = tarotCardManager.drawRandomTarotCards(selectedLayoutId)

        lifecycleScope.launch {
            val interpretation = OpenAIService.fetchTarotData(
                context = requireContext(),
                question = question,
                meaning = selectedMeaning,
                tarotCards = tarotCards,
                type = OpenAIService.TarotRequestType.INTERPRETATION
            )

            withContext(Dispatchers.Main) {
                showLoading(false)
                if (interpretation != null) {
                    try {
                        var textResponse = interpretation.trim()
                        val name: String
                        val meaning1: String
                        val meaning2: String
                        val meaning3: String
                        val summary: String

                        Log.e("OpenAI", "API Error: $textResponse")

                        // **純文字模式：使用 `extractMeaning()` 解析**
                        name = "塔羅牌解讀(點選圖片已進入每張牌的詳細解讀)"
                        meaning1 = extractMeaning(textResponse, "第一張牌")
                        meaning2 = extractMeaning(textResponse, "第二張牌")
                        meaning3 = extractMeaning(textResponse, "第三張牌")
                        summary = extractMeaning(textResponse, "總結")

                        Log.e("OpenAI", "name: $name")
                        Log.e("OpenAI", "meaning1: $meaning1")
                        Log.e("OpenAI", "meaning2: $meaning2")
                        Log.e("OpenAI", "meaning3: $meaning3")
                        Log.e("OpenAI", "summary: $summary")

                        sharedViewModel.setTarotData(question, tarotCards, summary, selectedLayoutId, meaning1, meaning2, meaning3)
                        updateLayoutForSelectedSpread(summary, meaning1, meaning2, meaning3)

                    } catch (e: Exception) {
                        Log.e("CardDrawingFragment", "解析塔羅解讀時發生錯誤", e)

                        showToast("解析塔羅解讀時發生錯誤")
                    }
                } else {
                    showToast(getString(R.string.error_interpretation_no_found))
                }
            }
        }
    }

    private fun extractMeaning(text: String, keyword: String): String {
        val regex = Regex("$keyword[:：]?\\s*(.+?)(?=(\\n|$|第二張牌|第三張牌|總結))", RegexOption.DOT_MATCHES_ALL)
        return regex.find(text)?.groupValues?.get(1)?.trim() ?: ""
    }


    /**
     * 導航至解釋畫面
     */
    private fun navigateToResultScreen(card: Pair<String, String>, meaningText: String) {
        val bundle = Bundle().apply {
            putString("question", question)
            putString("selected_card_name", card.first)
            putString("selected_card_position", card.second)
            putString("meaning_text", meaningText) // 新增這行，將該牌的解釋傳遞過去
            putInt("selectedLayoutId", selectedLayoutId)
        }
        findNavController().navigate(R.id.action_cardDrawingFragment_to_tarotResultFragment, bundle)
    }


    private fun showLoading(isLoading: Boolean) {
        binding.tarotResultText.text = "塔羅師解讀中..."
        binding.progressBar.visibility = View.VISIBLE
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
