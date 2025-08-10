package com.example.playlistmaker.playlists.ui

import com.example.playlistmaker.R
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.playlists.domain.model.Playlist

class PlaylistCardAdapter(): RecyclerView.Adapter<PlaylistCardViewHolder>() {

    var playlist: MutableList<Playlist> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistCardViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.playlist_card, parent, false)
        return PlaylistCardViewHolder(view)
    }

    override fun getItemCount(): Int {
        return playlist.size
    }

    override fun onBindViewHolder(holder: PlaylistCardViewHolder, position: Int) {
        holder.bind(playlist[position])
    }
}