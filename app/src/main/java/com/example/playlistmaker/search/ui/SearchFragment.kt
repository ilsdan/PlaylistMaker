package com.example.playlistmaker.search.ui

import android.content.Context.INPUT_METHOD_SERVICE
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentSearchBinding
import com.example.playlistmaker.search.domain.SearchScreenState
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.utils.debounce
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.getValue

class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private var searchEditTextValue: String = SEARCH_TEXT_DEF
    private val viewModel: SearchViewModel by viewModel()
    private lateinit var trackAdapter: TrackAdapter

    private fun hideKeyboard() {
        val inputMethodManager = requireContext().getSystemService(INPUT_METHOD_SERVICE) as? InputMethodManager
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

    private lateinit var onTrackClickDebounce: (Track) -> Unit

    private fun trackListViewCreate() {
        val onItemClickListener = object : OnItemClickListener {
            override fun onItemClick(item: Track) {
                onTrackClickDebounce(item)
            }
        }
        trackAdapter = TrackAdapter(onItemClickListener)
        binding.tracksList.adapter = trackAdapter
    }

    private fun openPlayer() {
        findNavController().navigate(R.id.action_searchFragment_to_playerFragment)
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
        binding.errorImage.setImageDrawable(requireContext().getDrawable(R.drawable.empty_response))
        binding.errorText.text = getString(R.string.nothing_was_found)
        binding.updateButton.isVisible = false
    }

    private fun showNetworkError() {
        binding.progressBar.isVisible = false
        binding.tracksList.isVisible = false
        binding.errorView.isVisible = true
        binding.errorImage.setImageDrawable(requireContext().getDrawable(R.drawable.network_error))
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
        binding.tracksList.isVisible = true
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        onTrackClickDebounce = debounce<Track>(CLICK_DEBOUNCE_DELAY, viewLifecycleOwner.lifecycleScope, false) { track ->
            viewModel.addTrackToHistory(track)
            openPlayer()
        }

        searchEditTextCreate()
        trackListViewCreate()
        errorViewCreate()
        historyViewCreate()

        viewModel.observeState().observe(viewLifecycleOwner) {
            render(it)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val SEARCH_TEXT_DEF = ""
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }

}