package edu.stanford.hci.terrell.dancebeat

import android.content.Context
import android.media.AudioFormat
import android.os.SystemClock
import android.util.Log
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Response
import com.android.volley.toolbox.HttpHeaderParser
import com.android.volley.toolbox.Volley
import com.github.squti.androidwaverecorder.WaveRecorder
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import javax.inject.Inject


class AudioSampler @Inject constructor (@ApplicationContext appContext: Context) {
    val queue = Volley.newRequestQueue(appContext)

    val filePath: String = appContext.cacheDir?.absolutePath + "/audioSample.wav"
    val waveRecorder = WaveRecorder(filePath)

    var timeStart: Long = -1
    var timeStop: Long = -1
    var timeDuration: Long = -1

    init {
        waveRecorder.waveConfig.sampleRate = 22050
        waveRecorder.waveConfig.channels = AudioFormat.CHANNEL_IN_MONO
        waveRecorder.waveConfig.audioEncoding = AudioFormat.ENCODING_PCM_16BIT
    }

    fun startRecording() {
        timeStart = SystemClock.elapsedRealtime()
        waveRecorder.startRecording()
    }

    fun stopRecording() {
        timeStop = SystemClock.elapsedRealtime()
        waveRecorder.stopRecording()
        timeDuration = timeStop - timeStart
    }

    fun analyzeRecording() {
        val request = object : VolleyFileUploadRequest(
            Method.POST,
            "http://192.168.86.50:8000/predict",
            Response.Listener {
                val json = String(it.data)
                Log.d("Test_Acc", "response is: ${json}")
            },
            Response.ErrorListener {
                Log.d("Test_Acc","error is: $it")
            }
        ) {
            override fun getByteData(): MutableMap<String, FileDataPart> {
                val audioBytes = File(filePath).readBytes()

                var params = HashMap<String, FileDataPart>()
                params["file"] = FileDataPart("audioSample", audioBytes, "wav")
                return params
            }
        }
        request.retryPolicy = DefaultRetryPolicy(
            10 * 1000,
            0,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        queue.add(request)
    }
}