package com.example.playlistmaker.player.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.favorite.domain.db.FavoriteInteractor
import com.example.playlistmaker.player.domain.TrackPlayer
import com.example.playlistmaker.player.domain.PlayStatus
import com.example.playlistmaker.player.domain.PlayerScreenState
import com.example.playlistmaker.playlists.domain.api.PlaylistsInteractor
import com.example.playlistmaker.playlists.domain.model.Playlist
import com.example.playlistmaker.playlists.ui.PlaylistScreenState
import com.example.playlistmaker.tracks.domian.models.Track
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch

class PlayerViewModel(
    private val favoriteInteractor: FavoriteInteractor,
    private val playlistsInteractor: PlaylistsInteractor,
    private var trackPlayer: TrackPlayer
) : ViewModel() {

    private var screenStateLiveData = MutableLiveData<PlayerScreenState>(PlayerScreenState.Loading)
    fun getScreenStateLiveData(): LiveData<PlayerScreenState> = screenStateLiveData
    private val playStatusLiveData = MutableLiveData<PlayStatus>()
    fun getPlayStatusLiveData(): LiveData<PlayStatus> = playStatusLiveData
    private val favoriteStatusLiveData = MutableLiveData<Boolean>()
    fun getFavoriteStatusLiveData(): LiveData<Boolean> = favoriteStatusLiveData

    private val playlistsLiveData = MutableLiveData<PlaylistScreenState>()
    fun getPlaylistsLiveData(): LiveData<PlaylistScreenState> = playlistsLiveData

    private fun getCurrentPlayStatus(): PlayStatus {
        return playStatusLiveData.value ?: PlayStatus(progress = 0f, isPlaying = false)
    }

    val toastChannel = Channel<Pair<Playlist, Boolean>>()

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

    fun addToPlaylist(playlist: Playlist) {
        viewModelScope.launch {
            playlistsInteractor.isTrackInPlaylist(track, playlist).collect { inPlaylist ->
                if (inPlaylist) {
                    toastChannel.send(playlist to true)
                } else {
                    playlistsInteractor.addTrackToPlaylist(playlist, track)
                    toastChannel.send(playlist to false)
                    show()
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

    private fun renderPlaylist(state: PlaylistScreenState) {
        playlistsLiveData.postValue(state)
    }

    fun show(){
        viewModelScope.launch {
            playlistsInteractor.playlists().collect { playlists ->
                renderPlaylist(PlaylistScreenState.Content(playlists))
            }
        }
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