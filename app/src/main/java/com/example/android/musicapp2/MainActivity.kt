package com.example.android.musicapp2

import android.app.PictureInPictureParams
import android.content.Intent
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Rational
import android.view.View
import android.widget.SeekBar
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.android.musicapp2.adapter.SongAdapter
import com.example.android.musicapp2.databinding.ActivityMainBinding
import com.example.android.musicapp2.model.DataModel
import com.example.android.musicapp2.repository.DataRepository
import com.example.android.musicapp2.service.MusicService
import com.example.android.musicapp2.state.ModeStateManager
import com.example.android.musicapp2.utils.PlayerManager
import com.example.android.musicapp2.viewmodel.MainViewModel
import com.example.android.musicapp2.viewmodel.MainViewModelFactory

class MainActivity : AppCompatActivity() {

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
            playerManager?.let {
                if (it.isPlaying()) {
                    binding.progressBar.progress = it.getPlaybackPercentage()
                    handler.postDelayed(this, 1000)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ModeStateManager.syncFromLiveData(viewModel.selectedMode)

        setupToolbar()
        setupRecyclerView()

        viewModel.data.observe(this) { songs ->
            setupPlayer(songs)
            setupAdapter(songs)
            setupPlaybackControls()
        }

        setupSeekBar()
        setupPipButton()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        window.statusBarColor = ContextCompat.getColor(this, android.R.color.black)
    }

    private fun setupRecyclerView() {
        binding.recyclerViewSongs.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewSongs.setHasFixedSize(true)
    }

    private fun setupPlayer(songs: List<DataModel>) {
        playerManager = PlayerManager.getInstance(this).apply {
            setPlaylist(songs)
            setOnPlaybackChangedListener {
                val previousIndex = lastPlayingIndex
                lastPlayingIndex = currentIndex

                updateNowPlaying()
                if (previousIndex != -1) adapter.notifyItemChanged(previousIndex)
                if (currentIndex != -1) adapter.notifyItemChanged(currentIndex)

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
                playerManager?.togglePlayback(index)
                updateNowPlaying()
                triggerWidgetUpdate()
            },
            isItemPlaying = { index ->
                playerManager?.let {
                    index == it.currentIndex && it.isPlaying()
                } ?: false
            }
        )
        binding.recyclerViewSongs.adapter = adapter
    }

    private fun setupPlaybackControls() {
        binding.buttonPlayPause.setOnClickListener {
            playerManager?.let {
                val index = it.currentIndex
                if (index != -1) {
                    it.togglePlayback(index)
                    updateNowPlaying()
                    triggerWidgetUpdate()
                }
            }
        }
    }

    private fun setupSeekBar() {
        binding.progressBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    playerManager?.let {
                        val newPosition = (it.getDuration() * (progress / 100f)).toLong()
                        it.seekTo(newPosition)
                    }
                }
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }

    private fun setupPipButton() {
        binding.buttonEnterPip.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4"
                player = ExoPlayer.Builder(this).build().also {
                    it.setMediaItem(MediaItem.fromUri(videoUrl))
                    it.prepare()
                    it.playWhenReady = true
                }

                binding.pipPlayerView.player = player
                binding.pipPlayerView.visibility = View.VISIBLE

                binding.toolbar.visibility = View.GONE
                binding.recyclerViewSongs.visibility = View.GONE
                binding.textViewCurrentTitle.visibility = View.GONE
                binding.imageViewNowPlayingIcon.visibility = View.GONE
                binding.buttonPlayPause.visibility = View.GONE
                binding.progressBar.visibility = View.GONE
                binding.buttonEnterPip.visibility = View.GONE

                val pipParams = PictureInPictureParams.Builder()
                    .setAspectRatio(Rational(16, 9))
                    .build()
                enterPictureInPictureMode(pipParams)
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
            action = "REFRESH_WIDGET"
        }
        ContextCompat.startForegroundService(this, intent)
    }

    override fun onUserLeaveHint() {
        playerManager?.let {
            if (it.isPlaying() && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val pipParams = PictureInPictureParams.Builder()
                    .setAspectRatio(Rational(16, 9))
                    .build()
                enterPictureInPictureMode(pipParams)
            }
        }
        super.onUserLeaveHint()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onPictureInPictureModeChanged(isInPictureInPictureMode: Boolean, newConfig: Configuration) {
        super.onPictureInPictureModeChanged(isInPictureInPictureMode, newConfig)
        binding.toolbar.visibility = if (isInPictureInPictureMode) View.GONE else View.VISIBLE
        binding.recyclerViewSongs.visibility = if (isInPictureInPictureMode) View.GONE else View.VISIBLE
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(progressUpdater)
        playerManager?.release()
        if (::player.isInitialized) player.release()
    }
}
