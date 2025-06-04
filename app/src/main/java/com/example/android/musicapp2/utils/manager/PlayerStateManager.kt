package com.example.android.musicapp2.utils.manager

import android.content.Context

object PlayerStateManager {

    private const val PREF_NAME = "player_prefs"
    private const val KEY_IS_PLAYING = "is_playing"
    private const val KEY_IS_LIKED = "is_liked"
    private const val KEY_SONG_TITLE = "song_title"
    private const val KEY_SONG_ARTIST = "song_artist"

    fun setPlaying(context: Context, isPlaying: Boolean) {
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            .edit().putBoolean(KEY_IS_PLAYING, isPlaying).apply()
    }

    fun isPlaying(context: Context): Boolean {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            .getBoolean(KEY_IS_PLAYING, false)
    }

    fun isLiked(context: Context): Boolean {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            .getBoolean(KEY_IS_LIKED, false)
    }

    fun setLiked(context: Context, liked: Boolean) {
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            .edit().putBoolean(KEY_IS_LIKED, liked).apply()
    }

    fun toggleLiked(context: Context) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val current = prefs.getBoolean(KEY_IS_LIKED, false)
        prefs.edit().putBoolean(KEY_IS_LIKED, !current).apply()
    }

    fun setCurrentSongTitle(context: Context, title: String) {
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            .edit().putString(KEY_SONG_TITLE, title).apply()
    }

    fun setCurrentArtist(context: Context, artist: String) {
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            .edit().putString(KEY_SONG_ARTIST, artist).apply()
    }

    fun getCurrentSongTitle(context: Context): String {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            .getString(KEY_SONG_TITLE, "Lo-Fi Chime") ?: "Lo-Fi Chime"
    }

    fun getCurrentArtist(context: Context): String {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            .getString(KEY_SONG_ARTIST, "Dream Tone") ?: "Dream Tone"
    }
}
