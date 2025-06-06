package com.example.playlistmaker.settings.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.playlistmaker.databinding.FragmentSearchBinding
import com.example.playlistmaker.databinding.FragmentSettingsBinding
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.getValue

class SettingsFragment : Fragment() {

    private val viewModel: SettingsViewModel by viewModel()

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    private fun themeSwitcherCreate() {
        val themeSwitcher = binding.themeSwitcher

        themeSwitcher.setOnCheckedChangeListener { switcher, checked ->
            viewModel.setDarkTheme(checked)
        }
    }

    private fun settingsButtonsCreate() {
        val shareAppListButton = binding.shareAppListButton
        shareAppListButton.setOnClickListener {
            viewModel.shareApp()
        }

        val writeToSupportListButton = binding.writeToSupportListButton
        writeToSupportListButton.setOnClickListener {
            viewModel.openSupport()
        }

        val userAgreementListButton = binding.userAgreementListButton
        userAgreementListButton.setOnClickListener {
            viewModel.openTerms()
        }
    }

    private fun changeDarkThemeSwitch(darkTheme: Boolean) {
        binding.themeSwitcher.isChecked = darkTheme
        AppCompatDelegate.setDefaultNightMode(
            if (darkTheme) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getDarkThemeLiveData().observe(viewLifecycleOwner) { isDarkTheme ->
            changeDarkThemeSwitch(isDarkTheme)
        }

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        themeSwitcherCreate()
        settingsButtonsCreate()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}