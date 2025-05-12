package com.example.playlistmaker.ui.search

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.ui.AudioPlayerActivity
import com.example.playlistmaker.Creator
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.api.TracksInteractor
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.ui.MainActivity
import com.google.android.material.button.MaterialButton
import com.google.gson.Gson

class SearchActivity : AppCompatActivity() {

    private lateinit var trackList: MutableList<Track>
    private lateinit var trackAdapter: TrackAdapter

    private lateinit var toolbar: Toolbar
    private lateinit var searchEditText: EditText
    private lateinit var clearButton: ImageView
    private lateinit var trackListView: RecyclerView

    private lateinit var clearHistoryButton: MaterialButton
    private lateinit var historySearchText: TextView

    private lateinit var errorView: LinearLayout
    private lateinit var errorImage: ImageView
    private lateinit var errorText: TextView
    private lateinit var updateButton: MaterialButton

    private lateinit var progressBar: ProgressBar

    var isHistoryView = false

    private lateinit var tracksInteractor: TracksInteractor

    private fun toolbarCreate() {
        toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun hideKeyboard() {
        val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        inputMethodManager?.hideSoftInputFromWindow(searchEditText.windowToken, 0)
    }

    private fun searchEditTextCreate() {
        searchEditText = findViewById<EditText>(R.id.searchEditText)
        clearButton = findViewById<ImageView>(R.id.clearSearchEditText)

        searchEditText.setText(searchEditTextValue)

        clearButton.setOnClickListener {
            searchEditText.setText("")
            trackList.clear()
            trackAdapter.notifyDataSetChanged()
            hideError()
            hideKeyboard()
            showHistory()
        }

        val simpleTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                searchEditTextValue = searchEditText.text.toString()

                if (searchEditTextValue.isEmpty()){
                    clearButton.visibility = View.GONE
                    hideError()
                    showHistory()
                } else {
                    clearButton.visibility = View.VISIBLE
                    searchDebounce()
                }
            }

            override fun afterTextChanged(s: Editable?) {
            }
        }
        searchEditText.addTextChangedListener(simpleTextWatcher)
        searchEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus && searchEditText.text.isEmpty()) {
                showHistory()
            }
        }
    }

    private var isClickAllowed = true
    private val handler = Handler(Looper.getMainLooper())

    private fun clickDebounce() : Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY)
        }
        return current
    }

    private fun trackListViewCreate() {

        trackListView = findViewById<RecyclerView>(R.id.tracksList)
        trackListView.layoutManager = LinearLayoutManager(this)

        trackList = mutableListOf()

        val onItemClickListener = object : OnItemClickListener {
            override fun onItemClick(item: Track) {
                addTrackToHistory(item)
                if (isHistoryView) {
                    trackList.clear()
                    trackList.addAll(getTrackHistory())
                    trackAdapter.notifyDataSetChanged()
                }

                if (clickDebounce())
                    openAudioPlayer(item)
            }
        }

        trackAdapter = TrackAdapter(trackList, onItemClickListener)

        trackListView.adapter = trackAdapter
    }

    private fun openAudioPlayer(track: Track) {
        val displayIntent = Intent(this, AudioPlayerActivity::class.java)
        displayIntent.putExtra("track", Gson().toJson(track))
        startActivity(displayIntent)
    }

    private fun errorViewCreate() {
        errorView =  findViewById<LinearLayout>(R.id.errorView)
        errorImage =  findViewById<ImageView>(R.id.errorImage)
        errorText =  findViewById<TextView>(R.id.errorText)
        updateButton = findViewById<MaterialButton>(R.id.updateButton)
        updateButton.setOnClickListener {
            trackListViewUpdate()
        }
    }

    private fun trackListViewUpdate() {
        if (searchEditTextValue.isEmpty()) return
        showView(LOADING_VIEW)

        tracksInteractor.searchTracks(searchEditTextValue, object: TracksInteractor.TracksConsumer {
            override fun consume(foundTracks: List<Track>?) {
                handler.post {
                    showView(LIST_VIEW)
                    if (!foundTracks.isNullOrEmpty()) {
                        showTrackList(foundTracks)
                    }
                    if (foundTracks != null && foundTracks.isEmpty() ) {
                        showError("Ничего не нашлось", R.drawable.empty_response, false)
                    }

                    if (foundTracks == null) {
                        showError("Проблемы со связью\n\nЗагрузка не удалась. Проверьте подключение к интернету",
                            R.drawable.network_error, true)
                    }
                }
            }
        })
    }

    private fun showError(text: String, icon: Int, showUpdateButton: Boolean ) {
        errorView.isVisible = true
        errorImage.setImageDrawable(getDrawable(icon))
        errorText.text = text
        updateButton.isVisible = showUpdateButton
    }

    private fun hideError() {
        errorView.isVisible = false
    }

    private fun showTrackList(trackList: List<Track>) {
        isHistoryView = false
        errorView.isVisible = false
        this.trackList.clear()
        this.trackList.addAll(trackList)
        trackAdapter.notifyDataSetChanged()
        hideHistory()
    }

    private fun historyViewCreate() {
        clearHistoryButton = findViewById<MaterialButton>(R.id.clearHistoryButton)
        historySearchText = findViewById<TextView>(R.id.HistorySearchText)
        clearHistoryButton.setOnClickListener {
            clearTrackHistory()
            trackList.clear()
            trackAdapter.notifyDataSetChanged()
            hideHistory()
        }
    }

    private fun clearTrackHistory() {
        tracksInteractor.cleanTracksHistory()
    }

    private fun addTrackToHistory(track: Track) {
        tracksInteractor.addTrackToHistory(track)
    }

    private fun getTrackHistory(): List<Track> {
        return tracksInteractor.getTracksHistory()
    }

    private fun hideHistory() {
        clearHistoryButton.isVisible = false
        historySearchText.isVisible = false
    }

    private fun showHistory() {
        this.trackList.clear()
        this.trackList.addAll(getTrackHistory())
        trackAdapter.notifyDataSetChanged()

        if (!tracksInteractor.isExistTracksHistory())
            return

        isHistoryView = true
        clearHistoryButton.isVisible = true
        historySearchText.isVisible = true
        errorView.isVisible = false
    }

    private fun showView(viewID: Int) {
        progressBar.isVisible = false
        errorView.isVisible = false
        trackListView.isVisible = false
        hideHistory()
        when (viewID) {
            LOADING_VIEW ->
                progressBar.isVisible = true
            LIST_VIEW ->
                trackListView.isVisible = true
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_search)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        toolbarCreate()
        searchEditTextCreate()
        trackListViewCreate()
        errorViewCreate()
        historyViewCreate()

        progressBar = findViewById<ProgressBar>(R.id.progressBar)

        tracksInteractor = Creator.provideTracksInteractor(this)

    }

    private val searchRunnable = Runnable { trackListViewUpdate() }

    private fun searchDebounce() {
        handler.removeCallbacks(searchRunnable)
        handler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)
    }

    companion object {
        const val SEARCH_TEXT = "SEARCH_TEXT"
        const val SEARCH_TEXT_DEF = ""
        private const val CLICK_DEBOUNCE_DELAY = 1000L
        private const val SEARCH_DEBOUNCE_DELAY = 2000L

        private const val LIST_VIEW = 0
        private const val HISTORY_LIST_VIEW = 1
        private const val ERROR_VIEW = 2
        private const val LOADING_VIEW = 3
        private const val DEFAULT_VIEW = 4
    }

    private var searchEditTextValue: String = SEARCH_TEXT_DEF

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SEARCH_TEXT, searchEditTextValue)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        searchEditTextValue = savedInstanceState.getString(SEARCH_TEXT, SEARCH_TEXT_DEF)
    }

}