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

        playerManager.setOnPlaybackChangedListener {
            updateNowPlayingStyle()
            updatePlayButton()
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
                list,
                { song, index -> playSong(song, index) },
                { index -> playerManager.getCurrentIndex() == index && playerManager.isPlaying() }
            )
        }
    }

    private fun playSong(song: DataModel, index: Int) {
        currentSong = song
        playerManager.togglePlayback(index)

        binding.textViewCurrentTitle.text = song.name
        Glide.with(this).load(song.imageUrl).into(binding.imageViewAlbumArt)
    }

    private fun updateNowPlayingStyle() {
        val isPlaying = playerManager.isPlaying()
        val color = if (isPlaying) getColor(android.R.color.white) else getColor(android.R.color.holo_orange_light)
        binding.nowPlayingCard.setCardBackgroundColor(color)
    }

    private fun updatePlayButton() {
        val icon = if (playerManager.isPlaying()) android.R.drawable.ic_media_pause else android.R.drawable.ic_media_play
        binding.buttonPlayPause.setImageResource(icon)
    }

    private fun setupNowPlayingControls() {
        binding.buttonPlayPause.setOnClickListener {
            val index = playerManager.getCurrentIndex()
            if (index != -1) {
                playerManager.togglePlayback(index)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        playerManager.release()
    }
}



