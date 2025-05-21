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
import com.example.android.musicapp2.utils.PlayerStateManager
import com.example.android.musicapp2.widget.MyMusicWidget
import com.example.android.musicapp2.widget.CircleWidget

class MusicService : Service() {

    companion object {
        val likedSongs = mutableSetOf<Int>()
    }

    private lateinit var playerManager: PlayerManager
    private var selectedMode = -1

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        initializeVideoPlayback()
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
            "REFRESH_WIDGET" -> {}
        }

        Handler(Looper.getMainLooper()).postDelayed({
            updateAllWidgets()
            updateCircleWidget()
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
        val song = playerManager.getCurrentData()
        val isPlaying = playerManager.isPlaying()
        val progress = playerManager.getPlaybackPercentage()
        val songTitle = PlayerStateManager.getCurrentSongTitle(this)
        val songArtist = PlayerStateManager.getCurrentArtist(this)

        ids.forEach { id ->
            val isExpanded = manager.getAppWidgetOptions(id)
                .getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT) >= 150
            val layoutId = if (isExpanded) R.layout.widget_expanded else R.layout.widget_now_playing
            val views = RemoteViews(packageName, layoutId)

            views.setTextViewText(R.id.widgetSongTitle, songTitle)
            if (!isExpanded) {
                views.setTextViewText(R.id.widgetArtist, songArtist)
            }
            song?.imageRes?.let {
                views.setImageViewResource(R.id.widgetAlbumArt, it)
            }

            val playIcon = if (isPlaying) R.drawable.pausebt else R.drawable.bigplay
            val playId = if (isExpanded) R.id.btn_play_pause else R.id.widgetPlay
            views.setImageViewResource(playId, playIcon)
            views.setProgressBar(R.id.music_progress, 100, progress, false)

            val likeIcon = if (song?.id != null && likedSongs.contains(song.id)) R.drawable.heart else R.drawable.whiteheart
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
            }

            manager.updateAppWidget(id, views)
        }
    }

    private fun updateCircleWidget() {
        val manager = AppWidgetManager.getInstance(this)
        val ids = manager.getAppWidgetIds(ComponentName(this, CircleWidget::class.java))
        val song = playerManager.getCurrentData()
        val isPlaying = playerManager.isPlaying()

        ids.forEach { id ->
            val views = RemoteViews(packageName, R.layout.widget_circle)
            views.setImageViewResource(R.id.circle_album_art, song?.imageRes ?: R.drawable.earlybirds)
            views.setImageViewResource(R.id.circle_play, if (isPlaying) R.drawable.pausebt else R.drawable.bigplay)
            views.setImageViewResource(R.id.circle_like, if (song?.id != null && likedSongs.contains(song.id)) R.drawable.heart else R.drawable.whiteheart)

            views.setOnClickPendingIntent(R.id.circle_play, MyMusicWidget.getPendingIntent(this, MyMusicWidget.ACTION_PLAY_PAUSE))
            views.setOnClickPendingIntent(R.id.circle_like, MyMusicWidget.getPendingIntent(this, MyMusicWidget.ACTION_LIKE))

            manager.updateAppWidget(id, views)
        }
    }

    private fun initializeVideoPlayback() {
        val player = PlayerManager.getInstance(this).getExoPlayer()
        val mediaItem = androidx.media3.common.MediaItem.fromUri("https://samplelib.com/lib/preview/mp4/sample-5s.mp4")
        player.setMediaItem(mediaItem)
        player.prepare()
        player.playWhenReady = true
    }

    override fun onBind(intent: Intent?): IBinder? = null
}











