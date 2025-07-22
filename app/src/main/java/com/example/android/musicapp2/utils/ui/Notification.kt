package com.example.android.musicapp2.utils.ui

import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.example.android.musicapp2.R
import com.example.android.musicapp2.controller.MusicController
import com.example.android.musicapp2.service.MusicService
import com.example.android.musicapp2.view.activity.MainActivity
import com.example.android.musicapp2.widget.MyMusicWidget

object Notification {

    private lateinit var mediaSession: MediaSessionCompat

    fun createNotification(context: Context, isPlaying: Boolean, songTitle: String): Notification {
        if (!::mediaSession.isInitialized) {
            mediaSession = MediaSessionCompat(context, "MusicSession").apply {
                setFlags(
                    MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS or
                            MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS
                )
            }
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

        val playPauseIcon = if (isPlaying) R.drawable.pausebt else R.drawable.play
        val playPauseText = if (isPlaying) "Pause" else "Play"

        val openAppIntent = Intent(context, MainActivity::class.java)
        val contentIntent = PendingIntent.getActivity(context, 0, openAppIntent, PendingIntent.FLAG_IMMUTABLE)

        val song = MusicController.getCurrentSong(context)
        val imageRes = song?.imageRes ?: R.drawable.group
        val largeIcon = BitmapFactory.decodeResource(context.resources, imageRes)

        return NotificationCompat.Builder(context, MusicService.CHANNEL_ID)
            .setContentTitle(songTitle)
            .setContentText("Now playing on MusicApp")
            .setSmallIcon(imageRes)
            .setLargeIcon(largeIcon)
            .setContentIntent(contentIntent)
            .addAction(R.drawable.prev, "Previous", prevIntent)
            .addAction(playPauseIcon, playPauseText, playPauseIntent)
            .addAction(R.drawable.next, "Next", nextIntent)
            .setStyle(
                androidx.media.app.NotificationCompat.MediaStyle()
                    .setMediaSession(mediaSession.sessionToken)
                    .setShowActionsInCompactView(0, 1, 2)
            )
            .setColorized(true)
            .setColor(ContextCompat.getColor(context, R.color.colorPrimary))
            .setOngoing(isPlaying)
            .setOnlyAlertOnce(true)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .build()
    }
}

