package com.example.playlistmaker.search.domain.impl

import com.example.playlistmaker.search.domain.api.TracksInteractor
import com.example.playlistmaker.search.domain.api.TracksRepository
import com.example.playlistmaker.search.domain.models.Track
import java.util.concurrent.Executors

class TracksInteractorImpl(private val repository: TracksRepository
) : TracksInteractor {
    private val executor = Executors.newCachedThreadPool()

    override var currentTrack: Track? = null

    override fun searchTracks(expression: String, consumer: TracksInteractor.TracksConsumer) {
        executor.execute {
            consumer.consume(repository.searchTracks(expression))
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