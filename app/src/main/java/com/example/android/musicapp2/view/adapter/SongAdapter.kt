package com.example.android.musicapp2.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.android.musicapp2.R
import com.example.android.musicapp2.databinding.ItemSongBinding
import com.example.android.musicapp2.model.DataModel

class SongAdapter(
    private val songs: List<DataModel>,
    private val onSongClick: (DataModel, Int) -> Unit,
    private val isItemPlaying: (Int) -> Boolean
) : RecyclerView.Adapter<SongAdapter.SongViewHolder>() {

    inner class SongViewHolder(private val binding: ItemSongBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(song: DataModel) {
            val position = bindingAdapterPosition

            val isPlaying = isItemPlaying(position)

            binding.textViewSongName.text = song.name

            binding.imageViewIcon.apply {
                visibility = if (isPlaying) View.VISIBLE else View.INVISIBLE
                setImageResource(R.drawable.group)
            }

            binding.buttonPlay.setImageResource(
                if (isPlaying) R.drawable.pause else R.drawable.play
            )

            binding.cardViewItem.setBackgroundResource(
                if (isPlaying) R.drawable.bg_mode_selected else R.drawable.bg_mode_unselected
            )

            binding.buttonPlay.setOnClickListener {
                val safePos = bindingAdapterPosition
                if (safePos != RecyclerView.NO_POSITION) {
                    onSongClick(song, safePos)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongViewHolder {
        val binding = ItemSongBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SongViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SongViewHolder, position: Int) {
        holder.bind(songs[position])
    }

    override fun getItemCount(): Int = songs.size
}
