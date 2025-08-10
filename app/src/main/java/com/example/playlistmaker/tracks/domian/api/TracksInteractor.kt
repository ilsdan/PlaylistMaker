package com.example.playlistmaker.tracks.domian.api

import com.example.playlistmaker.tracks.domian.models.Track
import kotlinx.coroutines.flow.Flow

interface TracksInteractor {

    fun searchTracks(expression: String): Flow<Pair<List<Track>?, String?>>
    fun getTracksHistory(): List<Track>
    fun addTrackToHistory(track: Track)
    fun cleanTracksHistory()
    fun isExistTracksHistory(): Boolean
}