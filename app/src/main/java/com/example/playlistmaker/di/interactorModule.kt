package com.example.playlistmaker.di

import com.example.playlistmaker.library.domain.db.FavoriteInteractor
import com.example.playlistmaker.library.domain.impl.FavoriteInteractorImpl
import com.example.playlistmaker.player.data.TrackPlayerImpl
import com.example.playlistmaker.player.domain.TrackPlayer
import com.example.playlistmaker.search.domain.api.TracksInteractor
import com.example.playlistmaker.search.domain.impl.TracksInteractorImpl
import com.example.playlistmaker.settings.domain.api.SettingsInteractor
import com.example.playlistmaker.settings.domain.impl.SettingsInteractorImpl
import com.example.playlistmaker.sharing.data.SharingInteractorImpl
import com.example.playlistmaker.sharing.domain.SharingInteractor
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val interactorModule = module {

    factory<TrackPlayer> {
        TrackPlayerImpl(get())
    }

    single<TracksInteractor> {
        TracksInteractorImpl(get())
    }

    factory<SharingInteractor> {
        SharingInteractorImpl(get(), androidContext())
    }

    factory<SettingsInteractor> {
        SettingsInteractorImpl(get())
    }

    factory<FavoriteInteractor> {
        FavoriteInteractorImpl(get())
    }
}