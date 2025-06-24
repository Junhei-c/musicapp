package com.example.android.musicapp2.utils.ui

import com.example.android.musicapp2.R
import com.example.android.musicapp2.databinding.ActivityMainBinding
import com.example.android.musicapp2.utils.manager.PlayerManager

object NowPlayingUpdater {
    fun update(binding: ActivityMainBinding, playerManager: PlayerManager) {
        val song = playerManager.getCurrentData()
        val isPlaying = playerManager.isPlaying()

        binding.textViewCurrentTitle.text = song?.name.orEmpty()
        song?.imageRes?.let { binding.imageViewNowPlayingIcon.setImageResource(it) }
        binding.buttonPlayPause.setImageResource(
            if (isPlaying) R.drawable.iconparkpauseone else R.drawable.iconparkplay
        )
        binding.progressBar.progress = playerManager.getPlaybackPercentage()
    }
}
