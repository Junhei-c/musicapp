package com.example.android.musicapp2.utils

import android.content.Context
import android.media.MediaPlayer

class AudioPlayer {

    private var mediaPlayer: MediaPlayer? = null

    fun play(context: Context, url: String) {
        stop()
        mediaPlayer = MediaPlayer().apply {
            setDataSource(url)
            setOnPreparedListener { it.start() }
            prepareAsync()
        }
    }

    fun stop() {
        mediaPlayer?.release()
        mediaPlayer = null
    }
}

