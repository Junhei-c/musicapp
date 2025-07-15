package com.example.android.musicapp2.utils.lifecycle

import androidx.media3.exoplayer.ExoPlayer

object LifecycleManager {
    fun cleanUp(player: ExoPlayer) {
        player.release()
    }
}

