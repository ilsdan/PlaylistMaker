package com.example.playlistmaker.library.domain.impl

import com.example.playlistmaker.library.domain.db.FavoriteInteractor
import com.example.playlistmaker.library.domain.db.FavoriteRepository
import com.example.playlistmaker.search.domain.models.Track
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