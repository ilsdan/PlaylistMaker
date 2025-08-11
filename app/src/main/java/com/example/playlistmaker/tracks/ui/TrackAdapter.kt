package com.example.playlistmaker.tracks.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R
import com.example.playlistmaker.tracks.domian.models.Track

interface OnItemClickListener {
    fun onItemClick(item: Track)
}

interface OnLongClickListener {
    fun onLongClick(item: Track) : Boolean
}

class TrackAdapter(private val onItemClickListener: OnItemClickListener) : RecyclerView.Adapter<TrackViewHolder>() {

    var tracks: MutableList<Track> = mutableListOf()

    lateinit var onLongClickListener: OnLongClickListener
    var isLongClickListener = false

    fun addLongClickListener(clickListener: OnLongClickListener) {
        isLongClickListener = true
        onLongClickListener = clickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val binding =
            LayoutInflater.from(parent.context).inflate(R.layout.track_view, parent, false)
        return TrackViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        holder.bind(tracks[position])
        holder.itemView.setOnClickListener { onItemClickListener.onItemClick(tracks[holder.adapterPosition]) }
        if (isLongClickListener)
            holder.itemView.setOnLongClickListener {onLongClickListener.onLongClick(tracks[holder.adapterPosition])}

    }

    override fun getItemCount(): Int {
        return tracks.size
    }
}