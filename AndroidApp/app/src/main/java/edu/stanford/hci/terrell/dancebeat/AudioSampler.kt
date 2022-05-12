package edu.stanford.hci.terrell.dancebeat

import android.content.Context
import android.media.AudioFormat
import android.os.SystemClock
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Response
import com.android.volley.toolbox.HttpHeaderParser
import com.android.volley.toolbox.Volley
import com.github.squti.androidwaverecorder.WaveRecorder
import com.google.gson.Gson
import dagger.hilt.android.qualifiers.ApplicationContext
import org.json.JSONObject
import java.io.File
import java.util.*
import javax.inject.Inject
import kotlin.collections.HashMap
import kotlin.concurrent.scheduleAtFixedRate
import kotlin.math.roundToLong


class AudioSampler @Inject constructor (@ApplicationContext appContext: Context, val toneFeedback: ToneFeedback) {

    var toneTask: TimerTask? = null
    var detectedBPM : MutableLiveData<Long> = MutableLiveData<Long>(120)

    val gson = Gson()
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
        toneTask?.cancel()
        timeStart = SystemClock.elapsedRealtime()
        waveRecorder.startRecording()
    }

    fun stopRecording() {
        timeStop = SystemClock.elapsedRealtime()
        waveRecorder.stopRecording()
        timeDuration = timeStop - timeStart
    }

    fun stopTone() {
        toneTask?.cancel()
    }

    fun parseBeats(respData: ByteArray): List<Beat> {
        val jsonString = String(respData)
        val beatData = gson.fromJson(jsonString, BeatData::class.java)
        return beatData.beats
    }

    fun calculatePeriod(beats: List<Beat>): Long {
        val periods = mutableListOf<Float>()

        for (i in beats.indices) {
            if (i != 0) {
                periods.add(beats[i].t - beats[i - 1].t)
            }
        }

        periods.sort()

        var p = if (periods.size % 2 == 1) {
            periods[periods.size / 2 + 1]
        } else {
            (periods[periods.size / 2] + periods[periods.size / 2 + 1]) / 2
        }

        p *= 1000
        return p.roundToLong()
    }

    fun calculateDelay(beats: List<Beat>, period: Long): Long {
        val timeNow = SystemClock.elapsedRealtime()
        val delays = mutableListOf<Long>()

        for (b in beats) {
            val timePassed = timeNow - (timeStart + (b.t * 1000).roundToLong())
            val numBeatsPassed = timePassed / period
            val timeRemaining = timePassed - numBeatsPassed * period
            delays.add(timeRemaining)
        }

        delays.sort()

        var d = if (delays.size % 2 == 1) {
            delays[delays.size / 2 + 1]
        } else {
            (delays[delays.size / 2] + delays[delays.size / 2 + 1]) / 2
        }
        return d
    }

    fun calculateBPM(period: Long): Long {
        var bpmF = period.toFloat() / 1000F
        bpmF = 1F / bpmF * 60F
        return bpmF.roundToLong()
    }

    fun analyzeRecording() {
        val request = object : VolleyFileUploadRequest(
            Method.POST,
            "http://192.168.86.50:8000/predict",
            Response.Listener {
                val beats = parseBeats(it.data)
                val period = calculatePeriod(beats)
                val delay = calculateDelay(beats, period)
                toneTask = Timer("BeatCount", false).scheduleAtFixedRate(delay, period) {
                    toneFeedback.playBeat()
                }
                val bpm = calculateBPM(period)
                detectedBPM.postValue(bpm)
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