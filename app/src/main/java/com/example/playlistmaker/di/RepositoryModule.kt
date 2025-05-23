package com.example.playlistmaker.di

import com.example.playlistmaker.search.data.TracksRepositoryImpl
import com.example.playlistmaker.search.domain.api.TracksRepository
import com.example.playlistmaker.settings.data.SettingsRepositoryImpl
import com.example.playlistmaker.settings.domain.api.SettingsRepository
import org.koin.dsl.module


val repositoryModule = module {

    single<TracksRepository> {
        TracksRepositoryImpl(get(), get())
    }

    single<SettingsRepository> {
        SettingsRepositoryImpl(get())
    }

}