package com.example.playlistmaker.settings.data

import android.content.SharedPreferences

class LocalSettingsStorage(private val sharedPreferences: SharedPreferences)  {
    private companion object {
        const val THEME_SWITCHER_KEY = "theme_switcher_key"
    }

    fun setDarkMode(darkMode: Boolean) {
        sharedPreferences.edit().putBoolean(THEME_SWITCHER_KEY, darkMode).apply()
    }

    fun isDarkMode(): Boolean? {
        if (!sharedPreferences.contains(THEME_SWITCHER_KEY))
            return null
        return sharedPreferences.getBoolean(THEME_SWITCHER_KEY, false)
    }
}