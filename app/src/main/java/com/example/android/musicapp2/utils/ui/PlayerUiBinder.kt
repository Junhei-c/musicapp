package com.example.android.musicapp2.utils.ui

import com.example.android.musicapp2.databinding.ActivityMainBinding
import com.example.android.musicapp2.model.DataModel
import com.example.android.musicapp2.utils.extensions.show
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
            songs = songs,
            onSongClick = onSongClick,
            isItemPlaying = isItemPlaying
        )
        binding.recyclerViewSongs.adapter = adapter
        return adapter
    }

    fun showAudioUI(binding: ActivityMainBinding) {
        binding.recyclerViewSongs.show()
        binding.toolbar.show()
        binding.modeToggleGroup.show()
        binding.imageViewNowPlayingIcon.show()
        binding.buttonPlayPause.show()
        binding.progressBar.show()
        binding.textViewCurrentTitle.show()
    }
}
