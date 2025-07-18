package com.example.playlistmaker.search.domain.api

import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface TracksInteractor {

    fun searchTracks(expression: String): Flow<Pair<List<Track>?, String?>>
    fun getTracksHistory(): List<Track>
    fun addTrackToHistory(track: Track)
    fun cleanTracksHistory()
    fun isExistTracksHistory(): Boolean
}