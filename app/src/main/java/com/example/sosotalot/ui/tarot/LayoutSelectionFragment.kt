package com.example.sosotalot.ui.tarot

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sosotalot.R
import com.example.sosotalot.databinding.FragmentLayoutSelectionBinding
import com.example.sosotalot.ui.adapter.TarotLayoutAdapter
import com.example.sosotalot.model.TarotLayout
import org.json.JSONObject

class LayoutSelectionFragment : Fragment() {

    private var _binding: FragmentLayoutSelectionBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLayoutSelectionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val layoutsJson = arguments?.getString("layoutsKeyJson") ?: "{}"
        val question = arguments?.getString("question") ?: ""

        val tarotLayouts = parseLayouts(layoutsJson)

        // 設置 RecyclerView
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext()) // 直向排列
        val adapter = TarotLayoutAdapter(tarotLayouts) { selected ->
            navigateToCardDrawing(selected, question) // 點擊圖片時導航到抽卡畫面
        }
        binding.recyclerView.adapter = adapter

        // **攔截返回鍵，防止返回 `QuestionInputFragment`**
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // 禁止返回 `QuestionInputFragment`
            }
        })
    }


    private fun navigateToCardDrawing(selectedLayout: TarotLayout, question: String) {
        val bundle = Bundle().apply {
            putInt("selectedLayoutId", selectedLayout.id)
            putString("selectedMeaning", selectedLayout.meaning)
            putInt("selectedImageResId", selectedLayout.imageResId)
            putString("question", question)
        }
        findNavController().navigate(R.id.action_layoutSelectionFragment_to_cardDrawingFragment, bundle)
    }

    private fun parseLayouts(jsonString: String): List<TarotLayout> {
        val jsonResponse = JSONObject(jsonString)
        val layouts = mutableListOf<TarotLayout>()

        jsonResponse.optJSONObject("single_card")?.let {
            layouts.add(
                TarotLayout(
                    id = 0,
                    imageResId = R.drawable.tarlot_back,
                    name = it.optString("name", "单张牌"),
                    meaning = it.optString("meaning", "未知意義"), // ✅ 解析 meaning
                    description = it.optString("description", "单张牌解读")
                )
            )
        }

        jsonResponse.optJSONObject("two_card_spread")?.let {
            layouts.add(
                TarotLayout(
                    id = 1,
                    imageResId = R.drawable.layout_two_cards_1,
                    name = it.optString("name", "两张牌阵型"),
                    meaning = it.optString("meaning", "未知意義"), // ✅ 解析 meaning
                    description = it.optString("description", "两张牌解读")
                )
            )
        }

        jsonResponse.optJSONObject("three_card_spread")?.let {
            layouts.add(
                TarotLayout(
                    id = 2,
                    imageResId = R.drawable.layout_three_cards_1,
                    name = it.optString("name", "三张牌阵型"),
                    meaning = it.optString("meaning", "未知意義"), // ✅ 解析 meaning
                    description = it.optString("description", "三张牌解读")
                )
            )
        }
        return layouts
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
