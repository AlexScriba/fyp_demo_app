//package com.example.demoapplication.ModelPackage
//
//import ai.onnxruntime.OnnxTensor
//import ai.onnxruntime.OrtEnvironment
//import ai.onnxruntime.OrtException
//import ai.onnxruntime.OrtSession
//import android.content.res.Resources
//import com.example.demoapplication.R
//import org.apache.commons.math3.util.MathArrays
//
//val NUM_BOW_WORDS = 100
//
//class KMModel(resources: Resources) {
//    private val session: OrtSession
//    private val environment: OrtEnvironment = OrtEnvironment.getEnvironment()
//
//    init {
//        // Load ONNX model from assets directory
//        val modelBytes = resources.openRawResource(R.raw.km).readBytes()
//        session = environment.createSession(modelBytes)
//    }
//
//    @Throws(OrtException::class)
//    override fun predict(input: Array<DoubleArray>): IntArray {
//        val inputName = session.inputNames?.iterator()?.next()
//
//        val inputTensor = OnnxTensor.createTensor(environment, input)
//        val pred = session.run(mapOf(inputName to inputTensor))
//
//        val output = pred[0].value as Array<FloatArray>
//        return output[0][0]
//    }
//
//    fun vstack(input: Array<DoubleArray>): DoubleArray {
//        return input.reduce { acc, arr -> acc + arr }
//    }
//
//    fun bowTransform(data: Array<DoubleArray>): DoubleArray {
//        val preds = this.predict(data)
//        val bow = MathArrays.binCount()
//
//    }
//
//}