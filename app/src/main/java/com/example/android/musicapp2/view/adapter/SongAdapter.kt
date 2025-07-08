package com.example.android.musicapp2.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.android.musicapp2.R
import com.example.android.musicapp2.databinding.ItemSongBinding
import com.example.android.musicapp2.model.DataModel

class SongAdapter(
    private val onSongClick: (DataModel, Int) -> Unit,
    private val isItemPlaying: (Int) -> Boolean
) : ListAdapter<DataModel, SongAdapter.SongViewHolder>(DiffCallback) {

    inner class SongViewHolder(private val binding: ItemSongBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(song: DataModel, position: Int) = with(binding) {
            val isPlaying = isItemPlaying(position)

            textViewSongName.text = song.name

            imageViewIcon.apply {
                visibility = if (isPlaying) View.VISIBLE else View.INVISIBLE
                setImageResource(R.drawable.group)
            }

            buttonPlay.setImageResource(
                if (isPlaying) R.drawable.pause else R.drawable.play
            )

            cardViewItem.setBackgroundResource(
                if (isPlaying) R.drawable.bg_mode_selected
                else R.drawable.bg_mode_unselected
            )

            val clickListener = View.OnClickListener {
                val safePos = bindingAdapterPosition
                if (safePos != RecyclerView.NO_POSITION) {
                    onSongClick(song, safePos)
                }
            }

            buttonPlay.setOnClickListener(clickListener)
            cardViewItem.setOnClickListener(clickListener)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongViewHolder {
        val binding = ItemSongBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return SongViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SongViewHolder, position: Int) {
        holder.bind(getItem(position), position)
    }

    fun updateSongs(newSongs: List<DataModel>) {
        submitList(newSongs)
    }

    companion object DiffCallback : DiffUtil.ItemCallback<DataModel>() {
        override fun areItemsTheSame(oldItem: DataModel, newItem: DataModel): Boolean {
            return oldItem.url == newItem.url
        }

        override fun areContentsTheSame(oldItem: DataModel, newItem: DataModel): Boolean {
            return oldItem == newItem
        }
    }
}