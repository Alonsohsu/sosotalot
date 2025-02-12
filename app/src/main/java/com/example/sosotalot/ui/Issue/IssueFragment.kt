package com.example.sosotalot.ui.Issue

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.sosotalot.databinding.FragmentIssueBinding

class IssueFragment : Fragment() {

    private var _binding: FragmentIssueBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentIssueBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.mail.setOnClickListener {
            sendEmailWithGmail()
        }
    }

    private fun sendEmailWithGmail() {
        val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:sunososobro@gmail.com") // 請更換為你的客服信箱
            putExtra(Intent.EXTRA_SUBJECT, "Bug 回報")
            putExtra(Intent.EXTRA_TEXT, "請詳細描述您遇到的問題：\n\n(請填寫您的反饋)")
            setPackage("com.google.android.gm") // **指定 Gmail 應用程式**
        }

        try {
            startActivity(emailIntent)
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "請先安裝 Gmail 應用程式", Toast.LENGTH_SHORT).show()
        }
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
