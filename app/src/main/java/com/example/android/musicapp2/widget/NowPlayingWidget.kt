package com.example.android.musicapp2.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.example.android.musicapp2.R

class NowPlayingWidget : AppWidgetProvider() {

    companion object {
        const val ACTION_PLAY = "com.example.android.musicapp2.ACTION_PLAY"
        const val ACTION_PREV = "com.example.android.musicapp2.ACTION_PREV"
        const val ACTION_NEXT = "com.example.android.musicapp2.ACTION_NEXT"
    }

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        for (appWidgetId in appWidgetIds) {
            val views = RemoteViews(context.packageName, R.layout.widget_now_playing)

            views.setOnClickPendingIntent(R.id.widgetPlay, getPendingIntent(context, ACTION_PLAY))
            views.setOnClickPendingIntent(R.id.widgetPrev, getPendingIntent(context, ACTION_PREV))
            views.setOnClickPendingIntent(R.id.widgetNext, getPendingIntent(context, ACTION_NEXT))

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        // Here you can integrate with PlayerManager if needed
    }

    private fun getPendingIntent(context: Context, action: String): PendingIntent {
        val intent = Intent(context, NowPlayingWidget::class.java).apply { this.action = action }
        return PendingIntent.getBroadcast(context, action.hashCode(), intent, PendingIntent.FLAG_IMMUTABLE)
    }
}
