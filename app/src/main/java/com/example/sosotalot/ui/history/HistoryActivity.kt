package com.example.sosotalot.ui.history

import HistoryItem
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sosotalot.databinding.ActivityHistoryBinding

class HistoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHistoryBinding
    private val historyList = mutableListOf<HistoryItem>() // 用于存储历史记录

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 初始化 RecyclerView
        val adapter = HistoryAdapter(historyList)
        binding.rvHistoryList.layoutManager = LinearLayoutManager(this)
        binding.rvHistoryList.adapter = adapter

        // 接收数据并更新列表
        intent?.let {
            val question = it.getStringExtra("question") ?: "未知问题"
            val tarotCard = it.getStringExtra("tarotCard") ?: "未知牌"
            val orientation = it.getStringExtra("orientation") ?: "未知方向"
            val answer = it.getStringExtra("answer") ?: "暂无解答"
            val time = System.currentTimeMillis() // 当前时间戳

            // 添加新记录
            historyList.add(HistoryItem(time, question, "$tarotCard ($orientation)", answer))
            adapter.notifyDataSetChanged()
        }
    }
}
