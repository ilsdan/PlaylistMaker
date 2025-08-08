package com.example.playlistmaker.tracks.data.converters

import com.example.playlistmaker.tracks.data.db.entity.TrackEntity
import com.example.playlistmaker.tracks.domian.models.Track

class TrackDbConvertor {
    fun map(track: Track): TrackEntity {
        return TrackEntity(
            track.trackId,
            track.isFavorite,
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

    fun map(trackEntity: TrackEntity): Track {
        return Track(
            trackEntity.trackId,
            false,
            trackEntity.trackName.toString(),
            trackEntity.artistName,
            trackEntity.trackTime.toString(),
            trackEntity.trackTime.toLong(),
            trackEntity.artworkUrl100.toString(),
            trackEntity.collectionName.toString(),
            trackEntity.releaseDate,
            trackEntity.primaryGenreName,
            trackEntity.country,
            trackEntity.previewUrl.toString()
        )
    }
}