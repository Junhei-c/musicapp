package com.example.android.musicapp2.utils

import android.content.Context
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
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
        player.setMediaItems(urls.mapIndexed { index, url ->
            MediaItem.Builder()
                .setUri(url)
                .setMediaMetadata(
                    MediaMetadata.Builder()
                        .setTitle("Track ${index + 1}")
                        .setArtist("Artist ${index + 1}")
                        .build()
                )
                .build()
        })
        player.prepare()
        savePlaylistToPrefs(urls)
    }

    private fun savePlaylistToPrefs(urls: List<String>) {
        val prefs = context.getSharedPreferences("player_prefs", Context.MODE_PRIVATE)
        prefs.edit().putStringSet("playlist", urls.toSet()).apply()
    }

    fun play(index: Int) {
        if (playlist.isEmpty()) return
        currentIndex = index
        player.seekTo(index, 0)
        player.playWhenReady = true
    }

    fun pause() {
        player.playWhenReady = false
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

    fun togglePlayback(index: Int) {
        if (currentIndex == index && player.isPlaying) {
            pause()
        } else {
            play(index)
        }
    }

    fun isPlaying(): Boolean = player.isPlaying

    fun getPlaylistSize(): Int = playlist.size

    fun getCurrentMediaItem(): MediaItem? = player.currentMediaItem

    fun setOnPlaybackChangedListener(callback: () -> Unit) {
        listener = callback
    }

    fun release() {
        player.release()
    }
}






