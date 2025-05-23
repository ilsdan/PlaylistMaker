package com.example.playlistmaker.search.ui

import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.search.domain.SearchScreenState
import com.example.playlistmaker.search.domain.api.TracksInteractor
import com.example.playlistmaker.search.domain.models.Track

class SearchViewModel(private val tracksInteractor: TracksInteractor) : ViewModel() {

    private val handler = Handler(Looper.getMainLooper())

    private val stateLiveData = MutableLiveData<SearchScreenState>()
    fun observeState(): LiveData<SearchScreenState> = stateLiveData

    private var latestSearchText: String? = null

    override fun onCleared() {
        handler.removeCallbacksAndMessages(SEARCH_REQUEST_TOKEN)
    }

    fun searchDebounce(changedText: String) {
        if (latestSearchText == changedText) {
            return
        }

        this.latestSearchText = changedText
        handler.removeCallbacksAndMessages(SEARCH_REQUEST_TOKEN)

        val searchRunnable = Runnable { searchRequest(changedText) }

        val postTime = SystemClock.uptimeMillis() + SEARCH_DEBOUNCE_DELAY
        handler.postAtTime(
            searchRunnable,
            SEARCH_REQUEST_TOKEN,
            postTime,
        )
    }

    fun showHistory() {
        if (tracksInteractor.isExistTracksHistory())
            renderState(SearchScreenState.History(tracksInteractor.getTracksHistory()))
    }

    fun clearTrackHistory() {
        tracksInteractor.cleanTracksHistory()
        renderState(SearchScreenState.Empty)
    }

    fun addTrackToHistory(track: Track) {
        tracksInteractor.currentTrack = track
        tracksInteractor.addTrackToHistory(track)
    }

    private fun searchRequest(searchEditTextValue: String) {
        if (searchEditTextValue.isEmpty()) return
        renderState(SearchScreenState.Loading)

        tracksInteractor.searchTracks(searchEditTextValue, object: TracksInteractor.TracksConsumer {
            override fun consume(foundTracks: List<Track>?) {
                handler.post {
                    if (!foundTracks.isNullOrEmpty()) {
                        renderState(SearchScreenState.Content(foundTracks))
                    }
                    if (foundTracks != null && foundTracks.isEmpty() ) {
                        renderState(SearchScreenState.EmptyError("Ничего не нашлось"))
                    }

                    if (foundTracks == null) {
                        renderState(SearchScreenState.NetworkError("Проблемы со связью"))
                    }
                }
            }
        })
    }

    private fun renderState(state: SearchScreenState) {
        stateLiveData.postValue(state)
    }

    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 1200L
        private val SEARCH_REQUEST_TOKEN = Any()
    }

}