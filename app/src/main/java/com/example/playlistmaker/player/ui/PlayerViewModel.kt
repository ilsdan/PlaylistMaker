package com.example.playlistmaker.player.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.player.domain.TrackPlayer
import com.example.playlistmaker.player.domain.PlayStatus
import com.example.playlistmaker.player.domain.PlayerScreenState
import com.example.playlistmaker.search.domain.models.Track

class PlayerViewModel(
    private val track: Track,
    private val trackPlayer: TrackPlayer,
) : ViewModel() {

    private var screenStateLiveData = MutableLiveData<PlayerScreenState>(PlayerScreenState.Loading)
    fun getScreenStateLiveData(): LiveData<PlayerScreenState> = screenStateLiveData
    private val playStatusLiveData = MutableLiveData<PlayStatus>()
    fun getPlayStatusLiveData(): LiveData<PlayStatus> = playStatusLiveData

    private fun getCurrentPlayStatus(): PlayStatus {
        return playStatusLiveData.value ?: PlayStatus(progress = 0f, isPlaying = false)
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
                    playStatusLiveData.value = getCurrentPlayStatus().copy(progress = progress)
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

    companion object {
        fun getViewModelFactory(track: Track): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                PlayerViewModel(
                    track,
                    Creator.provideTrackPlayer()
                )
            }
        }
    }
}