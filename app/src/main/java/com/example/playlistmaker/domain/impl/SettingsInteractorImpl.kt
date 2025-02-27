package com.example.playlistmaker.domain.impl

import android.content.Context
import com.example.playlistmaker.domain.api.SettingsInteractor
import com.example.playlistmaker.domain.api.SettingsRepository

class SettingsInteractorImpl(private val repository: SettingsRepository, context: Context): SettingsInteractor {
    override fun isSystemDarkMode(): Boolean {
        return repository.isSystemDarkMode()
    }

    override fun isDarkMode(): Boolean? {
        return repository.isDarkMode()
    }

    override fun setDarkMode(darkMode: Boolean) {
        repository.setDarkMode(darkMode)
    }
}