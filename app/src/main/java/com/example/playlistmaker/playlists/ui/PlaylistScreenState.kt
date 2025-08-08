package com.example.playlistmaker.playlists.ui

import com.example.playlistmaker.playlists.domain.model.Playlist

sealed interface PlaylistScreenState {
    object Loading: PlaylistScreenState
    object Empty: PlaylistScreenState
    data class Content(
        val playlist: List<Playlist>
    ): PlaylistScreenState
}