package com.example.playlistmaker.playlists.data.converters

import com.example.playlistmaker.playlists.data.db.entity.PlaylistEntity
import com.example.playlistmaker.playlists.domain.model.Playlist

class PlaylistDbConvertor {
    fun map(playlist: Playlist): PlaylistEntity {
        return PlaylistEntity(
            playlist.id,
            playlist.name,
            playlist.description,
            playlist.cover
        )
    }

    fun map(playlistEntity: PlaylistEntity): Playlist {
        return Playlist(
            playlistEntity.id,
            playlistEntity.name,
            playlistEntity.description,
            playlistEntity.cover,
            0
        )
    }
}