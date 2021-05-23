package com.jio.orientation.aidl

import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.IBinder
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.MutableLiveData


private const val DELAY = 8 * 1000 // 8ms

class OrientationService : LifecycleService(), SensorEventListener {

    companion object {
        val sensorData = MutableLiveData<FloatArray>()
    }

    private var manager: SensorManager? = null
    private var rotationSensor: Sensor? = null

    private fun sensorManager() {
        if (manager == null) {
            manager = getSystemService(SENSOR_SERVICE) as SensorManager
            rotationSensor = manager?.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)
            rotationSensor?.let {
                addRotationSensorListener()
            }
        }
    }

    private fun addRotationSensorListener() {
        manager?.registerListener(
            this,
            rotationSensor,
            DELAY
        )
    }

    private val myBinder: OrientationInterface.Stub = object : OrientationInterface.Stub() {
        override fun orientation(): String {
            sensorManager()
            return sensorData.value?.contentToString() ?: "Press button"
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        super.onBind(intent)
        return myBinder
    }

    override fun onSensorChanged(sensorEvent: SensorEvent?) {
        sensorEvent?.let {
            if (it.sensor.type == Sensor.TYPE_ROTATION_VECTOR) {
                sensorData.value = it.values
            }

        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
    }
}
