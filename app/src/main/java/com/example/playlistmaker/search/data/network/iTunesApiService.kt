package com.example.playlistmaker.search.data.network

import com.example.playlistmaker.search.data.dto.TracksSearchResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface iTunesApiService {
    @GET("search")
    fun searchTracks(
        @Query("term") term: String,
        @Query("results") entity: String = "song"
    ): Call<TracksSearchResponse>
}