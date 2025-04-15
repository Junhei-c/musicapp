package com.example.android.musicapp2.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.recyclerview.widget.RecyclerView
import com.example.android.musicapp2.databinding.ItemSongBinding
import com.example.android.musicapp2.model.DataModel
import com.example.android.musicapp2.utils.PlayerManager

class SongAdapter(
    private val songs: List<DataModel>,
    private val onSongClick: (DataModel, Int) -> Unit
) : RecyclerView.Adapter<SongAdapter.SongViewHolder>() {

    inner class SongViewHolder(private val binding: ItemSongBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(song: DataModel, position: Int) {
            binding.textViewSongName.text = song.name


            val isCurrentlyPlaying = PlayerManager::class.java
                .getDeclaredField("currentIndex")
                .apply { isAccessible = true }
                .getInt(PlayerManager(binding.root.context)) == position

            val iconRes = if (isCurrentlyPlaying) android.R.drawable.ic_media_pause
            else android.R.drawable.ic_media_play
            binding.buttonPlay.setImageResource(iconRes)

            binding.buttonPlay.setOnClickListener {
                onSongClick(song, position)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongViewHolder {
        val binding = ItemSongBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return SongViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SongViewHolder, position: Int) {
        holder.bind(songs[position], position)
    }

    override fun getItemCount(): Int = songs.size
}

