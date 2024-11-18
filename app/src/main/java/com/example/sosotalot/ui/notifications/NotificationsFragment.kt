package com.example.sosotalot.ui.notifications

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.sosotalot.R
import com.example.sosotalot.databinding.FragmentNotificationsBinding
import com.google.android.material.tabs.TabLayoutMediator

class NotificationsFragment : Fragment() {

    private var _binding: FragmentNotificationsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNotificationsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // 設置 Adapter
//        val adapter = ViewPagerAdapter(this)
//        binding.viewPager.adapter = adapter
//
//        // 與 TabLayout 聯動
//        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
//            tab.text = "Tab ${position + 1}"  // 動態設置 Tab 標籤
//        }.attach()

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    class ViewPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
        override fun getItemCount(): Int = 3  // Tab 數量

        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> Fragment1()
                1 -> Fragment2()
                else -> Fragment3()
            }
        }

        class Fragment1 : Fragment(R.layout.fragment_home)
        class Fragment2 : Fragment(R.layout.fragment_home)
        class Fragment3 : Fragment(R.layout.fragment_home)
    }
}
