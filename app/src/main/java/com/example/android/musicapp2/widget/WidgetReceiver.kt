package com.example.android.musicapp2.widget

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.android.musicapp2.service.MusicService

class WidgetReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {
        intent?.action?.let { action ->
            val serviceIntent = Intent(context, MusicService::class.java).apply {
                this.action = action
            }
            context.startService(serviceIntent)
        }
    }

    companion object {
        const val ACTION_PLAY = "com.example.android.musicapp2.ACTION_PLAY"
        const val ACTION_NEXT = "com.example.android.musicapp2.ACTION_NEXT"
        const val ACTION_PREV = "com.example.android.musicapp2.ACTION_PREV"
    }
}

