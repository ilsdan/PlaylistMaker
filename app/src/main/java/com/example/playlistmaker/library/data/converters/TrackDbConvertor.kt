package com.example.playlistmaker.library.data.converters

import com.example.playlistmaker.library.data.db.entity.TrackEntity
import com.example.playlistmaker.search.domain.models.Track

class TrackDbConvertor {
    fun map(track: Track): TrackEntity {
        return TrackEntity(
            track.trackId,
            track.trackName.toString(),
            track.artistName,
            track.trackTimeMillis.toString(),
            track.artworkUrl100.toString(),
            track.collectionName.toString(),
            track.releaseDate,
            track.primaryGenreName,
            track.country,
            track.previewUrl.toString()
        )
    }

    fun map(track: TrackEntity): Track {
        return Track(
            track.trackId,
            false,
            track.trackName.toString(),
            track.artistName,
            track.trackTime.toString(),
            track.trackTime.toLong(),
            track.artworkUrl100.toString(),
            track.collectionName.toString(),
            track.releaseDate,
            track.primaryGenreName,
            track.country,
            track.previewUrl.toString()
        )
    }
}