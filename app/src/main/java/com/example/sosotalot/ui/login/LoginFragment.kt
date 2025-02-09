package com.example.sosotalot.ui.login

import android.content.Intent
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
import com.example.sosotalot.data.firebase.FirebaseAuthManager
import com.example.sosotalot.data.firebase.FirebaseManager
import com.example.sosotalot.databinding.FragmentLoginBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
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
            .requestIdToken(getString(R.string.default_web_client_id)) // **必须请求 ID 令牌**
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)

        // 检查用户是否已经登录
        checkLoginStatus()

        return binding.root
    }

    private fun checkLoginStatus() {
        if (firebaseAuth.currentUser != null) {
            navigateToHomeScreen()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.googleLoginButton.setOnClickListener {
            if (isNetworkAvailable()) {
                handleGoogleLogin()
            } else {
                Toast.makeText(context, "No internet connection available", Toast.LENGTH_SHORT).show()
            }
        }

        binding.guestLoginButton.setOnClickListener {
            if (isNetworkAvailable()) {
                handleGuestLogin()
            } else {
                Toast.makeText(context, "No internet connection available", Toast.LENGTH_SHORT).show()
            }
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
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)!!
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                Log.e("GoogleSignIn", "Google sign-in failed", e)
                Toast.makeText(context, "Google 登录失败", Toast.LENGTH_SHORT).show()
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
                    val user = firebaseAuth.currentUser
                    user?.let {
                        // ✅ 存入 Firestore
                        FirebaseManager.saveUserDataToFirestore(it)

                        // ✅ 导航到主界面
                        navigateToHomeScreen()
                    }
                } else {
                    Log.e("GoogleAuth", "Firebase Authentication failed", task.exception)
                    Toast.makeText(context, "Google 认证失败", Toast.LENGTH_SHORT).show()
                }
            }
    }

    /**
     * 访客登录
     */
    private fun handleGuestLogin() {
        val prefs = requireActivity().getSharedPreferences("MyAppPrefs", AppCompatActivity.MODE_PRIVATE)
        val savedGuestUid = prefs.getString("guestUserId", null)

        if (savedGuestUid != null) {
            // ✅ 本機已經有訪客 ID，直接使用
            sharedPreferences.edit().putString("userId", savedGuestUid).apply()
            navigateToHomeScreen()
        } else {
            // 🚀 如果沒有訪客 ID，則創建新的匿名帳戶
            firebaseAuth.signInAnonymously().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = firebaseAuth.currentUser
                    user?.let {
                        sharedPreferences.edit().putBoolean("isLoggedIn", true).apply()
                        sharedPreferences.edit().putString("userId", it.uid).apply()
                        sharedPreferences.edit().putString("guestUserId", it.uid).apply() // ✅ 存入訪客 UID
                        FirebaseManager.saveUserDataToFirestore(it) // ✅ 存入 Firestore
                        navigateToHomeScreen()
                    }
                } else {
                    Toast.makeText(context, "Authentication failed.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


    private fun createNewGuestAccount() {
        firebaseAuth.signInAnonymously().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val user = firebaseAuth.currentUser
                user?.let {
                    sharedPreferences.edit().putString("guestUserId", it.uid).apply() // ✅ 儲存訪客 UID
                    FirebaseManager.saveUserDataToFirestore(it) // ✅ 存入 Firestore
                    navigateToHomeScreen()
                }
            } else {
                Toast.makeText(context, "Authentication failed.", Toast.LENGTH_SHORT).show()
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

    companion object {
        private const val RC_SIGN_IN = 9001
    }
}


