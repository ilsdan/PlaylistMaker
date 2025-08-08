package com.example.playlistmaker.playlists.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.playlistmaker.playlists.data.db.entity.PlaylistEntity

@Dao
interface PlaylistDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlaylist(playlistEntity: PlaylistEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlaylists(playlistsEntity: List<PlaylistEntity>)

    //"SELECT playlists.id, playlists.name, playlists.description, playlists.cover, COUNT(*) count  FROM playlists INNER JOIN track_playlists ON playlists.id=track_playlists.playlist_id"
    @Query("SELECT * FROM playlists")
    suspend fun getPlaylists(): List<PlaylistEntity>

    @Delete(entity = PlaylistEntity::class)
    suspend fun deletePlaylistEntity(playlistsEntity: PlaylistEntity)
}