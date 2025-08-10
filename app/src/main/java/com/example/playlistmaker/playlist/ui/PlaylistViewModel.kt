package com.example.playlistmaker.playlist.ui

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.playlists.domain.api.PlaylistsInteractor
import com.example.playlistmaker.playlists.domain.model.Playlist
import com.example.playlistmaker.search.domain.SearchScreenState
import kotlinx.coroutines.launch

class PlaylistViewModel(private val playlistsInteractor: PlaylistsInteractor): ViewModel() {

    private val stateLiveData = MutableLiveData<PlaylistScreenState>()
    fun observeState(): LiveData<PlaylistScreenState> = stateLiveData

    private fun renderState(state: PlaylistScreenState) {
        stateLiveData.postValue(state)
    }

    fun showPlaylistInfo(playlist: Playlist) {
        viewModelScope.launch {
            playlistsInteractor.tracksInPlaylist(playlist).collect { tracks ->
                Log.i("tracks", tracks.toString())
                renderState(PlaylistScreenState.Content(playlist, tracks))
            }
        }

    }
}