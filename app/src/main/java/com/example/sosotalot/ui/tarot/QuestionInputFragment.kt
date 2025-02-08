package com.example.sosotalot.ui.tarot

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.sosotalot.R
import com.example.sosotalot.databinding.FragmentQuestionInputBinding
import com.example.sosotalot.network.OpenAIService
import com.example.sosotalot.utils.NetworkUtils
import kotlinx.coroutines.launch

class QuestionInputFragment : Fragment() {
    private var _binding: FragmentQuestionInputBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tarotMasterId = arguments?.getInt("tarotMasterId") ?: -1

        val imageResource = when (tarotMasterId) {
            0 -> R.drawable.master_one_image
            1 -> R.drawable.master_two_image
            2 -> R.drawable.master_three_image
            else -> R.drawable.master_two_image
        }

        binding.imageTop.setImageResource(imageResource)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentQuestionInputBinding.inflate(inflater, container, false)
        binding.submitQuestionButton.setOnClickListener {
            val question = binding.editTextQuestion.text.toString().trim()
            if (question.isNotEmpty() && NetworkUtils.isNetworkAvailable(requireContext())) {
                showLoading(true)
                fetchAndNavigate(question)
            } else {
                Toast.makeText(requireContext(), "请输入问题并连接网络", Toast.LENGTH_SHORT).show()
            }
        }
        return binding.root
    }

    private fun fetchAndNavigate(question: String) {
        lifecycleScope.launch {
            showLoading(true) // 显示加载状态

            val responseJson = OpenAIService.fetchTarotData(
                context = requireContext(),
                question = question,
                type = OpenAIService.TarotRequestType.RECOMMENDED_LAYOUTS // ✅ 使用新 API
            )

            if (!responseJson.isNullOrEmpty()) {
                try {
                    val bundle = Bundle().apply {
                        putString("layoutsKeyJson", responseJson) // 直接传递 JSON 字符串
                    }
                    findNavController().navigate(R.id.action_questionInputFragment_to_layoutSelectionFragment, bundle)
                } catch (e: Exception) {
                    Log.e("fetchAndNavigate", "JSON 解析错误", e)
                    Toast.makeText(requireContext(), "数据解析失败，请稍后再试", Toast.LENGTH_LONG).show()
                }
            } else {
                Toast.makeText(requireContext(), "无法获取推荐的塔罗阵型，请稍后再试", Toast.LENGTH_LONG).show()
            }

            showLoading(false) // 关闭加载状态
        }
    }

    private fun showLoading(show: Boolean) {
        binding.progressBar.visibility = if (show) View.VISIBLE else View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

