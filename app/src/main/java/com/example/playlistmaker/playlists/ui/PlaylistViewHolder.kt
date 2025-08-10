package com.example.playlistmaker.playlists.ui

import android.os.Environment
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R
import com.example.playlistmaker.playlists.domain.model.Playlist
import java.io.File

class PlaylistViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
    private val title: TextView = itemView.findViewById(R.id.PlaylistName)
    private val description: TextView = itemView.findViewById(R.id.trackCount)
    private val cover: ImageView = itemView.findViewById(R.id.PlaylistCover)

    fun bind(playlist: Playlist) {
        title.text = playlist.name

        description.text = itemView.context.resources.getQuantityString(R.plurals.plurals_tracks, playlist.count.toInt(), playlist.count.toInt())

        if (playlist.cover != null){
            val filePath = File(itemView.context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "Covers")
            val file = File(filePath, playlist.cover)
            cover.setImageURI(file.toUri())
        }
    }
}