package com.example.playlistmaker.domain.impl

import android.content.Context
import com.example.playlistmaker.domain.api.TracksInteractor
import com.example.playlistmaker.domain.api.TracksRepository
import com.example.playlistmaker.domain.models.Track
import java.util.concurrent.Executors

class TracksInteractorImpl(private val repository: TracksRepository, context: Context) : TracksInteractor {
    private val executor = Executors.newCachedThreadPool()

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