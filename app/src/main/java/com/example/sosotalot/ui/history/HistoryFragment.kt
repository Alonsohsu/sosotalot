package com.example.sosotalot.ui.history

import HistoryItem
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sosotalot.databinding.FragmentHistoryBinding

class HistoryFragment : Fragment() {

    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!

    // 假设的历史记录列表，后续可以从数据库或其他持久化存储中加载
    private val historyList = mutableListOf<HistoryItem>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHistoryBinding.inflate(inflater, container, false)

        // 初始化 RecyclerView
        setupRecyclerView()

        // 添加示例数据（测试用）
        addSampleData()

        return binding.root
    }

    private fun setupRecyclerView() {
        val adapter = HistoryAdapter(historyList)
        binding.rvHistoryList.layoutManager = LinearLayoutManager(requireContext())
        binding.rvHistoryList.adapter = adapter
    }

    private fun addSampleData() {
        historyList.add(
            HistoryItem(
                time = System.currentTimeMillis(),
                question = "我的事业发展如何？",
                tarotCard = "The Lovers - 恋人 (正位)",
                answer = "深情、和谐、相互支持。"
            )
        )
        historyList.add(
            HistoryItem(
                time = System.currentTimeMillis(),
                question = "我近期的健康状况如何？",
                tarotCard = "The Devil - 恶魔 (逆位)",
                answer = "解脱束缚，改善健康问题的契机。"
            )
        )
        // 通过更新适配器通知 RecyclerView 数据已更改
        binding.rvHistoryList.adapter?.notifyDataSetChanged()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
