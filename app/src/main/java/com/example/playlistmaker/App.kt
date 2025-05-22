package com.example.playlistmaker

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.example.playlistmaker.settings.domain.api.SettingsInteractor
import com.example.playlistmaker.creator.Creator

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
}