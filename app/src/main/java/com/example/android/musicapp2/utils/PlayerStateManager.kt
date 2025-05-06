package com.example.android.musicapp2.utils

import android.content.Context

object PlayerStateManager {

    private const val PREF_NAME = "player_prefs"
    private const val KEY_IS_PLAYING = "is_playing"
    private const val KEY_IS_LIKED = "is_liked"

    fun setPlaying(context: Context, isPlaying: Boolean) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.edit().putBoolean(KEY_IS_PLAYING, isPlaying).apply()
    }

    fun isPlaying(context: Context): Boolean {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return prefs.getBoolean(KEY_IS_PLAYING, false)
    }

    fun isLiked(context: Context): Boolean {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return prefs.getBoolean(KEY_IS_LIKED, false)
    }

    fun toggleLiked(context: Context) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val current = prefs.getBoolean(KEY_IS_LIKED, false)
        prefs.edit().putBoolean(KEY_IS_LIKED, !current).apply()
    }
}