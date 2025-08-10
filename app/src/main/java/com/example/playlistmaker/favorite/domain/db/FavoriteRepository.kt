package com.example.playlistmaker.favorite.domain.db

import com.example.playlistmaker.tracks.domian.models.Track
import kotlinx.coroutines.flow.Flow

interface FavoriteRepository {
    fun favoriteTracks(): Flow<List<Track>>
    fun isTrackFavorite(id: Long): Flow<Boolean>
    suspend fun addFavoriteTrack(track: Track)
    suspend fun removeFavoriteTrack(track: Track)
}