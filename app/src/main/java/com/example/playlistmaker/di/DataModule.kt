package com.example.playlistmaker.di

import android.content.Context
import android.media.MediaPlayer
import com.example.playlistmaker.search.data.LocalStorage
import com.example.playlistmaker.search.data.network.NetworkClient
import com.example.playlistmaker.search.data.network.RetrofitNetworkClient
import com.example.playlistmaker.search.data.network.iTunesApiService
import com.example.playlistmaker.settings.data.LocalSettingsStorage
import com.example.playlistmaker.sharing.data.ExternalNavigator
import com.google.gson.Gson
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val dataModule = module {

    single {
        androidContext()
            .getSharedPreferences("local_storage", Context.MODE_PRIVATE)
    }

    single<iTunesApiService> {
        Retrofit.Builder()
            .baseUrl("https://itunes.apple.com")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(iTunesApiService::class.java)
    }

    factory { Gson() }

    single<LocalStorage> {
        LocalStorage(get(), get())
    }

    single<LocalSettingsStorage> {
        LocalSettingsStorage(get())
    }

    single {
        ExternalNavigator(androidContext())
    }

    single<NetworkClient> {
        RetrofitNetworkClient(get())
    }

    factory { MediaPlayer() }


}