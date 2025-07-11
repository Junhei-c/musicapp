package com.example.android.musicapp2.view.activity

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Rational
import android.view.View
import android.widget.FrameLayout
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.android.musicapp2.R
import com.example.android.musicapp2.databinding.ActivityMainBinding
import com.example.android.musicapp2.model.MediaTypeEnum
import com.example.android.musicapp2.repository.DataRepository
import com.example.android.musicapp2.service.MusicService
import com.example.android.musicapp2.utils.datastore.DataStoreManager
import com.example.android.musicapp2.utils.init.PlayerInitializer
import com.example.android.musicapp2.utils.lifecycle.LifecycleManager
import com.example.android.musicapp2.utils.manager.PlayerManager
import com.example.android.musicapp2.utils.mode.ModeToggleHandler
import com.example.android.musicapp2.utils.pip.PictureInPictureHelper
import com.example.android.musicapp2.utils.player.PlayerController
import com.example.android.musicapp2.utils.ui.MiniPlayerHandler
import com.example.android.musicapp2.utils.ui.NotificationHelper
import com.example.android.musicapp2.utils.ui.NotificationHelper.SongInteractionHandler
import com.example.android.musicapp2.utils.ui.NowPlayingUpdater
import com.example.android.musicapp2.utils.ui.PlayerUiBinder
import com.example.android.musicapp2.utils.ui.UiController
import com.example.android.musicapp2.utils.widget.WidgetUpdater
import com.example.android.musicapp2.view.adapter.SongAdapter
import com.example.android.musicapp2.viewmodel.MainViewModel
import com.example.android.musicapp2.viewmodel.MainViewModelFactory

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
    private lateinit var pipPlayerView: PlayerView

    private val viewModel: MainViewModel by viewModels {
        MainViewModelFactory(DataRepository())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        miniPlayerFrame = findViewById(R.id.miniPlayerFrame)
        miniPlayerView = findViewById(R.id.miniPlayerView)
        pipPlayerView = findViewById(R.id.pipPlayerView)

        MiniPlayerHandler.makeDraggable(miniPlayerView)
        MiniPlayerHandler.makeDraggable(pipPlayerView)

        setSupportActionBar(binding.toolbar)
        window.statusBarColor = ContextCompat.getColor(this, android.R.color.black)

        binding.recyclerViewSongs.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            setHasFixedSize(true)
        }

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
            PictureInPictureHelper.enterPipMode(this, ::player.isInitialized, pipAspectRatio)
        }

        ModeToggleHandler.initMode(
            context = this,
            viewModel = viewModel,
            scope = lifecycleScope,
            binding = binding
        ) { mode, buttonId ->
            currentMode = mode
            binding.modeToggleGroup.check(buttonId)
            UiController.updateToggleButtonColors(binding, buttonId)
        }

        binding.modeToggleGroup.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (isChecked) {
                ModeToggleHandler.handleToggle(
                    checkedId = checkedId,
                    currentMode = currentMode,
                    onModeChanged = { newMode ->
                        if (currentMode == MediaTypeEnum.VIDEO && newMode == MediaTypeEnum.AUDIO && player.isPlaying) {
                            MiniPlayerHandler.enterMiniPlayerMode(binding, player)
                        }
                        if (currentMode == MediaTypeEnum.AUDIO && newMode == MediaTypeEnum.VIDEO && miniPlayerFrame.visibility == View.VISIBLE) {
                            MiniPlayerHandler.exitMiniPlayerMode(binding, player)
                        }
                        currentMode = newMode
                        viewModel.filterDataByType(newMode)
                    },
                    saveMode = { DataStoreManager.saveMode(this@MainActivity, it) },
                    scope = lifecycleScope
                )
                UiController.updateToggleButtonColors(binding, checkedId)
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
                            SongInteractionHandler.handleVideoClick(binding, player, song.url)
                        } else {
                            playerManager?.getExoPlayer()?.let { exo ->
                                SongInteractionHandler.handleAudioClick(
                                    binding = binding,
                                    song = song,
                                    index = index,
                                    playerManager = playerManager!!,
                                    exoPlayer = exo,
                                    miniPlayerView = miniPlayerView,
                                    pipPlayerView = pipPlayerView,
                                    onWidgetUpdate = { WidgetUpdater.updateStandard(this) }
                                )
                            }
                        }

                        if (previousIndex != -1) adapter.notifyItemChanged(previousIndex)
                        adapter.notifyItemChanged(index)
                    },
                    isItemPlaying = { index -> index == selectedIndex }
                )
            } else {
                adapter.submitList(songs)
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
                        WidgetUpdater.updateStandard(this)
                    },
                    onUiUpdate = { NowPlayingUpdater.update(binding, playerManager!!) }
                )
                playerInitializer.initialize(songs)

                PlayerController.setupPlayPause(
                    binding,
                    playerManager!!,
                    adapterNotify = { adapter.notifyItemChanged(it) },
                    updateUi = { NowPlayingUpdater.update(binding, playerManager!!) },
                    refreshWidget = { WidgetUpdater.updateStandard(this) }
                )
            } else {
                playerManager?.setPlaylist(songs)
            }
        }

        binding.fabPlayPause.setOnClickListener {
            playerManager?.let {
                if (it.isPlaying()) it.pause() else it.resume()
                NowPlayingUpdater.update(binding, it)
                WidgetUpdater.updateStandard(this)

                val song = it.getCurrentData()
                val notif = NotificationHelper.createNotification(this, it.isPlaying(), song?.name ?: "")
                val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                manager.notify(MusicService.NOTIFICATION_ID, notif)

                ContextCompat.startForegroundService(this, Intent(this, MusicService::class.java))
            }
        }
    }

    override fun onUserLeaveHint() {
        PictureInPictureHelper.enterPipMode(this, ::player.isInitialized, pipAspectRatio)
        super.onUserLeaveHint()
    }

    override fun onPictureInPictureModeChanged(isInPictureInPictureMode: Boolean) {
        super.onPictureInPictureModeChanged(isInPictureInPictureMode)
        PictureInPictureHelper.handlePipChange(binding, isInPictureInPictureMode, currentMode == MediaTypeEnum.VIDEO)
    }

    override fun onDestroy() {
        super.onDestroy()
        LifecycleManager.cleanUp(playerInitializer, player)
    }
}
