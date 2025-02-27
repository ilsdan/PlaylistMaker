package com.example.playlistmaker.data

interface StorageClient {
    fun getString(key: String): String?
    fun setString(key: String, value: String)
    fun getBoolean(key: String): Boolean
    fun setBoolean(key: String, value: Boolean)
    fun remove(key: String)
    fun exist(key: String): Boolean
}