package com.example.playlistmaker.search.domain.api

import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.utils.Resource
import kotlinx.coroutines.flow.Flow

interface TracksRepository {
    fun searchTracks(expression: String): Flow<Resource<List<Track>>>
    fun getTracksHistory(): List<Track>
    fun addTrackToHistory(track: Track)
    fun cleanTracksHistory()
    fun isExistTracksHistory(): Boolean
}