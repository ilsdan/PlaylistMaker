package com.example.playlistmaker.favorite.ui

import com.example.playlistmaker.tracks.domian.models.Track

sealed interface FavoriteScreenState {
    object Loading : FavoriteScreenState
    object EmptyError : FavoriteScreenState
    data class Content(
        val tracks: List<Track>
    ) : FavoriteScreenState
}