package com.example.playlistmaker.di

import com.example.playlistmaker.library.data.FavoriteRepositoryImpl
import com.example.playlistmaker.library.data.converters.TrackDbConvertor
import com.example.playlistmaker.library.domain.db.FavoriteRepository
import com.example.playlistmaker.search.data.TracksRepositoryImpl
import com.example.playlistmaker.search.domain.api.TracksRepository
import com.example.playlistmaker.settings.data.SettingsRepositoryImpl
import com.example.playlistmaker.settings.domain.api.SettingsRepository
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
}