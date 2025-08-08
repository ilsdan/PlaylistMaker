package com.example.playlistmaker.di

import com.example.playlistmaker.favorite.domain.db.FavoriteInteractor
import com.example.playlistmaker.favorite.domain.impl.FavoriteInteractorImpl
import com.example.playlistmaker.player.data.TrackPlayerImpl
import com.example.playlistmaker.player.domain.TrackPlayer
import com.example.playlistmaker.playlists.domain.api.PlaylistsInteractor
import com.example.playlistmaker.playlists.domain.impl.PlaylistsInteractorImpl
import com.example.playlistmaker.tracks.domian.api.TracksInteractor
import com.example.playlistmaker.tracks.domian.impl.TracksInteractorImpl
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

    factory<TracksInteractor> {
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

    factory<PlaylistsInteractor> {
        PlaylistsInteractorImpl(get())
    }
}