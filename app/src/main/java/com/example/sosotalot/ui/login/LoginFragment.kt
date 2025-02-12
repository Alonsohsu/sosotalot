package com.example.sosotalot.ui.login

import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
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
import com.example.sosotalot.data.firebase.FirebaseManager
import com.example.sosotalot.databinding.FragmentLoginBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider

class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentLoginBinding.inflate(inflater, container, false)

        sharedPreferences = requireActivity().getSharedPreferences("MyAppPrefs", AppCompatActivity.MODE_PRIVATE)
        firebaseAuth = FirebaseAuth.getInstance()

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id)) // 必须请求 ID 令牌
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 設置 Firebase AuthStateListener
        firebaseAuth.addAuthStateListener { auth ->
            auth.currentUser?.let { user ->
                Log.d("LoginFragment", "用戶已登入: ${user.uid}")
                navigateToHomeScreen()
            }
        }

        // 檢查登入狀態（Firebase 可能未及時加載，使用延遲檢查）
        view.postDelayed({
            checkLoginStatus()
        }, 500)

        binding.googleLoginButton.setOnClickListener {
            if (isNetworkAvailable()) {
                handleGoogleLogin()
            } else {
                showToast("没有网络连接")
            }
        }

        binding.guestLoginButton.setOnClickListener {
            if (isNetworkAvailable()) {
                handleGuestLogin()
            } else {
                showToast("没有网络连接")
            }
        }
    }

    /**
     * 檢查登入狀態
     */
    private fun checkLoginStatus() {
        val savedGuestUid = sharedPreferences.getString("guestUserId", null)

        if (firebaseAuth.currentUser != null || savedGuestUid != null) {
            navigateToHomeScreen()
        }
    }

    /**
     * 触发 Google 登录
     */
    private fun handleGoogleLogin() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    /**
     * 处理 Google 登录的返回结果
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: android.content.Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)!!
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                Log.e("GoogleSignIn", "Google 登录失败", e)
                showToast("Google 登录失败")
            }
        }
    }

    /**
     * 使用 Google 认证 Firebase，并存入 Firestore
     */
    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    firebaseAuth.currentUser?.let {
                        FirebaseManager.saveUserDataToFirestore(it)
                        navigateToHomeScreen()
                    }
                } else {
                    Log.e("GoogleAuth", "Firebase 认证失败", task.exception)
                    showToast("Google 认证失败")
                }
            }
    }

    /**
     * 访客登录
     */
    private fun handleGuestLogin() {
        val savedGuestUid = sharedPreferences.getString("guestUserId", null)

        if (savedGuestUid != null) {
            sharedPreferences.edit().putString("userId", savedGuestUid).apply()
            navigateToHomeScreen()
        } else {
            firebaseAuth.signInAnonymously().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    firebaseAuth.currentUser?.let { user ->
                        sharedPreferences.edit().apply {
                            putBoolean("isLoggedIn", true)
                            putString("userId", user.uid)
                            putString("guestUserId", user.uid)
                        }.apply()
                        FirebaseManager.saveUserDataToFirestore(user)
                        navigateToHomeScreen()
                    }
                } else {
                    showToast("访客登录失败")
                }
            }
        }
    }

    /**
     * 导航到主界面
     */
    private fun navigateToHomeScreen() {
        findNavController().navigate(R.id.action_loginFragment_to_navigation_home)
    }

    /**
     * 检查网络状态
     */
    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = context?.getSystemService(ConnectivityManager::class.java)
        val activeNetwork = connectivityManager?.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    /**
     * 显示 Toast
     */
    private fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    companion object {
        private const val RC_SIGN_IN = 9001
    }
}
