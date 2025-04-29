package com.example.android.musicapp2.utils

import android.content.Context

object PlayerStateManager {

    private const val PREF_NAME = "player_prefs"
    private const val KEY_IS_PLAYING = "is_playing"

    fun setPlaying(context: Context, isPlaying: Boolean) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.edit().putBoolean(KEY_IS_PLAYING, isPlaying).apply()
    }

    fun isPlaying(context: Context): Boolean {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return prefs.getBoolean(KEY_IS_PLAYING, false)
    }
}
