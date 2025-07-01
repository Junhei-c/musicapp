package com.example.android.musicapp2.utils.ui

import android.annotation.SuppressLint
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import kotlin.math.max
import kotlin.math.min

object MiniPlayerDragger {
    @SuppressLint("ClickableViewAccessibility")
    fun makeDraggable(miniPlayerFrame: FrameLayout) {
        var dX = 0f
        var dY = 0f
        var parentWidth = 0
        var parentHeight = 0

        miniPlayerFrame.setOnTouchListener { view, event ->
            val parent = view.parent as? View
            if (parent != null) {
                if (parentWidth == 0 || parentHeight == 0) {
                    parentWidth = parent.width
                    parentHeight = parent.height
                }

                when (event.actionMasked) {
                    MotionEvent.ACTION_DOWN -> {
                        dX = event.rawX - view.x
                        dY = event.rawY - view.y
                    }

                    MotionEvent.ACTION_MOVE -> {
                        var newX = event.rawX - dX
                        var newY = event.rawY - dY


                        newX = max(0f, min(newX, (parentWidth - view.width).toFloat()))
                        newY = max(0f, min(newY, (parentHeight - view.height).toFloat()))

                        view.x = newX
                        view.y = newY
                    }
                }
            }
            true
        }
    }
}
