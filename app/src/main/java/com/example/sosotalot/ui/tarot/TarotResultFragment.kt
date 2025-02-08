package com.example.sosotalot.ui.tarot

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.sosotalot.databinding.FragmentTarotResultBinding
import kotlinx.coroutines.withContext

class TarotResultFragment : Fragment() {

    private var _binding: FragmentTarotResultBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTarotResultBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val question = arguments?.getString("question") ?: "未知问题"
        val tarotCards = arguments?.getSerializable("tarot_cards") as? ArrayList<Pair<String, String>> ?: arrayListOf()
        val interpretation = arguments?.getString("interpretation") ?: "暂无解读"

        binding.textViewCardName.text = tarotCards.joinToString("\n") { "${it.first} (${it.second})" }
        binding.textViewResult.text = "解读结果: $interpretation"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}


