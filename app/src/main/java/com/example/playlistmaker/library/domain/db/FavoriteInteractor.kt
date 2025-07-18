package com.example.playlistmaker.library.domain.db

import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface FavoriteInteractor {
    fun favoriteTracks(): Flow<List<Track>>
    fun isTrackFavorite(id: Long): Flow<Boolean>
    suspend fun addFavoriteTrack(track: Track)
    suspend fun removeFavoriteTrack(track: Track)
}