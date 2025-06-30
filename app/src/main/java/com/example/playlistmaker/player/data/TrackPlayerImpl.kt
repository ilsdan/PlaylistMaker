package com.example.playlistmaker.player.data

import android.media.MediaPlayer
import com.example.playlistmaker.player.domain.TrackPlayer
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class TrackPlayerImpl(private var mediaPlayer: MediaPlayer) : TrackPlayer {

    private lateinit var statusObserver: TrackPlayer.StatusObserver

    private var timerJob: Job? = null

    private var playerState = STATE_DEFAULT

    private fun startTimer() {
        timerJob = GlobalScope.launch {
            while (playerState == STATE_PLAYING) {
                delay(TIMER_DELAY)
                statusObserver.onProgress(mediaPlayer.currentPosition.toFloat())
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
            timerJob?.cancel()
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
        startTimer()
    }

    override fun pause() {
        mediaPlayer.pause()
        playerState = STATE_PAUSED
        statusObserver.onStop()
        timerJob?.cancel()
    }

    override fun release() {
        mediaPlayer.release()
    }

    companion object {
        private const val STATE_DEFAULT = 0
        private const val STATE_PREPARED = 1
        private const val STATE_PLAYING = 2
        private const val STATE_PAUSED = 3

        private const val TIMER_DELAY = 300L
    }

}