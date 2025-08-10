package com.example.playlistmaker.tracks.domian.impl

import com.example.playlistmaker.tracks.domian.api.TracksInteractor
import com.example.playlistmaker.tracks.domian.api.TracksRepository
import com.example.playlistmaker.tracks.domian.models.Track
import com.example.playlistmaker.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TracksInteractorImpl(private val repository: TracksRepository
) : TracksInteractor {

    override fun searchTracks(expression: String): Flow<Pair<List<Track>?, String?>> {
        return repository.searchTracks(expression).map { result ->
            when(result) {
                is Resource.Success -> {
                    Pair(result.data, null)
                }
                is Resource.Error -> {
                    Pair(null, result.message)
                }
            }
        }
    }

    override fun getTracksHistory(): List<Track> {
        return repository.getTracksHistory()
    }

    override fun addTrackToHistory(track: Track) {
        repository.addTrackToHistory(track)
    }

    override fun cleanTracksHistory() {
        repository.cleanTracksHistory()
    }

    override fun isExistTracksHistory(): Boolean {
        return repository.isExistTracksHistory()
    }
}