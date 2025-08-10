package com.example.playlistmaker.playlists.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlaylistsBinding
import com.example.playlistmaker.playlist.ui.PlaylistFragment
import com.example.playlistmaker.playlists.domain.model.Playlist
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.getValue

class PlaylistsFragment : Fragment() {

    private val viewModel: PlaylistsViewModel by viewModel()

    private var _binding: FragmentPlaylistsBinding? = null
    private val binding get() = _binding!!

    private var _playlistCardAdapter: PlaylistCardAdapter? = null
    private val playlistAdapter get() = _playlistCardAdapter!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentPlaylistsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.NewPlaylistButton.setOnClickListener {
            findNavController().navigate(R.id.action_libraryFragment_to_newPlaylistFragment)
        }

        viewModel.observeState().observe(viewLifecycleOwner) {
            render(it)
        }

        viewModel.show()

        _playlistCardAdapter = PlaylistCardAdapter(object : OnPlaylistClickListener {
            override fun onItemClick(item: Playlist) {
                findNavController().navigate(R.id.action_libraryFragment_to_playlistFragment,
                    PlaylistFragment.createArgs(item))
            }
        })
        binding.playlistsView.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.playlistsView.adapter = playlistAdapter

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
        binding.errorImage.isVisible = true
        binding.errorText.isVisible = true
        binding.playlistsView.isVisible = false
    }

    private fun showPlaylists(playlist: List<Playlist>) {
        binding.playlistsView.isVisible = true
        binding.errorImage.isVisible = false
        binding.errorText.isVisible = false
        playlistAdapter.playlist.clear()
        playlistAdapter.playlist.addAll(playlist)
        playlistAdapter.notifyDataSetChanged()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        _playlistCardAdapter = null
    }

    companion object {
        fun newInstance() = PlaylistsFragment()
    }
}