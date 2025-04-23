package com.example.android.musicapp2.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
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
            val context = binding.root.context
            val playing = isItemPlaying(position)


            binding.textViewSongName.text = song.name


            binding.imageViewIcon.visibility = if (playing) View.VISIBLE else View.INVISIBLE
            if (playing) {
                binding.imageViewIcon.setImageResource(R.drawable.group)
            }


            val playIcon = if (playing) android.R.drawable.ic_media_pause else android.R.drawable.ic_media_play
            binding.buttonPlay.setImageResource(playIcon)


            val cardColor = if (playing)
                ContextCompat.getColor(context, R.color.blue)
            else
                ContextCompat.getColor(context, R.color.white)

            binding.cardViewItem.setCardBackgroundColor(cardColor)


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





