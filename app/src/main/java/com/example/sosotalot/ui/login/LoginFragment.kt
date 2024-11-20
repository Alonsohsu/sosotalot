package com.example.sosotalot.ui.login

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.sosotalot.R
import com.example.sosotalot.databinding.FragmentLoginBinding

class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(inflater, container, false)

        sharedPreferences = requireActivity().getSharedPreferences("MyAppPrefs", AppCompatActivity.MODE_PRIVATE)



        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Google 登入按鈕監聽器
        binding.googleLoginButton.setOnClickListener {
            handleGoogleLogin()
        }

        // 遊客登入按鈕監聽器
        binding.guestLoginButton.setOnClickListener {
            handleGuestLogin()
        }
    }

    private fun handleGoogleLogin() {
        // Google 登入的邏輯
        Toast.makeText(requireContext(), "Google login clicked", Toast.LENGTH_SHORT).show()
        // TODO: 使用 Google API 實現登入邏輯
    }

    private fun handleGuestLogin() {
        // 遊客登入的邏輯
        Toast.makeText(requireContext(), "Guest login clicked", Toast.LENGTH_SHORT).show()
        // TODO: 設置訪客模式並跳轉到主頁面
        navigateToHomeScreen()
    }

    private fun navigateToHomeScreen() {
        // 導航到主頁面，根據你的導航邏輯實現
        Toast.makeText(requireContext(), "Navigating to Home Screen", Toast.LENGTH_SHORT).show()
    }
}
