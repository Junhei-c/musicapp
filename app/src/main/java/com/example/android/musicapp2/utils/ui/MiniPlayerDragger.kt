package com.example.android.musicapp2.utils.ui

import android.view.MotionEvent
import android.view.View.OnTouchListener
import android.widget.FrameLayout

object MiniPlayerDragger {
    fun makeDraggable(miniPlayerFrame: FrameLayout) {
        var dX = 0f
        var dY = 0f

        miniPlayerFrame.setOnTouchListener(OnTouchListener { view, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    dX = view.x - event.rawX
                    dY = view.y - event.rawY
                }
                MotionEvent.ACTION_MOVE -> {
                    view.animate()
                        .x(event.rawX + dX)
                        .y(event.rawY + dY)
                        .setDuration(0)
                        .start()
                }
            }
            true
        })
    }
}
