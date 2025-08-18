package com.example.playlistmaker.playlists.ui

import com.example.playlistmaker.playlists.domain.model.Playlist

interface OnPlaylistClickListener {
    fun onItemClick(item: Playlist)
}