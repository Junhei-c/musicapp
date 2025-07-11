package com.example.android.musicapp2.utils.ui

import android.annotation.SuppressLint
import android.view.MotionEvent
import android.view.View
import com.example.android.musicapp2.databinding.ActivityMainBinding

object MiniPlayerHandler {

    /** Draggable functionality for mini and PiP player views */
    @SuppressLint("ClickableViewAccessibility")
    fun makeDraggable(view: View) {
        var dX = 0f
        var dY = 0f

        view.setOnTouchListener { v, event ->
            when (event.actionMasked) {
                MotionEvent.ACTION_DOWN -> {
                    dX = event.rawX - v.x
                    dY = event.rawY - v.y
                    v.bringToFront()
                }
                MotionEvent.ACTION_MOVE -> {
                    v.x = event.rawX - dX
                    v.y = event.rawY - dY
                }
            }
            true
        }
    }


    fun enterMiniPlayerMode(binding: ActivityMainBinding, player: androidx.media3.exoplayer.ExoPlayer) {
        binding.nowPlayingCard.visibility = View.GONE
        binding.miniPlayerFrame.visibility = View.VISIBLE
        binding.miniPlayerView.player = player
        UiController.showAudioUI(binding)
        binding.pipPlayerView.visibility = View.GONE
    }


    fun exitMiniPlayerMode(binding: ActivityMainBinding, player: androidx.media3.exoplayer.ExoPlayer) {
        binding.miniPlayerFrame.visibility = View.GONE
        binding.nowPlayingCard.visibility = View.VISIBLE
        binding.pipPlayerView.player = player
    }
}



