package com.example.android.musicapp2.utils.init

import android.os.Handler
import android.os.Looper
import com.example.android.musicapp2.model.DataModel
import com.example.android.musicapp2.utils.manager.PlayerManager

class PlayerInitializer(
    private val playerManager: PlayerManager,
    private val onUpdate: (previousIndex: Int, currentIndex: Int) -> Unit,
    private val onUiUpdate: () -> Unit
) {
    private var lastKnownIndex = -1

    fun initialize(songs: List<DataModel>) {
        Handler(Looper.getMainLooper()).post {
            playerManager.setPlaylist(songs)

            playerManager.setOnPlaybackChangedListener {
                val prev = lastKnownIndex
                val curr = playerManager.currentIndex
                if (curr != prev) {
                    onUpdate(prev, curr)
                    lastKnownIndex = curr
                }
                onUiUpdate()
            }
        }
    }
}
