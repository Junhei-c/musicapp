package com.example.android.musicapp2.controller

import android.content.Context
import com.example.android.musicapp2.utils.manager.PlayerManager

object MusicController {

    private fun getPlayer(context: Context) = PlayerManager.getInstance(context)

    fun playNext(context: Context) {
        getPlayer(context).playNext()
    }

    fun playPrevious(context: Context) {
        getPlayer(context).playPrevious()
    }

    fun togglePlayback(context: Context) {
        val player = getPlayer(context)
        player.togglePlayback(player.currentIndex)
    }

    fun playByMode(context: Context, mode: Int) {
        val player = getPlayer(context)
        val index = when (mode) {
            0 -> 2
            1 -> 0
            2 -> 1
            else -> 0
        }
        player.playSongAt(index)
    }

    fun isPlaying(context: Context): Boolean =
        getPlayer(context).isPlaying()

    fun getCurrentSong(context: Context) =
        getPlayer(context).getCurrentData()

    fun getProgress(context: Context) =
        getPlayer(context).getPlaybackPercentage()
}
