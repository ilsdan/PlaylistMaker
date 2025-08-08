package com.example.playlistmaker.playlists.ui

import android.os.Environment
import com.example.playlistmaker.R
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.playlists.domain.model.Playlist
import java.io.File

class PlaylistCardViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

    private val title: TextView = itemView.findViewById(R.id.namePlaylist)
    private val description: TextView = itemView.findViewById(R.id.countTracks)
    private val cover: ImageView = itemView.findViewById(R.id.playlistCover)

    fun bind(playlist: Playlist) {
        title.text = playlist.name

        when (playlist.count.toInt() % 10) {
            1 -> description.text = "${playlist.count} трек"
            2, 3, 4 -> description.text = "${playlist.count} трека"
            else ->  description.text = "${playlist.count} треков"
        }

        if (playlist.cover != null){
            val filePath = File(itemView.context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "Covers")
            val file = File(filePath, playlist.cover)
            cover.setImageURI(file.toUri())
        }
    }
}