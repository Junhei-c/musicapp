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

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        playerManager = PlayerManager.getInstance(this)

        when (intent?.action) {
            MyMusicWidget.ACTION_PLAY_PAUSE -> playerManager.togglePlayback(playerManager.currentIndex)
            MyMusicWidget.ACTION_NEXT -> playerManager.playNext()
            MyMusicWidget.ACTION_PREV -> playerManager.playPrevious()
            MyMusicWidget.ACTION_LIKE -> {
                playerManager.getCurrentData()?.let {
                    if (likedSongs.contains(it.id)) likedSongs.remove(it.id) else likedSongs.add(it.id)
                }
            }
            MyMusicWidget.ACTION_MODE1 -> playerManager.play(2)
            MyMusicWidget.ACTION_MODE2 -> playerManager.play(0)
            MyMusicWidget.ACTION_MODE3 -> playerManager.play(1)
        }

        Handler(Looper.getMainLooper()).postDelayed({ updateAllWidgets() }, 200)
        return START_NOT_STICKY
    }

    private fun updateAllWidgets() {
        val manager = AppWidgetManager.getInstance(this)
        val component = ComponentName(this, MyMusicWidget::class.java)
        val widgetIds = manager.getAppWidgetIds(component)
        val data = playerManager.getCurrentData()
        val isPlaying = playerManager.isPlaying()

        for (id in widgetIds) {
            val options = manager.getAppWidgetOptions(id)
            val isExpanded = options.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT) >= 150
            val layoutId = if (isExpanded) R.layout.widget_expanded else R.layout.widget_now_playing
            val views = RemoteViews(packageName, layoutId)

            data?.let {
                views.setTextViewText(R.id.widgetSongTitle, it.name)
                views.setImageViewResource(R.id.widgetAlbumArt, it.imageRes)
            }

            val playIcon = if (isPlaying) R.drawable.pausebt else R.drawable.bigplay
            val playId = if (isExpanded) R.id.btn_play_pause else R.id.widgetPlay
            views.setImageViewResource(playId, playIcon)
            views.setOnClickPendingIntent(playId, MyMusicWidget.getPendingIntent(this, MyMusicWidget.ACTION_PLAY_PAUSE))

            views.setOnClickPendingIntent(if (isExpanded) R.id.btn_next else R.id.widgetNext,
                MyMusicWidget.getPendingIntent(this, MyMusicWidget.ACTION_NEXT))

            views.setOnClickPendingIntent(if (isExpanded) R.id.btn_prev else R.id.widgetPrev,
                MyMusicWidget.getPendingIntent(this, MyMusicWidget.ACTION_PREV))

            val isLiked = data?.id?.let { likedSongs.contains(it) } ?: false
            val likeIcon = if (isLiked) R.drawable.heart else R.drawable.whiteheart
            val likeId = if (isExpanded) R.id.btn_fav else R.id.heart
            views.setImageViewResource(likeId, likeIcon)
            views.setOnClickPendingIntent(likeId, MyMusicWidget.getPendingIntent(this, MyMusicWidget.ACTION_LIKE))

            if (isExpanded) {
                views.setOnClickPendingIntent(R.id.btn_mode1, MyMusicWidget.getPendingIntent(this, MyMusicWidget.ACTION_MODE1))
                views.setOnClickPendingIntent(R.id.btn_mode2, MyMusicWidget.getPendingIntent(this, MyMusicWidget.ACTION_MODE2))
                views.setOnClickPendingIntent(R.id.btn_mode3, MyMusicWidget.getPendingIntent(this, MyMusicWidget.ACTION_MODE3))
            }

            manager.updateAppWidget(id, views)
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null
}






