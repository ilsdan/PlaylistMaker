package com.example.playlistmaker

import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
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
    private  lateinit var country: TextView


    private lateinit var playButton: ImageView
    private lateinit var collectionButton: ImageView
    private lateinit var likeButton: ImageView
    private var play = true
    private var like = false
    private var inCollection = false

    private lateinit var track: Track

    private lateinit var toolbar: Toolbar

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

        playButton = findViewById<ImageView>(R.id.playPauseButton)
        playButton.setOnClickListener {
            when {
                play -> {
                    playButton.setImageDrawable(getDrawable(R.drawable.play_button))
                }
                else -> {
                    playButton.setImageDrawable(getDrawable(R.drawable.pause_button))
                }
            }
            play = !play

        }

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
}