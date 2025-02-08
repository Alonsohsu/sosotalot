package com.example.sosotalot.ui.profile

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.sosotalot.R
import com.example.sosotalot.databinding.FragmentMasterIntroBinding

class MasterIntroFragment : Fragment() {
    private var _binding: FragmentMasterIntroBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMasterIntroBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val masterId = arguments?.getInt("masterId") ?: -1
        displayMasterDetails(masterId)

        // 设置按钮的点击事件
        binding.btnConfirmSelection.setOnClickListener {
            navigateToQuestionInput(masterId)
        }
    }

    private fun navigateToQuestionInput(masterId: Int) {
        val bundle = Bundle().apply {
            putInt("tarotMasterId", masterId)
        }
        findNavController().navigate(R.id.action_masterIntroFragment_to_questionInputFragment, bundle)
    }


    private fun displayMasterDetails(masterId: Int) {
        val master = getMasterById(masterId)
        if (master != null) {
            binding.imageTop.setImageResource(master.imageResId)
            binding.textDescription.text = master.description
        } else {
            binding.textDescription.text = "Master not found"
        }
    }

    private fun getMasterById(id: Int): Master? {
        // This function should return a Master object based on the given ID
        // For demonstration, returning a dummy Master. Replace this logic as needed.
        return when (id) {
            0 -> Master(R.drawable.master_one_image, "蘿拉 - 深刻的心理洞察力和同理心。")
            1 -> Master(R.drawable.master_two_image, "馬克斯 - 策略性思考者，擅长职业和财务规划。")
            2 -> Master(R.drawable.master_three_image, "艾薇 - 靈性導師，提供深刻的靈性指導和啟發。")
            else -> null
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    data class Master(val imageResId: Int, val description: String)
}
