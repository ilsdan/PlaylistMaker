package com.example.playlistmaker.tracks.data.db.entity

import androidx.room.Entity

@Entity(tableName = "track_playlists")
data class PlaylistTracksCountEntity(
    val playlist_id: Long,
    val count: Long
)
