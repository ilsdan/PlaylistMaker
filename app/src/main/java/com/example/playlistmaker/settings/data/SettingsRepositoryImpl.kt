package com.example.playlistmaker.settings.data

import android.content.res.Configuration
import android.content.res.Resources
import com.example.playlistmaker.settings.domain.api.SettingsRepository


class SettingsRepositoryImpl(private val localStorage: LocalSettingsStorage): SettingsRepository {

    override fun isSystemDarkMode(): Boolean {
        val configuration = Resources.getSystem().configuration
        return configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
    }

    override fun isDarkMode(): Boolean? {
        return localStorage.isDarkMode()
    }

    override fun setDarkMode(darkMode: Boolean) {
        localStorage.setDarkMode(darkMode)
    }
}