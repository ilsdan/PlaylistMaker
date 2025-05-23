package com.example.playlistmaker

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.example.playlistmaker.settings.domain.api.SettingsInteractor
import com.example.playlistmaker.di.dataModule
import com.example.playlistmaker.di.interactorModule
import com.example.playlistmaker.di.repositoryModule
import com.example.playlistmaker.di.viewModelModule
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin

class App : Application() {

    private var darkTheme = false
    private val settingsInteractor: SettingsInteractor by inject()

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@App)
            modules(dataModule, repositoryModule, interactorModule, viewModelModule)
        }

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