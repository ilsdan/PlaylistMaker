package com.example.playlistmaker.settings.domain.api

interface SettingsRepository {
    fun isSystemDarkMode(): Boolean
    fun isDarkMode(): Boolean?
    fun setDarkMode(darkMode: Boolean)
}