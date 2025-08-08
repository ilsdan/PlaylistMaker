package com.example.playlistmaker.player.ui

import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlayerBinding
import com.example.playlistmaker.player.domain.PlayStatus
import com.example.playlistmaker.player.domain.PlayerScreenState
import com.example.playlistmaker.playlists.domain.model.Playlist
import com.example.playlistmaker.playlists.ui.OnItemClickListener
import com.example.playlistmaker.playlists.ui.PlaylistAdapter
import com.example.playlistmaker.playlists.ui.PlaylistScreenState
import com.example.playlistmaker.tracks.domian.models.Track
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_HIDDEN
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.Locale
import kotlin.getValue

class PlayerFragment : Fragment() {

    private var _binding: FragmentPlayerBinding? = null
    private val binding get() = _binding!!

    private lateinit var playlistAdapter: PlaylistAdapter

    private val viewModel:  PlayerViewModel by viewModel()

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

        binding.NewPlaylistButton.setOnClickListener {
            findNavController().navigate(R.id.action_playerFragment_to_newPlaylistFragment)
        }

        playlistAdapter = PlaylistAdapter(object : OnItemClickListener {
            override fun onItemClick(item: Playlist) {
                viewModel.addToPlaylist(item)
            }
        })
        binding.playlists.adapter = playlistAdapter

        val bottomSheetContainer = binding.playlistsBottomSheet

        val overlay = binding.overlay

        viewModel.getPlaylistsLiveData().observe(viewLifecycleOwner) {
            render(it)
        }
        viewModel.show()

        val bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetContainer).apply {
            state = STATE_HIDDEN
        }

        bottomSheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {

            override fun onStateChanged(bottomSheet: View, newState: Int) {

                when (newState) {
                    STATE_HIDDEN -> {
                        overlay.visibility = View.GONE
                    }
                    else -> {
                        overlay.visibility = View.VISIBLE
                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                binding.overlay.alpha = (slideOffset + 1)/2
            }
        })

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
            bottomSheetBehavior.apply {
                state = BottomSheetBehavior.STATE_HALF_EXPANDED
            }
        }

        binding.likeButton.setOnClickListener {
            viewModel.toggleFavorite()
        }


        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.toastChannel
                .receiveAsFlow()
                .flowWithLifecycle(viewLifecycleOwner.lifecycle)
                .collect {
                    if (!it.second) {
                        Toast.makeText(requireContext(), "Добавлено в плейлист ${it.first.name}", Toast.LENGTH_SHORT).show()
                        bottomSheetBehavior.state = STATE_HIDDEN
                    } else {
                        Toast.makeText(requireContext(), "Трек уже добавлен в плейлист ${it.first.name}", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }

    private fun render(state: PlaylistScreenState) {
        when (state) {
            is PlaylistScreenState.Loading -> showLoading()
            is PlaylistScreenState.Content -> showPlaylists(state.playlist)
            is PlaylistScreenState.Empty -> showEmpty()
        }
    }

    private fun showLoading() {
    }

    private fun showEmpty() {
    }

    private fun showPlaylists(playlist: List<Playlist>) {
        playlistAdapter.playlist.clear()
        playlistAdapter.playlist.addAll(playlist)
        playlistAdapter.notifyDataSetChanged()
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