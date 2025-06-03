package com.example.android.musicapp2.controller

import android.content.Context
import com.example.android.musicapp2.utils.PlayerManager

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
        when (mode) {
            0 -> player.play(2)
            1 -> player.play(0)
            2 -> player.play(1)
            else -> player.play(0)
        }
    }

    fun isPlaying(context: Context): Boolean = getPlayer(context).isPlaying()

    fun getCurrentSong(context: Context) = getPlayer(context).getCurrentData()

    fun getProgress(context: Context) = getPlayer(context).getPlaybackPercentage()
}