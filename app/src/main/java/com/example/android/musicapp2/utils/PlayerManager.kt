package com.example.android.musicapp2.utils

import android.content.Context
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.example.android.musicapp2.model.DataModel

class PlayerManager private constructor(context: Context) {

    private val player: ExoPlayer = ExoPlayer.Builder(context).build()
    private var playlist: List<DataModel> = emptyList()
    var currentIndex: Int = -1
        private set

    private var listener: (() -> Unit)? = null

    init {
        player.addListener(object : Player.Listener {
            override fun onEvents(player: Player, events: Player.Events) {
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

    fun togglePlayback(index: Int) {
        if (currentIndex == index && player.isPlaying) pause() else play(index)
    }

    fun playNext() {
        if (playlist.isNotEmpty()) play((currentIndex + 1) % playlist.size)
    }

    fun playPrevious() {
        if (playlist.isNotEmpty()) {
            val prev = if (currentIndex - 1 < 0) playlist.size - 1 else currentIndex - 1
            play(prev)
        }
    }

    fun isPlaying(): Boolean = player.isPlaying

    fun getPlaybackPercentage(): Int {
        val duration = player.duration
        val position = player.currentPosition
        return if (duration > 0) ((position * 100) / duration).toInt() else 0
    }

    fun getDuration(): Long = player.duration
    fun getCurrentPosition(): Long = player.currentPosition
    fun seekTo(positionMs: Long) = player.seekTo(positionMs)

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









