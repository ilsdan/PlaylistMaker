package com.example.playlistmaker.search.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.playlistmaker.player.ui.PlayerActivity
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivitySearchBinding
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.main.ui.MainActivity
import com.example.playlistmaker.search.domain.SearchScreenState
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchActivity : AppCompatActivity() {

    private var searchEditTextValue: String = SEARCH_TEXT_DEF
    private val viewModel: SearchViewModel by viewModel()
    private lateinit var binding: ActivitySearchBinding
    private lateinit var trackAdapter: TrackAdapter

    private fun toolbarCreate() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun hideKeyboard() {
        val inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as? InputMethodManager
        inputMethodManager?.hideSoftInputFromWindow(binding.searchEditText.windowToken, 0)
    }

    private fun searchEditTextCreate() {
        binding.searchEditText.setText(searchEditTextValue)
        binding.clearSearchEditText.setOnClickListener {
            binding.searchEditText.setText("")
            trackAdapter.tracks.clear()
            trackAdapter.notifyDataSetChanged()
            hideError()
            hideKeyboard()
            viewModel.showHistory()
        }

        val simpleTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.searchDebounce(
                    changedText = s?.toString() ?: ""
                )

                searchEditTextValue = binding.searchEditText.text.toString()

                if (binding.searchEditText.text.toString().isEmpty()){
                    binding.clearSearchEditText.isVisible = false
                    hideError()
                    viewModel.showHistory()
                } else {
                    binding.clearSearchEditText.isVisible = true
                }
            }

            override fun afterTextChanged(s: Editable?) {
            }
        }
        binding.searchEditText.addTextChangedListener(simpleTextWatcher)
        binding.searchEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus && binding.searchEditText.text.isEmpty()) {
                viewModel.showHistory()
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

        binding.tracksList.layoutManager = LinearLayoutManager(this)

        val onItemClickListener = object : OnItemClickListener {
            override fun onItemClick(item: Track) {
                if (clickDebounce())
                    viewModel.addTrackToHistory(item)
                    viewModel.showHistory()
                    openPlayer()
            }
        }
        trackAdapter = TrackAdapter(onItemClickListener)
        binding.tracksList.adapter = trackAdapter
    }

    private fun openPlayer() {
        val displayIntent = Intent(this, PlayerActivity::class.java)
        startActivity(displayIntent)
    }

    private fun errorViewCreate() {
        binding.updateButton.setOnClickListener {
            viewModel.searchDebounce(searchEditTextValue)
        }
    }

    private fun hideError() {
        binding.errorView.isVisible = false
    }

    private fun historyViewCreate() {
        binding.clearHistoryButton.setOnClickListener {
            viewModel.clearTrackHistory()
            trackAdapter.tracks.clear()
            trackAdapter.notifyDataSetChanged()
            hideHistory()
        }
    }

    private fun hideHistory() {
        binding.clearHistoryButton.isVisible = false
        binding.HistorySearchText.isVisible = false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        toolbarCreate()
        searchEditTextCreate()
        trackListViewCreate()
        errorViewCreate()
        historyViewCreate()

        viewModel.observeState().observe(this) {
            render(it)
        }
    }

    private fun render(state: SearchScreenState) {
        when (state) {
            is SearchScreenState.Empty -> showEmpty()
            is SearchScreenState.Loading -> showLoading()
            is SearchScreenState.Content -> showTracks(state.tracks)
            is SearchScreenState.History -> showHistory(state.tracks)
            is SearchScreenState.EmptyError -> showEmptyError()
            is SearchScreenState.NetworkError -> showNetworkError()
        }
    }

    private fun showEmpty() {
        binding.progressBar.isVisible = false
        binding.errorView.isVisible = false
        binding.tracksList.isVisible = false
        binding.clearHistoryButton.isVisible = false
        binding.HistorySearchText.isVisible = false
        binding.progressBar.isVisible = false
    }

    private fun showLoading() {
        binding.progressBar.isVisible = false
        binding.errorView.isVisible = false
        binding.tracksList.isVisible = false
        binding.clearHistoryButton.isVisible = false
        binding.HistorySearchText.isVisible = false
        binding.progressBar.isVisible = true
    }

    private fun showEmptyError() {
        binding.progressBar.isVisible = false
        binding.tracksList.isVisible = false
        binding.errorView.isVisible = true
        binding.errorImage.setImageDrawable(getDrawable(R.drawable.empty_response))
        binding.errorText.text = getString(R.string.nothing_was_found)
        binding.updateButton.isVisible = false
    }

    private fun showNetworkError() {
        binding.progressBar.isVisible = false
        binding.tracksList.isVisible = false
        binding.errorView.isVisible = true
        binding.errorImage.setImageDrawable(getDrawable(R.drawable.network_error))
        binding.errorText.text = getString(R.string.communication_problems)
        binding.updateButton.isVisible = true
    }

    private fun showTracks(tracks: List<Track>) {
        binding.clearHistoryButton.isVisible = false
        binding.HistorySearchText.isVisible = false
        binding.progressBar.isVisible = false
        binding.tracksList.isVisible = true
        trackAdapter.tracks.clear()
        trackAdapter.tracks.addAll(tracks)
        trackAdapter.notifyDataSetChanged()
    }

    private fun showHistory(tracks: List<Track>) {
        trackAdapter.tracks.clear()
        trackAdapter.tracks.addAll(tracks)
        trackAdapter.notifyDataSetChanged()
        binding.clearHistoryButton.isVisible = true
        binding.HistorySearchText.isVisible = true
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SEARCH_TEXT, searchEditTextValue)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        searchEditTextValue = savedInstanceState.getString(SEARCH_TEXT, SEARCH_TEXT_DEF)
    }

    companion object {
        private const val SEARCH_TEXT = "SEARCH_TEXT"
        private const val SEARCH_TEXT_DEF = ""
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }

}