package com.example.playlistmaker.favorite.domain.impl

import com.example.playlistmaker.favorite.domain.db.FavoriteInteractor
import com.example.playlistmaker.favorite.domain.db.FavoriteRepository
import com.example.playlistmaker.tracks.domian.models.Track
import kotlinx.coroutines.flow.Flow

class FavoriteInteractorImpl(
    private val favoriteRepository: FavoriteRepository
): FavoriteInteractor {
    override fun favoriteTracks(): Flow<List<Track>> {
        return favoriteRepository.favoriteTracks()
    }

    override fun isTrackFavorite(id: Long): Flow<Boolean> {
        return favoriteRepository.isTrackFavorite(id)
    }

    override suspend fun addFavoriteTrack(track: Track) {
        favoriteRepository.addFavoriteTrack(track)
    }

    override suspend fun removeFavoriteTrack(track: Track) {
        favoriteRepository.removeFavoriteTrack(track)
    }
}