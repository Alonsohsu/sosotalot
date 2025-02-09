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
        val prefs = requireActivity().getSharedPreferences("MyAppPrefs", AppCompatActivity.MODE_PRIVATE)

        // 會員 ID
        val userId = prefs.getString("userId", prefs.getString("guestUserId", "Unknown")) ?: "Unknown"
        binding.memberID.text = userId

        // 暱稱
        val nickname = prefs.getString("nickname", "未定義") ?: "未定義"
        binding.userNickname.text = nickname

        // 用戶身份
        val userRole = prefs.getString("userRole", "普通用戶") ?: "普通用戶"
        binding.userRole.text = userRole

        // 認證狀態
        val verificationStatus = prefs.getString("verificationStatus", "未認證") ?: "未認證"
        binding.verificationStatus.text = verificationStatus

        // 背包物品數
        val backpackItems = prefs.getInt("backpackItems", 0)
        binding.backpackItems.text = backpackItems.toString()

        // 能量點
        val energyPoints = prefs.getInt("energyPoints", 0)
        binding.energyPoints.text = energyPoints.toString()

        // 星光點
        val starlightPoints = prefs.getInt("starlightPoints", 0)
        binding.starlightPoints.text = starlightPoints.toString()

        // 福報
        val fortunePoints = prefs.getInt("fortunePoints", 0)
        binding.fortunePoints.text = fortunePoints.toString()

        // 祝福能量
        val blessingEnergy = prefs.getInt("blessingEnergy", 0)
        binding.blessingEnergy.text = blessingEnergy.toString()
    }

    private fun logoutUser() {
        val prefs = requireActivity().getSharedPreferences("MyAppPrefs", AppCompatActivity.MODE_PRIVATE)
        val savedGuestUid = prefs.getString("guestUserId", null) // 🚀 保留訪客 ID

        // ✅ 退出 Firebase 认证
        FirebaseAuth.getInstance().signOut()

        // ✅ 只清除登入狀態 & 一般用戶 ID，保留訪客 UID
        val editor = prefs.edit()
        editor.remove("isLoggedIn") // 清除登入狀態
        editor.remove("userId") // 清除一般用戶 ID

        if (savedGuestUid != null) {
            editor.putString("guestUserId", savedGuestUid) // 🔄 重新存入訪客 UID
        }

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
