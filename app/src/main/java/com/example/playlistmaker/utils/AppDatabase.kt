package com.example.playlistmaker.utils

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.playlistmaker.tracks.data.db.dao.TrackDao
import com.example.playlistmaker.tracks.data.db.entity.TrackEntity
import com.example.playlistmaker.playlists.data.db.dao.PlaylistDao
import com.example.playlistmaker.playlists.data.db.entity.PlaylistEntity
import com.example.playlistmaker.tracks.data.db.dao.TrackPlaylistsDao
import com.example.playlistmaker.tracks.data.db.entity.TrackPlaylistsEntity

@Database(version = 1, entities = [TrackEntity::class, PlaylistEntity::class, TrackPlaylistsEntity::class])
abstract class AppDatabase : RoomDatabase(){

    abstract fun trackDao(): TrackDao
    abstract fun playlistDao(): PlaylistDao
    abstract fun trackPlaylistsDao(): TrackPlaylistsDao

}