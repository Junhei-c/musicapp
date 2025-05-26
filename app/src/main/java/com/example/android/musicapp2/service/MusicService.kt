package com.example.android.musicapp2.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
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
        startAsForegroundService()

        when (intent?.action) {
            com.example.android.musicapp2.widget.MyMusicWidget.ACTION_PLAY_PAUSE ->
                MusicController.togglePlayback(this)

            com.example.android.musicapp2.widget.MyMusicWidget.ACTION_NEXT ->
                MusicController.playNext(this)

            com.example.android.musicapp2.widget.MyMusicWidget.ACTION_PREV ->
                MusicController.playPrevious(this)

            com.example.android.musicapp2.widget.MyMusicWidget.ACTION_LIKE ->
                toggleLike()

            com.example.android.musicapp2.widget.MyMusicWidget.ACTION_MODE1 -> {
                MusicController.playByMode(this, 0)
                ModeStateManager.selectedMode = 0
            }

            com.example.android.musicapp2.widget.MyMusicWidget.ACTION_MODE2 -> {
                MusicController.playByMode(this, 1)
                ModeStateManager.selectedMode = 1
            }

            com.example.android.musicapp2.widget.MyMusicWidget.ACTION_MODE3 -> {
                MusicController.playByMode(this, 2)
                ModeStateManager.selectedMode = 2
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













