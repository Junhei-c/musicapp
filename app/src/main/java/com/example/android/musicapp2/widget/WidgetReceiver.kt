package com.example.android.musicapp2.widget

import android.appwidget.AppWidgetManager
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import com.example.android.musicapp2.service.MusicService
import com.example.android.musicapp2.utils.PlayerStateManager

class  WidgetReceiver : BroadcastReceiver() {

    companion object {
        const val ACTION_PLAY = "com.example.android.musicapp2.ACTION_PLAY"
        const val ACTION_NEXT = "com.example.android.musicapp2.ACTION_NEXT"
        const val ACTION_PREV = "com.example.android.musicapp2.ACTION_PREV"
        const val ACTION_LIKE = "com.example.android.musicapp2.ACTION_LIKE"
    }

    override fun onReceive(context: Context, intent: Intent?) {
        intent?.action?.let { action ->
            if (action == ACTION_LIKE) {
                PlayerStateManager.toggleLiked(context)
                val updateIntent = Intent(context, NowPlayingWidget::class.java).apply {
                    this.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
                }
                val widgetManager = AppWidgetManager.getInstance(context)
                val widgetIds = widgetManager.getAppWidgetIds(ComponentName(context, NowPlayingWidget::class.java))
                updateIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, widgetIds)
                context.sendBroadcast(updateIntent)
                return
            }
            val serviceIntent = Intent(context, MusicService::class.java).apply {
                this.action = action
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(serviceIntent)
            } else {
                context.startService(serviceIntent)
            }
        }
    }
}


