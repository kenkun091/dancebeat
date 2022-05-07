package edu.stanford.hci.terrell.dancebeat

import android.content.Context
import android.media.AudioFormat
import com.android.volley.Response
import com.github.squti.androidwaverecorder.WaveRecorder
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import javax.inject.Inject

class AudioSampler @Inject constructor (@ApplicationContext appContext: Context) {
    val filePath: String = appContext.cacheDir?.absolutePath + "/audioSample.wav"
    val waveRecorder = WaveRecorder(filePath)

    init {
        waveRecorder.waveConfig.sampleRate = 22050
        waveRecorder.waveConfig.channels = AudioFormat.CHANNEL_IN_MONO
        waveRecorder.waveConfig.audioEncoding = AudioFormat.ENCODING_PCM_16BIT
    }

    fun startRecording() {
        waveRecorder.startRecording()
    }

    fun stopRecording() {
        waveRecorder.stopRecording()
    }

    fun analyzeRecording() {
        val request = object : VolleyFileUploadRequest(
            Method.POST,
            "https://test.com",
            Response.Listener {
                println("response is: $it")
            },
            Response.ErrorListener {
                println("error is: $it")
            }
        ) {
            override fun getByteData(): MutableMap<String, FileDataPart> {
                val audioBytes = File(filePath).readBytes()

                var params = HashMap<String, FileDataPart>()
                params["audioFile"] = FileDataPart("audioSample", audioBytes, "wav")
                return params
            }
        }
    }
}