package com.example.playlistmaker.search.domain

import com.example.playlistmaker.search.domain.models.Track

sealed interface SearchScreenState {
    object Loading : SearchScreenState
    object Empty : SearchScreenState

    data class Content(
        val tracks: List<Track>
    ) : SearchScreenState

    data class History(
        val tracks: List<Track>
    ) : SearchScreenState

    data class EmptyError(
        val errorMessage: String
    ) : SearchScreenState

    data class NetworkError(
        val errorMessage: String
    ) : SearchScreenState
}