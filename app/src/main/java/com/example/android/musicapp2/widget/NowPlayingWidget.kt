package com.example.android.musicapp2.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.RemoteViews
import com.example.android.musicapp2.R
import com.example.android.musicapp2.utils.manager.PlayerStateManager

class NowPlayingWidget : AppWidgetProvider() {

    override fun onUpdate(context: Context, manager: AppWidgetManager, widgetIds: IntArray) {
        widgetIds.forEach { updateWidget(context, manager, it) }
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)

        val validActions = listOf(
            AppWidgetManager.ACTION_APPWIDGET_UPDATE,
            WidgetReceiver.ACTION_PLAY,
            WidgetReceiver.ACTION_NEXT,
            WidgetReceiver.ACTION_PREV,
            WidgetReceiver.ACTION_LIKE
        )

        if (intent.action in validActions) {
            val manager = AppWidgetManager.getInstance(context)
            val ids = manager.getAppWidgetIds(ComponentName(context, NowPlayingWidget::class.java))
            onUpdate(context, manager, ids)
        }
    }

    override fun onAppWidgetOptionsChanged(
        context: Context,
        manager: AppWidgetManager,
        widgetId: Int,
        newOptions: Bundle
    ) {
        super.onAppWidgetOptionsChanged(context, manager, widgetId, newOptions)
        updateWidget(context, manager, widgetId)
    }

    private fun updateWidget(context: Context, manager: AppWidgetManager, widgetId: Int) {
        val views = RemoteViews(context.packageName, R.layout.widget_now_playing)

        val isPlaying = PlayerStateManager.isPlaying(context)
        val isLiked = PlayerStateManager.isLiked(context)

        views.setImageViewResource(R.id.widgetPlay, if (isPlaying) R.drawable.pausebt else R.drawable.bigplay)
        views.setImageViewResource(R.id.heart, if (isLiked) R.drawable.heart else R.drawable.whiteheart)

        views.setOnClickPendingIntent(R.id.widgetPlay, pendingIntent(context, WidgetReceiver.ACTION_PLAY))
        views.setOnClickPendingIntent(R.id.widgetNext, pendingIntent(context, WidgetReceiver.ACTION_NEXT))
        views.setOnClickPendingIntent(R.id.widgetPrev, pendingIntent(context, WidgetReceiver.ACTION_PREV))
        views.setOnClickPendingIntent(R.id.heart, pendingIntent(context, WidgetReceiver.ACTION_LIKE))
        views.setTextViewText(R.id.widgetSongTitle, PlayerStateManager.getCurrentSongTitle(context))
        views.setTextViewText(R.id.widgetArtist, PlayerStateManager.getCurrentArtist(context))


        manager.updateAppWidget(widgetId, views)
    }

    private fun pendingIntent(context: Context, action: String): PendingIntent {
        val intent = Intent(context, WidgetReceiver::class.java).apply { this.action = action }
        return PendingIntent.getBroadcast(
            context,
            action.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }
}







