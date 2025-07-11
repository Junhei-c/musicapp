package com.example.android.musicapp2.utils.ui

import com.example.android.musicapp2.databinding.ActivityMainBinding
import com.example.android.musicapp2.model.DataModel
import com.example.android.musicapp2.view.adapter.SongAdapter


object PlayerUiBinder {

    fun bindAdapter(
        binding: ActivityMainBinding,
        songs: List<DataModel>,
        selectedIndex: Int,
        onSongClick: (DataModel, Int) -> Unit,
        isItemPlaying: (Int) -> Boolean
    ): SongAdapter {
        val adapter = SongAdapter(
            onSongClick = onSongClick,
            isItemPlaying = isItemPlaying
        )
        binding.recyclerViewSongs.adapter = adapter
        adapter.submitList(songs)
        return adapter
    }

}
