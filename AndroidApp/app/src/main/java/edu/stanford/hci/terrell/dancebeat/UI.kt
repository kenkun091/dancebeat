package edu.stanford.hci.terrell.dancebeat

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun MainScreen(dataViewModel: DataViewModel) {
Column() {
    Row() {
        Button(
            // below line is use to add onclick
            // parameter for our button onclick
            onClick = {
                // when user is clicking the button
                // we are displaying a toast message.
                dataViewModel.testTTS()
            }
        )
        {
            Text(text = "TTS_Test", color = Color.White)
        }
        Button(
            onClick = {
                dataViewModel.startStopTrackingSteps()
            }
        )
        {
            Text(text = "Step_Test", color = Color.White)
        }
        Button(
            // below line is use to add onclick
            // parameter for our button onclick
            onClick = {
                // when user is clicking the button
                // we are displaying a toast message.
                dataViewModel.testTTS()
            }
        )
        {
            Text(text = "TTS_Test", color = Color.White)
        }
    }
}
}