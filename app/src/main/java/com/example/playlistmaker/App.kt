package com.example.playlistmaker

import android.app.Application
import android.content.res.Configuration
import android.content.res.Resources
import androidx.appcompat.app.AppCompatDelegate

const val PLAYLIST_MAKER_PREFERENCES = "playlist_maker_preferences"
const val THEME_SWITCHER_KEY = "theme_switcher_key"
const val HISTORY_KEY = "search_history"

class App : Application() {

    private var darkTheme = false

    private fun isSystemDarkMode(): Boolean {
        val configuration = Resources.getSystem().configuration
        return configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
    }

    override fun onCreate() {
        super.onCreate()

        val sharedPrefs = getSharedPreferences(PLAYLIST_MAKER_PREFERENCES, MODE_PRIVATE)

        darkTheme = if (!sharedPrefs.contains(THEME_SWITCHER_KEY)) {
            isSystemDarkMode()
        } else {
            sharedPrefs.getBoolean(THEME_SWITCHER_KEY, false)
        }

        AppCompatDelegate.setDefaultNightMode(
            if (darkTheme) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )

    }

    fun switchTheme(darkThemeEnabled: Boolean) {
        darkTheme = darkThemeEnabled

        val sharedPrefs = getSharedPreferences(PLAYLIST_MAKER_PREFERENCES, MODE_PRIVATE)

        sharedPrefs.edit()
            .putBoolean(THEME_SWITCHER_KEY, darkTheme)
            .apply()

        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnabled) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }
}