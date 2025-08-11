package com.example.playlistmaker.playlists.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Playlist(
    val id: Long?,
    var name: String,
    var description: String?,
    val cover: String?,
    var count: Long
): Parcelable
