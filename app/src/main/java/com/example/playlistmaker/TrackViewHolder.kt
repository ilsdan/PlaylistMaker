package com.example.playlistmaker

import android.icu.text.SimpleDateFormat
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import java.util.Locale

class TrackViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private val trackNameTextView: TextView = itemView.findViewById(R.id.trackName)
    private val trackDescriptionTextView: TextView = itemView.findViewById(R.id.trackDescription)
    private val artworkUrl100ImageView: ImageView = itemView.findViewById(R.id.TrackCover)

    fun bind(track: Track) {
        trackNameTextView.text = track.trackName
        trackDescriptionTextView.text = "${track.artistName} ◉ ${SimpleDateFormat("mm:ss", Locale.getDefault()).format(track.trackTimeMillis)}"

        Glide.with(itemView.context).load(track.artworkUrl100)
            .placeholder(R.drawable.track_placeholder)
            .fitCenter()
            .centerCrop()
            .transform(RoundedCorners(2))
            .into(artworkUrl100ImageView)
    }
}