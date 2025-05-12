package com.example.playlistmaker.settings.domain.api

interface SettingsInteractor {
    fun isSystemDarkMode(): Boolean
    fun isDarkMode(): Boolean?
    fun setDarkMode(darkMode: Boolean)
}