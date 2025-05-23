package com.example.playlistmaker.search.data

import com.example.playlistmaker.search.data.network.NetworkClient
import com.example.playlistmaker.search.domain.api.TracksRepository
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.search.data.dto.TracksSearchRequest
import com.example.playlistmaker.search.data.dto.TracksSearchResponse

class TracksRepositoryImpl(private val networkClient: NetworkClient, private val localStorage: LocalStorage) :
    TracksRepository {

    override fun searchTracks(expression: String): List<Track> {
        val response = networkClient.doRequest(TracksSearchRequest(expression))
        if (response.resultCode == 200) {
            return (response as TracksSearchResponse).results.map {
                Track(
                    it.trackId,
                    it.trackName,
                    it.artistName,
                    it.trackTime,
                    it.trackTimeMillis,
                    it.artworkUrl100,
                    it.collectionName,
                    it.releaseDate,
                    it.primaryGenreName,
                    it.country,
                    it.previewUrl
                )
            }
        } else {
            return emptyList()
        }
    }

    override fun getTracksHistory(): List<Track> {
        return localStorage.getTracksHistory()
    }

    override fun addTrackToHistory(track: Track) {
        localStorage.addTrackToHistory(track)
    }

    override fun cleanTracksHistory() {
        localStorage.cleanTracksHistory()
    }

    override fun isExistTracksHistory(): Boolean {
        return localStorage.isExistTracksHistory()
    }
}