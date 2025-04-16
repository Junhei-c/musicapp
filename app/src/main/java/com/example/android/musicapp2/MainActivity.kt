package com.example.android.musicapp2

import android.graphics.Color
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
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

    private var currentSong: DataModel? = null
    private var currentIndex: Int = -1

    private val viewModel: MainViewModel by viewModels {
        MainViewModelFactory(DataRepository())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        playerManager = PlayerManager(this)
        binding.recyclerViewSongs.layoutManager = LinearLayoutManager(this)

        viewModel.data.observe(this) { songList ->
            playerManager.setPlaylist(songList.map { it.url })

            adapter = SongAdapter(
                songList,
                onSongClick = { song, index ->
                    currentSong = song
                    currentIndex = index
                    playerManager.togglePlayback(index)
                    updateNowPlaying()
                },
                isItemPlaying = { index ->
                    index == currentIndex && playerManager.isPlaying()
                }
            )

            binding.recyclerViewSongs.adapter = adapter

            playerManager.setOnPlaybackChangedListener {
                updateNowPlaying()
                adapter.notifyDataSetChanged()
            }
        }

        binding.buttonPlayPause.setOnClickListener {
            if (currentIndex != -1) {
                playerManager.togglePlayback(currentIndex)
                updateNowPlaying()
            }
        }
    }

    private fun updateNowPlaying() {
        if (playerManager.isPlaying()) {
            binding.textViewCurrentTitle.text = currentSong?.name.orEmpty()
            binding.textViewCurrentTitle.setTextColor(Color.BLUE)
            currentSong?.imageRes?.let {
                binding.imageViewNowPlayingIcon.setImageResource(it)
            }
            binding.buttonPlayPause.setImageResource(android.R.drawable.ic_media_pause)
        } else {
            binding.buttonPlayPause.setImageResource(android.R.drawable.ic_media_play)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        playerManager.release()
    }
}










