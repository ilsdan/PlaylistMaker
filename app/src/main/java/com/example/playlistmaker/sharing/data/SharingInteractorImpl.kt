package com.example.playlistmaker.sharing.data

import android.content.Context
import com.example.playlistmaker.R
import com.example.playlistmaker.sharing.domain.SharingInteractor

class SharingInteractorImpl(
    private val externalNavigator: ExternalNavigator,
    private val context: Context
) : SharingInteractor {
    override fun shareApp() {
        externalNavigator.shareLink(getShareAppLink())
    }

    override fun openTerms() {
        externalNavigator.openLink(getTermsLink())
    }

    override fun openSupport() {
        externalNavigator.openEmail(getSupportEmailData())
    }

    private fun getShareAppLink(): String {
        return context.getString(R.string.course_link)
    }

    private fun getSupportEmailData(): EmailData {
        return EmailData(context.getString(R.string.email), context.getString(R.string.subject), context.getString(
            R.string.message))
    }

    private fun getTermsLink(): String {
        return context.getString(R.string.agreement_link)
    }
}