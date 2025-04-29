package com.example.android.musicapp2.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.example.android.musicapp2.R
import com.example.android.musicapp2.utils.PlayerManager

class NowPlayingWidget : AppWidgetProvider() {

    companion object {
        const val ACTION_PLAY_PAUSE = "com.example.android.musicapp2.ACTION_PLAY_PAUSE"
        const val ACTION_NEXT = "com.example.android.musicapp2.ACTION_NEXT"
        const val ACTION_PREV = "com.example.android.musicapp2.ACTION_PREV"

        private var playerManager: PlayerManager? = null
    }

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        playerManager = PlayerManager(context.applicationContext)

        for (widgetId in appWidgetIds) {
            val views = RemoteViews(context.packageName, R.layout.widget_now_playing)

            views.setOnClickPendingIntent(R.id.widgetPlay, getPendingIntent(context, ACTION_PLAY_PAUSE))
            views.setOnClickPendingIntent(R.id.widgetNext, getPendingIntent(context, ACTION_NEXT))
            views.setOnClickPendingIntent(R.id.widgetPrev, getPendingIntent(context, ACTION_PREV))

            appWidgetManager.updateAppWidget(widgetId, views)
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)

        if (playerManager == null) {
            playerManager = PlayerManager(context.applicationContext)
        }

        when (intent.action) {
            ACTION_PLAY_PAUSE -> {
                if (playerManager!!.isPlaying()) {
                    playerManager!!.pause()
                } else {
                    playerManager!!.play(playerManager!!.currentIndex.takeIf { it != -1 } ?: 0)
                }
            }
            ACTION_NEXT -> {
                playerManager!!.playNext()
            }
            ACTION_PREV -> {
                playerManager!!.playPrevious()
            }
        }
    }

    private fun getPendingIntent(context: Context, action: String): PendingIntent {
        val intent = Intent(context, NowPlayingWidget::class.java).apply { this.action = action }
        return PendingIntent.getBroadcast(context, action.hashCode(), intent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
    }
}

