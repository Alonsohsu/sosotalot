package com.example.sosotalot.ui.history

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.coding.meet.storeimagesinroomdatabase.HistoryModel
import com.example.sosotalot.databinding.HistoryItemBinding
import java.util.Date

class HistoryAdapter(private val historyList: MutableList<HistoryModel>) :
    RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder>() {

    inner class HistoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = HistoryItemBinding.bind(itemView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = HistoryItemBinding.inflate(inflater, parent, false)
        return HistoryViewHolder(binding.root)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        val item = historyList[position]
        holder.binding.apply {
            tvTime.text = "时间: ${Date(item.time)}"
            tvQuestion.text = "问题: ${item.question}"
            tvTarotCard.text = "牌: ${item.tarotCard}"

            // 讓解答需要點擊才顯示
            tvAnswer.text = "点击查看解答"
            tvAnswer.setOnClickListener {
                tvAnswer.text = "解答: ${item.answer}"
            }
        }
    }

    override fun getItemCount(): Int = historyList.size
}