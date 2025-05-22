package com.example.android.musicapp2.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.RemoteViews
import com.example.android.musicapp2.R
import com.example.android.musicapp2.service.MusicService

class MyMusicWidget : AppWidgetProvider() {

    companion object {
        const val ACTION_PLAY_PAUSE = "com.example.android.musicapp2.ACTION_PLAY_PAUSE"
        const val ACTION_NEXT = "com.example.android.musicapp2.ACTION_NEXT"
        const val ACTION_PREV = "com.example.android.musicapp2.ACTION_PREV"
        const val ACTION_LIKE = "com.example.android.musicapp2.ACTION_LIKE"
        const val ACTION_MODE1 = "com.example.android.musicapp2.ACTION_MODE1"
        const val ACTION_MODE2 = "com.example.android.musicapp2.ACTION_MODE2"
        const val ACTION_MODE3 = "com.example.android.musicapp2.ACTION_MODE3"

        fun getPendingIntent(context: Context, action: String): PendingIntent {
            val intent = Intent(context, MusicService::class.java).apply {
                this.action = action
            }
            return PendingIntent.getService(
                context,
                action.hashCode(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        }
    }

    override fun onUpdate(context: Context, manager: AppWidgetManager, ids: IntArray) {
        for (id in ids) updateWidget(context, manager, id)
    }

    override fun onAppWidgetOptionsChanged(context: Context, manager: AppWidgetManager, id: Int, options: Bundle) {
        updateWidget(context, manager, id)
    }

    private fun updateWidget(context: Context, manager: AppWidgetManager, id: Int) {
        val options = manager.getAppWidgetOptions(id)
        val isExpanded = options.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT) >= 150
        val layoutId = if (isExpanded) R.layout.widget_expanded else R.layout.widget_now_playing
        val views = RemoteViews(context.packageName, layoutId)


        val prefs = context.getSharedPreferences("music_prefs", Context.MODE_PRIVATE)
        val title = prefs.getString("title", "No Title")
        val artist = prefs.getString("artist", "No Artist")

        views.setTextViewText(R.id.widgetSongTitle, title)
        if (!isExpanded) {
            views.setTextViewText(R.id.widgetArtist, artist)
        }


        views.setImageViewResource(R.id.widgetAlbumArt, R.drawable.earlybirds)


        val intent = Intent(context, MusicService::class.java).apply {
            action = "REFRESH_WIDGET"
        }
        context.startService(intent)

        manager.updateAppWidget(id, views)
    }
}








