package com.example.playlistmaker.settings.domain.impl

import com.example.playlistmaker.settings.domain.api.SettingsInteractor
import com.example.playlistmaker.settings.domain.api.SettingsRepository

class SettingsInteractorImpl(private val repository: SettingsRepository):
    SettingsInteractor {
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