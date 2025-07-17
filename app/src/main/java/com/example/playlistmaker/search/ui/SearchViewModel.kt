package com.example.playlistmaker.search.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.search.domain.SearchScreenState
import com.example.playlistmaker.search.domain.api.TracksInteractor
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.utils.debounce
import kotlinx.coroutines.launch

class SearchViewModel(private val tracksInteractor: TracksInteractor) : ViewModel() {

    private val stateLiveData = MutableLiveData<SearchScreenState>()
    fun observeState(): LiveData<SearchScreenState> = stateLiveData

    private var latestSearchText: String? = null

    private val tracksSearchDebounce = debounce<String>(SEARCH_DEBOUNCE_DELAY, viewModelScope, true) { changedText ->
        searchRequest(changedText)
    }

    fun searchDebounce(changedText: String) {
        if (latestSearchText != changedText) {
            latestSearchText = changedText
            tracksSearchDebounce(changedText)
        }
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

    private fun searchRequest(newSearchText: String) {
        if (newSearchText.isNotEmpty()) {

            renderState(SearchScreenState.Loading)

            viewModelScope.launch {
                tracksInteractor
                    .searchTracks(newSearchText)
                    .collect { pair ->
                        if (pair.second == null){
                            renderState(SearchScreenState.Content(pair.first!!))
                        } else {
                            if (pair.second == "Проверьте подключение к интернету") {
                                renderState(SearchScreenState.NetworkError("Проблемы со связью"))
                            }
                        }
                        if (pair.first != null) {
                            if (pair.first!!.isEmpty()) {
                                renderState(SearchScreenState.EmptyError("Ничего не нашлось"))
                            }
                        }
                    }
            }
        }
    }

    private fun renderState(state: SearchScreenState) {
        stateLiveData.postValue(state)
    }

    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 1200L
    }

}