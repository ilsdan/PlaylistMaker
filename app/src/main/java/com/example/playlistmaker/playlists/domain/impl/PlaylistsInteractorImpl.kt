package com.example.playlistmaker.playlists.domain.impl


import android.net.Uri
import android.util.Log
import com.example.playlistmaker.playlists.domain.api.PlaylistRepository
import com.example.playlistmaker.playlists.domain.api.PlaylistsInteractor
import com.example.playlistmaker.playlists.domain.model.Playlist
import com.example.playlistmaker.tracks.domian.models.Track
import kotlinx.coroutines.flow.Flow

class PlaylistsInteractorImpl(
    private val playlistRepository: PlaylistRepository
):PlaylistsInteractor {
    override fun playlists(): Flow<List<Playlist>> {
        return playlistRepository.playlists()
    }

    override suspend fun addPlaylist(name: String, description: String?, imageUri: Uri?) {
        playlistRepository.addPlaylist(name, description, imageUri)
    }

    override suspend fun removePlaylist(playlist: Playlist) {
        playlistRepository.removePlaylist(playlist)
    }

    override suspend fun addTrackToPlaylist(playlist: Playlist, track: Track) {
        playlistRepository.addTrackToPlaylist(playlist, track)
    }

    override fun isTrackInPlaylist(track: Track, playlist: Playlist): Flow<Boolean> {
        return playlistRepository.isTrackInPlaylist(track, playlist)
    }

    override fun tracksInPlaylist(playlist: Playlist): Flow<List<Track>> {
        return playlistRepository.tracksInPlaylist(playlist)
    }
}