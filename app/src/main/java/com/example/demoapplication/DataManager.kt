package com.example.demoapplication

import com.example.demoapplication.DataPoint

class SensorInitData(val name: String, val type: Int)

interface DataManager {
    val numPoints: Int

    val recordedData: Map<Int, List<DataPoint>>

    fun startRecording()
    fun stopRecording()
    fun clear()
    fun getRecentPoints(): Map<Int, List<DataPoint>>
}
