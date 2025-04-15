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
    private val songList = mutableListOf<DataModel>()
    private lateinit var adapter: SongAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        playerManager = PlayerManager(this)

        setupRecyclerView()
        setupPlayerListener()
        observeSongs()
        setupNowPlayingControls()
    }

    private fun setupRecyclerView() {
        adapter = SongAdapter(
            songs = songList,
            onSongClick = { _, index ->
                playerManager.togglePlayback(index)
                updateNowPlayingUI()
            },
            isItemPlaying = { index ->
                playerManager.currentIndex == index && playerManager.isPlaying()
            }
        )
        binding.recyclerViewSongs.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewSongs.adapter = adapter
    }

    private fun setupPlayerListener() {
        playerManager.setOnPlaybackChangedListener {
            updateNowPlayingUI()
        }
    }

    private fun observeSongs() {
        viewModel.data.observe(this) { list ->
            songList.clear()
            songList.addAll(list)
            adapter.notifyDataSetChanged()
            playerManager.setPlaylist(list.map { it.url })
        }
    }

    private fun updateNowPlayingUI() {
        val index = playerManager.currentIndex
        val isPlaying = playerManager.isPlaying()

        if (index in songList.indices) {
            val current = songList[index]
            binding.textViewCurrentTitle.text = current.name
            binding.imageViewNowPlayingIcon.setImageResource(current.imageRes)
        } else {
            binding.textViewCurrentTitle.text = ""
            binding.imageViewNowPlayingIcon.setImageDrawable(null)
        }

        val iconRes = if (isPlaying) android.R.drawable.ic_media_pause else android.R.drawable.ic_media_play
        binding.buttonPlayPause.setImageResource(iconRes)

        adapter.notifyDataSetChanged()
    }

    private fun setupNowPlayingControls() {
        binding.buttonPlayPause.setOnClickListener {
            val index = playerManager.currentIndex
            if (index != -1) {
                playerManager.togglePlayback(index)
                updateNowPlayingUI()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        playerManager.release()
    }
}








