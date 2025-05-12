package com.example.playlistmaker.settings.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.playlistmaker.settings.domain.api.SettingsInteractor
import com.example.playlistmaker.sharing.domain.SharingInteractor

class SettingsViewModel(
    private val sharingInteractor: SharingInteractor,
    private val settingsInteractor: SettingsInteractor,
) : ViewModel() {

    private var darkThemeLiveData = MutableLiveData(true)
    fun getDarkThemeLiveData(): LiveData<Boolean> = darkThemeLiveData

    init {
        val darkTheme = if (settingsInteractor.isDarkMode() == null) {
            settingsInteractor.isSystemDarkMode()
        } else {
            settingsInteractor.isDarkMode()!!
        }
        darkThemeLiveData.value = darkTheme
    }

    fun setDarkTheme(darkTheme: Boolean) {
        settingsInteractor.setDarkMode(darkTheme)
        darkThemeLiveData.value = darkTheme
    }

    companion object {
        fun getViewModelFactory(sharingInteractor: SharingInteractor, settingsInteractor: SettingsInteractor): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                SettingsViewModel(
                    sharingInteractor,
                    settingsInteractor,
                )
            }
        }
    }

    fun shareApp() {
        sharingInteractor.shareApp()
    }

    fun openSupport() {
        sharingInteractor.openSupport()
    }

    fun openTerms() {
        sharingInteractor.openTerms()
    }
}