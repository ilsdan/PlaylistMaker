package com.example.playlistmaker.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.playlistmaker.Creator
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.api.SettingsInteractor
import com.google.android.material.switchmaterial.SwitchMaterial

class SettingsActivity : AppCompatActivity() {

    private lateinit var themeSwitcher: SwitchMaterial

    private lateinit var settingsInteractor: SettingsInteractor

    private fun themeSwitcherCreate() {
        themeSwitcher = findViewById<SwitchMaterial>(R.id.themeSwitcher)

        val darkTheme = if (settingsInteractor.isDarkMode() == null) {
            settingsInteractor.isSystemDarkMode()
        } else {
            settingsInteractor.isDarkMode()!!
        }

        themeSwitcher.isChecked = darkTheme
        themeSwitcher.setOnCheckedChangeListener { switcher, checked ->
            settingsInteractor.setDarkMode(checked)
            AppCompatDelegate.setDefaultNightMode(
                if (checked) {
                    AppCompatDelegate.MODE_NIGHT_YES
                } else {
                    AppCompatDelegate.MODE_NIGHT_NO
                }
            )
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_settings)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        settingsInteractor = Creator.provideSettingsInteractor(this)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        themeSwitcherCreate()

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
            writeIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
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