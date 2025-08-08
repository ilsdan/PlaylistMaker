package com.example.playlistmaker.tracks.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.playlistmaker.tracks.data.db.entity.PlaylistTracksCountEntity
import com.example.playlistmaker.tracks.data.db.entity.TrackPlaylistsEntity

@Dao
interface TrackPlaylistsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrackPlaylists(trackPlaylistsEntity: TrackPlaylistsEntity)

    @Query("SELECT * FROM track_playlists WHERE track_id = :trackId")
    suspend fun getPlaylistsByTrackId(trackId: Long): List<TrackPlaylistsEntity>

    @Query("SELECT playlist_id, COUNT(*) count FROM track_playlists GROUP BY playlist_id")
    suspend fun getPlaylistsTrackCount(): List<PlaylistTracksCountEntity>


}