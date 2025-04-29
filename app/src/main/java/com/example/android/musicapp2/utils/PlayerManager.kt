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
    fun playNext() {
        if (playlist.isEmpty()) return
        val nextIndex = (currentIndex + 1) % playlist.size
        play(nextIndex)
    }

    fun playPrevious() {
        if (playlist.isEmpty()) return
        val prevIndex = if (currentIndex - 1 < 0) playlist.size - 1 else currentIndex - 1
        play(prevIndex)
    }

    fun getPlaylistSize(): Int {
        return playlist.size
    }



    fun setPlaylist(urls: List<String>) {
        playlist = urls
        player.setMediaItems(urls.map { MediaItem.fromUri(it) })
        player.prepare()

        savePlaylistToPrefs(urls)
    }

    private fun savePlaylistToPrefs(urls: List<String>) {
        val prefs = context.getSharedPreferences("player_prefs", Context.MODE_PRIVATE)
        prefs.edit().putStringSet("playlist", urls.toSet()).apply()
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





