package com.example.playlistmaker.tracks.data.db.entity


import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "track_playlists")
data class TrackPlaylistsEntity(
    @PrimaryKey
    val id: Long?,
    val track_id: Long,
    val playlist_id: Long
)