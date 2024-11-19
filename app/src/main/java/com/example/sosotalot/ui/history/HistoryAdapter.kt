package com.example.sosotalot.ui.history

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.sosotalot.databinding.HistoryItemBinding
import java.util.Date

//class HistoryAdapter(private val historyList: MutableList<HistoryItem>) :
//    RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder>() {
//
//    inner class HistoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//        val binding = HistoryItemBinding.bind(itemView)
//    }
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
//        val inflater = LayoutInflater.from(parent.context)
//        val binding = HistoryItemBinding.inflate(inflater, parent, false)
//        return HistoryViewHolder(binding.root)
//    }
//
//    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
//        val item = historyList[position]
//        holder.binding.apply {
//            tvTime.text = "时间: ${Date(item.time)}"
//            tvQuestion.text = "问题: ${item.question}"
//            tvTarotCard.text = "牌: ${item.tarotCard}"
//            tvAnswer.text = "解答: ${item.answer}"
//        }
//    }
//
//    override fun getItemCount(): Int = historyList.size
//}
