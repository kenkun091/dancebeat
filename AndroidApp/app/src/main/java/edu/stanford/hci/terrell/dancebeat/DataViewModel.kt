package edu.stanford.hci.terrell.dancebeat

import android.content.Context
import android.speech.tts.TextToSpeech
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class DataViewModel @Inject constructor(@ApplicationContext context: Context, val stepSampler: StepSampler, val audioSampler: AudioSampler): ViewModel() {

    private val textToSpeechEngine: TextToSpeech by lazy {
        // Pass in context and the listener.
        TextToSpeech(context,
            TextToSpeech.OnInitListener { status ->
                // set our locale only if init was success.
                if (status == TextToSpeech.SUCCESS) {
                    textToSpeechEngine.language = Locale.US
                }
            })
    }

    private var trackSteps = false

    fun recordAudio() {
        viewModelScope.launch {
            Log.d("Test_Audio", "Recording Started")
            audioSampler.startRecording()
            delay(8 * 1000)
            audioSampler.stopRecording()
            Log.d("Test_Audio", "Recording Stopped")
            Log.d("Test_Audio", "Sending Recording for Analysis")
            audioSampler.analyzeRecording()
        }
    }

    fun startStopTrackingSteps() {
        if (trackSteps) {
            trackSteps = false
            stepSampler.stopTracking()
        }
        else {
            trackSteps = true
            stepSampler.startTracking()
            viewModelScope.launch {
                while(trackSteps) {
                    delay(6000)
                    textToSpeechEngine.speak(stepSampler.rollingBPM.value.toString(), TextToSpeech.QUEUE_ADD, null, "bpm")
                }
            }
        }
    }

    fun getStepBPM(): LiveData<Long> {
        return stepSampler.rollingBPM
    }

    fun getAudioBPM(): LiveData<Long> {
        return audioSampler.detectedBPM
    }

    fun testTTS() {
            textToSpeechEngine.speak("1", TextToSpeech.QUEUE_ADD, null, "tts1")
            textToSpeechEngine.speak("2", TextToSpeech.QUEUE_ADD, null, "tts2")
            textToSpeechEngine.speak("3", TextToSpeech.QUEUE_ADD, null, "tts3")
            textToSpeechEngine.speak("4", TextToSpeech.QUEUE_ADD, null, "tts4")
            textToSpeechEngine.speak("5", TextToSpeech.QUEUE_ADD, null, "tts5")
            textToSpeechEngine.speak("6", TextToSpeech.QUEUE_ADD, null, "tts6")
    }

    fun onPause() {
        textToSpeechEngine.stop()
    }

    fun onDestroy() {
        textToSpeechEngine.shutdown()
    }
}