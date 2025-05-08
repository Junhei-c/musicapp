package com.example.android.musicapp2

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.SeekBar
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.android.musicapp2.adapter.SongAdapter
import com.example.android.musicapp2.databinding.ActivityMainBinding
import com.example.android.musicapp2.model.DataModel
import com.example.android.musicapp2.repository.DataRepository
import com.example.android.musicapp2.utils.PlayerManager
import com.example.android.musicapp2.viewmodel.MainViewModel
import com.example.android.musicapp2.viewmodel.MainViewModelFactory

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var playerManager: PlayerManager
    private lateinit var adapter: SongAdapter

    private val viewModel: MainViewModel by viewModels {
        MainViewModelFactory(DataRepository())
    }

    private val handler = Handler(Looper.getMainLooper())
    private val progressUpdater = object : Runnable {
        override fun run() {
            if (playerManager.isPlaying()) {
                binding.progressBar.progress = playerManager.getPlaybackPercentage()
                handler.postDelayed(this, 1000)
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
        }

        binding.buttonPlayPause.setOnClickListener {
            val index = playerManager.currentIndex
            if (index != -1) playerManager.togglePlayback(index)
        }

        binding.progressBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    val newPosition = (playerManager.getDuration() * (progress / 100f)).toLong()
                    playerManager.seekTo(newPosition)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
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
            }
        }
    }

    private fun setupAdapter(songs: List<DataModel>) {
        adapter = SongAdapter(
            songs = songs,
            onSongClick = { _, index ->
                playerManager.togglePlayback(index)
            },
            isItemPlaying = { index ->
                index == playerManager.currentIndex && playerManager.isPlaying()
            }
        )
        binding.recyclerViewSongs.adapter = adapter
    }

    private fun updateNowPlaying() {
        val song = playerManager.getCurrentData()
        val isPlaying = playerManager.isPlaying()

        binding.textViewCurrentTitle.apply {
            text = song?.name.orEmpty()
            setTextColor(ContextCompat.getColor(context, R.color.black))
        }

        song?.imageRes?.let { binding.imageViewNowPlayingIcon.setImageResource(it) }

        binding.buttonPlayPause.setImageResource(
            if (isPlaying) R.drawable.iconparkpauseone else R.drawable.iconparkplay
        )

        binding.progressBar.progress = playerManager.getPlaybackPercentage()
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(progressUpdater)
        playerManager.release()
    }
}
























