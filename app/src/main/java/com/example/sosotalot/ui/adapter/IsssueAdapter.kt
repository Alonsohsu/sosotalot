package com.example.sosotalot.ui.Issue

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.sosotalot.R
import com.example.sosotalot.model.Issue

class IssueAdapter(
    private val issueList: List<Issue>,
    private val onLikeClick: (Issue) -> Unit
) : RecyclerView.Adapter<IssueAdapter.IssueViewHolder>() {

    class IssueViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val titleTextView: TextView = view.findViewById(R.id.issueTitle)
        val descriptionTextView: TextView = view.findViewById(R.id.issueDescription)
        val likeCountTextView: TextView = view.findViewById(R.id.likeCountText)
        val likeButton: ImageButton = view.findViewById(R.id.likeButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IssueViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_issue, parent, false)
        return IssueViewHolder(view)
    }

    override fun onBindViewHolder(holder: IssueViewHolder, position: Int) {
        val issue = issueList[position]
        holder.titleTextView.text = issue.title
        holder.descriptionTextView.text = issue.description
        holder.likeCountTextView.text = "👍 ${issue.likes}"

        holder.likeButton.setOnClickListener {
            onLikeClick(issue)
        }
    }

    override fun getItemCount(): Int = issueList.size
}
