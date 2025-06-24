package com.example.android.musicapp2.utils.pip

import android.app.Activity
import android.app.PictureInPictureParams
import android.os.Build
import android.util.Rational
import android.view.View
import com.example.android.musicapp2.databinding.ActivityMainBinding

object PictureInPictureHelper {

    fun enterPipMode(activity: Activity, playerInitialized: Boolean, aspectRatio: Rational) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && playerInitialized) {
            val params = PictureInPictureParams.Builder()
                .setAspectRatio(aspectRatio)
                .build()
            activity.enterPictureInPictureMode(params)
        }
    }

    fun handlePipChange(binding: ActivityMainBinding, isInPipMode: Boolean, isVideoMode: Boolean) {
        if (isInPipMode) {
            binding.modeToggleGroup.visibility = View.GONE
        } else if (isVideoMode) {
            binding.modeToggleGroup.visibility = View.VISIBLE
        }
    }
}
