package com.example.android.musicapp2.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.example.android.musicapp2.R
import com.example.android.musicapp2.utils.PlayerStateManager

class NowPlayingWidget : AppWidgetProvider() {

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        for (appWidgetId in appWidgetIds) {
            val views = RemoteViews(context.packageName, R.layout.widget_now_playing)

            val isPlaying = PlayerStateManager.isPlaying(context)

            val playIcon = if (isPlaying) R.drawable.pausebt else R.drawable.bigplay
            views.setImageViewResource(R.id.widgetPlay, playIcon)

            views.setOnClickPendingIntent(R.id.widgetPlay, getPendingIntent(context, WidgetReceiver.ACTION_PLAY))
            views.setOnClickPendingIntent(R.id.widgetNext, getPendingIntent(context, WidgetReceiver.ACTION_NEXT))
            views.setOnClickPendingIntent(R.id.widgetPrev, getPendingIntent(context, WidgetReceiver.ACTION_PREV))

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }

    private fun getPendingIntent(context: Context, action: String): PendingIntent {
        val intent = Intent(context, WidgetReceiver::class.java).apply {
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





