package com.example.android.musicapp2.utils.init

import android.os.Handler
import android.os.Looper
import com.example.android.musicapp2.model.DataModel
import com.example.android.musicapp2.utils.manager.PlayerManager

class PlayerInitializer(
    private val playerManager: PlayerManager,
    private val onUpdate: (Int, Int) -> Unit,
    private val onUiUpdate: () -> Unit
) {
    private val handler = Handler(Looper.getMainLooper())
    private val progressUpdater = object : Runnable {
        override fun run() {
            if (playerManager.isPlaying()) {
                onUiUpdate()
                handler.postDelayed(this, 1000)
            }
        }
    }

    fun initialize(songs: List<DataModel>) {
        playerManager.setPlaylist(songs)
        playerManager.setOnPlaybackChangedListener {
            val current = playerManager.currentIndex
            onUpdate(playerManager.currentIndex, current)
            handler.removeCallbacks(progressUpdater)
            if (playerManager.isPlaying()) handler.post(progressUpdater)
        }
    }

    fun release() {
        handler.removeCallbacks(progressUpdater)
        playerManager.release()
    }
}
