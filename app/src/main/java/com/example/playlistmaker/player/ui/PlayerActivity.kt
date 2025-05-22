package com.example.playlistmaker.player.ui

import android.icu.text.SimpleDateFormat
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivityAudioplayerBinding
import com.example.playlistmaker.player.domain.PlayStatus
import com.example.playlistmaker.player.domain.PlayerScreenState
import com.example.playlistmaker.search.domain.models.Track
import com.google.gson.Gson
import java.util.Locale

class PlayerActivity : AppCompatActivity() {

    private lateinit var viewModel: PlayerViewModel
    private lateinit var binding: ActivityAudioplayerBinding

    private var like = false
    private var inCollection = false

    private lateinit var track: Track

    private fun startPlayer() {
        binding.playPauseButton.setImageDrawable(getDrawable(R.drawable.pause_button))
    }

    private fun pausePlayer() {
        binding.playPauseButton.setImageDrawable(getDrawable(R.drawable.play_button))
    }

    private fun toolbarCreate() {
        setSupportActionBar(binding.toolbar)
        getSupportActionBar()?.setDisplayShowTitleEnabled(false)

        binding.toolbar.setNavigationOnClickListener {
            super.onBackPressed()
            finish()
        }
    }

    private fun showTrackInfo() {
        Glide.with(applicationContext).load(track.artworkUrl100?.replaceAfterLast('/',"512x512bb.jpg"))
            .placeholder(R.drawable.track_placeholder)
            .fitCenter()
            .centerCrop()
            .transform((RoundedCorners(resources.getDimensionPixelSize(R.dimen.cover_rounded_corners_radius))))
            .into(binding.cover)
        binding.trackName.text = track.trackName
        binding.artistName.text = track.artistName
        binding.trackTime.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(track.trackTimeMillis)
        binding.collectionName.text = track.collectionName
        val date = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").parse(track.releaseDate)
        binding.releaseDate.text = SimpleDateFormat("yyyy", Locale.getDefault()).format(date)
        binding.primaryGenreName.text = track.primaryGenreName
        binding.country.text = track.country
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAudioplayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        track = Gson().fromJson(intent.getStringExtra("track"), Track::class.java)

        viewModel = ViewModelProvider(this, PlayerViewModel.getViewModelFactory(track))[PlayerViewModel::class.java]

        viewModel.getScreenStateLiveData().observe(this) { screenState ->
            when (screenState) {
                is PlayerScreenState.Content -> {
                    binding.progressBar.isVisible = false
                    binding.playPauseButton.isVisible = true
                    binding.progressBar.isVisible = false
                    binding.playPauseButton.isVisible = true
                    binding.currentTrackTime.text = "0:00"
                    binding.playPauseButton.setImageDrawable(getDrawable(R.drawable.play_button))
                }
                is PlayerScreenState.Loading -> {
                }
            }
        }

        viewModel.getPlayStatusLiveData().observe(this) { playStatus ->
            changeButtonStyle(playStatus)
            binding.currentTrackTime.text = SimpleDateFormat("m:ss", Locale.getDefault()).format(playStatus.progress)
        }

        toolbarCreate()
        showTrackInfo()

        binding.playPauseButton.setOnClickListener {
            viewModel.playbackControl()
        }

        binding.collectionButton.setOnClickListener {
            inCollection = !inCollection
            when {
                inCollection -> {
                    binding.collectionButton.setImageDrawable(getDrawable(R.drawable.add_collection_button))
                }

                else -> {
                    binding.collectionButton.setImageDrawable(getDrawable(R.drawable.done_collection_button))
                }
            }
        }

        binding.likeButton.setOnClickListener {
            like = !like
            when {
                like -> {
                    binding.likeButton.setImageDrawable(getDrawable(R.drawable.like_fill_track_button))
                }

                else -> {
                    binding.likeButton.setImageDrawable(getDrawable(R.drawable.like_track_button))
                }
            }
        }
    }

    private fun changeButtonStyle(playStatus: PlayStatus) {
        if (playStatus.isPlaying) {
            startPlayer()
        } else {
            pausePlayer()
        }
    }
}