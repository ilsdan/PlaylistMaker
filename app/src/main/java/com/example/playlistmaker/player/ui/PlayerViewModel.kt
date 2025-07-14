package com.example.playlistmaker.player.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.library.domain.db.FavoriteInteractor
import com.example.playlistmaker.player.domain.TrackPlayer
import com.example.playlistmaker.player.domain.PlayStatus
import com.example.playlistmaker.player.domain.PlayerScreenState
import com.example.playlistmaker.search.domain.api.TracksInteractor
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.launch

class PlayerViewModel(
    private val tracksInteractor: TracksInteractor,
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

    fun toggleFavorite() {
        viewModelScope.launch {
            favoriteInteractor.isTrackFavorite(tracksInteractor.currentTrack!!.trackId).collect { isFavorite ->
                tracksInteractor.currentTrack!!.isFavorite = !isFavorite
                if (!isFavorite) {
                    addToFavorite(tracksInteractor.currentTrack!!)
                } else {
                    removeFromFavorite(tracksInteractor.currentTrack!!)
                }
                favoriteStatusLiveData.postValue(
                    tracksInteractor.currentTrack!!.isFavorite
                )
            }
        }
    }

    fun isTrackFavorite() {
        viewModelScope.launch {
            favoriteInteractor.isTrackFavorite(tracksInteractor.currentTrack!!.trackId).collect { isFavorite ->
                if (isFavorite) {
                    tracksInteractor.currentTrack!!.isFavorite = true
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

    init {
        if (tracksInteractor.currentTrack != null) {
            isTrackFavorite()
            screenStateLiveData.postValue(
                PlayerScreenState.Loading
            )
            trackPlayer.prepare(
                trackURL = tracksInteractor.currentTrack!!.previewUrl.toString(),
                statusObserver = object : TrackPlayer.StatusObserver {
                    override fun onPrepared() {
                        screenStateLiveData.postValue(
                            PlayerScreenState.Content(tracksInteractor.currentTrack!!)
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
}