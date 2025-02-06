package com.example.sosotalot.ui.login

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.sosotalot.R
import com.example.sosotalot.databinding.FragmentLoginBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException

class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(inflater, container, false)

        sharedPreferences = requireActivity().getSharedPreferences("MyAppPrefs", AppCompatActivity.MODE_PRIVATE)

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)


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
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    private fun handleGuestLogin() {
        // 遊客登入的邏輯
        Toast.makeText(requireContext(), "Guest login clicked", Toast.LENGTH_SHORT).show()
        // TODO: 設置訪客模式並跳轉到主頁面
        navigateToHomeScreen()
    }

    private fun navigateToHomeScreen() {
        val navController = findNavController()
        navController.navigate(R.id.action_loginFragment_to_navigation_home)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                // 登录成功，获取用户信息
                handleSignInResult(account)
            } catch (e: ApiException) {
                // 登录失败，处理错误
                Log.e("GoogleLogin", "Sign-in failed: ${e.statusCode}")
                Toast.makeText(requireContext(), "Login Failed", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun handleSignInResult(account: GoogleSignInAccount?) {
        if (account != null) {
            val userName = account.displayName
            val userEmail = account.email

            // 将用户信息保存到 SharedPreferences 或 ViewModel
            sharedPreferences.edit().putBoolean("isLoggedIn", true).apply()

            // 导航到主界面
            findNavController().navigate(R.id.navigation_divination)

            Toast.makeText(requireContext(), "Welcome $userName", Toast.LENGTH_SHORT).show()
        }
    }

    companion object {
        private const val RC_SIGN_IN = 9001
    }
}
