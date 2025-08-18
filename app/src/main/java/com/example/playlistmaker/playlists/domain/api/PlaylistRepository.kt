package com.example.playlistmaker.playlists.domain.api

import android.net.Uri
import com.example.playlistmaker.playlists.domain.model.Playlist
import com.example.playlistmaker.tracks.domian.models.Track
import kotlinx.coroutines.flow.Flow

interface PlaylistRepository {
    fun playlists(): Flow<List<Playlist>>
    suspend fun addPlaylist(name: String, description: String?, imageUri: Uri?)
    suspend fun removePlaylist(playlist: Playlist)
    suspend fun addTrackToPlaylist(playlist: Playlist, track: Track)
    fun isTrackInPlaylist(track: Track, playlist: Playlist): Flow<Boolean>
    fun tracksInPlaylist(playlist: Playlist): Flow<List<Track>>
    suspend fun removeTrackFromPlaylist(track: Track, playlist: Playlist)
    suspend fun updatePlaylist(playlistId: Long, name: String, description: String?, imageUri: Uri?)
}