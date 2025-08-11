package com.example.playlistmaker.playlist.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.playlists.domain.api.PlaylistsInteractor
import com.example.playlistmaker.playlists.domain.model.Playlist
import com.example.playlistmaker.sharing.domain.SharingInteractor
import com.example.playlistmaker.tracks.domian.models.Track
import kotlinx.coroutines.launch

class PlaylistViewModel(private val playlistsInteractor: PlaylistsInteractor,
    private  val sharingInteractor: SharingInteractor): ViewModel() {

    private lateinit var playlist: Playlist

    private val stateLiveData = MutableLiveData<PlaylistScreenState>()
    fun observeState(): LiveData<PlaylistScreenState> = stateLiveData

    lateinit var tracks: List<Track>

    private fun renderState(state: PlaylistScreenState) {
        stateLiveData.postValue(state)
    }

    fun showPlaylistInfo(playlis: Playlist) {
        playlist = playlis

        viewModelScope.launch {
            playlistsInteractor.playlists().collect { playlists ->
                playlists.forEach { playlistr ->
                    if (playlistr.id == playlis.id)
                        playlist = playlistr
                }
            }

            playlistsInteractor.tracksInPlaylist(playlist).collect { tracksList ->
                tracks = tracksList
                renderState(PlaylistScreenState.Content(playlist, tracks))
            }
        }
    }

    fun deleteTrack(track: Track) {
        viewModelScope.launch {
            playlistsInteractor.removeTrackFromPlaylist(track, playlist)
            showPlaylistInfo(playlist)
        }
    }

    fun deletePlaylist() {
        viewModelScope.launch {
            playlistsInteractor.removePlaylist(playlist)
        }
    }

}