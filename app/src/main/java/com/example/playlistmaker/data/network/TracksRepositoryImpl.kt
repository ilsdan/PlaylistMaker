package com.example.playlistmaker.data.network

import android.content.Context
import android.util.Log
import com.example.playlistmaker.data.NetworkClient
import com.example.playlistmaker.data.dto.TracksSearchResponse
import com.example.playlistmaker.data.dto.TracksSearchRequest
import com.example.playlistmaker.data.storage.SharedPrefsStorageClient
import com.example.playlistmaker.domain.api.TracksRepository
import com.example.playlistmaker.domain.models.Track
import com.google.gson.Gson

const val HISTORY_KEY = "search_history"

class TracksRepositoryImpl(private val networkClient: NetworkClient, private val context: Context) : TracksRepository {

    private val storageClient = SharedPrefsStorageClient(context)

    override fun searchTracks(expression: String): List<Track> {
        val response = networkClient.doRequest(TracksSearchRequest(expression))
        if (response.resultCode == 200) {
            Log.i("tracks", (response as TracksSearchResponse).results[0].toString())
            return (response as TracksSearchResponse).results.map {
                Track(it.trackId,
                    it.trackName,
                    it.artistName,
                    it.trackTime,
                    it.trackTimeMillis,
                    it.artworkUrl100,
                    it.collectionName,
                    it.releaseDate,
                    it.primaryGenreName,
                    it.country,
                    it.previewUrl)
            }
        } else {
            return emptyList()
        }
    }

    override fun getTracksHistory(): List<Track> {

        val json = storageClient.getString(HISTORY_KEY) ?: return emptyList()
        return Gson().fromJson(json, Array<Track>::class.java).toList()
    }

    override fun addTrackToHistory(track: Track) {
        val history = getTracksHistory().toMutableList()
        history.removeAll { it.trackId == track.trackId }
        history.add(0, track)
        if (history.size > 10) {
            history.removeAt(history.lastIndex)
        }
        val json = Gson().toJson(history)
        storageClient.setString(HISTORY_KEY, json)
    }

    override fun cleanTracksHistory() {
        storageClient.remove(HISTORY_KEY)
    }

    override fun isExistTracksHistory(): Boolean {
        return storageClient.exist(HISTORY_KEY)
    }
}