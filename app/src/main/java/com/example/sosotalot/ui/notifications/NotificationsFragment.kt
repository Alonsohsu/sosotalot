package com.example.sosotalot.ui.notifications

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.sosotalot.R
import com.example.sosotalot.databinding.FragmentNotificationsBinding
import com.google.android.material.tabs.TabLayoutMediator

class NotificationsFragment : Fragment() {

    private var _binding: FragmentNotificationsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentNotificationsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.logoutButton.setOnClickListener {
            logoutUser()
        }

        return root
    }

    private fun logoutUser() {
        // 清除 SharedPreferences 中的登录状态
        val editor = requireActivity().getSharedPreferences("MyAppPrefs", AppCompatActivity.MODE_PRIVATE).edit()
        editor.clear()  // 清除所有数据
        editor.apply()

        // 可以选择重启应用或返回登录界面
        navigateToLoginScreen()
    }

    private fun navigateToLoginScreen() {
        findNavController().navigate(R.id.loginFragment)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

