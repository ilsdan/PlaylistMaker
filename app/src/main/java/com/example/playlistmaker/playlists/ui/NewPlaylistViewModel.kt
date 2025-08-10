package com.example.playlistmaker.playlists.ui

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.playlists.domain.api.PlaylistsInteractor
import kotlinx.coroutines.launch

class NewPlaylistViewModel(
    private val playlistsInteractor: PlaylistsInteractor
) : ViewModel() {

    fun addPlaylist(name: String, description: String?, imageUri: Uri?) {
        viewModelScope.launch {
            playlistsInteractor.addPlaylist(name, description, imageUri)
        }
    }
}