package edu.stanford.hci.terrell.dancebeat

import android.media.AudioManager
import android.media.ToneGenerator
import android.util.Log
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ToneFeedback @Inject constructor() {
    private val toneGenerator =
        ToneGenerator(AudioManager.STREAM_ACCESSIBILITY, ToneGenerator.MAX_VOLUME)
    private val maxToneLength = 10;

    fun playBeat() {
        Log.d("Beat", "BEAT")
        toneGenerator.startTone(ToneGenerator.TONE_PROP_BEEP, maxToneLength)
    }
}
