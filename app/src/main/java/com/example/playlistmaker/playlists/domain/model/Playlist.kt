package com.example.playlistmaker.playlists.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Playlist(
    val id: Long?,
    val name: String,
    val description: String?,
    val cover: String?,
    var count: Long
): Parcelable
