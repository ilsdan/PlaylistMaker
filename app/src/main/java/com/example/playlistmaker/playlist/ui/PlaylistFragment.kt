package com.example.playlistmaker.playlist.ui

import android.content.Intent
import java.text.SimpleDateFormat
import android.os.Bundle
import android.os.Environment
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.net.toUri
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlaylistBinding
import com.example.playlistmaker.player.ui.PlayerFragment
import com.example.playlistmaker.playlists.domain.model.Playlist
import com.example.playlistmaker.playlists.ui.NewPlaylistFragment
import com.example.playlistmaker.tracks.domian.models.Track
import com.example.playlistmaker.tracks.ui.OnItemClickListener
import com.example.playlistmaker.tracks.ui.OnLongClickListener
import com.example.playlistmaker.tracks.ui.TrackAdapter
import com.example.playlistmaker.utils.debounce
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_COLLAPSED
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_HIDDEN
import com.google.android.material.dialog.MaterialAlertDialogBuilder
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
            is PlaylistScreenState.Content -> {
                showTracks(state.tracks)
                showPlaylistInfo(state.playlist)
            }
        }
    }

    private fun showPlaylistInfo(playlist: Playlist) {
        if (playlist.cover != null){
            val filePath = File(requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES), "Covers")
            val file = File(filePath, playlist.cover)
            binding.playlistCover.setImageURI(file.toUri())
            binding.playlistCoverSmall.setImageURI(file.toUri())
        }

        binding.playlistName.text = playlist.name
        binding.title.text = playlist.name

        if (playlist.description != null)
            binding.playlistDescription.text = playlist.description
        else
            binding.playlistDescription.text = requireContext().getString(R.string.no_description)
    }

    private fun showTracks(tracks: List<Track>) {
        trackAdapter.tracks.clear()
        trackAdapter.tracks.addAll(tracks)
        trackAdapter.notifyDataSetChanged()


        var tracksTime: Long = 0
        tracks.forEach { track ->
            tracksTime += track.trackTimeMillis
        }

        if (tracks.isEmpty()){
            binding.playlistInfo.text = requireContext().getString(R.string.no_tracks)
            binding.number.text = requireContext().getString(R.string.no_tracks)
        } else {
            binding.playlistInfo.text = "${requireContext().resources.getQuantityString(R.plurals.plurals_minutes, SimpleDateFormat("mm", Locale.getDefault()).format(tracksTime).toInt(), SimpleDateFormat("mm", Locale.getDefault()).format(tracksTime).toInt())} ● ${requireContext().resources.getQuantityString(R.plurals.plurals_tracks, tracks.count(), tracks.count())}"
            binding.number.text = "${requireContext().resources.getQuantityString(R.plurals.plurals_tracks, tracks.count(), tracks.count())}"
        }

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



        val bottomSheetContainer = binding.menuPlaylistBottomSheet
        val overlay = binding.overlay
        val bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetContainer).apply {
            state = STATE_HIDDEN
        }

        bottomSheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {

            override fun onStateChanged(bottomSheet: View, newState: Int) {

                when (newState) {
                    STATE_HIDDEN -> {
                        overlay.isVisible = false
                    }
                    else -> {
                        overlay.isVisible = true
                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                binding.overlay.alpha = (slideOffset + 1)/2
            }
        })

        binding.playlistButtonDetails.setOnClickListener {
            bottomSheetBehavior.state = STATE_COLLAPSED
        }


        binding.editInformation.setOnClickListener {
            findNavController().navigate(R.id.action_playlistFragment_to_newPlaylistFragment,
                NewPlaylistFragment.createArgs(requireArguments().getParcelable<Playlist>(PLAYLIST)!!))

        }

        onTrackClickDebounce = debounce<Track>(CLICK_DEBOUNCE_DELAY, viewLifecycleOwner.lifecycleScope, false) { track ->
            openPlayer(track)
        }

        trackAdapter = TrackAdapter(object : OnItemClickListener {
            override fun onItemClick(item: Track) {
                onTrackClickDebounce(item)
            }
        })
        trackAdapter.addLongClickListener(object : OnLongClickListener {
            override fun onLongClick(item: Track): Boolean {
                MaterialAlertDialogBuilder(requireContext())
                    .setMessage(getString(R.string.do_you_want_to_delete_track))
                    .setNegativeButton(getString(R.string.no)) { dialog, which ->
                    }
                    .setPositiveButton(getString(R.string.yes)) { dialog, which ->
                        viewModel.deleteTrack(item)
                    }
                    .show()
                return true
            }
        })
        binding.tracksList.adapter = trackAdapter

        binding.deletePlaylist.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext())
                .setMessage(getString(R.string.want_delete_playlist, requireArguments().getParcelable<Playlist>(PLAYLIST)!!.name))

                .setNegativeButton(getString(R.string.no)) { dialog, which ->
                }
                .setPositiveButton(getString(R.string.yes)) { dialog, which ->
                    viewModel.deletePlaylist()
                    findNavController().popBackStack()
                }
                .show()
        }

        binding.share.setOnClickListener {
            sharingTracks()
        }

        binding.playlistButtonShare.setOnClickListener {
            sharingTracks()
        }

        viewModel.observeState().observe(viewLifecycleOwner) {
            render(it)
        }
    }

    fun sharingTracks() {
        if (viewModel.tracks.isEmpty()){
            Toast.makeText(requireContext(), requireContext().getString(R.string.no_tracklist_to_share_in_this_playlist),
                Toast.LENGTH_SHORT).show()
        }
        else {
            viewModel.tracks
            var text = "${requireContext().resources.getQuantityString(R.plurals.plurals_tracks, viewModel.tracks.count(), viewModel.tracks.count())}\n"
            viewModel.tracks.forEachIndexed { index, track ->

                val time = SimpleDateFormat("mm:ss", Locale.getDefault()).format(track.trackTimeMillis)

                text += "${index+1}. " + "${track.artistName} - ${track.trackName} ($time)"  + "\n"
            }

            val sendIntent: Intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, text)
                type = "text/plain"
            }

            val shareIntent = Intent.createChooser(sendIntent, null)
            startActivity(shareIntent)
        }
    }

    companion object {
        private const val PLAYLIST = "playlist"
        fun createArgs(playlist: Playlist): Bundle =
            bundleOf(PLAYLIST to playlist)

        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }
}