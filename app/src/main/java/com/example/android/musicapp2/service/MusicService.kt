package com.example.android.musicapp2.service

import android.app.Service
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Intent
import android.os.IBinder
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.example.android.musicapp2.R
import com.example.android.musicapp2.utils.PlayerManager
import com.example.android.musicapp2.utils.PlayerStateManager
import com.example.android.musicapp2.widget.NowPlayingWidget
import com.example.android.musicapp2.widget.WidgetReceiver

class MusicService : Service() {

    private lateinit var playerManager: PlayerManager

    override fun onCreate() {
        super.onCreate()
        playerManager = PlayerManager(this)
        preloadPlaylist()
        startForegroundNotification()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (playerManager.getPlaylistSize() == 0) {
            preloadPlaylist()
        }

        when (intent?.action) {
            WidgetReceiver.ACTION_PLAY -> {
                if (playerManager.getPlaylistSize() > 0) {
                    if (playerManager.isPlaying()) {
                        playerManager.pause()
                        PlayerStateManager.setPlaying(this, false)
                    } else {
                        playerManager.play(playerManager.currentIndex.takeIf { it >= 0 } ?: 0)
                        PlayerStateManager.setPlaying(this, true)
                    }
                }
            }
            WidgetReceiver.ACTION_NEXT -> {
                if (playerManager.getPlaylistSize() > 0) {
                    playerManager.playNext()
                    PlayerStateManager.setPlaying(this, true)
                }
            }
            WidgetReceiver.ACTION_PREV -> {
                if (playerManager.getPlaylistSize() > 0) {
                    playerManager.playPrevious()
                    PlayerStateManager.setPlaying(this, true)
                }
            }

        }

        updateWidgetSongInfo()
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun preloadPlaylist() {
        val urls = listOf(
            "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3",
            "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-2.mp3",
            "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-3.mp3"
        )
        playerManager.setPlaylist(urls)
    }

    private fun updateWidgetSongInfo() {
        val currentItem = playerManager.getCurrentMediaItem() ?: return
        val title = currentItem.mediaMetadata.title?.toString() ?: "Unknown"
        val artist = currentItem.mediaMetadata.artist?.toString() ?: "Unknown"

        val views = RemoteViews(packageName, R.layout.widget_now_playing)
        views.setTextViewText(R.id.widgetSongTitle, title)
        views.setTextViewText(R.id.widgetArtist, artist)

        val widgetManager = AppWidgetManager.getInstance(this)
        val widgetIds = widgetManager.getAppWidgetIds(ComponentName(this, NowPlayingWidget::class.java))
        widgetManager.updateAppWidget(widgetIds, views)
    }

    private fun startForegroundNotification() {
        val channelId = "music_player_channel"

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val channel = android.app.NotificationChannel(
                channelId,
                "Music Player",
                android.app.NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(android.app.NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("Music Player")
            .setContentText("Playing music...")
            .setSmallIcon(R.drawable.group)
            .build()

        startForeground(1, notification)
    }
}


