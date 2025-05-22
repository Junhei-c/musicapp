package com.example.android.musicapp2.state

import androidx.lifecycle.LiveData

object ModeStateManager {
    var selectedMode: Int = -1

    fun syncFromLiveData(liveData: LiveData<Int>) {
        liveData.observeForever { mode ->
            selectedMode = mode ?: -1
        }
    }
}
