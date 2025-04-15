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
        setupNowPlayingControls()
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
        playerManager.play(index)

        // UI: Update now playing box
        binding.textViewCurrentTitle.text = song.name
        Glide.with(this).load(song.imageUrl).into(binding.imageViewAlbumArt)
        updateNowPlayingStyle()

        // UI: Update play/pause icon
        updatePlayButton()

        // Notify adapter of change
        binding.recyclerViewSongs.adapter?.notifyDataSetChanged()
    }

    private fun updateNowPlayingStyle() {
        val isPlaying = playerManager.isPlaying()
        val backgroundColor = if (isPlaying) {
            getColor(android.R.color.white)
        } else {
            getColor(android.R.color.holo_orange_light)
        }
        binding.nowPlayingCard.setCardBackgroundColor(backgroundColor)
    }

    private fun updatePlayButton() {
        val icon = if (playerManager.isPlaying()) android.R.drawable.ic_media_pause
        else android.R.drawable.ic_media_play
        binding.buttonPlayPause.setImageResource(icon)
    }

    private fun setupNowPlayingControls() {
        binding.buttonPlayPause.setOnClickListener {
            val index = playerManager.currentIndex
            if (index != -1) {
                if (playerManager.isPlaying()) {
                    playerManager.pause()
                } else {
                    playerManager.play(index)
                }
                updatePlayButton()
                updateNowPlayingStyle()
                binding.recyclerViewSongs.adapter?.notifyDataSetChanged()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        playerManager.release()
    }
}



