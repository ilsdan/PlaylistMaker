package com.example.playlistmaker

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

data class Track(
    val trackName: String,
    val artistName: String,
    val trackTime: String,
    val trackTimeMillis: Long,
    val artworkUrl100: String?
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