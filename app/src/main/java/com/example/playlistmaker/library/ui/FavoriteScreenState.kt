package com.example.playlistmaker.library.ui

import com.example.playlistmaker.search.domain.models.Track

sealed interface FavoriteScreenState {
    object Loading : FavoriteScreenState
    object EmptyError : FavoriteScreenState
    data class Content(
        val tracks: List<Track>
    ) : FavoriteScreenState

}