package com.example.android.musicapp2.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.example.android.musicapp2.R
import com.example.android.musicapp2.utils.PlayerStateManager

class NowPlayingWidget : AppWidgetProvider() {

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        for (appWidgetId in appWidgetIds) {
            updateWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)

        when (intent.action) {
            AppWidgetManager.ACTION_APPWIDGET_UPDATE,
            WidgetReceiver.ACTION_PLAY,
            WidgetReceiver.ACTION_NEXT,
            WidgetReceiver.ACTION_PREV -> {
                val appWidgetManager = AppWidgetManager.getInstance(context)
                val componentName = ComponentName(context, NowPlayingWidget::class.java)
                val appWidgetIds = appWidgetManager.getAppWidgetIds(componentName)
                onUpdate(context, appWidgetManager, appWidgetIds)
            }
        }
    }

    private fun updateWidget(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int) {
        val views = RemoteViews(context.packageName, R.layout.widget_now_playing)

        val isPlaying = PlayerStateManager.isPlaying(context)
        val playIcon = if (isPlaying) R.drawable.pausebt else R.drawable.bigplay
        views.setImageViewResource(R.id.widgetPlay, playIcon)

        views.setOnClickPendingIntent(R.id.widgetPlay, getBroadcastPendingIntent(context, WidgetReceiver.ACTION_PLAY))
        views.setOnClickPendingIntent(R.id.widgetNext, getBroadcastPendingIntent(context, WidgetReceiver.ACTION_NEXT))
        views.setOnClickPendingIntent(R.id.widgetPrev, getBroadcastPendingIntent(context, WidgetReceiver.ACTION_PREV))

        appWidgetManager.updateAppWidget(appWidgetId, views)
    }

    private fun getBroadcastPendingIntent(context: Context, action: String): PendingIntent {
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





