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

    private var onPlaybackChanged: (() -> Unit)? = null

    init {
        player.addListener(object : Player.Listener {
            override fun onEvents(player: Player, events: Player.Events) {
                currentIndex = player.currentMediaItemIndex
                onPlaybackChanged?.invoke()
            }
        })
    }

    fun setPlaylist(data: List<DataModel>) {
        playlist = data
        player.setMediaItems(data.map { it.toMediaItem() })
        player.prepare()
    }

    fun play(index: Int) {
        if (index in playlist.indices) {
            currentIndex = index
            val media = playlist[index]

            PlayerStateManager.setCurrentSongTitle(context, media.name)
            PlayerStateManager.setCurrentArtist(context, "Unknown Artist")

            player.seekTo(index, 0)
            player.playWhenReady = true
        }
    }

    fun pause() {
        player.playWhenReady = false
    }

    fun togglePlayback(index: Int) {
        if (currentIndex == index && player.isPlaying) {
            pause()
            onPlaybackChanged?.invoke()
        } else {
            play(index)
            onPlaybackChanged?.invoke()
        }
    }


    fun playNext() {
        if (playlist.isNotEmpty()) {
            val nextIndex = (currentIndex + 1) % playlist.size
            play(nextIndex)
        }
    }

    fun playPrevious() {
        if (playlist.isNotEmpty()) {
            val prevIndex = if (currentIndex - 1 < 0) playlist.size - 1 else currentIndex - 1
            play(prevIndex)
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

    fun getCurrentData(): DataModel? = playlist.getOrNull(currentIndex)

    fun setOnPlaybackChangedListener(listener: () -> Unit) {
        onPlaybackChanged = listener
    }

    fun getExoPlayer(): ExoPlayer = player

    fun release() {
        player.release()
    }

    private fun DataModel.toMediaItem(): MediaItem {
        val metadata = MediaMetadata.Builder()
            .setTitle(this.name)
            .setArtist("Unknown Artist")
            .build()

        return MediaItem.Builder()
            .setUri(this.url)
            .setMediaMetadata(metadata)
            .setTag(this.mediaType)
            .build()
    }

    companion object {
        @Volatile
        private var instance: PlayerManager? = null

        fun getInstance(context: Context): PlayerManager =
            instance ?: synchronized(this) {
                instance ?: PlayerManager(context.applicationContext).also { instance = it }
            }
    }
}




