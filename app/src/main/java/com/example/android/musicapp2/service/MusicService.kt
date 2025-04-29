package com.example.android.musicapp2.service

import android.app.Service
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Intent
import android.os.IBinder
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


    private fun refreshWidget() {
        val ids = AppWidgetManager.getInstance(this)
            .getAppWidgetIds(ComponentName(this, NowPlayingWidget::class.java))
        val intent = Intent(this, NowPlayingWidget::class.java).apply {
            action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
            putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids)
        }
        sendBroadcast(intent)
    }
}


