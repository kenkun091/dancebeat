package edu.stanford.hci.terrell.dancebeat

import android.content.Intent
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import android.media.session.MediaSession
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.KeyEvent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role.Companion.Button
import androidx.compose.ui.tooling.preview.Preview
import edu.stanford.hci.terrell.dancebeat.ui.theme.DanceBeatTheme
import java.util.*


class MainActivity : ComponentActivity() {
    private val textToSpeechEngine: TextToSpeech by lazy {
        // Pass in context and the listener.
        TextToSpeech(this,
            TextToSpeech.OnInitListener { status ->
                // set our locale only if init was success.
                if (status == TextToSpeech.SUCCESS) {
                    textToSpeechEngine.language = Locale.US
                }
            })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DanceBeatTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    Column() {
                        Row() {
                            Button(
                                // below line is use to add onclick
                                // parameter for our button onclick
                                onClick = {
                                    // when user is clicking the button
                                    // we are displaying a toast message.
                                    speakTest()
                                }
                            )
                            {
                                Text(text = "TTS_Test", color = Color.White)
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onPause() {
        textToSpeechEngine.stop()
        super.onPause()
    }

    override fun onDestroy() {
        textToSpeechEngine.shutdown()
        super.onDestroy()
    }

    fun speakTest() {
        textToSpeechEngine.speak("1", TextToSpeech.QUEUE_ADD, null, "tts1")
        textToSpeechEngine.speak("2", TextToSpeech.QUEUE_ADD, null, "tts2")
        textToSpeechEngine.speak("3", TextToSpeech.QUEUE_ADD, null, "tts3")
        textToSpeechEngine.speak("4", TextToSpeech.QUEUE_ADD, null, "tts4")
        textToSpeechEngine.speak("5", TextToSpeech.QUEUE_ADD, null, "tts5")
        textToSpeechEngine.speak("6", TextToSpeech.QUEUE_ADD, null, "tts6")
    }
}