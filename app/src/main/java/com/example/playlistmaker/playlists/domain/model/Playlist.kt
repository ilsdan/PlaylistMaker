package com.example.playlistmaker.playlists.domain.model

data class Playlist(
    val id: Long?,
    val name: String,
    val description: String?,
    val cover: String?,
    var count: Long
)
