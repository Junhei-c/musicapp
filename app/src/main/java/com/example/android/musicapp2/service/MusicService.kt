package com.example.android.musicapp2.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import com.example.android.musicapp2.controller.MusicController
import com.example.android.musicapp2.state.ModeStateManager
import com.example.android.musicapp2.utils.ui.NotificationHelper
import com.example.android.musicapp2.widget.MyMusicWidget
import com.example.android.musicapp2.widget.WidgetUpdater
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MusicService : Service() {

    companion object {
        const val NOTIFICATION_ID = 1
        const val CHANNEL_ID = "music_service_channel"
        const val ACTION_START_FOREGROUND = "start_foreground"
        val likedSongs = mutableSetOf<Int>()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("MusicService", "Service started with intent: ${intent?.action}")
        createNotificationChannel()

        when (intent?.action) {
            ACTION_START_FOREGROUND -> {
                val isPlaying = intent.getBooleanExtra("isPlaying", false)
                val title = intent.getStringExtra("title") ?: "Now Playing"
                val notification: Notification =
                    NotificationHelper.createNotification(this, isPlaying, title)
                startForeground(NOTIFICATION_ID, notification)
            }

            MyMusicWidget.ACTION_PLAY_PAUSE -> {
                MusicController.togglePlayback(this)
            }

            MyMusicWidget.ACTION_NEXT -> {
                MusicController.playNext(this)
            }

            MyMusicWidget.ACTION_PREV -> {
                MusicController.playPrevious(this)
            }

            MyMusicWidget.ACTION_LIKE -> {
                toggleLike()
            }

            MyMusicWidget.ACTION_MODE1 -> {
                MusicController.playByMode(this, 0)
                ModeStateManager.selectedMode = 0
            }

            MyMusicWidget.ACTION_MODE2 -> {
                MusicController.playByMode(this, 1)
                ModeStateManager.selectedMode = 1
            }

            MyMusicWidget.ACTION_MODE3 -> {
                MusicController.playByMode(this, 2)
                ModeStateManager.selectedMode = 2
            }

            "REFRESH_WIDGET" -> {
                // Just update widgets
            }
        }

        CoroutineScope(Dispatchers.IO).launch {
            withContext(Dispatchers.Main) {
                WidgetUpdater.updateStandard(this@MusicService)
                WidgetUpdater.updateCircle(this@MusicService)
            }
        }

        return START_STICKY
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Music Playback",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Notification channel for music controls"
            }
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    private fun toggleLike() {
        val song = MusicController.getCurrentSong(this)
        song?.let {
            if (!likedSongs.add(it.id)) likedSongs.remove(it.id)
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null
}


