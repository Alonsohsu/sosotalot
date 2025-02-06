package com.example.sosotalot.ui.tarot

import android.os.Bundle
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
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTarotMasterSelectionBinding.inflate(inflater, container, false)
        setupListeners()
        return binding.root
    }

    private fun setupListeners() {
        binding.tarotMasterRadioGroup.setOnCheckedChangeListener { group, checkedId ->
            navigateToQuestionInput()
        }
    }

    private fun navigateToQuestionInput() {
//        findNavController().navigate(R.id.action_tarotMasterSelectionFragment_to_questionInputFragment)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
