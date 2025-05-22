package com.example.playlistmaker.creator

import android.content.Context
import com.example.playlistmaker.player.domain.TrackPlayer
import com.example.playlistmaker.player.data.TrackPlayerImpl
import com.example.playlistmaker.settings.data.SettingsRepositoryImpl
import com.example.playlistmaker.search.data.network.RetrofitNetworkClient
import com.example.playlistmaker.search.data.TracksRepositoryImpl
import com.example.playlistmaker.settings.domain.api.SettingsRepository
import com.example.playlistmaker.search.domain.api.TracksInteractor
import com.example.playlistmaker.search.domain.api.TracksRepository
import com.example.playlistmaker.search.domain.impl.TracksInteractorImpl
import com.example.playlistmaker.search.data.LocalStorage
import com.example.playlistmaker.settings.data.LocalSettingsStorage
import com.example.playlistmaker.settings.domain.api.SettingsInteractor
import com.example.playlistmaker.settings.domain.impl.SettingsInteractorImpl
import com.example.playlistmaker.sharing.data.ExternalNavigator
import com.example.playlistmaker.sharing.data.SharingInteractorImpl
import com.example.playlistmaker.sharing.domain.SharingInteractor

object Creator {
    private fun getTracksRepository(context: Context): TracksRepository {
        return TracksRepositoryImpl(RetrofitNetworkClient(), LocalStorage(context.getSharedPreferences("local_storage", Context.MODE_PRIVATE)))
    }

    fun provideTracksInteractor(context: Context): TracksInteractor {
        return TracksInteractorImpl(getTracksRepository(context))
    }

    private fun getSettingsRepository(context: Context): SettingsRepository {
        return SettingsRepositoryImpl(LocalSettingsStorage(context.getSharedPreferences("local_storage", Context.MODE_PRIVATE)))
    }

    fun provideSettingsInteractor(context: Context): SettingsInteractor {
        return SettingsInteractorImpl(getSettingsRepository(context), context)
    }

    fun getExternalNavigator(context: Context): ExternalNavigator {
        return ExternalNavigator(context)
    }

    fun provideSharingInteractor(context: Context): SharingInteractor {
        return SharingInteractorImpl(getExternalNavigator(context), context)
    }

    fun provideTrackPlayer(): TrackPlayer {
        return TrackPlayerImpl()
    }
}