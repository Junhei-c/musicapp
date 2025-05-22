package com.example.android.musicapp2.controller

import android.content.Context
import com.example.android.musicapp2.utils.PlayerManager

object MusicController {
    fun playNext(context: Context) = PlayerManager.getInstance(context).playNext()

    fun playPrevious(context: Context) = PlayerManager.getInstance(context).playPrevious()

    fun togglePlayback(context: Context) {
        val player = PlayerManager.getInstance(context)
        player.togglePlayback(player.currentIndex)
    }

    fun playByMode(context: Context, mode: Int) {
        val player = PlayerManager.getInstance(context)
        when (mode) {
            0 -> player.play(2)
            1 -> player.play(0)
            2 -> player.play(1)
        }
    }

    fun isPlaying(context: Context): Boolean = PlayerManager.getInstance(context).isPlaying()

    fun getCurrentSong(context: Context) = PlayerManager.getInstance(context).getCurrentData()

    fun getProgress(context: Context) = PlayerManager.getInstance(context).getPlaybackPercentage()
}

