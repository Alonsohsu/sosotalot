package com.example.sosotalot.ui.notifications

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
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

        // 模拟数据
        val versionList = listOf(
            mapOf("versionName" to "版本 1.0.0", "releaseDate" to "2024-01-01"),
            mapOf("versionName" to "版本 1.1.0", "releaseDate" to "2024-02-15"),
            mapOf("versionName" to "版本 1.2.0", "releaseDate" to "2024-03-10")
        )

        // 找到 ListView
//        val listView: ListView = view.findViewById(R.id.versionListView)

        // 创建适配器
//        val adapter = SimpleAdapter(
//            this,
//            versionList,
//            android.R.layout.simple_list_item_2, // 系统提供的布局，显示两行文字
//            arrayOf("versionName", "releaseDate"),
//            intArrayOf(android.R.id.text1, android.R.id.text2)
//        )

        // 绑定适配器
//        listView.adapter = adapter

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

        class Fragment1 : Fragment(R.layout.fragment_dashboard)
        class Fragment2 : Fragment(R.layout.fragment_dashboard)
        class Fragment3 : Fragment(R.layout.fragment_dashboard)
    }
}
