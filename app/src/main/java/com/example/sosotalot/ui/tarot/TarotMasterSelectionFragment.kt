package com.example.sosotalot.ui.tarot

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.sosotalot.R
import com.example.sosotalot.databinding.FragmentTarotMasterSelectionBinding

class TarotMasterSelectionFragment : Fragment() {

    private var _binding: FragmentTarotMasterSelectionBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTarotMasterSelectionBinding.inflate(inflater, container, false)
        setupListeners()
        return binding.root
    }

    private fun setupListeners() {
        // 监听按钮点击事件而不是单选按钮改变事件
        binding.confirmSelectionButton.setOnClickListener {
            navigateToQuestionInput()
        }
    }

    private fun navigateToQuestionInput() {
        if (findNavController().currentDestination?.id == R.id.tarotMasterSelectionFragment) {
            findNavController().navigate(R.id.action_tarotMasterSelectionFragment_to_questionInputFragment)
        } else {
            Log.e("Navigation", "Attempted to navigate away from TarotMasterSelectionFragment but it's not the current destination.")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

