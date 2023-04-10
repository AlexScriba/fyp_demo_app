package com.example.demoapplication

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.os.SystemClock
import android.provider.ContactsContract.Data

class SensorData(
    val name: String,
    val type: Int,
    private var _x: Float = 0f,
    private var _y: Float = 0f,
    private var _z: Float = 0f,
    private var timeStamp: Long = 0,
) : SensorEventListener {
    /** Is the sensor recording data right now */
    private var recording: Boolean = false
    public val isRecording: Boolean
        get() {
            return recording
        }

    /** Timestamp to record data points with timestamp since recording start */
    private var initTimestamp: Long = SystemClock.elapsedRealtimeNanos()

    /** Data strucutes to store recording history */
    private var history: MutableList<DataPoint> = mutableListOf()
    private var recentUpdates: MutableList<DataPoint> = mutableListOf()

    val dataPoints: List<DataPoint>
        get() {
            return history
        }

    val historyLength: Int
        get() {
            return history.size
        }

    fun getRecentPointsAndClear(): List<DataPoint> {
        val points = recentUpdates
        recentUpdates = mutableListOf()

        return points
    }

    /** Sensor value accessors */
    val x: Float
        get() {
            return _x
        }
    val y: Float
        get() {
            return _y
        }
    val z: Float
        get() {
            return _z
        }

    /** Set values of sensors */
    private fun setValues(tx: Float, ty: Float, tz: Float, tst: Long) {
        this._x = tx
        this._y = ty
        this._z = tz
        this.timeStamp = tst

        if (recording) {
            val dp = DataPoint(tx, ty, tz, tst)
            this.history.add(dp)
            this.recentUpdates.add(dp)
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    override fun onSensorChanged(event: SensorEvent?) {
        if (event == null) return
        if (event.sensor.type != this.type) return

        this.setValues(
            event.values[0],
            event.values[1],
            event.values[2],
            event.timestamp - initTimestamp
        )
    }

    fun resetTimeStamp() {
        this.initTimestamp = SystemClock.elapsedRealtimeNanos()
    }

    fun clear() {
        history = mutableListOf()

        this.recording = false
        this.setValues(0f, 0f, 0f, 0)
    }

    fun startRecording() {
        this.recording = true
    }

    fun stopRecording() {
        this.recording = false
    }

    override fun toString(): String {
        return "${name}:\nx: ${x}\ny: ${y}\nz: ${z}\nts: ${timeStamp}"
    }
}
