package com.example.playlistmaker.di

import androidx.lifecycle.ViewModelProvider
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
        TrackPlayerImpl()
    }

    single<TracksInteractor> {
        TracksInteractorImpl(get())
    }

    single<SharingInteractor> {
        SharingInteractorImpl(get(), androidContext())
    }

    single<SettingsInteractor> {
        SettingsInteractorImpl(get())
    }

}