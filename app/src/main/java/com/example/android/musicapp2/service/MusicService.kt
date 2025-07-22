package com.example.android.musicapp2.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.exoplayer.ExoPlayer
import com.example.android.musicapp2.utils.manager.PlayerManager
import com.example.android.musicapp2.utils.ui.Notification
import com.example.android.musicapp2.widget.MyMusicWidget
import com.example.android.musicapp2.widget.WidgetUpdater

class MusicService : Service() {

    companion object {
        const val NOTIFICATION_ID = 1001
        const val CHANNEL_ID = "music_service_channel"
        const val ACTION_START_FOREGROUND = "start_foreground"
    }

    private lateinit var player: ExoPlayer

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()

        player = PlayerManager.getInstance(this).getExoPlayer().apply {
            setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(C.USAGE_MEDIA)
                    .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
                    .build(),
                true
            )
            playWhenReady = true
        }

        startForeground(
            NOTIFICATION_ID,
            Notification.createNotification(
                this,
                player.isPlaying,
                player.mediaMetadata.title?.toString() ?: "Now Playing"
            )
        )
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            MyMusicWidget.ACTION_PLAY_PAUSE -> {
                if (player.isPlaying) player.pause() else player.play()
            }
            MyMusicWidget.ACTION_NEXT -> {
                player.seekToNext()
            }
            MyMusicWidget.ACTION_PREV -> {
                player.seekToPrevious()
            }
            "REFRESH_WIDGET" -> {
                WidgetUpdater.updateStandard(this)
                WidgetUpdater.updateCircle(this)
            }
        }

        val notification = Notification.createNotification(
            this,
            player.isPlaying,
            player.mediaMetadata.title?.toString() ?: "Now Playing"
        )

        startForeground(NOTIFICATION_ID, notification)
        return START_STICKY
    }

    override fun onDestroy() {
        player.release()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Music Playback",
                NotificationManager.IMPORTANCE_LOW
            )
            (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
                .createNotificationChannel(channel)
        }
    }
}

