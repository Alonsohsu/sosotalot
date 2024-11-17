package com.example.sosotalot.ui.history

import HistoryItem
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.sosotalot.R
import com.example.sosotalot.databinding.HistoryItemBinding
import java.text.SimpleDateFormat
import java.util.*

class HistoryAdapter(private val items: List<HistoryItem>) :
    RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder>() {

    inner class HistoryViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val binding = HistoryItemBinding.bind(view)

        fun bind(item: HistoryItem) {
            val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
            binding.tvHistoryTime.text = "时间: ${sdf.format(Date(item.time))}"
            binding.tvHistoryQuestion.text = "问题: ${item.question}"
            binding.tvHistoryCard.text = "牌: ${item.tarotCard}"
            binding.tvHistoryAnswer.text = "解答: ${item.answer}"
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.history_item, parent, false)
        return HistoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size
}
