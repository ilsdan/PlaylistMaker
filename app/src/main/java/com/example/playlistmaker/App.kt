package com.example.playlistmaker

import android.app.Application
import android.content.res.Configuration
import android.content.res.Resources
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import com.example.playlistmaker.domain.api.SettingsInteractor
import com.example.playlistmaker.domain.api.TracksInteractor

class App : Application() {

    private var darkTheme = false
    private lateinit var settingsInteractor: SettingsInteractor

    override fun onCreate() {
        super.onCreate()

        settingsInteractor = Creator.provideSettingsInteractor(this)

        darkTheme = if (settingsInteractor.isDarkMode() == null) {
            settingsInteractor.isSystemDarkMode()
        } else {
            settingsInteractor.isDarkMode()!!
        }

        AppCompatDelegate.setDefaultNightMode(
            if (darkTheme) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )

    }

    fun isDarkTheme(): Boolean {
        return darkTheme
    }

    fun switchTheme(darkThemeEnabled: Boolean) {
        darkTheme = darkThemeEnabled

        settingsInteractor.setDarkMode(darkTheme)

        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnabled) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }
}