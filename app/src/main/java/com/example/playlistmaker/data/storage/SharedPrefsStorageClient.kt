package com.example.playlistmaker.data.storage

import android.content.Context
import android.content.Context.MODE_PRIVATE
import com.example.playlistmaker.data.StorageClient

const val PLAYLIST_MAKER_PREFERENCES = "playlist_maker_preferences"
const val HISTORY_KEY = "search_history"

class SharedPrefsStorageClient(context: Context): StorageClient {

    private val sharedPrefs = context.getSharedPreferences(PLAYLIST_MAKER_PREFERENCES, MODE_PRIVATE)

    override fun getString(key: String): String? {
        return sharedPrefs.getString(key, null)
    }

    override fun setString(key: String, value: String) {
        sharedPrefs.edit().putString(key, value).apply()
    }

    override fun remove(key: String) {
        sharedPrefs.edit().remove(HISTORY_KEY).apply()
    }

    override fun exist(key: String): Boolean {
        return sharedPrefs.contains(key)
    }

    override fun getBoolean(key: String): Boolean {
        return sharedPrefs.getBoolean(key, false)
    }

    override fun setBoolean(key: String, value: Boolean) {
        sharedPrefs.edit().putBoolean(key, value).apply()
    }
}