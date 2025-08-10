package com.example.playlistmaker.playlists.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.playlists.domain.api.PlaylistsInteractor
import kotlinx.coroutines.launch

class PlaylistsViewModel(
    private val playlistsInteractor: PlaylistsInteractor
) : ViewModel() {

    private val stateLiveData = MutableLiveData<PlaylistScreenState>()
    fun observeState(): LiveData<PlaylistScreenState> = stateLiveData

    private fun renderState(state: PlaylistScreenState) {
        stateLiveData.postValue(state)
    }

    fun show(){
        viewModelScope.launch {
            playlistsInteractor.playlists().collect { playlists ->
                if (playlists.isEmpty()) {
                    renderState(PlaylistScreenState.Empty)
                } else {
                    renderState(PlaylistScreenState.Content(playlists))
                }
            }
        }
    }
}