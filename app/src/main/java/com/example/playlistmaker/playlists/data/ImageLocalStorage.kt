package com.example.playlistmaker.playlists.data

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import java.io.File
import java.io.FileOutputStream

class ImageLocalStorage(private val context: Context) {
    fun saveImageToPrivateStorage(uri: Uri, directory: String, name: String) {
        val filePath = File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), directory)
        if (!filePath.exists()){
            filePath.mkdirs()
        }
        val file = File(filePath, "$name")
        val inputStream = context.contentResolver.openInputStream(uri)
        val outputStream = FileOutputStream(file)
        BitmapFactory
            .decodeStream(inputStream)
            .compress(Bitmap.CompressFormat.JPEG, 30, outputStream)


    }
}