package com.example.sosotalot.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.sosotalot.R
import com.example.sosotalot.databinding.FragmentUserProfileBinding
import com.google.firebase.auth.FirebaseAuth

class UserProfileFragment : Fragment() {
    private var _binding: FragmentUserProfileBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUserProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupUserProfile()
        binding.logoutButton.setOnClickListener {
            logoutUser()
        }
    }

    private fun setupUserProfile() {
        // 获取 SharedPreferences 实例
        val prefs = requireActivity().getSharedPreferences("MyAppPrefs", AppCompatActivity.MODE_PRIVATE)

        // 从 SharedPreferences 获取用户 ID
        val userId = prefs.getString("userId", "Unknown")

        // 设置用户 ID 到 TextView
        binding.memberID.text = getString(R.string.user_id_display, userId)

        // 可以添加更多信息的显示逻辑，例如从 SharedPreferences 获取额外的用户信息
    }

    private fun logoutUser() {
        // ✅ 退出 Firebase 认证
        FirebaseAuth.getInstance().signOut()

        // ✅ 清除 SharedPreferences 中的用户数据
        val editor = requireActivity().getSharedPreferences("MyAppPrefs", AppCompatActivity.MODE_PRIVATE).edit()
        editor.clear()
        editor.apply()

        // ✅ 返回登录界面
        navigateToLoginScreen()
    }


    private fun navigateToLoginScreen() {
        findNavController().navigate(R.id.action_userProfileFragment_to_loginFragment) // ✅ 导航到登录界面
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

