package com.example.android.musicapp2

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
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
    private var currentSong: DataModel? = null
    private lateinit var songList: List<DataModel>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        playerManager = PlayerManager(this)

        setupRecyclerView()
        observeSongs()
        setupPlaybackControls()
    }

    private fun setupRecyclerView() {
        binding.recyclerViewSongs.layoutManager = LinearLayoutManager(this)
    }

    private fun observeSongs() {
        viewModel.data.observe(this) { list ->
            songList = list
            playerManager.setPlaylist(list.map { it.url })
            binding.recyclerViewSongs.adapter = SongAdapter(list) { song, index ->
                playSong(song, index)
            }
        }
    }

    private fun playSong(song: DataModel, index: Int) {
        currentSong = song
        binding.textViewCurrentTitle.text = song.name
        Glide.with(this).load(song.imageUrl).into(binding.imageViewAlbumArt)
        playerManager.play(index)
        updatePlayButton()
    }

    private fun updatePlayButton() {
        val icon = if (playerManager.isPlaying()) android.R.drawable.ic_media_pause else android.R.drawable.ic_media_play
        binding.buttonPlayPause.setImageResource(icon)
    }

    private fun setupPlaybackControls() {
        binding.buttonPlayPause.setOnClickListener {
            if (playerManager.isPlaying()) {
                playerManager.pause()
            } else {
                currentSong?.let { song ->
                    val index = songList.indexOf(song)
                    if (index != -1) playerManager.play(index)
                }
            }
            updatePlayButton()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        playerManager.release()
    }
}



