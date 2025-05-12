package com.example.playlistmaker.ui

import android.icu.text.SimpleDateFormat
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.models.Track
import com.google.gson.Gson
import java.util.Locale

class AudioPlayerActivity : AppCompatActivity() {

    private  lateinit var cover: ImageView
    private  lateinit var trackName: TextView
    private  lateinit var artistName: TextView
    private  lateinit var trackTime: TextView
    private  lateinit var collectionName: TextView
    private  lateinit var releaseDate: TextView
    private  lateinit var primaryGenreName: TextView
    private lateinit var country: TextView

    private lateinit var currentTrackTime: TextView

    private lateinit var playButton: ImageView
    private lateinit var collectionButton: ImageView
    private lateinit var likeButton: ImageView
    private lateinit var progressBar: ProgressBar

    private var like = false
    private var inCollection = false

    private lateinit var track: Track

    private lateinit var toolbar: Toolbar

    private var mediaPlayer = MediaPlayer()

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
                currentTrackTime.text = SimpleDateFormat("m:ss", Locale.getDefault()).format(mediaPlayer.currentPosition)
                handler.postDelayed(this, TIMER_DELAY)
            }
        }
    }

    private fun preparePlayer(url: String) {
        mediaPlayer.setDataSource(url)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            progressBar.isVisible = false
            playButton.isVisible = true
            currentTrackTime.text = "0:00"
            playButton.setImageDrawable(getDrawable(R.drawable.play_button))
            playerState = STATE_PREPARED
        }
        mediaPlayer.setOnCompletionListener {
            playButton.setImageDrawable(getDrawable(R.drawable.play_button))
            playerState = STATE_PREPARED
            currentTrackTime.text = "0:00"
            handler.removeCallbacks(timer)
        }
    }

    private fun startPlayer() {
        mediaPlayer.start()
        playButton.setImageDrawable(getDrawable(R.drawable.pause_button))
        playerState = STATE_PLAYING
        handler.post(timer)
    }

    private fun pausePlayer() {
        mediaPlayer.pause()
        playButton.setImageDrawable(getDrawable(R.drawable.play_button))
        playerState = STATE_PAUSED
        handler.removeCallbacks(timer)
    }

    private fun playbackControl() {
        when(playerState) {
            STATE_PLAYING -> {
                pausePlayer()
            }
            STATE_PREPARED, STATE_PAUSED -> {
                startPlayer()
            }
        }
    }

    private fun toolbarCreate() {
        toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        getSupportActionBar()?.setDisplayShowTitleEnabled(false)

        toolbar.setNavigationOnClickListener {
            super.onBackPressed()
            finish()
        }
    }

    private fun showTrackInfo() {
        cover = findViewById(R.id.cover)
        Glide.with(applicationContext).load(track.artworkUrl100?.replaceAfterLast('/',"512x512bb.jpg"))
            .placeholder(R.drawable.track_placeholder)
            .fitCenter()
            .centerCrop()
            .transform((RoundedCorners(resources.getDimensionPixelSize(R.dimen.cover_rounded_corners_radius))))
            .into(cover)

        trackName = findViewById<TextView>(R.id.trackName)
        trackName.text = track.trackName

        artistName = findViewById<TextView>(R.id.artistName)
        artistName.text = track.artistName

        trackTime = findViewById<TextView>(R.id.trackTime)
        trackTime.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(track.trackTimeMillis)

        collectionName = findViewById<TextView>(R.id.collectionName)
        collectionName.text = track.collectionName

        releaseDate = findViewById<TextView>(R.id.releaseDate)
        val date = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").parse(track.releaseDate)
        releaseDate.text = SimpleDateFormat("yyyy", Locale.getDefault()).format(date)

        primaryGenreName = findViewById<TextView>(R.id.primaryGenreName)
        primaryGenreName.text = track.primaryGenreName

        country = findViewById<TextView>(R.id.country)
        country.text = track.country

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_audioplayer)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        track = Gson().fromJson(intent.getStringExtra("track"), Track::class.java)
        toolbarCreate()
        showTrackInfo()

        currentTrackTime = findViewById<TextView>(R.id.currentTrackTime)
        progressBar = findViewById<ProgressBar>(R.id.progressBar)
        playButton = findViewById<ImageView>(R.id.playPauseButton)
        playButton.setOnClickListener {
            playbackControl()
        }

        preparePlayer(track.previewUrl.toString())

        collectionButton = findViewById<ImageView>(R.id.collectionButton)
        collectionButton.setOnClickListener {
            inCollection = !inCollection
            when {
                inCollection -> {
                    collectionButton.setImageDrawable(getDrawable(R.drawable.add_collection_button))
                }

                else -> {
                    collectionButton.setImageDrawable(getDrawable(R.drawable.done_collection_button))
                }
            }

        }

        likeButton = findViewById<ImageView>(R.id.likeButton)
        likeButton.setOnClickListener {
            like = !like
            when {
                like -> {
                    likeButton.setImageDrawable(getDrawable(R.drawable.like_fill_track_button))
                }

                else -> {
                    likeButton.setImageDrawable(getDrawable(R.drawable.like_track_button))
                }
            }

        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
        handler.removeCallbacks(timer)
    }

    override fun onPause() {
        super.onPause()
        if (playerState == STATE_PLAYING)
            pausePlayer()
    }

}