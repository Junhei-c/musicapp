package com.example.android.musicapp2.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.RemoteViews
import com.example.android.musicapp2.R
import com.example.android.musicapp2.repository.DataRepository
import com.example.android.musicapp2.service.MusicService

class MyMusicWidget : AppWidgetProvider() {

    companion object {
        const val ACTION_PLAY_PAUSE = "com.example.android.musicapp2.ACTION_PLAY_PAUSE"
        const val ACTION_NEXT = "com.example.android.musicapp2.ACTION_NEXT"
        const val ACTION_PREV = "com.example.android.musicapp2.ACTION_PREV"
        const val ACTION_LIKE = "com.example.android.musicapp2.ACTION_LIKE"

        @JvmStatic
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

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        for (widgetId in appWidgetIds) {
            updateWidget(context, appWidgetManager, widgetId)
        }
    }

    override fun onAppWidgetOptionsChanged(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int,
        newOptions: Bundle
    ) {
        updateWidget(context, appWidgetManager, appWidgetId)
    }

    private fun updateWidget(
        context: Context,
        appWidgetManager: AppWidgetManager,
        widgetId: Int
    ) {
        val options = appWidgetManager.getAppWidgetOptions(widgetId)
        val isExpanded = options.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT) >= 150
        val layoutRes = if (isExpanded) R.layout.widget_expanded else R.layout.widget_now_playing

        val views = RemoteViews(context.packageName, layoutRes)
        val song = DataRepository().getData().firstOrNull()

        song?.let {
            views.setTextViewText(R.id.widgetSongTitle, it.name)
            views.setImageViewResource(R.id.widgetAlbumArt, it.imageRes)
        }

        if (isExpanded) {
            views.setOnClickPendingIntent(R.id.btn_play_pause, getPendingIntent(context, ACTION_PLAY_PAUSE))
            views.setOnClickPendingIntent(R.id.btn_next, getPendingIntent(context, ACTION_NEXT))
            views.setOnClickPendingIntent(R.id.btn_prev, getPendingIntent(context, ACTION_PREV))
            views.setOnClickPendingIntent(R.id.btn_fav, getPendingIntent(context, ACTION_LIKE))
        } else {
            views.setOnClickPendingIntent(R.id.widgetPlay, getPendingIntent(context, ACTION_PLAY_PAUSE))
            views.setOnClickPendingIntent(R.id.widgetNext, getPendingIntent(context, ACTION_NEXT))
            views.setOnClickPendingIntent(R.id.widgetPrev, getPendingIntent(context, ACTION_PREV))
            views.setOnClickPendingIntent(R.id.heart, getPendingIntent(context, ACTION_LIKE))
        }

        appWidgetManager.updateAppWidget(widgetId, views)
    }
}





