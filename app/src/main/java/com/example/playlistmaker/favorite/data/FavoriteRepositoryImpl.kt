package com.example.playlistmaker.favorite.data

import com.example.playlistmaker.tracks.data.converters.TrackDbConvertor
import com.example.playlistmaker.tracks.data.db.dao.TrackDao
import com.example.playlistmaker.tracks.data.db.entity.TrackEntity
import com.example.playlistmaker.favorite.domain.db.FavoriteRepository
import com.example.playlistmaker.tracks.domian.models.Track
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
        if (trackDao.getTrack(id).isEmpty()){
            emit(false)
        } else{
            emit(trackDao.getTrack(id)[0].isFavorite)
        }
    }

    override suspend fun addFavoriteTrack(track: Track) {
        trackDao.insertTrack(trackDbConvertor.map(track))
    }

    override suspend fun removeFavoriteTrack(track: Track) {
        trackDao.insertTrack(trackDbConvertor.map(track))
    }

    private fun convertFromTrackEntity(tracks: List<TrackEntity>): List<Track> {
        return tracks.map { track -> trackDbConvertor.map(track) }
    }

    private fun convertToTrackEntity(tracks: List<Track>): List<TrackEntity> {
        return tracks.map { track -> trackDbConvertor.map(track) }
    }
}