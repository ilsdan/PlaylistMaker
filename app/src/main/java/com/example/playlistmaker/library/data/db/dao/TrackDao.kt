package com.example.playlistmaker.library.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.playlistmaker.library.data.db.entity.TrackEntity

@Dao
interface TrackDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrack(track: TrackEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTracks(tracks: List<TrackEntity>)

    @Query("SELECT * FROM favorite_tracks")
    suspend fun getFavoriteTracks(): List<TrackEntity>

    @Query("SELECT * FROM favorite_tracks WHERE id = :id")
    suspend fun getTrack(id: Long): List<TrackEntity>

    @Update(entity = TrackEntity::class, onConflict = OnConflictStrategy.REPLACE)
    fun updateTrack(trackEntity: TrackEntity)

    @Delete(entity = TrackEntity::class)
    suspend fun deleteTrackEntity(trackEntity: TrackEntity)

}