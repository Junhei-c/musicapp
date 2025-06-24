package com.example.android.musicapp2.utils.lifecycle

import androidx.media3.exoplayer.ExoPlayer
import com.example.android.musicapp2.utils.init.PlayerInitializer

object LifecycleManager {

    fun cleanUp(playerInitializer: PlayerInitializer, exoPlayer: ExoPlayer?) {
        playerInitializer.release()
        exoPlayer?.release()
    }
}
