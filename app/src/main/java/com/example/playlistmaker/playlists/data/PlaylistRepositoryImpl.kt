package com.example.playlistmaker.playlists.data

import android.net.Uri
import android.util.Log
import androidx.core.net.toFile
import com.example.playlistmaker.playlists.data.converters.PlaylistDbConvertor
import com.example.playlistmaker.playlists.data.db.dao.PlaylistDao
import com.example.playlistmaker.playlists.data.db.entity.PlaylistEntity
import com.example.playlistmaker.playlists.domain.api.PlaylistRepository
import com.example.playlistmaker.playlists.domain.model.Playlist
import com.example.playlistmaker.playlists.ui.PlaylistScreenState
import com.example.playlistmaker.tracks.data.db.dao.TrackPlaylistsDao
import com.example.playlistmaker.tracks.data.db.entity.TrackPlaylistsEntity
import com.example.playlistmaker.tracks.domian.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow


class PlaylistRepositoryImpl(
    private val playlistDao: PlaylistDao,
    private val trackPlaylistsDao: TrackPlaylistsDao,
    private val imageLocalStorage: ImageLocalStorage
): PlaylistRepository {

    private val playlistDbConvertor: PlaylistDbConvertor = PlaylistDbConvertor()

    override fun playlists(): Flow<List<Playlist>> = flow {
        val playlists = convertFromPlaylistsEntity(playlistDao.getPlaylists())

        playlists.forEach { playlist ->
            trackPlaylistsDao.getPlaylistsTrackCount().forEach {
                playlist.count
                if (playlist.id == it.playlist_id) {
                    playlist.count = it.count
                }
            }
        }


        emit(playlists)
    }

    override suspend fun addPlaylist(name: String, description: String?, imageUri: Uri?) {

        var imageName: String? = null

        if (imageUri != null){
            imageName = "${imageUri.toString().substring(imageUri.toString().lastIndexOf('/') + 1)}.jpg"
                imageLocalStorage.saveImageToPrivateStorage(
                    imageUri,
                    "Covers",
                    imageName
                )
        }

        val playlistEntity = PlaylistEntity(
            null,
            name,
            description,
            imageName
        )

        playlistDao.insertPlaylist(playlistEntity)
    }

    override suspend fun removePlaylist(playlist: Playlist) {
        playlistDao.deletePlaylistEntity(playlistDbConvertor.map(playlist))
    }

    override suspend fun addTrackToPlaylist(playlist: Playlist, track: Track) {
        trackPlaylistsDao.insertTrackPlaylists(TrackPlaylistsEntity(null, track.trackId, playlist.id!!))
    }

    override fun isTrackInPlaylist(track: Track, playlist: Playlist): Flow<Boolean> = flow  {

        var inPlaylist = false

        trackPlaylistsDao.getPlaylistsByTrackId(track.trackId).forEach {
            if (it.playlist_id == playlist.id)
                inPlaylist = true
        }

        emit(inPlaylist)
    }

    private fun convertFromPlaylistsEntity(playlistsEntity: List<PlaylistEntity>): List<Playlist> {
        return playlistsEntity.map { track -> playlistDbConvertor.map(track) }
    }
}