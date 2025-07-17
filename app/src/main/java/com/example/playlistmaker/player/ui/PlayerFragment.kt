package com.example.playlistmaker.player.ui

import android.icu.text.SimpleDateFormat
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlayerBinding
import com.example.playlistmaker.player.domain.PlayStatus
import com.example.playlistmaker.player.domain.PlayerScreenState
import com.example.playlistmaker.search.domain.models.Track
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.Locale
import kotlin.getValue

class PlayerFragment : Fragment() {

    private var _binding: FragmentPlayerBinding? = null
    private val binding get() = _binding!!

    private val viewModel:  PlayerViewModel by viewModel()

    private var inCollection = false

    private fun startPlayer() {
        binding.playPauseButton.setImageDrawable(requireContext().getDrawable(R.drawable.pause_button))
    }

    private fun pausePlayer() {
        binding.playPauseButton.setImageDrawable(requireContext().getDrawable(R.drawable.play_button))
    }

    private fun showTrackInfo(track: Track) {
        Glide.with(requireContext()).load(track.artworkUrl100?.replaceAfterLast('/',"512x512bb.jpg"))
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

    private fun changeButtonStyle(playStatus: PlayStatus) {
        if (playStatus.isPlaying) {
            startPlayer()
        } else {
            pausePlayer()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        viewModel.initPlayer(requireArguments().getParcelable<Track>(TRACK)!!)
        _binding = FragmentPlayerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.toolbar.setNavigationIcon(requireContext().getDrawable(R.drawable.arrow_back))
        binding.toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }

        viewModel.getScreenStateLiveData().observe(viewLifecycleOwner) { screenState ->
            when (screenState) {
                is PlayerScreenState.Content -> {
                    showTrackInfo(screenState.trackModel)
                    binding.progressBar.isVisible = false
                    binding.playPauseButton.isVisible = true
                    binding.progressBar.isVisible = false
                    binding.playPauseButton.isVisible = true
                    binding.currentTrackTime.text = "0:00"
                    binding.playPauseButton.setImageDrawable(requireContext().getDrawable(R.drawable.play_button))
                    binding.likeButton.isActivated = true
                    if (screenState.trackModel.isFavorite) {
                        binding.likeButton.setImageDrawable(requireContext().getDrawable(R.drawable.like_fill_track_button))
                    } else {
                        binding.likeButton.setImageDrawable(requireContext().getDrawable(R.drawable.like_track_button))
                    }
                }
                is PlayerScreenState.Loading -> {
                    binding.likeButton.isActivated = false
                    binding.progressBar.isVisible = true
                }
            }
        }

        viewModel.getPlayStatusLiveData().observe(viewLifecycleOwner) { playStatus ->
            changeButtonStyle(playStatus)
            binding.currentTrackTime.text = SimpleDateFormat("m:ss", Locale.getDefault()).format(playStatus.progress)
        }

        viewModel.getFavoriteStatusLiveData().observe(viewLifecycleOwner) {
            if (it) {
                binding.likeButton.setImageDrawable(requireContext().getDrawable(R.drawable.like_fill_track_button))
            } else {
                binding.likeButton.setImageDrawable(requireContext().getDrawable(R.drawable.like_track_button))
            }
        }

        binding.playPauseButton.setOnClickListener {
            viewModel.playbackControl()
        }

        binding.collectionButton.setOnClickListener {
            inCollection = !inCollection
            when {
                inCollection -> {
                    binding.collectionButton.setImageDrawable(requireContext().getDrawable(R.drawable.add_collection_button))
                }

                else -> {
                    binding.collectionButton.setImageDrawable(requireContext().getDrawable(R.drawable.done_collection_button))
                }
            }
        }

        binding.likeButton.setOnClickListener {
            viewModel.toggleFavorite()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val TRACK = "track"
        fun createArgs(track: Track): Bundle =
            bundleOf(TRACK to track)
    }
}