package com.example.sosotalot.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.sosotalot.R
import com.example.sosotalot.model.TarotLayout

class TarotLayoutAdapter(
    private val layouts: List<TarotLayout>,
    private val onItemClick: (TarotLayout) -> Unit
) : RecyclerView.Adapter<TarotLayoutAdapter.ViewHolder>() {

    private var selectedPosition: Int = -1 // 記錄選中的索引

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.layoutImage)
        val nameTextView: TextView = itemView.findViewById(R.id.layoutName)
        val meaningTextView: TextView = itemView.findViewById(R.id.layoutMeaning) // ✅ 確保綁定
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_tarot_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val layout = layouts[position]

        holder.imageView.setImageResource(layout.imageResId)
        holder.nameTextView.text = layout.name
        holder.meaningTextView.text = layout.meaning // ✅ 確保 meaning 正確顯示

        holder.itemView.setOnClickListener {
            selectedPosition = position
            onItemClick(layout)
            notifyDataSetChanged() // 刷新 UI
        }

        // 选中效果：高亮或添加边框
        if (position == selectedPosition) {
            holder.itemView.setBackgroundResource(R.drawable.baseline_account_circle_24) // 選中高亮
        } else {
            holder.itemView.setBackgroundResource(0) // 清除選中效果
        }
    }

    override fun getItemCount(): Int = layouts.size
}

