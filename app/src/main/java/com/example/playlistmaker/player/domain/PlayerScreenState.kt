package com.example.playlistmaker.player.domain

import com.example.playlistmaker.tracks.domian.models.Track

sealed class PlayerScreenState {
    object Loading: PlayerScreenState()
    data class Content(
        val trackModel: Track,
    ): PlayerScreenState()
}