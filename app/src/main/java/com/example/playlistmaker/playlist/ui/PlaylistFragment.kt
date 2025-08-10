package com.example.playlistmaker.playlist.ui

import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.os.Environment
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.core.os.bundleOf
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlaylistBinding
import com.example.playlistmaker.player.ui.PlayerFragment
import com.example.playlistmaker.playlists.domain.model.Playlist
import com.example.playlistmaker.tracks.domian.models.Track
import com.example.playlistmaker.tracks.ui.OnItemClickListener
import com.example.playlistmaker.tracks.ui.TrackAdapter
import com.example.playlistmaker.utils.debounce
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import java.util.Locale
import kotlin.getValue

class PlaylistFragment : Fragment() {

    private var _binding: FragmentPlaylistBinding? = null
    private val binding get() = _binding!!

    private val viewModel:  PlaylistViewModel by viewModel()

    private lateinit var trackAdapter: TrackAdapter

    private lateinit var onTrackClickDebounce: (Track) -> Unit

    private fun render(state: PlaylistScreenState) {
        when (state) {
            is PlaylistScreenState.Loading -> showLoading()
            is PlaylistScreenState.Content -> showTracks(state.playlist, state.tracks)
        }
    }

    private fun showTracks(playlist: Playlist, tracks: List<Track>) {
        trackAdapter.tracks.clear()
        trackAdapter.tracks.addAll(tracks)
        trackAdapter.notifyDataSetChanged()


        if (playlist.cover != null){
            val filePath = File(requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES), "Covers")
            val file = File(filePath, playlist.cover)
            binding.playlistCover.setImageURI(file.toUri())
        }

        binding.playlistName.text = playlist.name

        if (playlist.description != null)
            binding.playlistDescription.text = playlist.description

        var tracksTime: Long = 0
        tracks.forEach { track ->
            tracksTime += track.trackTimeMillis
        }

        if (tracks.isEmpty()){
            binding.playlistInfo.text = requireContext().getString(R.string.no_tracks)
        } else {
            binding.playlistInfo.text = "${requireContext().resources.getQuantityString(R.plurals.plurals_minutes, SimpleDateFormat("mm", Locale.getDefault()).format(tracksTime).toInt(), SimpleDateFormat("mm", Locale.getDefault()).format(tracksTime).toInt())} ● ${requireContext().resources.getQuantityString(R.plurals.plurals_tracks, playlist.count.toInt(), playlist.count.toInt())}"
        }

    }

    private fun showLoading() {

    }

    private fun openPlayer(track: Track) {
        findNavController().navigate(R.id.action_playlistFragment_to_playerFragment,
            PlayerFragment.createArgs(track))
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        viewModel.showPlaylistInfo(requireArguments().getParcelable<Playlist>(PLAYLIST)!!)
        _binding = FragmentPlaylistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbar.setNavigationIcon(requireContext().getDrawable(R.drawable.arrow_back))
        binding.toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }

        onTrackClickDebounce = debounce<Track>(CLICK_DEBOUNCE_DELAY, viewLifecycleOwner.lifecycleScope, false) { track ->
            openPlayer(track)
        }

        val onItemClickListener = object : OnItemClickListener {
            override fun onItemClick(item: Track) {
                onTrackClickDebounce(item)
            }
        }
        trackAdapter = TrackAdapter(onItemClickListener)
        binding.tracksList.adapter = trackAdapter


        viewModel.observeState().observe(viewLifecycleOwner) {
            render(it)
        }
    }

    companion object {
        private const val PLAYLIST = "playlist"
        fun createArgs(playlist: Playlist): Bundle =
            bundleOf(PLAYLIST to playlist)

        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }
}