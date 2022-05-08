package edu.stanford.hci.terrell.dancebeat

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.SystemClock
import android.util.Log
import androidx.core.content.ContextCompat.getSystemService
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlin.math.sqrt

class StepSampler @Inject constructor(@ApplicationContext context: Context): SensorEventListener{
    val timeStart: Long = -1

    val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    val sensor: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION)

    var rollingBPM: Long = 120
    var rollingSensorHz: Long  = 60
    var downbeatOffset: Long  = 0
    var maxHistory = rollingSensorHz * 10
    val accelerometerHistory = mutableListOf<AccelerometerEvent>()

    val smoothing = 60

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
    override fun onSensorChanged(event: SensorEvent?) {
        if (event == null) {
            return
        }
        val t = event.timestamp
        val x = event.values[0]
        val y = event.values[1]
        val z = event.values[2]
        // var a = sqrt((x * x) + (y * y) + (z * z))
        var a = sqrt(y * y)
        // Log.i("Test_ACC", "t2: " + SystemClock.elapsedRealtime())
        // Log.i("Test_ACC", "x: " + x.toString())
        // Log.i("Test_ACC", "y: " + y.toString())
        // Log.i("Test_ACC", "z: " + z.toString())

        if (accelerometerHistory.size > 0) {
            val aPast = accelerometerHistory[accelerometerHistory.size - 1].a
            a = ((a - aPast) / smoothing) + aPast
            a = a / 2
        }

        accelerometerHistory.add(AccelerometerEvent(t, a))
        if (accelerometerHistory.size > maxHistory) {
            accelerometerHistory.removeFirst()
        }

        rollingBPM = calculateRollingBPM(accelerometerHistory)
        Log.i("Test_ACC", "BPM: " + rollingBPM.toString())
    }

    fun calculateRollingBPM(history: List<AccelerometerEvent>): Long {
        val peaks = findPeaks(history)

        var sum: Long = 0

        for (i in peaks.indices) {
            if (i > 0) {
                sum += peaks[i].t - peaks[i - 1].t
            }
        }
        if (peaks.size - 1  == 0 ) {
            return 60
        }
        val offset = sum / (peaks.size - 1)
        val bpm = offset / 1000000
        return bpm
    }

    private fun findPeaks(history: List<AccelerometerEvent>): List<AccelerometerEvent> {
        val peaks = mutableListOf<AccelerometerEvent>()

        for (i in history.indices) {
            if (i > 0 && i < history.size - 1 ) {
                if ((history[i].a > history[i - 1].a) && (history[i].a > history[i + 1].a)) {
                    peaks.add(history[i])
                }
            }
        }

        return peaks
    }

    fun startTracking() {
        sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_GAME)
    }

    fun stopTracking() {
        sensorManager.unregisterListener(this)
    }
}

data class AccelerometerEvent(val t: Long, val a: Float)