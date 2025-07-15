package com.example.android.musicapp2.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.widget.RemoteViews
import com.example.android.musicapp2.R
import com.example.android.musicapp2.state.LikedSongsManager
import com.example.android.musicapp2.utils.manager.PlayerManager

class CircleWidget : AppWidgetProvider() {

    companion object {
        const val ACTION_PLAY = "com.example.android.musicapp2.ACTION_PLAY"
        const val ACTION_LIKE = "com.example.android.musicapp2.ACTION_LIKE"
    }

    override fun onUpdate(context: Context, manager: AppWidgetManager, ids: IntArray) {
        ids.forEach { updateWidgetUI(context, manager, it) }
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        val playerManager = PlayerManager.getInstance(context)
        val song = playerManager.getCurrentData()

        when (intent.action) {
            ACTION_PLAY -> {
                playerManager.togglePlayback(playerManager.currentIndex)
            }
            ACTION_LIKE -> {
                song?.let {
                    if (!LikedSongsManager.likedSongs.add(it.id)) {
                        LikedSongsManager.likedSongs.remove(it.id)
                    }

                }
            }
        }


        Handler(Looper.getMainLooper()).postDelayed({
            val manager = AppWidgetManager.getInstance(context)
            val ids = manager.getAppWidgetIds(ComponentName(context, CircleWidget::class.java))
            ids.forEach { updateWidgetUI(context, manager, it) }
        }, 150)
    }

    private fun updateWidgetUI(context: Context, manager: AppWidgetManager, widgetId: Int) {
        val views = RemoteViews(context.packageName, R.layout.widget_circle)
        val playerManager = PlayerManager.getInstance(context)
        val song = playerManager.getCurrentData()
        val isPlaying = playerManager.isPlaying()

        views.setImageViewResource(R.id.circle_album_art, song?.imageRes ?: R.drawable.earlybirds)
        views.setImageViewResource(
            R.id.circle_play,
            if (isPlaying) R.drawable.pausebt else R.drawable.bigplay
        )
        views.setImageViewResource(
            R.id.circle_like,
            if (song?.id != null && LikedSongsManager.likedSongs.contains(song.id))
                R.drawable.heart else R.drawable.whiteheart
        )

        views.setOnClickPendingIntent(R.id.circle_play, getPendingIntent(context, ACTION_PLAY))
        views.setOnClickPendingIntent(R.id.circle_like, getPendingIntent(context, ACTION_LIKE))

        manager.updateAppWidget(widgetId, views)
    }

    private fun getPendingIntent(context: Context, action: String): PendingIntent {
        val intent = Intent(context, CircleWidget::class.java).apply {
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








