package com.example.android.musicapp2.utils.datastore

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "settings")

object DataStoreManager {
    private val MODE_KEY = stringPreferencesKey("current_mode")

    suspend fun saveMode(context: Context, mode: String) {
        context.dataStore.edit { prefs ->
            prefs[MODE_KEY] = mode
        }
    }

    fun getMode(context: Context): Flow<String?> {
        return context.dataStore.data.map { prefs ->
            prefs[MODE_KEY]
        }
    }
}
