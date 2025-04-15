package com.example.android.musicapp2.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.android.musicapp2.databinding.ItemSongBinding
import com.example.android.musicapp2.model.DataModel

class SongAdapter(
    private var songs: List<DataModel>,
    private val onSongClick: (DataModel, Int) -> Unit,
    private val isItemPlaying: (Int) -> Boolean
) : RecyclerView.Adapter<SongAdapter.SongViewHolder>() {

    inner class SongViewHolder(private val binding: ItemSongBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(song: DataModel, position: Int) {
            binding.textViewSongName.text = song.name
            binding.imageViewIcon.setImageResource(song.imageRes)

            val iconRes = if (isItemPlaying(position))
                android.R.drawable.ic_media_pause
            else
                android.R.drawable.ic_media_play

            binding.buttonPlay.setImageResource(iconRes)
            binding.buttonPlay.setOnClickListener {
                onSongClick(song, position)
            }

            binding.textViewNowPlaying.text = if (isItemPlaying(position)) "[Now Playing]" else ""
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

    fun updateSongs(newSongs: List<DataModel>) {
        this.songs = newSongs
        notifyDataSetChanged()
    }
}







