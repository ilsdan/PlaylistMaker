package com.example.playlistmaker

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

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

    var isHistoryView = false

    private val itunesBaseUrl = "https://itunes.apple.com"
    private val retrofit = Retrofit.Builder()
        .baseUrl(itunesBaseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    private val trackApiService = retrofit.create(iTunesApi::class.java)

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

                if (s.isNullOrEmpty()){
                    clearButton.visibility = View.GONE
                    hideError()
                    showHistory()
                } else {
                    clearButton.visibility = View.VISIBLE
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
            }
        }

        trackAdapter = TrackAdapter(trackList, onItemClickListener)

        trackListView.adapter = trackAdapter
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
        trackApiService.searchTracks(searchEditText.text.toString()).enqueue(object : Callback<TrackResponse> {
            override fun onResponse(
                call: Call<TrackResponse>,
                response: Response<TrackResponse>
            ) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null && responseBody.resultCount > 0) {
                        showTrackList(responseBody.results)
                    } else {
                        showError("Ничего не нашлось", R.drawable.empty_response, false)
                    }
                } else {
                    showError("Проблемы со связью\n\nЗагрузка не удалась. Проверьте подключение к интернету", R.drawable.network_error, true)
                }
            }

            override fun onFailure(call: Call<TrackResponse>, t: Throwable) {
                showError("Проблемы со связью\n\nЗагрузка не удалась. Проверьте подключение к интернету", R.drawable.network_error, true)
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
        getSharedPreferences(PLAYLIST_MAKER_PREFERENCES, MODE_PRIVATE).edit().remove(HISTORY_KEY).apply()
    }

    private fun addTrackToHistory(track: Track) {
        val history = getTrackHistory().toMutableList()
        history.removeAll { it.trackId == track.trackId }
        history.add(0, track)
        if (history.size > 10) {
            history.removeLast()
        }
        val json = Gson().toJson(history)
        getSharedPreferences(PLAYLIST_MAKER_PREFERENCES, MODE_PRIVATE).edit()
            .putString(HISTORY_KEY, json)
            .apply()
    }

    private fun getTrackHistory(): List<Track> {
        val json = getSharedPreferences(PLAYLIST_MAKER_PREFERENCES, MODE_PRIVATE).getString(HISTORY_KEY, null) ?: return emptyList()
        return Gson().fromJson(json, Array<Track>::class.java).toList()
    }

    private fun hideHistory() {
        clearHistoryButton.isVisible = false
        historySearchText.isVisible = false
    }

    private fun showHistory() {
        if (!getSharedPreferences(PLAYLIST_MAKER_PREFERENCES, MODE_PRIVATE).contains(HISTORY_KEY))
            return
        isHistoryView = true
        clearHistoryButton.isVisible = true
        historySearchText.isVisible = true
        errorView.isVisible = false
        this.trackList.clear()
        this.trackList.addAll(getTrackHistory())
        trackAdapter.notifyDataSetChanged()
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

        searchEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                trackListViewUpdate()
            }
            false
        }
    }

    companion object {
        const val SEARCH_TEXT = "SEARCH_TEXT"
        const val SEARCH_TEXT_DEF = ""
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