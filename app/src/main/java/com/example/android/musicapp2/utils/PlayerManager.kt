package com.example.android.musicapp2.utils

import android.content.Context
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.example.android.musicapp2.model.DataModel

class PlayerManager private constructor(private val context: Context) {

    private val player: ExoPlayer = ExoPlayer.Builder(context).build()
    private var playlist: List<DataModel> = emptyList()
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

    fun setPlaylist(data: List<DataModel>) {
        playlist = data
        player.setMediaItems(data.map {
            MediaItem.Builder()
                .setUri(it.url)
                .setMediaMetadata(
                    MediaMetadata.Builder()
                        .setTitle(it.name)
                        .setArtist("Unknown Artist")
                        .build()
                ).build()
        })
        player.prepare()
    }

    fun getCurrentData(): DataModel? = playlist.getOrNull(currentIndex)

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

    companion object {
        @Volatile private var instance: PlayerManager? = null

        fun getInstance(context: Context): PlayerManager {
            return instance ?: synchronized(this) {
                instance ?: PlayerManager(context.applicationContext).also { instance = it }
            }
        }
    }
}





