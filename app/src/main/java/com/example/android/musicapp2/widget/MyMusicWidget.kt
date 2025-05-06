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

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        intent.action?.let { action ->
            val serviceIntent = Intent(context, MusicService::class.java).apply {
                this.action = action
            }
            context.startService(serviceIntent)
        }
    }

    private fun updateWidget(
        context: Context,
        appWidgetManager: AppWidgetManager,
        widgetId: Int
    ) {
        val options = appWidgetManager.getAppWidgetOptions(widgetId)
        val minHeight = options.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT)
        val layoutRes = if (minHeight >= 150) R.layout.widget_expanded else R.layout.widget_now_playing

        val data = DataRepository().getData().firstOrNull()
        val views = RemoteViews(context.packageName, layoutRes)

        data?.let {
            views.setTextViewText(R.id.widgetSongTitle, it.name)
            views.setImageViewResource(R.id.widgetAlbumArt, it.imageRes)
        }

        views.setOnClickPendingIntent(R.id.widgetPlay, getPendingIntent(context, ACTION_PLAY_PAUSE))
        views.setOnClickPendingIntent(R.id.widgetNext, getPendingIntent(context, ACTION_NEXT))
        views.setOnClickPendingIntent(R.id.widgetPrev, getPendingIntent(context, ACTION_PREV))
        views.setOnClickPendingIntent(R.id.heart, getPendingIntent(context, ACTION_LIKE))

        appWidgetManager.updateAppWidget(widgetId, views)
    }

    private fun getPendingIntent(context: Context, action: String): PendingIntent {
        val intent = Intent(context, MyMusicWidget::class.java).apply {
            this.action = action
        }
        return PendingIntent.getBroadcast(
            context,
            action.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }
}


