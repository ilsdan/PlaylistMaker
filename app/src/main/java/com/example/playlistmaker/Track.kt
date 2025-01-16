package com.example.playlistmaker

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

data class Track(
    val trackId: Int,
    val trackName: String,
    val artistName: String,
    val trackTime: String,
    val trackTimeMillis: Long,
    val artworkUrl100: String?,
    val collectionName: String?,
    val releaseDate: String,
    val primaryGenreName: String,
    val country: String,
    val previewUrl: String
)

data class TrackResponse(
    val resultCount: Int,
    val results: List<Track>
)

interface iTunesApi {
    @GET("search")
    fun searchTracks(
        @Query("term") term: String,
        @Query("results") entity: String = "song"
    ): Call<TrackResponse>

}