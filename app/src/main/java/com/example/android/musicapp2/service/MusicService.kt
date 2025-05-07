package com.example.android.musicapp2.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.example.android.musicapp2.widget.MyMusicWidget
import com.example.android.musicapp2.utils.PlayerManager

class MusicService : Service() {

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val action = intent?.action ?: return START_NOT_STICKY
        val player = PlayerManager.getInstance(applicationContext)

        when (action) {
            MyMusicWidget.ACTION_PLAY_PAUSE -> {
                if (player.isPlaying()) {
                    player.pause()
                } else {
                    if (player.getPlaylistSize() == 0) {
                        player.setPlaylist(com.example.android.musicapp2.repository.DataRepository().getData())
                        player.play(0)
                    } else {
                        player.play(player.currentIndex.takeIf { it >= 0 } ?: 0)
                    }
                }
            }

            MyMusicWidget.ACTION_NEXT -> player.playNext()
            MyMusicWidget.ACTION_PREV -> player.playPrevious()
            MyMusicWidget.ACTION_LIKE -> {

            }
        }

        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null
}





