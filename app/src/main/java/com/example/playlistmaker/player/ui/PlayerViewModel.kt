package com.example.playlistmaker.player.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.player.domain.TrackPlayer
import com.example.playlistmaker.player.domain.PlayStatus
import com.example.playlistmaker.player.domain.PlayerScreenState
import com.example.playlistmaker.search.domain.api.TracksInteractor

class PlayerViewModel(
    private val tracksInteractor: TracksInteractor,
    private val trackPlayer: TrackPlayer
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
        if (tracksInteractor.currentTrack != null) {
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