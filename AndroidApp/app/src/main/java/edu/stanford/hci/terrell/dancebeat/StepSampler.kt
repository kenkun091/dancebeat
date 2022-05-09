package edu.stanford.hci.terrell.dancebeat

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.SystemClock
import android.util.Log
import androidx.core.content.ContextCompat.getSystemService
import androidx.lifecycle.MutableLiveData
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlin.math.sqrt
import kotlin.math.abs

class StepSampler @Inject constructor(@ApplicationContext context: Context): SensorEventListener{
    val timeStart: Long = -1

    val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    val accelerometer: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION)
    val gravity: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY)

    var rollingBPM: MutableLiveData<Long> = MutableLiveData<Long>(120)
    var rollingSensorHz: Long  = 60
    var downbeatOffset: Long  = 0
    var maxHistory = rollingSensorHz * 3
    val accelerometerHistory = mutableListOf<AccelerometerEvent>()

    val smoothing = 60F

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
    // http://aosabook.org/en/500L/a-pedometer-in-the-real-world.html
    var x_g = 0F
    var y_g = 10F
    var z_g = 0F

    override fun onSensorChanged(event: SensorEvent?) {
        if (event == null) {
            return
        }

        if (event.sensor.type == Sensor.TYPE_GRAVITY) {
            x_g = (event.values[0] - x_g) / smoothing + x_g
            y_g = (event.values[1] - y_g) / smoothing + y_g
            z_g = (event.values[2] - z_g) / smoothing + z_g
            return
        }

        val t_u = event.timestamp
        val x_u = event.values[0]
        val y_u = event.values[1]
        val z_u  = event.values[2]

         // var a = sqrt((x_u * x_u) + (y_u * y_u) + (z_u * z_u))

         var a = ((x_u * x_g) + (y_u * y_g) + (z_u * z_g))
        // a = abs(a)
        // var a = sqrt(y_u * y_u)
        // Log.i("Test_ACC", "t2: " + SystemClock.elapsedRealtime())
        // Log.i("Test_ACC", "x: " + x.toString())
        // Log.i("Test_ACC", "y: " + y.toString())
        // Log.i("Test_ACC", "z: " + z.toString())

        if (accelerometerHistory.size > 0) {
            val aPast = accelerometerHistory[accelerometerHistory.size - 1].a
            a = ((a - aPast) / smoothing) + aPast
        }

        accelerometerHistory.add(AccelerometerEvent(t_u, a))
        if (accelerometerHistory.size > maxHistory) {
            accelerometerHistory.removeFirst()
        }

        rollingBPM.postValue(calculateRollingBPM(rollingBPM.value!!, accelerometerHistory))
        Log.i("Test_ACC", "BPM: " + rollingBPM.value.toString())
    }

    fun calculateRollingBPM(currBpm: Long, history: List<AccelerometerEvent>): Long {
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
        val avgOffset = sum / (peaks.size - 1)
        //var bpm = (avgOffset.toFloat() / (1000000F)).toLong()
        // Log.d("Test_ACC", "Offset: " + avgOffset.toString())
        //var bpm = (avgOffset.toFloat() / 1000000F * 60F).toLong()
        var bpmF = avgOffset.toFloat() / 1000000F
        bpmF = 1F / bpmF * 60000F
        // bpmF = (bpmF - currBpm.toFloat()) / smoothing + currBpm.toFloat()
        return (bpmF).toLong()
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
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME)
        sensorManager.registerListener(this, gravity, SensorManager.SENSOR_DELAY_GAME)

    }

    fun stopTracking() {
        sensorManager.unregisterListener(this)
    }
}

data class AccelerometerEvent(val t: Long, val a: Float)