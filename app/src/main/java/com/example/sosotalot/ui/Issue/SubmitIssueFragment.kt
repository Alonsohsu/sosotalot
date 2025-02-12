package com.example.sosotalot.ui.Issue

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.sosotalot.databinding.FragmentSubmitIssueBinding
import com.example.sosotalot.model.Issue
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

class SubmitIssueFragment : Fragment() {

    private var _binding: FragmentSubmitIssueBinding? = null
    private val binding get() = _binding!!
    private val firestore = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSubmitIssueBinding.inflate(inflater, container, false)
        setupListeners()
        return binding.root
    }

    private fun setupListeners() {
        binding.submitButton.setOnClickListener {
            val issueDescription = binding.issueEditText.text.toString().trim()

            if (issueDescription.isEmpty()) {
                Toast.makeText(requireContext(), "請輸入問題描述", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            submitIssueToFirebase(issueDescription)
        }
    }

    private fun submitIssueToFirebase(description: String) {
        val issueId = UUID.randomUUID().toString()
        val issue = Issue(id = issueId, description = description, likes = 0)

        firestore.collection("issues").document(issueId)
            .set(issue)
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "問題提交成功！", Toast.LENGTH_SHORT).show()
                findNavController().popBackStack() // 提交成功後返回 `BugFragment`
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "提交失敗，請重試", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
