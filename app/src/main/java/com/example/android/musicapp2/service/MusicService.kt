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
import androidx.core.app.NotificationCompat
import com.example.android.musicapp2.R
import com.example.android.musicapp2.controller.MusicController
import com.example.android.musicapp2.state.ModeStateManager
import com.example.android.musicapp2.widget.WidgetUpdater
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MusicService : Service() {

    companion object {
        const val NOTIFICATION_ID = 1
        const val CHANNEL_ID = "music_service_channel"
        val likedSongs = mutableSetOf<Int>()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("MusicService", "Service started with intent: ${intent?.action}")
        startAsForegroundService()

        when (intent?.action) {
            com.example.android.musicapp2.widget.MyMusicWidget.ACTION_PLAY_PAUSE -> {
                Log.d("MusicService", "ACTION_PLAY_PAUSE received")
                MusicController.togglePlayback(this)
            }

            com.example.android.musicapp2.widget.MyMusicWidget.ACTION_NEXT -> {
                Log.d("MusicService", "ACTION_NEXT received")
                MusicController.playNext(this)
            }

            com.example.android.musicapp2.widget.MyMusicWidget.ACTION_PREV -> {
                Log.d("MusicService", "ACTION_PREV received")
                MusicController.playPrevious(this)
            }

            com.example.android.musicapp2.widget.MyMusicWidget.ACTION_LIKE -> {
                Log.d("MusicService", "ACTION_LIKE received")
                toggleLike()
            }

            com.example.android.musicapp2.widget.MyMusicWidget.ACTION_MODE1 -> {
                Log.d("MusicService", "ACTION_MODE1 received")
                MusicController.playByMode(this, 0)
                ModeStateManager.selectedMode = 0
            }

            com.example.android.musicapp2.widget.MyMusicWidget.ACTION_MODE2 -> {
                Log.d("MusicService", "ACTION_MODE2 received")
                MusicController.playByMode(this, 1)
                ModeStateManager.selectedMode = 1
            }

            com.example.android.musicapp2.widget.MyMusicWidget.ACTION_MODE3 -> {
                Log.d("MusicService", "ACTION_MODE3 received")
                MusicController.playByMode(this, 2)
                ModeStateManager.selectedMode = 2
            }

            "REFRESH_WIDGET" -> {
                Log.d("MusicService", "REFRESH_WIDGET received")
                // Let it fall through to widget update below
            }
        }

        CoroutineScope(Dispatchers.IO).launch {
            withContext(Dispatchers.Main) {
                WidgetUpdater.updateStandard(this@MusicService)
                WidgetUpdater.updateCircle(this@MusicService)
            }
        }

        return START_NOT_STICKY
    }

    private fun startAsForegroundService() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Music Playback",
                NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }

        val notification: Notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Music Player")
            .setContentText("Playing music")
            .setSmallIcon(R.drawable.group)
            .setOngoing(true)
            .build()

        startForeground(NOTIFICATION_ID, notification)
    }

    private fun toggleLike() {
        val song = MusicController.getCurrentSong(this)
        song?.let {
            if (!likedSongs.add(it.id)) likedSongs.remove(it.id)
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
