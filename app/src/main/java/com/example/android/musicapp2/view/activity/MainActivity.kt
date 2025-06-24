package com.example.android.musicapp2.view.activity

import android.app.PictureInPictureParams
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Rational
import android.view.View
import android.widget.FrameLayout
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.android.musicapp2.R
import com.example.android.musicapp2.databinding.ActivityMainBinding
import com.example.android.musicapp2.model.MediaTypeEnum
import com.example.android.musicapp2.repository.DataRepository
import com.example.android.musicapp2.service.MusicService
import com.example.android.musicapp2.state.ModeStateManager
import com.example.android.musicapp2.utils.datastore.DataStoreManager
import com.example.android.musicapp2.utils.extensions.hide
import com.example.android.musicapp2.utils.extensions.show
import com.example.android.musicapp2.utils.init.PlayerInitializer
import com.example.android.musicapp2.utils.manager.PlayerManager
import com.example.android.musicapp2.utils.ui.MiniPlayerDragger
import com.example.android.musicapp2.utils.ui.NowPlayingUpdater
import com.example.android.musicapp2.utils.ui.PlayerUiBinder
import com.example.android.musicapp2.view.adapter.SongAdapter
import com.example.android.musicapp2.viewmodel.MainViewModel
import com.example.android.musicapp2.viewmodel.MainViewModelFactory
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private val pipAspectRatio = Rational(16, 9)
    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: SongAdapter
    private lateinit var player: ExoPlayer
    private var playerManager: PlayerManager? = null
    private lateinit var playerInitializer: PlayerInitializer
    private var lastPlayingIndex: Int = -1
    private var selectedIndex: Int = -1
    private var currentMode: MediaTypeEnum = MediaTypeEnum.AUDIO

    private lateinit var miniPlayerFrame: FrameLayout
    private lateinit var miniPlayerView: PlayerView

    private val viewModel: MainViewModel by viewModels {
        MainViewModelFactory(DataRepository())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        miniPlayerFrame = findViewById(R.id.miniPlayerFrame)
        miniPlayerView = findViewById(R.id.miniPlayerView)

        MiniPlayerDragger.makeDraggable(miniPlayerFrame)

        ModeStateManager.syncFromLiveData(viewModel.selectedMode)

        setSupportActionBar(binding.toolbar)
        window.statusBarColor = ContextCompat.getColor(this, android.R.color.black)

        binding.recyclerViewSongs.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewSongs.setHasFixedSize(true)

        player = ExoPlayer.Builder(this).build()

        binding.progressBar.setOnSeekBarChangeListener(object : android.widget.SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: android.widget.SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    val duration = playerManager?.getDuration() ?: 0L
                    playerManager?.seekTo(duration * progress / 100)
                }
            }

            override fun onStartTrackingTouch(seekBar: android.widget.SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: android.widget.SeekBar?) {}
        })

        binding.buttonEnterPip.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && ::player.isInitialized) {
                enterPictureInPictureMode(
                    PictureInPictureParams.Builder()
                        .setAspectRatio(pipAspectRatio)
                        .build()
                )
            }
        }

        lifecycleScope.launch {
            DataStoreManager.getMode(this@MainActivity).collect { savedMode ->
                val mode = if (savedMode == "VIDEO") MediaTypeEnum.VIDEO else MediaTypeEnum.AUDIO
                currentMode = mode
                val buttonId = if (mode == MediaTypeEnum.AUDIO) R.id.buttonAudio else R.id.buttonVideo
                binding.modeToggleGroup.check(buttonId)
                viewModel.filterDataByType(mode)
                updateToggleButtonColors(buttonId)
            }
        }

        binding.modeToggleGroup.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (isChecked) {
                val newMode = if (checkedId == R.id.buttonAudio) MediaTypeEnum.AUDIO else MediaTypeEnum.VIDEO

                if (newMode != currentMode) {
                    if (currentMode == MediaTypeEnum.VIDEO && newMode == MediaTypeEnum.AUDIO && ::player.isInitialized && player.isPlaying) {
                        enterMiniPlayerMode()
                    }
                    if (currentMode == MediaTypeEnum.AUDIO && newMode == MediaTypeEnum.VIDEO && miniPlayerFrame.visibility == View.VISIBLE) {
                        exitMiniPlayerMode()
                    }

                    currentMode = newMode
                    viewModel.filterDataByType(currentMode)
                    lifecycleScope.launch {
                        DataStoreManager.saveMode(this@MainActivity, newMode.name)
                    }
                }
                updateToggleButtonColors(checkedId)
            }
        }

        viewModel.data.observe(this) { songs ->
            if (!::adapter.isInitialized) {
                adapter = PlayerUiBinder.bindAdapter(
                    binding = binding,
                    songs = songs,
                    selectedIndex = selectedIndex,
                    onSongClick = { song, index ->
                        val previousIndex = selectedIndex
                        selectedIndex = index
                        if (song.mediaType == MediaTypeEnum.VIDEO) {
                            playVideoInline(song.url)
                        } else {
                            if (::player.isInitialized) {
                                player.stop()
                                player.clearMediaItems()
                                binding.pipPlayerView.player = null
                                miniPlayerView.player = null
                            }
                            playerManager?.playSongAt(index)
                            NowPlayingUpdater.update(binding, playerManager!!)
                            if (miniPlayerFrame.visibility == View.VISIBLE) exitMiniPlayerMode()
                            PlayerUiBinder.showAudioUI(binding)
                        }
                        triggerWidgetUpdate()
                        if (previousIndex != -1) adapter.notifyItemChanged(previousIndex)
                        adapter.notifyItemChanged(index)
                    },
                    isItemPlaying = { index -> index == selectedIndex }
                )
            } else {
                adapter.updateSongs(songs)
            }

            if (playerManager == null) {
                playerManager = PlayerManager.getInstance(this)
                playerInitializer = PlayerInitializer(
                    playerManager!!,
                    onUpdate = { prev, curr ->
                        lastPlayingIndex = curr
                        selectedIndex = curr
                        if (prev != -1) adapter.notifyItemChanged(prev)
                        if (curr != -1) adapter.notifyItemChanged(curr)
                        triggerWidgetUpdate()
                    },
                    onUiUpdate = { NowPlayingUpdater.update(binding, playerManager!!) }
                )
                playerInitializer.initialize(songs)
            } else {
                playerManager?.setPlaylist(songs)
            }
        }

        binding.buttonPlayPause.setOnClickListener {
            playerManager?.currentIndex?.takeIf { it != -1 }?.let {
                playerManager?.playSongAt(it)
                NowPlayingUpdater.update(binding, playerManager!!)
                triggerWidgetUpdate()
                adapter.notifyItemChanged(it)
            }
        }
    }

    private fun playVideoInline(mediaUrl: String) {
        if (!::player.isInitialized) {
            player = ExoPlayer.Builder(this).build()
        }
        player.setMediaItem(MediaItem.fromUri(mediaUrl))
        player.prepare()
        player.playWhenReady = true
        binding.pipPlayerView.player = player
        binding.pipPlayerView.visibility = View.VISIBLE
        binding.pipPlayerView.show()
        binding.toolbar.hide()
        binding.imageViewNowPlayingIcon.hide()
        binding.buttonPlayPause.hide()
        binding.progressBar.hide()
        binding.textViewCurrentTitle.hide()
    }

    private fun enterMiniPlayerMode() {
        binding.nowPlayingCard.hide()
        miniPlayerFrame.show()
        miniPlayerView.player = player
        binding.toolbar.show()
        binding.imageViewNowPlayingIcon.show()
        binding.buttonPlayPause.show()
        binding.progressBar.show()
        binding.textViewCurrentTitle.show()
        binding.pipPlayerView.hide()
    }

    private fun exitMiniPlayerMode() {
        miniPlayerFrame.hide()
        binding.nowPlayingCard.show()
        binding.pipPlayerView.player = player
    }

    private fun triggerWidgetUpdate() {
        val intent = Intent(this, MusicService::class.java).apply {
            action = getString(R.string.widget_action_refresh)
        }
        ContextCompat.startForegroundService(this, intent)
    }

    override fun onUserLeaveHint() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && ::player.isInitialized && player.isPlaying) {
            enterPictureInPictureMode(
                PictureInPictureParams.Builder().setAspectRatio(pipAspectRatio).build()
            )
        }
        super.onUserLeaveHint()
    }

    override fun onPictureInPictureModeChanged(isInPictureInPictureMode: Boolean) {
        super.onPictureInPictureModeChanged(isInPictureInPictureMode)
        if (isInPictureInPictureMode) {
            binding.modeToggleGroup.hide()
        } else if (currentMode == MediaTypeEnum.VIDEO) {
            binding.modeToggleGroup.show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        playerInitializer.release()
        if (::player.isInitialized) player.release()
    }

    private fun updateToggleButtonColors(checkedId: Int) {
        val selectedColor = Color.parseColor("#11387B")
        val unselectedColor = Color.parseColor("#D1E2E7")
        val buttons = listOf(binding.buttonAudio, binding.buttonVideo)
        buttons.forEach { button ->
            button.setBackgroundColor(unselectedColor)
            button.setTextColor(Color.BLACK)
        }
        val selectedButton = findViewById<MaterialButton>(checkedId)
        selectedButton.setBackgroundColor(selectedColor)
        selectedButton.setTextColor(Color.WHITE)
    }
}
