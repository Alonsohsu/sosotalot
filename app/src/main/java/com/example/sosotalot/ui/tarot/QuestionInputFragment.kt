package com.example.sosotalot.ui.tarot

import android.os.Bundle
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
            val layouts = OpenAIService.fetchRecommendedLayouts(question)
            if (layouts != null && layouts.isNotEmpty()) {
                val bundle = Bundle()
                bundle.putStringArrayList("layoutsKey", ArrayList(layouts))
//                findNavController().navigate(R.id.action_questionInputFragment_to_layoutSelectionFragment, bundle)
            } else {
                Toast.makeText(requireContext(), "无法获取推荐的塔罗阵型，请稍后再试", Toast.LENGTH_LONG).show()
            }
            showLoading(false)
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

