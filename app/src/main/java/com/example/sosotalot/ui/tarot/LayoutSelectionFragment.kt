package com.example.sosotalot.ui.tarot

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.sosotalot.R
import com.example.sosotalot.databinding.FragmentLayoutSelectionBinding
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

        val jsonResponse = JSONObject(layoutsJson)
        val layoutMap = mutableMapOf<String, String>()
        val layoutDescriptions = mutableMapOf<String, String>()

        jsonResponse.optJSONObject("single_card")?.let {
            layoutMap["单张牌"] = it.optString("name", "")
            layoutDescriptions["单张牌"] = it.optString("description", "")
        }
        jsonResponse.optJSONObject("two_card_spread")?.let {
            layoutMap["两张牌阵型"] = it.optString("name", "")
            layoutDescriptions["两张牌阵型"] = it.optString("description", "")
        }
        jsonResponse.optJSONObject("three_card_spread")?.let {
            layoutMap["三张牌阵型"] = it.optString("name", "")
            layoutDescriptions["三张牌阵型"] = it.optString("description", "")
        }

        binding.radioGroup.removeAllViews()
        layoutMap.forEach { (layoutName, recommendedLayout) ->
            if (recommendedLayout.isNotBlank()) {
                val radioButton = RadioButton(requireContext()).apply {
                    text = "$layoutName - $recommendedLayout"
                    tag = layoutDescriptions[layoutName]
                    id = View.generateViewId()
                }
                binding.radioGroup.addView(radioButton)
            }
        }

        binding.confirmLayoutButton.setOnClickListener {
            val selectedRadioButton = binding.radioGroup.findViewById<RadioButton>(binding.radioGroup.checkedRadioButtonId)
            val selectedDescription = selectedRadioButton?.tag?.toString() ?: return@setOnClickListener

            val bundle = Bundle().apply {
                putString("selectedLayout", selectedDescription)
                putString("question", question)
            }
            findNavController().navigate(R.id.action_layoutSelectionFragment_to_cardDrawingFragment, bundle)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
