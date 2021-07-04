package com.bu.selfstudy.ui.recentword

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bu.selfstudy.R
import com.bu.selfstudy.databinding.WordListItemBinding
import com.bu.selfstudy.data.model.WordTuple
import com.bu.selfstudy.tool.showToast
import com.qtalk.recyclerviewfastscroller.RecyclerViewFastScroller

class RecentWordAdapter(val listFragment: RecentWordFragment):
        PagedListAdapter<WordTuple, RecentWordAdapter.ViewHolder>(WordDiffCallback),
        RecyclerViewFastScroller.OnPopupTextUpdate {

    private val asyncListDiffer = object: AsyncListDiffer<WordTuple>(this, WordDiffCallback){}
    var tracker: SelectionTracker<Long>? = null

    inner class ViewHolder(val binding: WordListItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = WordListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val holder = ViewHolder(binding)
        holder.itemView.setOnClickListener {
            val word = getItem(holder.adapterPosition)
            if(word != null)
                "已點擊 ${word.wordName}".showToast()
        }

        holder.binding.markButton.setOnClickListener{
            val word = getItem(holder.adapterPosition)
            if(word != null)
                listFragment.updateMarkWord(word.id, !word.isMark)
        }

        return holder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        currentList?.let {
            if(it.size > it.loadedCount)
                it.loadAround(it.size-1)
            if(it.size == it.loadedCount)
                listFragment.refreshWordIdList(it)
        }
        val word = getItem(position)
        if(word != null){
            holder.binding.wordNameTextView.text = word.wordName
            holder.binding.pronunciationTextView.text = word.pronunciation
            holder.binding.markButton.setIconResource(
                    if(word.isMark)
                        R.drawable.ic_baseline_star_24
                    else
                        R.drawable.ic_round_star_border_24
            )
            tracker?.let {
                holder.itemView.isActivated = it.isSelected(word.id)
            }
        }else{
            holder.binding.wordNameTextView.text = ""
            holder.binding.pronunciationTextView.text = ""
            holder.binding.markButton.setIconResource(R.drawable.ic_round_star_border_24
            )
        }
        //holder.binding.wordInfoTextView.text = (position+1).toString()
        holder.binding.divider.visibility =
                if(position == asyncListDiffer.currentList.size-1) View.GONE else View.VISIBLE
    }

    companion object WordDiffCallback : DiffUtil.ItemCallback<WordTuple>(){
        override fun areItemsTheSame(oldItem: WordTuple, newItem: WordTuple): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: WordTuple, newItem: WordTuple): Boolean {
            return oldItem == newItem
        }
    }

    override fun onChange(position: Int): CharSequence {
        return if(getItem(position) != null)
            getItem(position)!!.wordName
        else
            ""
    }
}