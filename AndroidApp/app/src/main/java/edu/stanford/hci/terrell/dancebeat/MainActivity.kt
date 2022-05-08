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
import androidx.activity.viewModels
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
import dagger.hilt.android.AndroidEntryPoint
import edu.stanford.hci.terrell.dancebeat.ui.theme.DanceBeatTheme
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity: ComponentActivity() {
    private val dataViewModel :DataViewModel by viewModels()
    var trackSteps = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DanceBeatTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    MainScreen(dataViewModel = dataViewModel)
                }
            }
        }
    }
    override fun onPause() {
        dataViewModel.onPause()
        super.onPause()
    }

    override fun onDestroy() {
        dataViewModel.onDestroy()
        super.onDestroy()
    }
}