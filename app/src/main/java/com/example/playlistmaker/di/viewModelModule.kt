package com.example.playlistmaker.di

import com.example.playlistmaker.favorite.ui.FavoriteTracksViewModel
import com.example.playlistmaker.playlists.ui.PlaylistsViewModel
import com.example.playlistmaker.player.ui.PlayerViewModel
import com.example.playlistmaker.playlists.ui.NewPlaylistViewModel
import com.example.playlistmaker.search.ui.SearchViewModel
import com.example.playlistmaker.settings.ui.SettingsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {

    viewModel {
        SearchViewModel(get())
    }

    viewModel {
        SettingsViewModel(get(), get())
    }

    viewModel {
        PlayerViewModel(get(), get(), get())
    }

    viewModel {
        FavoriteTracksViewModel(get())
    }

    viewModel {
        PlaylistsViewModel(get())
    }

    viewModel {
        NewPlaylistViewModel(get())
    }
}