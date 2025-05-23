package com.example.playlistmaker.search.data

import android.content.SharedPreferences
import com.example.playlistmaker.search.domain.models.Track
import com.google.gson.Gson


class LocalStorage(private val sharedPreferences: SharedPreferences, private val gson: Gson) {

    fun getTracksHistory(): List<Track> {
        val json = sharedPreferences.getString(HISTORY_KEY, null) ?: return emptyList()
        return gson.fromJson(json, Array<Track>::class.java).toList()
    }

    fun addTrackToHistory(track: Track) {
        val history = getTracksHistory().toMutableList()
        history.removeAll { it.trackId == track.trackId }
        history.add(0, track)
        if (history.size > 10) {
            history.removeAt(history.lastIndex)
        }
        val json = gson.toJson(history)
        sharedPreferences.edit().putString(HISTORY_KEY, json).apply()
    }

    fun cleanTracksHistory() {
        sharedPreferences.edit().remove(HISTORY_KEY).apply()
    }

    fun isExistTracksHistory(): Boolean {
        return sharedPreferences.contains(HISTORY_KEY)
    }

    private companion object {
        const val HISTORY_KEY = "search_history"
    }

}