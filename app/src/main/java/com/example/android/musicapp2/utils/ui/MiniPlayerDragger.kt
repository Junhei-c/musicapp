package com.example.android.musicapp2.utils.ui

import android.annotation.SuppressLint
import android.view.MotionEvent
import android.view.View

object MiniPlayerDragger {
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
}



