package com.example.android.musicapp2.utils

import android.content.Context
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer

class PlayerManager(private val context: Context) {

    private val player: ExoPlayer = ExoPlayer.Builder(context).build()
    private var playlist: List<String> = emptyList()
    var currentIndex: Int = -1
        private set

    private var listener: (() -> Unit)? = null

    init {
        player.addListener(object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                listener?.invoke()
            }

            override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                currentIndex = player.currentMediaItemIndex
                listener?.invoke()
            }
        })
    }

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

    fun togglePlayback(index: Int) {
        if (currentIndex == index && player.isPlaying) {
            pause()
        } else {
            play(index)
        }
    }

    fun isPlaying(): Boolean = player.isPlaying

    fun setOnPlaybackChangedListener(callback: () -> Unit) {
        listener = callback
    }

    fun release() {
        player.release()
    }
}





