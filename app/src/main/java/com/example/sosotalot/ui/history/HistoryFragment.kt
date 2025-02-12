package com.example.sosotalot.ui.history

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.coding.meet.storeimagesinroomdatabase.HistoryDatabase
import com.coding.meet.storeimagesinroomdatabase.HistoryModel
import com.example.sosotalot.databinding.FragmentHistoryBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HistoryFragment : Fragment() {

    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!
    private lateinit var database: HistoryDatabase // Room 数据库实例

    // 历史记录列表，加载自数据库
    private val historyList = mutableListOf<HistoryModel>()
    private lateinit var adapter: HistoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHistoryBinding.inflate(inflater, container, false)

        // 初始化 RecyclerView
//        setupRecyclerView()
//
//        // 初始化数据库实例
//        database = DatabaseInstance.getDatabase(requireContext().applicationContext)
//
//        // 加载数据库中的历史记录
//        loadHistoryFromDatabase()
//        setClearAllHistoryListener()
        return binding.root
    }

    private fun setClearAllHistoryListener() {
//        binding.btnClearHistory.setOnClickListener {
//            AlertDialog.Builder(requireContext())
//                .setTitle("确认删除")
//                .setMessage("您确定要删除所有记录吗？此操作无法恢复！")
//                .setPositiveButton("删除") { _, _ ->
//                    CoroutineScope(Dispatchers.IO).launch {
//                        database.historyDao.deleteAllHistory() // 执行删除操作
//                        CoroutineScope(Dispatchers.Main).launch {
//                            historyList.clear()
//                            adapter.notifyDataSetChanged()
//                            Toast.makeText(requireContext(), "所有记录已删除", Toast.LENGTH_SHORT).show()
//                        }
//                    }
//                }
//                .setNegativeButton("取消", null)
//                .show()
//        }
    }


    private fun setupRecyclerView() {
//        adapter = HistoryAdapter(historyList)
//        binding.rvHistoryList.layoutManager = LinearLayoutManager(requireContext())
//        binding.rvHistoryList.adapter = adapter
    }


    private fun loadHistoryFromDatabase() {
//        database.historyDao.getAllHistory().observe(viewLifecycleOwner) { records ->
//            historyList.clear() // 清除旧数据 防止重复添加
//            historyList.addAll(records) // 将新数据添加到列表中
//            binding.rvHistoryList.adapter?.notifyDataSetChanged() // 通知适配器数据已更新
//        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
