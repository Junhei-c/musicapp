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
            WidgetReceiver.ACTION_PLAY -> togglePlayPause()
            WidgetReceiver.ACTION_NEXT -> playNext()
            WidgetReceiver.ACTION_PREV -> playPrevious()
        }

        updateWidgetSongInfo()
        refreshWidget()
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

    private fun togglePlayPause() {
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

    private fun playNext() {
        if (playerManager.getPlaylistSize() > 0) {
            playerManager.playNext()
            PlayerStateManager.setPlaying(this, true)
        }
    }

    private fun playPrevious() {
        if (playerManager.getPlaylistSize() > 0) {
            playerManager.playPrevious()
            PlayerStateManager.setPlaying(this, true)
        }
    }

    private fun updateWidgetSongInfo() {
        val currentItem = playerManager.getCurrentMediaItem() ?: return
        val title = currentItem.mediaMetadata.title?.toString() ?: "Unknown"
        val artist = currentItem.mediaMetadata.artist?.toString() ?: "Unknown"

        val views = RemoteViews(packageName, R.layout.widget_now_playing).apply {
            setTextViewText(R.id.widgetSongTitle, title)
            setTextViewText(R.id.widgetArtist, artist)
        }

        val widgetManager = AppWidgetManager.getInstance(this)
        val widgetIds = widgetManager.getAppWidgetIds(ComponentName(this, NowPlayingWidget::class.java))
        widgetManager.updateAppWidget(widgetIds, views)
    }

    private fun refreshWidget() {
        val intent = Intent(this, NowPlayingWidget::class.java).apply {
            action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
        }
        val widgetManager = AppWidgetManager.getInstance(this)
        val widgetIds = widgetManager.getAppWidgetIds(ComponentName(this, NowPlayingWidget::class.java))
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, widgetIds)
        sendBroadcast(intent)
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



