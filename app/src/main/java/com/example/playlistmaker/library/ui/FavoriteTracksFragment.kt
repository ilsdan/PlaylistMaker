package com.example.playlistmaker.library.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentFavoriteTracksBinding
import com.example.playlistmaker.player.ui.PlayerFragment
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.search.ui.OnItemClickListener
import com.example.playlistmaker.search.ui.TrackAdapter
import com.example.playlistmaker.utils.debounce
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.getValue

class FavoriteTracksFragment : Fragment() {

    private val viewModel: PlaylistsViewModel by viewModel()

    private var _binding: FragmentFavoriteTracksBinding? = null
    private val binding get() = _binding!!

    private var _trackAdapter: TrackAdapter? = null
    private val trackAdapter get() = _trackAdapter!!

    private lateinit var onTrackClickDebounce: (Track) -> Unit

    private fun trackListViewCreate() {
        val onItemClickListener = object : OnItemClickListener {
            override fun onItemClick(item: Track) {
                onTrackClickDebounce(item)
            }
        }
        _trackAdapter = TrackAdapter(onItemClickListener)
        binding.tracksList.adapter = trackAdapter
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentFavoriteTracksBinding.inflate(inflater, container, false)
        return binding.root
    }

    private fun openPlayer(track: Track) {
        findNavController().navigate(R.id.playerFragment,
            PlayerFragment.createArgs(track))
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        onTrackClickDebounce = debounce<Track>(CLICK_DEBOUNCE_DELAY, viewLifecycleOwner.lifecycleScope, false) { track ->
            openPlayer(track)
        }

        trackListViewCreate()

        viewModel.observeState().observe(viewLifecycleOwner) {
            render(it)
        }

        viewModel.show()
    }

    private fun render(state: FavoriteScreenState) {
        when (state) {
            is FavoriteScreenState.Loading -> showLoading()
            is FavoriteScreenState.Content -> showTracks(state.tracks)
            is FavoriteScreenState.EmptyError -> showEmptyError()
        }
    }

    private fun showLoading() {
        binding.progressBar.isVisible = true
    }

    private fun showEmptyError() {
        binding.progressBar.isVisible = false
        binding.tracksList.isVisible = false
        binding.errorImage.isVisible = true
        binding.errorText.isVisible = true
    }

    private fun showTracks(tracks: List<Track>) {
        binding.errorImage.isVisible = false
        binding.errorText.isVisible = false
        binding.progressBar.isVisible = false
        binding.tracksList.isVisible = true
        trackAdapter.tracks.clear()
        trackAdapter.tracks.addAll(tracks)
        trackAdapter.notifyDataSetChanged()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        _trackAdapter = null
    }

    companion object {
        private const val CLICK_DEBOUNCE_DELAY = 1000L
        fun newInstance() = FavoriteTracksFragment()
    }
}