package com.example.playlistmaker.library.data

import com.example.playlistmaker.library.data.converters.TrackDbConvertor
import com.example.playlistmaker.library.data.db.dao.TrackDao
import com.example.playlistmaker.library.data.db.entity.TrackEntity
import com.example.playlistmaker.library.domain.db.FavoriteRepository
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow


class FavoriteRepositoryImpl(
    private val trackDao: TrackDao,
    private val trackDbConvertor: TrackDbConvertor
): FavoriteRepository {
    override fun favoriteTracks(): Flow<List<Track>> = flow {
        val tracks = trackDao.getFavoriteTracks()
        emit(convertFromTrackEntity(tracks))
    }

    override fun isTrackFavorite(id: Long): Flow<Boolean> = flow {
        emit(!trackDao.getTrack(id).isEmpty())
    }

    override suspend fun addFavoriteTrack(track: Track) {
        trackDao.insertTrack(trackDbConvertor.map(track))
    }

    override suspend fun removeFavoriteTrack(track: Track) {
        trackDao.deleteTrackEntity(trackDbConvertor.map(track))
    }

    private fun convertFromTrackEntity(tracks: List<TrackEntity>): List<Track> {
        return tracks.map { track -> trackDbConvertor.map(track) }
    }

    private fun convertToTrackEntity(tracks: List<Track>): List<TrackEntity> {
        return tracks.map { track -> trackDbConvertor.map(track) }
    }
}