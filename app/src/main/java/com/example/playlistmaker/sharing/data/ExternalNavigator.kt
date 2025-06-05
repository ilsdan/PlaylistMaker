package com.example.playlistmaker.sharing.data

import android.content.Context
import android.content.Intent
import android.net.Uri

class ExternalNavigator(private val context: Context) {
    fun shareLink(link: String) {
        val shareIntent = Intent()
        shareIntent.setAction(Intent.ACTION_SEND)
        shareIntent.putExtra(Intent.EXTRA_TEXT, link)
        shareIntent.setType("text/plain")
        shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(shareIntent)
    }

    fun openLink(url: String) {
        val openIntent = Intent(Intent.ACTION_VIEW)
        openIntent.setData(Uri.parse(url))
        openIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(openIntent)
    }

    fun openEmail(emailData: EmailData) {
        val writeIntent = Intent(Intent.ACTION_SENDTO)
        writeIntent.data = Uri.parse("mailto:")
        writeIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(emailData.address))
        writeIntent.putExtra(Intent.EXTRA_SUBJECT, emailData.subject)
        writeIntent.putExtra(Intent.EXTRA_TEXT, emailData.message)
        writeIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(writeIntent)
    }
}