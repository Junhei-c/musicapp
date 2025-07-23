package com.example.android.musicapp2.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.exoplayer.ExoPlayer
import com.example.android.musicapp2.R
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
    private val handler = Handler(Looper.getMainLooper())

    private val updateNotificationRunnable = object : Runnable {
        override fun run() {
            val song = PlayerManager.getInstance(this@MusicService).getCurrentData()
            val title = song?.name ?: "Now Playing"
            val imageRes = song?.imageRes ?: R.drawable.group
            val duration = player.duration.takeIf { it > 0 } ?: 1L
            val progress = ((player.currentPosition.toFloat() / duration) * 100).toInt()

            val notification = Notification.createNotification(
                context = this@MusicService,
                isPlaying = player.isPlaying,
                songTitle = title,
                imageRes = imageRes,
                progress = progress
            )

            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.notify(NOTIFICATION_ID, notification)

            handler.postDelayed(this, 1000) // Update every second
        }
    }

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

        val song = PlayerManager.getInstance(this).getCurrentData()
        val title = song?.name ?: "Now Playing"
        val imageRes = song?.imageRes ?: R.drawable.group
        val duration = player.duration.takeIf { it > 0 } ?: 1L
        val progress = ((player.currentPosition.toFloat() / duration) * 100).toInt()

        val notification = Notification.createNotification(
            context = this,
            isPlaying = player.isPlaying,
            songTitle = title,
            imageRes = imageRes,
            progress = progress
        )

        startForeground(NOTIFICATION_ID, notification)
        handler.post(updateNotificationRunnable)

        return START_STICKY
    }

    override fun onDestroy() {
        handler.removeCallbacks(updateNotificationRunnable)
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

