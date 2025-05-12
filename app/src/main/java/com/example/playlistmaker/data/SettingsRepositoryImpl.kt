package com.example.playlistmaker.data

import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import com.example.playlistmaker.data.storage.SharedPrefsStorageClient
import com.example.playlistmaker.domain.api.SettingsRepository

const val THEME_SWITCHER_KEY = "theme_switcher_key"

class SettingsRepositoryImpl(context: Context): SettingsRepository {

    private val storageClient = SharedPrefsStorageClient(context)

    override fun isSystemDarkMode(): Boolean {
        val configuration = Resources.getSystem().configuration
        return configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
    }

    override fun isDarkMode(): Boolean? {
        if (!storageClient.exist(THEME_SWITCHER_KEY))
            return null
        return storageClient.getBoolean(THEME_SWITCHER_KEY)
    }

    override fun setDarkMode(darkMode: Boolean) {
        storageClient.setBoolean(THEME_SWITCHER_KEY, darkMode)
    }
}