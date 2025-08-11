package com.example.playlistmaker.playlists.ui

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.playlists.domain.api.PlaylistsInteractor
import com.example.playlistmaker.playlists.domain.model.Playlist
import kotlinx.coroutines.launch

class NewPlaylistViewModel(
    private val playlistsInteractor: PlaylistsInteractor
) : ViewModel() {

    fun addPlaylist(name: String, description: String?, imageUri: Uri?) {
        viewModelScope.launch {
            playlistsInteractor.addPlaylist(name, description, imageUri)
        }
    }

    fun updatePlaylist(playlistId: Long, name: String, description: String?, imageUri: Uri?){
        viewModelScope.launch {
            playlistsInteractor.updatePlaylist(playlistId, name, description , imageUri)
        }
    }
}