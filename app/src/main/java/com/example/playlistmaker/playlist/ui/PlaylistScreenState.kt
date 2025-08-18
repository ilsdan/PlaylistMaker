package com.example.playlistmaker.playlist.ui

import com.example.playlistmaker.playlists.domain.model.Playlist
import com.example.playlistmaker.tracks.domian.models.Track

sealed interface PlaylistScreenState {
    data class Content(
        val playlist: Playlist,
        val tracks: List<Track>
    ) : PlaylistScreenState
}