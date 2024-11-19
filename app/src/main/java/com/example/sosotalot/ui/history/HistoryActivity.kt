package com.example.sosotalot.ui.history

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sosotalot.databinding.ActivityHistoryBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HistoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHistoryBinding
//    private lateinit var database: AppDatabase // Room 数据库实例
//    private val historyList = mutableListOf<HistoryItem>() // 用于存储历史记录

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 初始化数据库
//        database = DatabaseInstance.getDatabase(this)

        // 初始化 RecyclerView
//        val adapter = HistoryAdapter(historyList)
//        binding.rvHistoryList.layoutManager = LinearLayoutManager(this)
//        binding.rvHistoryList.adapter = adapter

        // 从数据库加载历史记录
//        loadHistory(adapter)
    }

    // 从数据库加载历史记录
//    private fun loadHistory(adapter: HistoryAdapter) {
//        CoroutineScope(Dispatchers.IO).launch {
////            val records = database.historyDao().getAllRecords()
////            withContext(Dispatchers.Main) {
////                historyList.addAll(records)
////                adapter.notifyDataSetChanged()
////            }
//        }
//    }
}