package com.example.android.musicapp2.utils.player

import com.example.android.musicapp2.databinding.ActivityMainBinding
import com.example.android.musicapp2.utils.manager.PlayerManager

object PlayerController {

    fun setupPlayPause(binding: ActivityMainBinding, playerManager: PlayerManager, adapterNotify: (Int) -> Unit, updateUi: () -> Unit, refreshWidget: () -> Unit) {
        binding.buttonPlayPause.setOnClickListener {
            playerManager.currentIndex.takeIf { it != -1 }?.let {
                playerManager.playSongAt(it)
                updateUi()
                refreshWidget()
                adapterNotify(it)
            }
        }
    }
}
