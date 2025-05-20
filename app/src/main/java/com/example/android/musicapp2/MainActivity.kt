package com.example.android.musicapp2

import android.app.PictureInPictureParams
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Rational
import android.widget.SeekBar
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.android.musicapp2.adapter.SongAdapter
import com.example.android.musicapp2.databinding.ActivityMainBinding
import com.example.android.musicapp2.model.DataModel
import com.example.android.musicapp2.repository.DataRepository
import com.example.android.musicapp2.service.MusicService
import com.example.android.musicapp2.utils.PlayerManager
import com.example.android.musicapp2.viewmodel.MainViewModel
import com.example.android.musicapp2.viewmodel.MainViewModelFactory

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var playerManager: PlayerManager? = null
    private lateinit var adapter: SongAdapter

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

        setupToolbar()
        setupRecyclerView()

        viewModel.data.observe(this) { songs ->
            setupPlayer(songs)
            setupAdapter(songs)

            binding.buttonPlayPause.setOnClickListener {
                val manager = playerManager ?: return@setOnClickListener
                val index = manager.currentIndex
                if (index != -1) {
                    manager.togglePlayback(index)
                    updateNowPlaying()
                    triggerWidgetUpdate()
                }
            }
        }

        binding.buttonEnterPip.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val pipParams = PictureInPictureParams.Builder()
                    .setAspectRatio(Rational(16, 9))
                    .build()
                enterPictureInPictureMode(pipParams)
            }
        }

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

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        window.statusBarColor = ContextCompat.getColor(this, android.R.color.black)
    }

    private fun setupRecyclerView() {
        binding.recyclerViewSongs.layoutManager = LinearLayoutManager(this)
    }

    private fun setupPlayer(songs: List<DataModel>) {
        playerManager = PlayerManager.getInstance(this).apply {
            setPlaylist(songs)
            setOnPlaybackChangedListener {
                updateNowPlaying()
                adapter.notifyDataSetChanged()
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

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(progressUpdater)
        playerManager?.release()
    }
}

























