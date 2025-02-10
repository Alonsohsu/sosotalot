package com.example.sosotalot.ui.profile

import android.os.Bundle
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
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMasterIntroBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 获取前一个 Fragment 传来的 masterId
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
            binding.imageTop.setImageResource(master.imageResId) // 更改 ImageView 的图片
            binding.textName.text = getString(master.nameResId)
            binding.textDescription.text = getString(master.descriptionResId)
            binding.textExpertise.text = getString(master.expertiseResId)
        } else {
            binding.textDescription.text = "Master not found"
        }
    }

    private fun getMasterById(id: Int): Master? {
        return when (id) {
            0 -> Master(
                imageResId = R.drawable.master_one_image,
                nameResId = R.string.tarot_master_one_name,
                descriptionResId = R.string.tarot_master_one_description,
                expertiseResId = R.string.tarot_master_one_expertise
            )
            1 -> Master(
                imageResId = R.drawable.master_two_image,
                nameResId = R.string.tarot_master_two_name,
                descriptionResId = R.string.tarot_master_two_description,
                expertiseResId = R.string.tarot_master_two_expertise
            )
            2 -> Master(
                imageResId = R.drawable.master_three_image,
                nameResId = R.string.tarot_master_three_name,
                descriptionResId = R.string.tarot_master_three_description,
                expertiseResId = R.string.tarot_master_three_expertise
            )
            3 -> Master(
                imageResId = R.drawable.master_four_image,
                nameResId = R.string.tarot_master_four_name,
                descriptionResId = R.string.tarot_master_four_description,
                expertiseResId = R.string.tarot_master_four_expertise
            )
            else -> null
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    data class Master(
        val imageResId: Int,
        val nameResId: Int,
        val descriptionResId: Int,
        val expertiseResId: Int
    )
}
