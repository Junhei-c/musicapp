package com.example.android.musicapp2

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.android.musicapp2.adapter.SongAdapter
import com.example.android.musicapp2.databinding.ActivityMainBinding
import com.example.android.musicapp2.model.DataModel
import com.example.android.musicapp2.utils.PlayerManager
import com.example.android.musicapp2.viewmodel.MainViewModel
import com.example.android.musicapp2.viewmodel.MainViewModelFactory

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels { MainViewModelFactory() }

    private lateinit var playerManager: PlayerManager
    private lateinit var songList: List<DataModel>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        playerManager = PlayerManager(this)

        playerManager.setOnPlaybackChangedListener {
            updateNowPlayingUI()
            binding.recyclerViewSongs.adapter?.notifyDataSetChanged()
        }

        setupRecyclerView()
        observeSongs()
        setupNowPlayingControls()
    }

    private fun setupRecyclerView() {
        binding.recyclerViewSongs.layoutManager = LinearLayoutManager(this)
    }

    private fun observeSongs() {
        viewModel.data.observe(this) { list ->
            songList = list
            playerManager.setPlaylist(list.map { it.url })
            binding.recyclerViewSongs.adapter = SongAdapter(
                songs = list,
                onSongClick = { song, index ->
                    playerManager.togglePlayback(index)
                    updateNowPlayingUI()
                },
                isItemPlaying = { index ->
                    index == playerManager.currentIndex && playerManager.isPlaying()
                }
            )
        }
    }

    private fun setupNowPlayingControls() {
        binding.buttonPlayPause.setOnClickListener {
            val index = playerManager.currentIndex
            if (index in songList.indices) {
                playerManager.togglePlayback(index)
                updateNowPlayingUI()
                binding.recyclerViewSongs.adapter?.notifyDataSetChanged()
            }
        }
    }

    private fun updateNowPlayingUI() {
        val index = playerManager.currentIndex
        if (index in songList.indices) {
            val song = songList[index]

            binding.textViewCurrentTitle.text = song.name

            val icon = if (playerManager.isPlaying())
                android.R.drawable.ic_media_pause
            else
                android.R.drawable.ic_media_play

            binding.buttonPlayPause.setImageResource(icon)
        } else {
            binding.textViewCurrentTitle.text = ""
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        playerManager.release()
    }
}




