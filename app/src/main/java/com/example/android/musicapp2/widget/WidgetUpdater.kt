package com.example.android.musicapp2.widget

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.widget.RemoteViews
import com.example.android.musicapp2.R
import com.example.android.musicapp2.controller.MusicController
import com.example.android.musicapp2.state.LikedSongsManager
import com.example.android.musicapp2.state.ModeStateManager

object WidgetUpdater {

    fun updateStandard(context: Context) {
        val manager = AppWidgetManager.getInstance(context)
        val ids = manager.getAppWidgetIds(ComponentName(context, MyMusicWidget::class.java))
        val song = MusicController.getCurrentSong(context)
        val isPlaying = MusicController.isPlaying(context)
        val progress = MusicController.getProgress(context)
        val songTitle = song?.name ?: "No Title"
        val songArtist = "Unknown Artist"

        ids.forEach { id ->
            val isExpanded = manager.getAppWidgetOptions(id)
                .getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT) >= 150
            val layoutId = if (isExpanded) R.layout.widget_expanded else R.layout.widget_now_playing
            val views = RemoteViews(context.packageName, layoutId)

            views.setTextViewText(R.id.widgetSongTitle, songTitle)
            if (!isExpanded) views.setTextViewText(R.id.widgetArtist, songArtist)
            song?.imageRes?.let { views.setImageViewResource(R.id.widgetAlbumArt, it) }

            val playIcon = if (isPlaying) R.drawable.pausebt else R.drawable.bigplay
            val playId = if (isExpanded) R.id.btn_play_pause else R.id.widgetPlay
            views.setImageViewResource(playId, playIcon)
            views.setProgressBar(R.id.music_progress, 100, progress, false)

            val likeIcon = if (song?.id != null && LikedSongsManager.likedSongs.contains(song.id)) R.drawable.heart else R.drawable.whiteheart
            val likeId = if (isExpanded) R.id.btn_fav else R.id.heart
            views.setImageViewResource(likeId, likeIcon)

            views.setOnClickPendingIntent(playId, MyMusicWidget.getPendingIntent(context, MyMusicWidget.ACTION_PLAY_PAUSE))
            views.setOnClickPendingIntent(
                if (isExpanded) R.id.btn_next else R.id.widgetNext,
                MyMusicWidget.getPendingIntent(context, MyMusicWidget.ACTION_NEXT)
            )
            views.setOnClickPendingIntent(
                if (isExpanded) R.id.btn_prev else R.id.widgetPrev,
                MyMusicWidget.getPendingIntent(context, MyMusicWidget.ACTION_PREV)
            )
            views.setOnClickPendingIntent(likeId, MyMusicWidget.getPendingIntent(context, MyMusicWidget.ACTION_LIKE))

            if (isExpanded) {
                val selectedMode = ModeStateManager.selectedMode

                views.setOnClickPendingIntent(R.id.btn_mode1, MyMusicWidget.getPendingIntent(context, MyMusicWidget.ACTION_MODE1))
                views.setOnClickPendingIntent(R.id.btn_mode2, MyMusicWidget.getPendingIntent(context, MyMusicWidget.ACTION_MODE2))
                views.setOnClickPendingIntent(R.id.btn_mode3, MyMusicWidget.getPendingIntent(context, MyMusicWidget.ACTION_MODE3))

                views.setInt(R.id.btn_mode1, "setBackgroundResource",
                    if (selectedMode == 0) R.drawable.bg_mode_selected else R.drawable.bg_mode_unselected)
                views.setInt(R.id.btn_mode2, "setBackgroundResource",
                    if (selectedMode == 1) R.drawable.bg_mode_selected else R.drawable.bg_mode_unselected)
                views.setInt(R.id.btn_mode3, "setBackgroundResource",
                    if (selectedMode == 2) R.drawable.bg_mode_selected else R.drawable.bg_mode_unselected)
            }

            manager.updateAppWidget(id, views)
        }
    }

    fun updateCircle(context: Context) {
        val manager = AppWidgetManager.getInstance(context)
        val ids = manager.getAppWidgetIds(ComponentName(context, CircleWidget::class.java))
        val song = MusicController.getCurrentSong(context)
        val isPlaying = MusicController.isPlaying(context)

        ids.forEach { id ->
            val views = RemoteViews(context.packageName, R.layout.widget_circle)
            views.setImageViewResource(R.id.circle_album_art, song?.imageRes ?: R.drawable.earlybirds)
            views.setImageViewResource(R.id.circle_play, if (isPlaying) R.drawable.pausebt else R.drawable.bigplay)
            views.setImageViewResource(R.id.circle_like, if (song?.id != null && LikedSongsManager.likedSongs.contains(song.id)) R.drawable.redheart else R.drawable.heart)

            views.setOnClickPendingIntent(R.id.circle_play, MyMusicWidget.getPendingIntent(context, MyMusicWidget.ACTION_PLAY_PAUSE))
            views.setOnClickPendingIntent(R.id.circle_like, MyMusicWidget.getPendingIntent(context, MyMusicWidget.ACTION_LIKE))

            manager.updateAppWidget(id, views)
        }
    }
}