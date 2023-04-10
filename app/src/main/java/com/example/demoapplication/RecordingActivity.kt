package com.example.demoapplication

import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorManager
import android.media.Ringtone
import android.media.RingtoneManager
import android.net.Uri
import android.os.*
import android.view.Gravity
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import kotlinx.coroutines.Runnable

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

val RECORD_DURATION: Long = 7000
val START_DELAY: Long = 5000
val UPDATE_GRAPH_INTERVAL: Long = 125

/**
 * Recording activity for recording phase of demo
 * Starts recording, handles graphs and backend calls
 */
class RecordingActivity : AppCompatActivity() {
    private lateinit var sensorManager: SensorManager
    private lateinit var dataManager: DataManager

    private lateinit var txtCountdown: TextView
    private lateinit var txtCountdownMessage: TextView

    private var vibrator: Vibrator? = null
    private var ringtone: Ringtone? = null

    private lateinit var accelerometerChart: LineChart
    private lateinit var gyroscopeChart: LineChart
    private lateinit var gravityChart: LineChart
    private lateinit var rotationChart: LineChart

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.recording_screen)

        // Set up sensorManager
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager

        val sensorSelection = listOf(
            SensorInitData("Accelerometer", Sensor.TYPE_ACCELEROMETER),
            SensorInitData("Gyroscope", Sensor.TYPE_GYROSCOPE),
            SensorInitData("Gravity", Sensor.TYPE_GRAVITY),
            SensorInitData("Rotation", Sensor.TYPE_ROTATION_VECTOR),
        )

        dataManager = DataManagerConstant(
            sensorManager,
            sensorSelection
        )

        // Init vibration and sound
        vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

        val notification: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        ringtone = RingtoneManager.getRingtone(applicationContext, notification)

        // init UI elements
        txtCountdown = findViewById<TextView>(R.id.txt_countdown)
        txtCountdownMessage = findViewById<TextView>(R.id.txt_countdown_message)

        // Set up Chart
        accelerometerChart = findViewById(R.id.accelerometer_chart)
        gyroscopeChart = findViewById(R.id.gyroscope_chart)
        gravityChart = findViewById(R.id.gravity_chart)
        rotationChart = findViewById(R.id.rotation_chart)

//        val isDarkMode = DataService.isInDarkMode(this)
        val isDarkMode = true

        DataService.configureChart("Accelerometer", accelerometerChart, isDarkMode)
        DataService.configureChart("Gyroscope", gyroscopeChart, isDarkMode)
        DataService.configureChart("Gravity", gravityChart, isDarkMode)
        DataService.configureChart("Rotation", rotationChart, isDarkMode)

        // Start the recording
        displayMessage("Please prepare.")
        txtCountdownMessage.text = getString(R.string.countdown_start_message)
        runAfterCountdown(START_DELAY) { this.startRecording() }
    }

    /**
     * Handle start and stop recording
     */
    private fun startRecording() {
        // handle UI
        displayMessage("Recording")
        txtCountdownMessage.text = getString(R.string.recording_for_message)
        runAfterCountdown(RECORD_DURATION) { stopRecording() }
        pingUser()

        // handle data manager
        dataManager.startRecording()

        // handle chart
        startChart()
    }

    private fun stopRecording() {
        // handle data manager
        dataManager.stopRecording()
        pingUser()

        // Handle chart
        stopChart()

        txtCountdownMessage.text = getString(R.string.evatuating_message)
        txtCountdown.text = ""

        // handle UI
        displayMessage("Finished recording. Evaluating data.")

        makeApiCall(dataManager.recordedData)
    }

    /**
     * Handle messages to user
     */
    private fun displayMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }


    /**
     * Handle countdown
     */
    private var activeCountdown: CountDownTimer? = null

    private fun runAfterCountdown(time: Long, func: () -> Unit) {
        activeCountdown?.cancel()

        txtCountdown.text = (time / 1000L).toString()

        activeCountdown = object : CountDownTimer(time, (time / 1000L)) {
            override fun onTick(millisUntilFin: Long) {
                txtCountdown.text = (millisUntilFin / 1000 + 1).toString()
            }

            override fun onFinish() {
                func()
            }
        }

        activeCountdown?.start()
    }

    private fun stopCountdown() {
        activeCountdown?.cancel()
        txtCountdown.text = ""
    }

    /**
     * Handle user notification
     */
    private fun pingUser(vibrate: Boolean = true, sound: Boolean = true) {
        if (vibrate) buzzVibrator()
        if (sound) playSound()
    }

    private fun buzzVibrator(duration: Long = 400) {
        if (Build.VERSION.SDK_INT >= 26) {
            vibrator?.vibrate(
                VibrationEffect.createOneShot(
                    duration,
                    VibrationEffect.DEFAULT_AMPLITUDE
                )
            )
        } else {
            vibrator?.vibrate(duration)
        }
    }

    private fun playSound() {
        ringtone?.play()
    }

    // TEMP
    private fun onResultReceived(res: ResponseBody?) {
        if (res == null) {
            displayMessage("Result was null")
            return
        }

        startActivity(Intent(this, ResultActivity::class.java).apply {
            putExtra("result", res.result)
            putExtra("attackerProbability", res.attackerProbability)
            putExtra("userProbability", res.userProbability)
        })
    }

    private fun makeApiCall(data: Map<Int, List<DataPoint>>) {
        val accData = pointsToFloats(data[Sensor.TYPE_ACCELEROMETER]!!)
        val gyroData = pointsToFloats(data[Sensor.TYPE_GYROSCOPE]!!)
        val gravData = pointsToFloats(data[Sensor.TYPE_GRAVITY]!!)
        val rotData = pointsToFloats(data[Sensor.TYPE_ROTATION_VECTOR]!!)

        val requestBody = RequestBody(
            accelerometer = accData,
            gyroscope = gyroData,
            gravity = gravData,
            rotation = rotData
        )

        RetrofitClient.instance.makeRequest(requestBody).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val result = response.body()
                    onResultReceived(result)
                } else {
                    displayMessage("There was some error with the response: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                displayMessage("There was an error connecting to the Network: ${t.message}")
            }
        })
    }

    private var mHandler: Handler = Handler(Looper.getMainLooper())
    private var chartUpdateRunnable: Runnable = object : Runnable {
        override fun run() {
            val map = dataManager.getRecentPoints()
            addEntriesToChart(accelerometerChart, map[Sensor.TYPE_ACCELEROMETER]!!)
            addEntriesToChart(gyroscopeChart, map[Sensor.TYPE_GYROSCOPE]!!)
            addEntriesToChart(gravityChart, map[Sensor.TYPE_GRAVITY]!!)
            addEntriesToChart(rotationChart, map[Sensor.TYPE_ROTATION_VECTOR]!!)

            mHandler.postDelayed(this, UPDATE_GRAPH_INTERVAL)
        }
    }

    private fun startChart() {
        mHandler.post(chartUpdateRunnable)
    }

    private fun stopChart() {
        mHandler.removeCallbacks(chartUpdateRunnable)
    }

    private fun addEntriesToChart(chart: LineChart, points: List<DataPoint>) {
        val data = chart.data

        for (point in points) {
            data.addEntry(Entry(point.timeStamp.toFloat(), point.x), 0)
            data.addEntry(Entry(point.timeStamp.toFloat(), point.y), 1)
            data.addEntry(Entry(point.timeStamp.toFloat(), point.z), 2)
        }
        data.notifyDataChanged()

        chart.notifyDataSetChanged()
        chart.moveViewToX(data.entryCount.toFloat())
    }

}