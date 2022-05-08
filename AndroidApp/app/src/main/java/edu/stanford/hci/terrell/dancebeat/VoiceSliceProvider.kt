package edu.stanford.hci.terrell.dancebeat

import android.app.PendingIntent
import androidx.slice.Slice
import androidx.slice.SliceProvider
import androidx.slice.builders.ListBuilder
import androidx.slice.builders.SliceAction
import android.content.ContentResolver
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.core.graphics.drawable.IconCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job

class VoiceSliceProvider : SliceProvider() {
    val sliceScope = CoroutineScope(Job() + Dispatchers.IO)

    /**
     * Instantiate any required objects. Return true if the provider was successfully created,
     * false otherwise.
     */
    override fun onCreateSliceProvider(): Boolean {
        return true
    }

    /**
     * Converts URL to content URI (i.e. content://edu.stanford.hci.dancebeat...)
     */
    override fun onMapIntentToUri(intent: Intent?): Uri {
        // Note: implementing this is only required if you plan on catching URL requests.
        // This is an example solution.
        var uriBuilder: Uri.Builder = Uri.Builder().scheme(ContentResolver.SCHEME_CONTENT)
        if (intent == null) return uriBuilder.build()
        val data = intent.data
        val dataPath = data?.path
        if (data != null && dataPath != null) {
            val path = dataPath.replace("/", "")
            uriBuilder = uriBuilder.path(path)
        }
        val context = context
        if (context != null) {
            uriBuilder = uriBuilder.authority(context.packageName)
        }
        return uriBuilder.build()
    }

    /**
     * Construct the Slice and bind data if available.
     */
    override fun onBindSlice(sliceUri: Uri): Slice? {
        // Note: you should switch your build.gradle dependency to
        // slice-builders-ktx for a nicer interface in Kotlin.
        val context = context ?: return null

        val sliceParams = sliceUri.path!!.split("/")

        val slice = when(sliceParams[1]) {
            "startTracking" -> {
                ListBuilder(context, sliceUri, ListBuilder.INFINITY)
                    .addRow(
                        ListBuilder.RowBuilder()
                            .setTitle("Tracking Started")
                            .setSubtitle("Music Tracking Started")
                            .setPrimaryAction(createActivityAction()!!)
                    )
                    .build()
            }
            "stopTracking" -> {
                ListBuilder(context, sliceUri, ListBuilder.INFINITY)
                    .addRow(
                        ListBuilder.RowBuilder()
                            .setTitle("Tracking Stopped")
                            .setSubtitle("Not Tracking Music")
                            .setPrimaryAction(createActivityAction()!!)
                    )
                    .build()
            }
            else -> {
                // Error: Path not found.
                Log.e("MediaTrailError", "Slice Error! sliceUriPath: ${sliceUri.path}, Slice Action: ${sliceParams[1]}")
                ListBuilder(context, sliceUri, ListBuilder.INFINITY)
                    .addRow(
                        ListBuilder.RowBuilder()
                            .setTitle("Error")
                            .setSubtitle("SliceUri not Found!")
                            .setPrimaryAction(createActivityAction()!!)
                    )
                    .build()
            }
        }
        return slice
    }

    private fun createActivityAction(): SliceAction? {

        // Instead of returning null, you should create a SliceAction. Here is an example:
        return SliceAction.create(
            PendingIntent.getActivity(
                context, 0, Intent(context, MainActivity::class.java), PendingIntent.FLAG_IMMUTABLE
            ),
            IconCompat.createWithResource(context, R.drawable.ic_launcher_foreground),
            ListBuilder.ICON_IMAGE,
            "Open App"
        )
    }

    /**
     * Slice has been pinned to external process. Subscribe to data source if necessary.
     */
    override fun onSlicePinned(sliceUri: Uri?) {
        // When data is received, call context.contentResolver.notifyChange(sliceUri, null) to
        // trigger ActionSliceProvider#onBindSlice(Uri) again.
    }

    /**
     * Unsubscribe from data source if necessary.
     */
    override fun onSliceUnpinned(sliceUri: Uri?) {
        // Remove any observers if necessary to avoid memory leaks.
    }
}