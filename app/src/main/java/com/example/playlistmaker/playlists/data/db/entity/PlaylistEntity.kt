package com.example.playlistmaker.playlists.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "playlists")
data class PlaylistEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long?,
    val name: String,
    val description: String?,
    val cover: String?
)
