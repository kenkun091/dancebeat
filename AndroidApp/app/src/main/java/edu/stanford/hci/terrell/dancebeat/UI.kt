package edu.stanford.hci.terrell.dancebeat

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun MainScreen(dataViewModel: DataViewModel) {
    val danceBPM = dataViewModel.getStepBPM().observeAsState()
    val musicBPM = dataViewModel.getAudioBPM().observeAsState()

    Column() {
        Row() {
            Text("Dance BPM: " + danceBPM.value.toString(), fontSize = 50.sp)
        }
        Row() {
            Text("Music BPM: " + musicBPM.value.toString(), fontSize = 50.sp)
        }
        Row() {
            Button(
                shape = RoundedCornerShape(10.dp),
                onClick = {
                    dataViewModel.startStopTrackingSteps()
                }
            )
            {
                Text(text = "Track Steps", color = Color.White)
            }
            Button(
                shape = RoundedCornerShape(10.dp),
                onClick = {
                    dataViewModel.recordAudio()
                }
            )
            {
                Text(text = "Track Music", color = Color.White)
            }
            Button(
                shape = RoundedCornerShape(10.dp),
                onClick = {
                    dataViewModel.stopTone()
                }
            )
            {
                Text(text = "Stop Beat", color = Color.White)
            }
        }
    }
}