package com.example.playlistmaker.playlists.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R
import com.example.playlistmaker.playlists.domain.model.Playlist

interface OnItemClickListener {
    fun onItemClick(item: Playlist)
}

class PlaylistAdapter(private val onItemClickListener: OnItemClickListener): RecyclerView.Adapter<PlaylistViewHolder>() {

    var playlist: MutableList<Playlist> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.playlist_view, parent, false)
        return PlaylistViewHolder(view)
    }

    override fun getItemCount(): Int {
        return playlist.size
    }

    override fun onBindViewHolder(holder: PlaylistViewHolder, position: Int) {
        holder.bind(playlist[position])
        holder.itemView.setOnClickListener { onItemClickListener.onItemClick(playlist[holder.adapterPosition]) }
    }
}