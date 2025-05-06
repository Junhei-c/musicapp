package com.example.android.musicapp2.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.example.android.musicapp2.widget.MyMusicWidget

class MusicService : Service() {

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            MyMusicWidget.ACTION_PLAY_PAUSE -> Log.d("MusicService", "Play/Pause clicked")
            MyMusicWidget.ACTION_NEXT -> Log.d("MusicService", "Next clicked")
            MyMusicWidget.ACTION_PREV -> Log.d("MusicService", "Previous clicked")
            MyMusicWidget.ACTION_LIKE -> Log.d("MusicService", "Like clicked")
        }
        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null
}





