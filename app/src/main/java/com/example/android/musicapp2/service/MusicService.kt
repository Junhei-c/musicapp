package com.example.android.musicapp2.service

import android.app.Service
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.widget.RemoteViews
import com.example.android.musicapp2.R
import com.example.android.musicapp2.utils.PlayerManager
import com.example.android.musicapp2.widget.MyMusicWidget

class MusicService : Service() {

    private lateinit var playerManager: PlayerManager
    private val likedSongs = mutableSetOf<Int>()
    private var selectedMode = -1

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        playerManager = PlayerManager.getInstance(this)

        when (intent?.action) {
            MyMusicWidget.ACTION_PLAY_PAUSE -> playerManager.togglePlayback(playerManager.currentIndex)
            MyMusicWidget.ACTION_NEXT -> playerManager.playNext()
            MyMusicWidget.ACTION_PREV -> playerManager.playPrevious()
            MyMusicWidget.ACTION_LIKE -> toggleLike()
            MyMusicWidget.ACTION_MODE1 -> {
                playerManager.play(2)
                selectedMode = 0
            }
            MyMusicWidget.ACTION_MODE2 -> {
                playerManager.play(0)
                selectedMode = 1
            }
            MyMusicWidget.ACTION_MODE3 -> {
                playerManager.play(1)
                selectedMode = 2
            }
        }

        Handler(Looper.getMainLooper()).postDelayed({
            updateAllWidgets()
        }, 100)

        return START_NOT_STICKY
    }

    private fun toggleLike() {
        playerManager.getCurrentData()?.let {
            if (!likedSongs.add(it.id)) likedSongs.remove(it.id)
        }
    }

    private fun updateAllWidgets() {
        val manager = AppWidgetManager.getInstance(this)
        val ids = manager.getAppWidgetIds(ComponentName(this, MyMusicWidget::class.java))

        ids.forEach { id ->
            val options = manager.getAppWidgetOptions(id)
            val isExpanded = options.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT) >= 150
            val layoutId = if (isExpanded) R.layout.widget_expanded else R.layout.widget_now_playing
            val views = RemoteViews(packageName, layoutId)

            updateWidgetUI(views, isExpanded)
            manager.updateAppWidget(id, views)
        }
    }

    private fun updateWidgetUI(views: RemoteViews, isExpanded: Boolean) {
        val song = playerManager.getCurrentData()
        val isPlaying = playerManager.isPlaying()
        val progress = playerManager.getPlaybackPercentage()


        song?.let {
            views.setTextViewText(R.id.widgetSongTitle, it.name)
            views.setImageViewResource(R.id.widgetAlbumArt, it.imageRes)
        }


        val playIcon = if (isPlaying) R.drawable.pausebt else R.drawable.bigplay
        val playId = if (isExpanded) R.id.btn_play_pause else R.id.widgetPlay
        views.setImageViewResource(playId, playIcon)
        views.setProgressBar(R.id.music_progress, 100, progress, false)


        val isLiked = song?.id?.let { likedSongs.contains(it) } ?: false
        val likeIcon = if (isLiked) R.drawable.heart else R.drawable.whiteheart
        val likeId = if (isExpanded) R.id.btn_fav else R.id.heart
        views.setImageViewResource(likeId, likeIcon)


        views.setOnClickPendingIntent(playId, MyMusicWidget.getPendingIntent(this, MyMusicWidget.ACTION_PLAY_PAUSE))
        views.setOnClickPendingIntent(
            if (isExpanded) R.id.btn_next else R.id.widgetNext,
            MyMusicWidget.getPendingIntent(this, MyMusicWidget.ACTION_NEXT)
        )
        views.setOnClickPendingIntent(
            if (isExpanded) R.id.btn_prev else R.id.widgetPrev,
            MyMusicWidget.getPendingIntent(this, MyMusicWidget.ACTION_PREV)
        )
        views.setOnClickPendingIntent(likeId, MyMusicWidget.getPendingIntent(this, MyMusicWidget.ACTION_LIKE))


        if (isExpanded) {
            views.setOnClickPendingIntent(R.id.btn_mode1, MyMusicWidget.getPendingIntent(this, MyMusicWidget.ACTION_MODE1))
            views.setOnClickPendingIntent(R.id.btn_mode2, MyMusicWidget.getPendingIntent(this, MyMusicWidget.ACTION_MODE2))
            views.setOnClickPendingIntent(R.id.btn_mode3, MyMusicWidget.getPendingIntent(this, MyMusicWidget.ACTION_MODE3))

            views.setInt(R.id.btn_mode1, "setBackgroundResource",
                if (selectedMode == 0) R.drawable.bg_mode_selected else R.drawable.bg_mode_unselected)

            views.setInt(R.id.btn_mode2, "setBackgroundResource",
                if (selectedMode == 1) R.drawable.bg_mode_selected else R.drawable.bg_mode_unselected)

            views.setInt(R.id.btn_mode3, "setBackgroundResource",
                if (selectedMode == 2) R.drawable.bg_mode_selected else R.drawable.bg_mode_unselected)
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null
}










