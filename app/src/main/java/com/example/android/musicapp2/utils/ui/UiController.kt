package com.example.android.musicapp2.utils.ui

import android.graphics.Color
import com.example.android.musicapp2.databinding.ActivityMainBinding
import com.example.android.musicapp2.utils.extensions.hide
import com.example.android.musicapp2.utils.extensions.show
import com.google.android.material.button.MaterialButton

object UiController {

    fun showAudioUI(binding: ActivityMainBinding) {
        binding.recyclerViewSongs.show()
        binding.toolbar.show()
        binding.modeToggleGroup.show()
        binding.imageViewNowPlayingIcon.show()
        binding.buttonPlayPause.show()
        binding.progressBar.show()
        binding.textViewCurrentTitle.show()
    }

    fun hideAudioUI(binding: ActivityMainBinding) {
        binding.toolbar.hide()
        binding.imageViewNowPlayingIcon.hide()
        binding.buttonPlayPause.hide()
        binding.progressBar.hide()
        binding.textViewCurrentTitle.hide()
    }

    fun updateToggleButtonColors(binding: ActivityMainBinding, checkedId: Int) {
        val selectedColor = Color.parseColor("#11387B")
        val unselectedColor = Color.parseColor("#D1E2E7")
        val buttons = listOf(binding.buttonAudio, binding.buttonVideo)
        buttons.forEach { button ->
            button.setBackgroundColor(unselectedColor)
            button.setTextColor(Color.BLACK)
        }
        val selectedButton = binding.root.findViewById<MaterialButton>(checkedId)
        selectedButton.setBackgroundColor(selectedColor)
        selectedButton.setTextColor(Color.WHITE)
    }
}
