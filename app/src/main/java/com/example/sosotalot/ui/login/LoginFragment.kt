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
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentLoginBinding.inflate(inflater, container, false)

        sharedPreferences = requireActivity().getSharedPreferences("MyAppPrefs", AppCompatActivity.MODE_PRIVATE)

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)

        // 检查用户是否已经登录过
        checkLoginStatus()

        return binding.root
    }

    private fun checkLoginStatus() {
        if (sharedPreferences.getBoolean("isLoggedIn", false)) {
            navigateToHomeScreen()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.googleLoginButton.setOnClickListener {
            handleGoogleLogin()
        }

        binding.guestLoginButton.setOnClickListener {
            handleGuestLogin()
        }
    }

    private fun handleGoogleLogin() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    private fun handleGuestLogin() {
        // 将登录状态设置为true
        sharedPreferences.edit().putBoolean("isLoggedIn", true).apply()
        navigateToHomeScreen()
    }

    private fun navigateToHomeScreen() {
        findNavController().navigate(R.id.action_loginFragment_to_navigation_home)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                // 登录成功，处理登录结果
                handleSignInResult(account)
            } catch (e: ApiException) {
                Log.e("GoogleLogin", "Sign-in failed: ${e.statusCode}")
                Toast.makeText(requireContext(), "Login Failed", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun handleSignInResult(account: GoogleSignInAccount?) {
        if (account != null) {
            // 将登录状态设置为true
            sharedPreferences.edit().putBoolean("isLoggedIn", true).apply()
            navigateToHomeScreen()
        }
    }

    companion object {
        private const val RC_SIGN_IN = 9001
    }
}
