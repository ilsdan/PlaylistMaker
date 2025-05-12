package com.example.playlistmaker.search.domain.api

import com.example.playlistmaker.search.domain.models.Track

interface TracksInteractor {
    fun searchTracks(expression: String, consumer: TracksConsumer)
    fun getTracksHistory(): List<Track>
    fun addTrackToHistory(track: Track)
    fun cleanTracksHistory()
    fun isExistTracksHistory(): Boolean

    interface TracksConsumer {
        fun consume(foundTracks: List<Track>?)
    }
}