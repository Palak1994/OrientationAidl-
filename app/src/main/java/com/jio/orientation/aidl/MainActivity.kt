package com.jio.orientation.aidl

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer

class MainActivity : AppCompatActivity() {


    private var orientationInterface: OrientationInterface? = null
    private var orientationConnection: ServiceConnection? = null
    private var orientationIntent: Intent? = null
    private lateinit var orientationText: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        orientationText = findViewById(R.id.imu)
        addSensorDataObserver()
        bindOrientationService()
    }

    private fun addSensorDataObserver() {
        OrientationService.sensorData.observe(this, Observer {
            orientationText.text = it.contentToString()
        })
    }

    private fun getDataFromAIDL() {
        orientationInterface?.orientation()?.let {
            orientationText.text = it
        }
    }

    private fun bindOrientationService() {
        orientationIntent = Intent(this, OrientationService::class.java)
        orientationConnection = object : ServiceConnection {
            override fun onServiceConnected(componentName: ComponentName?, binder: IBinder?) {
                orientationInterface = OrientationInterface.Stub.asInterface(binder)
                getDataFromAIDL()
            }

            override fun onServiceDisconnected(componentName: ComponentName?) {
            }
        }
        orientationIntent?.let {
            orientationConnection?.let {
                bindService(
                    orientationIntent,
                    it,
                    Context.BIND_AUTO_CREATE
                )
            }
        }
    }
}