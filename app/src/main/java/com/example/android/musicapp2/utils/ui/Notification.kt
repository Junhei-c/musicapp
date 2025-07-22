package com.example.android.musicapp2.utils.ui

import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.example.android.musicapp2.R
import com.example.android.musicapp2.service.MusicService
import com.example.android.musicapp2.view.activity.MainActivity
import com.example.android.musicapp2.widget.MyMusicWidget

object Notification {

    fun createNotification(
        context: Context,
        isPlaying: Boolean,
        songTitle: String,
        imageRes: Int,
        progress: Int
    ): Notification {
        val views = RemoteViews(context.packageName, R.layout.custom_notification)
        val expandedViews = RemoteViews(context.packageName, R.layout.custom_notification_expanded)


        views.setTextViewText(R.id.title, songTitle)
        views.setTextViewText(R.id.subtext, "Now playing on MusicApp")
        views.setImageViewResource(R.id.thumbnail, imageRes)
        views.setImageViewResource(R.id.btn_play_pause, if (isPlaying) R.drawable.pausebt else R.drawable.bigplay)
        views.setOnClickPendingIntent(R.id.btn_prev, MyMusicWidget.getPendingIntent(context, MyMusicWidget.ACTION_PREV))
        views.setOnClickPendingIntent(R.id.btn_play_pause, MyMusicWidget.getPendingIntent(context, MyMusicWidget.ACTION_PLAY_PAUSE))
        views.setOnClickPendingIntent(R.id.btn_next, MyMusicWidget.getPendingIntent(context, MyMusicWidget.ACTION_NEXT))


        expandedViews.setTextViewText(R.id.expanded_title, songTitle)
        expandedViews.setTextViewText(R.id.expanded_album, songTitle)
        expandedViews.setImageViewResource(R.id.expanded_thumbnail, imageRes)
        expandedViews.setProgressBar(R.id.expanded_progress, 100, progress, false)
        expandedViews.setImageViewResource(R.id.expanded_btn_play_pause, if (isPlaying) R.drawable.pausebt else R.drawable.bigplay)
        expandedViews.setOnClickPendingIntent(R.id.expanded_btn_prev, MyMusicWidget.getPendingIntent(context, MyMusicWidget.ACTION_PREV))
        expandedViews.setOnClickPendingIntent(R.id.expanded_btn_play_pause, MyMusicWidget.getPendingIntent(context, MyMusicWidget.ACTION_PLAY_PAUSE))
        expandedViews.setOnClickPendingIntent(R.id.expanded_btn_next, MyMusicWidget.getPendingIntent(context, MyMusicWidget.ACTION_NEXT))

        val openAppIntent = Intent(context, MainActivity::class.java)
        val contentIntent = PendingIntent.getActivity(context, 0, openAppIntent, PendingIntent.FLAG_IMMUTABLE)

        return NotificationCompat.Builder(context, MusicService.CHANNEL_ID)
            .setSmallIcon(imageRes)
            .setContentIntent(contentIntent)
            .setCustomContentView(views)
            .setCustomBigContentView(expandedViews)
            .setStyle(NotificationCompat.DecoratedCustomViewStyle())
            .setOngoing(isPlaying)
            .setOnlyAlertOnce(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .build()
    }
}
