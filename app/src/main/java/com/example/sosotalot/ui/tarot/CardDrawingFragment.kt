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

//        Log.d("CardDrawingFragment", "牌阵ID: $selectedLayoutId, 描述: $selectedMeaning, 图片ID: $selectedImageResId, 问题: $question")

        // 初始化畫面
        if (sharedViewModel.drawnCards.value.isNullOrEmpty()) {
            initInitialCard()
        } else {
            updateLayoutForSelectedSpread("")
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
            updateLayoutForSelectedSpread("")
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
    private fun updateLayoutForSelectedSpread(interpreter: String) {
        binding.cardContainer.removeAllViews()
        binding.tarotResultText.text = interpreter
        val tarotCards = sharedViewModel.drawnCards.value ?: emptyList()

        when (selectedLayoutId) {
            0 -> addSingleCard(tarotCards)
            1 -> addMultipleCards(tarotCards, 2)
            2 -> addMultipleCards(tarotCards, 3)
            else -> Toast.makeText(context, "無效的牌陣", Toast.LENGTH_SHORT).show()
        }

        binding.viewExplanationButton.visibility = View.VISIBLE  // 顯示按鈕
    }

    /**
     * 添加單張牌
     */
    private fun addSingleCard(tarotCards: List<Pair<String, String>>) {
        if (tarotCards.isNotEmpty()) {
            val card = tarotCards[0]
            val imageView = createTarotImageViewForCard(card)
            imageView.setOnClickListener { navigateToResultScreen(card) }
            binding.cardContainer.addView(imageView)
        } else {
            showToast("没有可用的卡牌信息")
        }
    }

    /**
     * 添加多張牌（兩張或三張）
     */
    private fun addMultipleCards(tarotCards: List<Pair<String, String>>, count: Int) {
        if (tarotCards.size >= count) {
            repeat(count) { index ->
                val card = tarotCards[index]
                val imageView = createTarotImageViewForCard(card)
                imageView.setOnClickListener { navigateToResultScreen(card) }
                binding.cardContainer.addView(imageView)
            }
        } else {
            showToast("卡牌数量不足以显示 $count 张")
        }
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

        val tarotCards = tarotCardManager.drawRandomTarotCards()

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
                    sharedViewModel.setTarotData(question, tarotCards, interpretation, selectedLayoutId)
                    updateLayoutForSelectedSpread(interpretation )
                } else {
                    showToast(getString(R.string.error_interpretation_no_found))
                }
            }
        }
    }

    /**
     * 導航至解釋畫面
     */
    private fun navigateToResultScreen(card: Pair<String, String>) {
        val bundle = Bundle().apply {
            putString("question", question)
            putString("selected_card_name", card.first)
            putString("selected_card_position", card.second)
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
