package com.example.playlistmaker.di

import com.example.playlistmaker.favorite.data.FavoriteRepositoryImpl
import com.example.playlistmaker.tracks.data.converters.TrackDbConvertor
import com.example.playlistmaker.favorite.domain.db.FavoriteRepository
import com.example.playlistmaker.playlists.data.PlaylistRepositoryImpl
import com.example.playlistmaker.playlists.domain.api.PlaylistRepository
import com.example.playlistmaker.tracks.data.impl.TracksRepositoryImpl
import com.example.playlistmaker.tracks.domian.api.TracksRepository
import com.example.playlistmaker.settings.data.SettingsRepositoryImpl
import com.example.playlistmaker.settings.domain.api.SettingsRepository
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module


val repositoryModule = module {

    factory<TracksRepository> {
        TracksRepositoryImpl(get(), get())
    }

    factory<SettingsRepository> {
        SettingsRepositoryImpl(get())
    }

    factory { TrackDbConvertor() }

    factory<FavoriteRepository> {
        FavoriteRepositoryImpl(get(), get())
    }

    factory<PlaylistRepository> {
        PlaylistRepositoryImpl(get(), get(), get(), get(),get())
    }
}