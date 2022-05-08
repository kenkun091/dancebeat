package edu.stanford.hci.terrell.dancebeat

import android.app.Application
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.service.voice.VoiceInteractionService
import androidx.slice.SliceManager
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class DanceBeatApplication : Application() {
    private val SLICE_AUTHORITY = "content://edu.stanford.hci.terrell.dancebeat/"
    private val SLICE_PERMISSIONS = "SLICE_PERMISSIONS"


    override fun onCreate() {
        super.onCreate()
        val preferences =
            applicationContext.getSharedPreferences("dancebeat_prefs", Context.MODE_PRIVATE);

        var granted = preferences.getBoolean(SLICE_PERMISSIONS, false)

        if (!granted) {
            val prefEdit = preferences.edit()
            prefEdit.putBoolean(SLICE_PERMISSIONS, true)
            prefEdit.apply()
            grantSlicePermissions()
        }
    }

    private fun grantSlicePermissions() {
        val context = applicationContext

        val sliceProviderURI = Uri.Builder().scheme(ContentResolver.SCHEME_CONTENT)
            .authority(SLICE_AUTHORITY)
            .build()

        val assistantPackage = getAssistantPackage(context) ?: return
        SliceManager.getInstance(context).grantSlicePermission(assistantPackage, sliceProviderURI)
    }

    private fun getAssistantPackage(context: Context): String? {
        val packageManager = context.packageManager
        val resolveList = packageManager.queryIntentServices(
            Intent(VoiceInteractionService.SERVICE_INTERFACE), 0
        )

        return resolveList.firstOrNull()?.serviceInfo?.packageName
    }
}