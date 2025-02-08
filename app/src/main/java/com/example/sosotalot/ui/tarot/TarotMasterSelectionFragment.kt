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
        // 设置每个塔罗师头像的点击监听器
        binding.imageMasterOne.setOnClickListener {
            navigateToQuestionInput(R.id.action_tarotMasterSelectionFragment_to_masterIntroFragment, 0)
        }
        binding.imageMasterTwo.setOnClickListener {
            navigateToQuestionInput(R.id.action_tarotMasterSelectionFragment_to_masterIntroFragment, 1)
        }
        binding.imageMasterThree.setOnClickListener {
            navigateToQuestionInput(R.id.action_tarotMasterSelectionFragment_to_masterIntroFragment, 2)
        }
    }

    private fun navigateToQuestionInput(actionId: Int, masterId: Int) {
        val bundle = Bundle().apply {
            putInt("tarotMasterId", masterId)
        }

        if (findNavController().currentDestination?.id == R.id.tarotMasterSelectionFragment) {
            findNavController().navigate(actionId, bundle)
        } else {
            Log.e("Navigation", "Attempted to navigate away from TarotMasterSelectionFragment but it's not the current destination.")
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
