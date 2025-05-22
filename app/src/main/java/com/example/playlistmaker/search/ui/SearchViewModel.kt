package com.example.playlistmaker.search.ui

import android.app.Application
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.search.domain.SearchScreenState
import com.example.playlistmaker.search.domain.api.TracksInteractor
import com.example.playlistmaker.search.domain.models.Track

class SearchViewModel(application: Application) : AndroidViewModel(application) {

    private val tracksInteractor = Creator.provideTracksInteractor(getApplication())
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

        fun getViewModelFactory(): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                SearchViewModel(this[APPLICATION_KEY] as Application)
            }
        }
    }

}