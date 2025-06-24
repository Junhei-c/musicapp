package com.example.android.musicapp2.utils.mode

import android.content.Context
import androidx.lifecycle.LifecycleCoroutineScope
import com.example.android.musicapp2.databinding.ActivityMainBinding
import com.example.android.musicapp2.model.MediaTypeEnum
import com.example.android.musicapp2.utils.datastore.DataStoreManager
import com.example.android.musicapp2.viewmodel.MainViewModel
import kotlinx.coroutines.launch

object ModeToggleHandler {
    fun initMode(context: Context, viewModel: MainViewModel, scope: LifecycleCoroutineScope, binding: ActivityMainBinding, onModeSet: (MediaTypeEnum, Int) -> Unit) {
        scope.launch {
            DataStoreManager.getMode(context).collect { savedMode ->
                val mode = if (savedMode == "VIDEO") MediaTypeEnum.VIDEO else MediaTypeEnum.AUDIO
                val buttonId = if (mode == MediaTypeEnum.AUDIO) com.example.android.musicapp2.R.id.buttonAudio else com.example.android.musicapp2.R.id.buttonVideo
                onModeSet(mode, buttonId)
                viewModel.filterDataByType(mode)
            }
        }
    }

    fun handleToggle(
        checkedId: Int,
        currentMode: MediaTypeEnum,
        onModeChanged: (MediaTypeEnum) -> Unit,
        saveMode: suspend (String) -> Unit,
        scope: LifecycleCoroutineScope
    ) {
        val newMode = if (checkedId == com.example.android.musicapp2.R.id.buttonAudio) MediaTypeEnum.AUDIO else MediaTypeEnum.VIDEO
        if (newMode != currentMode) {
            onModeChanged(newMode)
            scope.launch { saveMode(newMode.name) }
        }
    }
}

