package com.example.playlistmaker

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_settings)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val shareAppListButton = findViewById<TextView>(R.id.share_app_list_button)
        shareAppListButton.setOnClickListener {
            val shareIntent = Intent()
            val link = getString(R.string.course_link)
            shareIntent.setAction(Intent.ACTION_SEND)
            shareIntent.putExtra(Intent.EXTRA_TEXT, link)
            shareIntent.setType("text/plain")
            startActivity(shareIntent)
        }

        val writeToSupportListButton = findViewById<TextView>(R.id.write_to_support_list_button)
        writeToSupportListButton.setOnClickListener {
            val email = getString(R.string.email)
            val subject = getString(R.string.subject)
            val message = getString(R.string.message)
            val writeIntent = Intent(Intent.ACTION_SENDTO)
            writeIntent.data = Uri.parse("mailto:")
            writeIntent.putExtra(Intent.EXTRA_EMAIL, email)
            writeIntent.putExtra(Intent.EXTRA_SUBJECT, subject)
            writeIntent.putExtra(Intent.EXTRA_TEXT, message)
            startActivity(writeIntent)
        }

        val userAgreementListButton = findViewById<TextView>(R.id.user_agreement_list_button)
        userAgreementListButton.setOnClickListener {
            val url = getString(R.string.agreement_link)
            val openIntent = Intent(Intent.ACTION_VIEW)
            openIntent.setData(Uri.parse(url))
            startActivity(openIntent)
        }
    }
}