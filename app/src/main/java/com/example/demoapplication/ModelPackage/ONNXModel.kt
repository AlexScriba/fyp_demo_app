//package com.example.demoapplication.ModelPackage
//
//import ai.onnxruntime.OnnxTensor
//import ai.onnxruntime.OrtEnvironment
//import ai.onnxruntime.OrtException
//import ai.onnxruntime.OrtSession
//import android.content.res.Resources
//import android.graphics.ColorSpace
//import android.hardware.Sensor
//import com.example.demoapplication.DataPoint
//import com.example.demoapplication.R
//import com.example.demoapplication.SensorData
//import org.apache.commons.math3.analysis.interpolation.SplineInterpolator
//import org.apache.commons.math3.analysis.polynomials.PolynomialSplineFunction
//import org.apache.commons.math3.util.MathArrays
//
//class ONNXModel(private val interpolateNum: Int, resources: Resources) : ColorSpace.Model {
//    private val session: OrtSession
//    private val environment: OrtEnvironment = OrtEnvironment.getEnvironment()
//
//    init {
//        // Load ONNX model from assets directory
//        val modelBytes = resources.openRawResource(R.raw.model).readBytes()
//        session = environment.createSession(modelBytes)
//    }
//
//    @Throws(OrtException::class)
//    override
//    fun predict(input: Array<DoubleArray>): Float {
//        val inputName = session.inputNames?.iterator()?.next()
//
//        val inputTensor = OnnxTensor.createTensor(environment, input)
//        val pred = session.run(mapOf(inputName to inputTensor))
//
//        val output = pred[0].value as Array<FloatArray>
//        return output[0][0]
//    }
//
//    fun prepare(data: Map<Int, List<DataPoint>>): FloatArray {
//        val accData = interpolate(data[Sensor.TYPE_ACCELEROMETER]!!, interpolateNum)
//        val gravData = interpolate(data[Sensor.TYPE_GRAVITY]!!, interpolateNum)
//        val gyroData = interpolate(data[Sensor.TYPE_GYROSCOPE]!!, interpolateNum)
//        val rotData = interpolate(data[Sensor.TYPE_ROTATION_VECTOR]!!, interpolateNum)
//
//        val accumData =
//            Array(interpolateNum) { i -> accData[i] + gyroData[i] + gravData[i] + rotData[i] }
//
//        // TODO ::: get kmeans model into here and make BOW
//    }
//
//    // TODO ::: Decide if I want the return type to be List or Array
//    fun interpolate(data: List<DataPoint>, numPointsAfter: Int): Array<DoubleArray> {
//        val timeStamps = data.map { point -> point.timeStamp.toDouble() }.toDoubleArray()
//
//        val newTimeStamps =
//            MathArrays.normalizeArray(DoubleArray(numPointsAfter) { i ->
//                timeStamps.min() + i * (timeStamps.max() - timeStamps.min()) / (numPointsAfter - 1)
//            }, 0.0)
//
//        val resData = data.map { point ->
//            doubleArrayOf(
//                point.x.toDouble(),
//                point.y.toDouble(),
//                point.z.toDouble(),
//            )
//        }
//
//        val res = Array(resData[0].size) { DoubleArray(numPointsAfter) }
//
//        val interpolator = SplineInterpolator()
//
//        for (i in resData[0].indices) {
//            val y = resData.map { it[i] }.toDoubleArray()
//            val spline: PolynomialSplineFunction = interpolator.interpolate(timeStamps, y)
//
//            val new_y = DoubleArray(newTimeStamps.size) { j -> spline.value(newTimeStamps[j]) }
//            res[i] = new_y
//        }
//
//        return res
//    }
//
//}