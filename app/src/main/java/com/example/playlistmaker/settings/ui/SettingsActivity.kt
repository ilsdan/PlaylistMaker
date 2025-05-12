package com.example.playlistmaker.settings.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivitySettingsBinding
import com.example.playlistmaker.main.ui.MainActivity

class SettingsActivity : AppCompatActivity() {

    private lateinit var viewModel: SettingsViewModel

    private lateinit var binding: ActivitySettingsBinding

    private fun themeSwitcherCreate() {
        val themeSwitcher = binding.themeSwitcher

        themeSwitcher.setOnCheckedChangeListener { switcher, checked ->
            viewModel.setDarkTheme(checked)
        }
    }

    private fun toolBarCreate() {
        val toolbar = binding.toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_settings)

        viewModel = ViewModelProvider(this, SettingsViewModel.getViewModelFactory(Creator.provideSharingInteractor(this), Creator.provideSettingsInteractor(this)))[SettingsViewModel::class.java]

        viewModel.getDarkThemeLiveData().observe(this) { isDarkTheme ->
            changeDarkThemeSwitch(isDarkTheme)
        }

        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        toolBarCreate()
        themeSwitcherCreate()
        settingsButtonsCreate()
    }

    private fun SettingsActivity.changeDarkThemeSwitch(darkTheme: Boolean) {
        binding.themeSwitcher.isChecked = darkTheme
        AppCompatDelegate.setDefaultNightMode(
            if (darkTheme) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }
}



