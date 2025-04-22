package com.example.android.musicapp2.adapter

import android.graphics.Color
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

        fun bind(song: DataModel, position: Int) {
            val playing = isItemPlaying(position)

            // Set song name and add padding if playing
            binding.textViewSongName.text = song.name
            binding.textViewSongName.setPadding(if (playing) 40 else 0, 0, 0, 0)

            // Show icon only if playing
            binding.imageViewIcon.visibility = if (playing) View.VISIBLE else View.GONE
            if (playing) {
                binding.imageViewIcon.setImageResource(R.drawable.group)
            }

            // Show Now Playing label
            binding.textViewNowPlaying.text = if (playing) "Now Playing" else ""
            binding.textViewNowPlaying.visibility = if (playing) View.VISIBLE else View.GONE

            // Set play/pause icon
            val playIcon = if (playing) android.R.drawable.ic_media_pause else android.R.drawable.ic_media_play
            binding.buttonPlay.setImageResource(playIcon)

            // Highlight the card and root background when playing
            val cardColor = if (playing) Color.parseColor("#2196F3") else Color.WHITE
            binding.cardViewItem.setCardBackgroundColor(cardColor)
            binding.root.setBackgroundColor(cardColor)

            // Button click listener
            binding.buttonPlay.setOnClickListener {
                onSongClick(song, position)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongViewHolder {
        val binding = ItemSongBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SongViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SongViewHolder, position: Int) {
        holder.bind(songs[position], position)
    }

    override fun getItemCount(): Int = songs.size
}


