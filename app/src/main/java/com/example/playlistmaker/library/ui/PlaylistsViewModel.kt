package com.example.playlistmaker.library.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.library.domain.db.FavoriteInteractor
import kotlinx.coroutines.launch

class PlaylistsViewModel(
    private val favoriteInteractor: FavoriteInteractor
) : ViewModel() {

    private val stateLiveData = MutableLiveData<FavoriteScreenState>()
    fun observeState(): LiveData<FavoriteScreenState> = stateLiveData

    private fun renderState(state: FavoriteScreenState) {
        stateLiveData.postValue(state)
    }

    fun show(){
        renderState(FavoriteScreenState.Loading)
        viewModelScope.launch {
            favoriteInteractor.favoriteTracks().collect { tracks ->
                if (tracks.isEmpty())
                    renderState(FavoriteScreenState.EmptyError)
                else
                    renderState(FavoriteScreenState.Content(tracks))
            }
        }
    }
}