package com.example.demoapplication

class DataPoint(
    val x: Float,
    val y: Float,
    val z: Float,
    val timeStamp: Long,
) {
    val asCSV: String
        get() {
            return "${x},${y},${z},${timeStamp}"
        }
}

fun pointToFloat(point: DataPoint): FloatArray {
    return floatArrayOf(point.x, point.y, point.z, point.timeStamp.toFloat())
}

fun pointsToFloats(data: List<DataPoint>): List<FloatArray> {
    return data.map { pointToFloat(it) }
}
