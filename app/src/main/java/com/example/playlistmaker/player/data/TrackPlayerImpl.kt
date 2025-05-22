package com.example.playlistmaker.player.data

import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import com.example.playlistmaker.player.domain.TrackPlayer

class TrackPlayerImpl : TrackPlayer {

    private var mediaPlayer = MediaPlayer()

    private lateinit var statusObserver: TrackPlayer.StatusObserver

    private val handler = Handler(Looper.getMainLooper())

    companion object {
        private const val STATE_DEFAULT = 0
        private const val STATE_PREPARED = 1
        private const val STATE_PLAYING = 2
        private const val STATE_PAUSED = 3

        private const val TIMER_DELAY = 100L
    }

    private var playerState = STATE_DEFAULT

    private val timer = object : Runnable {
        override fun run() {
            if (playerState == STATE_PLAYING) {
                statusObserver.onProgress(mediaPlayer.currentPosition.toFloat())
                handler.postDelayed(this, TIMER_DELAY)
            }
        }
    }

    override fun prepare(url: String, statusObserver: TrackPlayer.StatusObserver) {
        mediaPlayer.setDataSource(url)
        mediaPlayer.prepareAsync()
        this.statusObserver = statusObserver
        mediaPlayer.setOnPreparedListener {
            statusObserver.onPrepared()
            playerState = STATE_PREPARED
        }
        mediaPlayer.setOnCompletionListener {
            statusObserver.onCompletion()
            playerState = STATE_PREPARED
            handler.removeCallbacks(timer)
        }
    }

    override fun playbackControl() {
        when (playerState) {
            STATE_PLAYING -> pause()
            STATE_PREPARED, STATE_PAUSED -> play()
        }
    }

    override fun play() {
        mediaPlayer.start()
        playerState = STATE_PLAYING
        statusObserver.onPlay()
        handler.post(timer)
    }

    override fun pause() {
        mediaPlayer.pause()
        playerState = STATE_PAUSED
        statusObserver.onStop()
        handler.removeCallbacks(timer)
    }

    override fun release() {
        mediaPlayer.release()
    }

}