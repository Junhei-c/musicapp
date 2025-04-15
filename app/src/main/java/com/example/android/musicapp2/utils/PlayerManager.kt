package com.example.android.musicapp2.utils

import android.content.Context
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer

class PlayerManager(private val context: Context) {
    private val player = ExoPlayer.Builder(context).build()
    private var playlist: List<String> = emptyList()

    var currentIndex = -1
        private set

    fun setPlaylist(urls: List<String>) {
        playlist = urls
        player.setMediaItems(urls.map { MediaItem.fromUri(it) })
        player.prepare()
    }

    fun play(index: Int) {
        currentIndex = index
        player.seekTo(index, 0)
        player.playWhenReady = true
    }

    fun pause() {
        player.playWhenReady = false
    }

    fun isPlaying(): Boolean = player.isPlaying

    fun release() {
        player.release()
    }
}

