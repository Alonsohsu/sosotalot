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
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        setupListeners()  // 確保每次返回此 Fragment 時監聽器都被重新設置
    }

    private fun setupListeners() {
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
        val navController = findNavController()
        val currentDestination = navController.currentDestination?.id

        val bundle = Bundle().apply {
            putInt("tarotMasterId", masterId)
        }

        if (currentDestination == R.id.tarotMasterSelectionFragment) {
            navController.navigate(actionId, bundle)
        } else {
            Log.e("Navigation", "Attempted to navigate away from TarotMasterSelectionFragment but it's not the current destination. Current destination is: $currentDestination")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
