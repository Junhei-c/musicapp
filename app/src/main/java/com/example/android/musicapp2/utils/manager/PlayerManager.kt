package com.example.android.musicapp2.utils.manager

import android.content.Context
import android.os.Handler
import android.os.Looper
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.example.android.musicapp2.model.DataModel

class PlayerManager private constructor(private val context: Context) {

    private val player: ExoPlayer = ExoPlayer.Builder(context).build()
    private val handler = Handler(Looper.getMainLooper())

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

            override fun onPlaybackStateChanged(state: Int) {
                if (state == Player.STATE_ENDED) {
                    playNext()
                }
            }
        })
    }

    fun setPlaylist(data: List<DataModel>) {
        playlist = data
        val mediaItems = data.map {
            MediaItem.Builder()
                .setUri(it.url)
                .setMediaMetadata(
                    MediaMetadata.Builder().setTitle(it.name).build()
                ).build()
        }
        handler.post {
            player.setMediaItems(mediaItems)
            player.prepare()
            player.playWhenReady = false
        }
    }

    fun playSongAt(index: Int) {
        if (index !in playlist.indices || (index == currentIndex && player.isPlaying)) return

        val song = playlist[index]
        val mediaItem = MediaItem.Builder()
            .setUri(song.url)
            .setMediaMetadata(
                MediaMetadata.Builder().setTitle(song.name).build()
            ).build()

        currentIndex = index

        handler.post {
            player.setMediaItem(mediaItem)
            player.prepare()
            player.play()
            onPlaybackChanged?.invoke()
        }
    }

    fun togglePlayback(index: Int) {
        if (index in playlist.indices) {
            if (currentIndex == index && isPlaying()) {
                handler.post { player.pause() }
            } else {
                playSongAt(index)
            }
        }
    }

    fun playNext() {
        if (playlist.isNotEmpty()) {
            val nextIndex = (currentIndex + 1) % playlist.size
            playSongAt(nextIndex)
        }
    }

    fun playPrevious() {
        if (playlist.isNotEmpty()) {
            val prevIndex = if (currentIndex - 1 < 0) playlist.size - 1 else currentIndex - 1
            playSongAt(prevIndex)
        }
    }

    fun pause() {
        handler.post { player.pause() }
    }

    fun isPlaying(): Boolean = player.isPlaying
    fun getDuration(): Long = player.duration
    fun getCurrentPosition(): Long = player.currentPosition
    fun getPlaybackPercentage(): Int {
        val duration = player.duration
        val position = player.currentPosition
        return if (duration > 0) ((position * 100) / duration).toInt() else 0
    }

    fun getCurrentData(): DataModel? = playlist.getOrNull(currentIndex)
    fun getExoPlayer(): ExoPlayer = player
    fun seekTo(positionMs: Long) = handler.post { player.seekTo(positionMs) }
    fun setOnPlaybackChangedListener(listener: () -> Unit) {
        onPlaybackChanged = listener
    }

    fun seekTo(index: Int) {
        if (index in playlist.indices) {
            currentIndex = index
            handler.post {
                player.seekTo(index, 0)
                onPlaybackChanged?.invoke()
            }
        }
    }

    fun resume() {
        handler.post {
            player.play()
            onPlaybackChanged?.invoke()
        }
    }

    fun release() = player.release()

    companion object {
        @Volatile private var instance: PlayerManager? = null

        fun getInstance(context: Context): PlayerManager =
            instance ?: synchronized(this) {
                instance ?: PlayerManager(context.applicationContext).also { instance = it }
            }
    }
}



