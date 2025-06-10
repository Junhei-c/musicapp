package com.example.android.musicapp2.view.view.activitym

import android.app.PictureInPictureParams
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Rational
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.media3.exoplayer.ExoPlayer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.android.musicapp2.R
import com.example.android.musicapp2.databinding.ActivityMainBinding
import com.example.android.musicapp2.model.DataModel
import com.example.android.musicapp2.repository.DataRepository
import com.example.android.musicapp2.service.MusicService
import com.example.android.musicapp2.state.ModeStateManager
import com.example.android.musicapp2.utils.extensions.hide
import com.example.android.musicapp2.utils.extensions.show
import com.example.android.musicapp2.utils.manager.PlayerManager
import com.example.android.musicapp2.view.adapter.SongAdapter
import com.example.android.musicapp2.viewmodel.MainViewModel
import com.example.android.musicapp2.viewmodel.MainViewModelFactory

class MainActivity : AppCompatActivity() {

    private val pipAspectRatio = Rational(16, 9)

    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: SongAdapter
    private lateinit var player: ExoPlayer
    private var playerManager: PlayerManager? = null
    private var lastPlayingIndex: Int = -1

    private val viewModel: MainViewModel by viewModels {
        MainViewModelFactory(DataRepository())
    }

    private val handler = Handler(Looper.getMainLooper())
    private val progressUpdater = object : Runnable {
        override fun run() {
            playerManager?.takeIf { it.isPlaying() }?.let {
                binding.progressBar.progress = it.getPlaybackPercentage()
                handler.postDelayed(this, 1000)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ModeStateManager.syncFromLiveData(viewModel.selectedMode)

        setSupportActionBar(binding.toolbar)
        window.statusBarColor = ContextCompat.getColor(this, android.R.color.black)

        binding.recyclerViewSongs.setHasFixedSize(true)
        binding.recyclerViewSongs.layoutManager = LinearLayoutManager(this)

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
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                player = ExoPlayer.Builder(this).build().apply {
                    setMediaItem(androidx.media3.common.MediaItem.fromUri(getString(R.string.video_url)))
                    prepare()
                    playWhenReady = true
                }

                binding.pipPlayerView.player = player
                binding.pipPlayerView.show()

                binding.toolbar.hide()
                binding.recyclerViewSongs.hide()
                binding.textViewCurrentTitle.hide()
                binding.imageViewNowPlayingIcon.hide()
                binding.buttonPlayPause.hide()
                binding.progressBar.hide()
                binding.buttonEnterPip.hide()

                enterPictureInPictureMode(
                    PictureInPictureParams.Builder()
                        .setAspectRatio(pipAspectRatio)
                        .build()
                )
            }
        }

        viewModel.data.observe(this) { songs ->
            if (!::adapter.isInitialized) {
                setupAdapter(songs)
            }
            if (playerManager == null) {
                setupPlayer(songs)
                setupPlaybackControls()
            }
        }
    }

    private fun setupPlayer(songs: List<DataModel>) {
        playerManager = PlayerManager.getInstance(this).apply {
            setPlaylist(songs)
            setOnPlaybackChangedListener {
                val previousIndex = lastPlayingIndex
                lastPlayingIndex = currentIndex

                updateNowPlaying()

                if (::adapter.isInitialized) {
                    if (previousIndex != -1) adapter.notifyItemChanged(previousIndex)
                    if (currentIndex != -1) adapter.notifyItemChanged(currentIndex)
                }

                handler.removeCallbacks(progressUpdater)
                if (isPlaying()) handler.post(progressUpdater)

                triggerWidgetUpdate()
            }
        }
    }

    private fun setupAdapter(songs: List<DataModel>) {
        adapter = SongAdapter(
            songs = songs,
            onSongClick = { _, index ->
                val previousIndex = playerManager?.currentIndex ?: -1
                playerManager?.togglePlayback(index)

                updateNowPlaying()
                triggerWidgetUpdate()

                if (previousIndex != -1) adapter.notifyItemChanged(previousIndex)
                adapter.notifyItemChanged(index)
            },
            isItemPlaying = { index ->
                playerManager?.let { index == it.currentIndex && it.isPlaying() } ?: false
            }
        )
        binding.recyclerViewSongs.adapter = adapter
    }

    private fun setupPlaybackControls() {
        binding.buttonPlayPause.setOnClickListener {
            playerManager?.currentIndex?.takeIf { it != -1 }?.let {
                playerManager?.togglePlayback(it)
                updateNowPlaying()
                triggerWidgetUpdate()
                adapter.notifyItemChanged(it)
            }
        }
    }

    private fun updateNowPlaying() {
        val song = playerManager?.getCurrentData()
        val isPlaying = playerManager?.isPlaying() == true

        binding.textViewCurrentTitle.text = song?.name.orEmpty()
        song?.imageRes?.let { binding.imageViewNowPlayingIcon.setImageResource(it) }

        binding.buttonPlayPause.setImageResource(
            if (isPlaying) R.drawable.iconparkpauseone else R.drawable.iconparkplay
        )

        binding.progressBar.progress = playerManager?.getPlaybackPercentage() ?: 0
    }

    private fun triggerWidgetUpdate() {
        val intent = Intent(this, MusicService::class.java).apply {
            action = getString(R.string.widget_action_refresh)
        }
        ContextCompat.startForegroundService(this, intent)
    }

    override fun onUserLeaveHint() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && playerManager?.isPlaying() == true) {
            enterPictureInPictureMode(
                PictureInPictureParams.Builder()
                    .setAspectRatio(pipAspectRatio)
                    .build()
            )
        }
        super.onUserLeaveHint()
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(progressUpdater)
        playerManager?.release()
        if (::player.isInitialized) player.release()
    }
}
