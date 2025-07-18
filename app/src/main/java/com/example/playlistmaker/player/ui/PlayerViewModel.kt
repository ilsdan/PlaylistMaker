package com.example.playlistmaker.player.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.library.domain.db.FavoriteInteractor
import com.example.playlistmaker.player.domain.TrackPlayer
import com.example.playlistmaker.player.domain.PlayStatus
import com.example.playlistmaker.player.domain.PlayerScreenState
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.launch

class PlayerViewModel(
    private val favoriteInteractor: FavoriteInteractor,
    private val trackPlayer: TrackPlayer
) : ViewModel() {

    private var screenStateLiveData = MutableLiveData<PlayerScreenState>(PlayerScreenState.Loading)
    fun getScreenStateLiveData(): LiveData<PlayerScreenState> = screenStateLiveData
    private val playStatusLiveData = MutableLiveData<PlayStatus>()
    fun getPlayStatusLiveData(): LiveData<PlayStatus> = playStatusLiveData
    private val favoriteStatusLiveData = MutableLiveData<Boolean>()
    fun getFavoriteStatusLiveData(): LiveData<Boolean> = favoriteStatusLiveData

    private fun getCurrentPlayStatus(): PlayStatus {
        return playStatusLiveData.value ?: PlayStatus(progress = 0f, isPlaying = false)
    }

    lateinit var track: Track

    fun toggleFavorite() {
        viewModelScope.launch {
            favoriteInteractor.isTrackFavorite(track.trackId).collect { isFavorite ->
                track.isFavorite = !isFavorite
                if (!isFavorite) {
                    addToFavorite(track)
                } else {
                    removeFromFavorite(track)
                }
                favoriteStatusLiveData.postValue(
                    track.isFavorite
                )
            }
        }
    }

    fun isTrackFavorite() {
        viewModelScope.launch {
            favoriteInteractor.isTrackFavorite(track.trackId).collect { isFavorite ->
                if (isFavorite) {
                    track.isFavorite = true
                    favoriteStatusLiveData.postValue(true)
                }
            }
        }
    }

    suspend fun removeFromFavorite(track: Track) {
        favoriteInteractor.removeFavoriteTrack(track)
    }

    suspend fun addToFavorite(track: Track) {
        favoriteInteractor.addFavoriteTrack(track)
    }

    fun playerPause() {
        trackPlayer.pause()
    }

    fun playbackControl(){
        trackPlayer.playbackControl()
    }

    override fun onCleared() {
        playerPause()
        trackPlayer.release()
    }

    fun initPlayer(_track: Track){

        track = _track

        isTrackFavorite()
        screenStateLiveData.postValue(
            PlayerScreenState.Loading
        )
        trackPlayer.prepare(
            trackURL = track.previewUrl.toString(),
            statusObserver = object : TrackPlayer.StatusObserver {
                override fun onPrepared() {
                    screenStateLiveData.postValue(
                        PlayerScreenState.Content(track)
                    )
                }

                override fun onCompletion() {
                    playStatusLiveData.value = PlayStatus(progress = 0f, isPlaying = false)
                }

                override fun onProgress(progress: Float) {
                    playStatusLiveData.postValue(getCurrentPlayStatus().copy(progress = progress))
                }

                override fun onStop() {
                    playStatusLiveData.value = getCurrentPlayStatus().copy(isPlaying = false)
                }

                override fun onPlay() {
                    playStatusLiveData.value = getCurrentPlayStatus().copy(isPlaying = true)
                }
            },
        )
    }
}