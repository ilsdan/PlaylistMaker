package com.example.playlistmaker.player.data

interface TrackPlayer {
    fun prepare(trackURL: String, statusObserver: StatusObserver)
    fun play()
    fun pause()
    fun playbackControl()

    fun release()

    interface StatusObserver {
        fun onPrepared()
        fun onCompletion()
        fun onProgress(progress: Float)
        fun onStop()
        fun onPlay()
    }
}