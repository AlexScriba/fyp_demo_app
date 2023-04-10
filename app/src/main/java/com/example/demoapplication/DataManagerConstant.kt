package com.example.demoapplication

import android.hardware.SensorManager

/**
 * DataManager implementation that uses SensorDataConstant for sensor recording
 */
class DataManagerConstant(
    private val sensorManager: SensorManager,
    private val initData: List<SensorInitData>
) : DataManager {
    private val sensors: MutableMap<Int, SensorData>

    init {
        sensors = mountSensors(sensorManager, initData)
    }

    /** Returns the number of datapoints currently recorded */
    override val numPoints: Int
        get() {
            val list = sensors[sensors.keys.first()]

            if (list != null) return list.historyLength
            return -1
        }

    /** Gets map containing current data of all sensors */
    override val recordedData: Map<Int, List<DataPoint>>
        get() {
            return sensors.mapValues { (_, sensor) -> sensor.dataPoints }
        }

    /** Mounts sensors on Systems SensorManager and creates map of SensorData for each sensor */
    fun mountSensors(
        sensorManager: SensorManager,
        initData: List<SensorInitData>
    ): MutableMap<Int, SensorData> {
        val sensors: MutableMap<Int, SensorData> = mutableMapOf()

        for (sData in initData) {
            val data = SensorData(sData.name, sData.type, 0.0f, 0.0f, 0.0f, timeStamp = 0)

            sensors[sData.type] = data

            sensorManager.getDefaultSensor(sData.type)?.also { sensor ->
                sensorManager.registerListener(
                    data,
                    sensor,
                    android.hardware.SensorManager.SENSOR_DELAY_FASTEST
                )
            }
        }

        return sensors
    }

    /** Starts recroding on all sensors */
    override fun startRecording() {
        sensors.forEach { (_, sensor) -> sensor.startRecording() }
    }

    /** Stops recording for all sensors */
    override fun stopRecording() {
        sensors.forEach { (_, sensor) -> sensor.stopRecording() }
    }

    /** Clears all sensors recordings */
    override fun clear() {
        sensors.forEach { (_, sensor) -> sensor.clear() }
    }

    override fun getRecentPoints(): Map<Int, List<DataPoint>> {
        return sensors.mapValues { (_, sensor) -> sensor.getRecentPointsAndClear() }
    }
}
