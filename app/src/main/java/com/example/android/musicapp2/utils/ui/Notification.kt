package com.example.android.musicapp2.utils.ui

import android.app.Notification
import android.content.Context
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.view.View
import androidx.core.app.NotificationCompat
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import com.example.android.musicapp2.R
import com.example.android.musicapp2.databinding.ActivityMainBinding
import com.example.android.musicapp2.model.DataModel
import com.example.android.musicapp2.service.MusicService
import com.example.android.musicapp2.utils.extensions.show
import com.example.android.musicapp2.utils.manager.PlayerManager
import com.example.android.musicapp2.widget.MyMusicWidget

object NotificationHelper {
    private lateinit var mediaSession: MediaSessionCompat

    fun createNotification(context: Context, isPlaying: Boolean, songTitle: String): Notification {
        if (!::mediaSession.isInitialized) {
            mediaSession = MediaSessionCompat(context, "MusicSession")
            mediaSession.setFlags(
                MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS or
                        MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS
            )
        }

        val stateBuilder = PlaybackStateCompat.Builder()
            .setActions(
                PlaybackStateCompat.ACTION_PLAY or
                        PlaybackStateCompat.ACTION_PAUSE or
                        PlaybackStateCompat.ACTION_SKIP_TO_NEXT or
                        PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS
            )

        mediaSession.setPlaybackState(stateBuilder.build())

        val playPauseIntent = MyMusicWidget.getPendingIntent(context, MyMusicWidget.ACTION_PLAY_PAUSE)
        val nextIntent = MyMusicWidget.getPendingIntent(context, MyMusicWidget.ACTION_NEXT)
        val prevIntent = MyMusicWidget.getPendingIntent(context, MyMusicWidget.ACTION_PREV)

        val playPauseIcon = if (isPlaying) R.drawable.pausebt else R.drawable.bigplay
        val playPauseText = if (isPlaying) "Pause" else "Play"

        return NotificationCompat.Builder(context, MusicService.CHANNEL_ID)
            .setContentTitle(songTitle)
            .setSmallIcon(R.drawable.group)
            .addAction(R.drawable.prev, "Previous", prevIntent)
            .addAction(playPauseIcon, playPauseText, playPauseIntent)
            .addAction(R.drawable.next, "Next", nextIntent)
            .setStyle(
                androidx.media.app.NotificationCompat.MediaStyle()
                    .setMediaSession(mediaSession.sessionToken)
                    .setShowActionsInCompactView(0, 1, 2)
            )
            .setOngoing(isPlaying)
            .setOnlyAlertOnce(true)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .build()
    }

    object SongInteractionHandler {

        fun handleAudioClick(
            binding: ActivityMainBinding,
            song: DataModel,
            index: Int,
            playerManager: PlayerManager,
            exoPlayer: ExoPlayer,
            miniPlayerView: androidx.media3.ui.PlayerView,
            pipPlayerView: androidx.media3.ui.PlayerView,
            onWidgetUpdate: () -> Unit
        ) {
            playerManager.seekTo(index)
            playerManager.resume()

            miniPlayerView.player = exoPlayer
            pipPlayerView.player = exoPlayer

            NowPlayingUpdater.update(binding, playerManager)
            UiController.showAudioUI(binding)
            binding.pipPlayerView.visibility = View.GONE

            onWidgetUpdate()
        }

        fun handleVideoClick(
            binding: ActivityMainBinding,
            exoPlayer: ExoPlayer,
            url: String
        ) {
            exoPlayer.setMediaItem(MediaItem.fromUri(url))
            exoPlayer.prepare()
            exoPlayer.playWhenReady = true

            binding.pipPlayerView.player = exoPlayer
            binding.pipPlayerView.visibility = View.VISIBLE
            binding.pipPlayerView.show()

            UiController.hideAudioUI(binding)
        }
    }
}
