//package com.example.demoapplication.ModelPackage
//
//import ai.onnxruntime.OrtEnvironment
//import ai.onnxruntime.OrtException
//import ai.onnxruntime.OrtSession
//import ai.onnxruntime.OnnxTensor
//import android.content.res.Resources
//
//import org.apache.commons.math3.analysis.interpolation.SplineInterpolator
//import org.apache.commons.math3.analysis.interpolation.UnivariateInterpolator
//import org.apache.commons.math3.analysis.polynomials.PolynomialSplineFunction
//import org.apache.commons.math3.util.MathArrays
//
//import com.example.demoapplication.DataPoint
//import com.example.demoapplication.ModelPackage.ONNXModel
//import com.example.demoapplication.R
//
//interface Model {
//    fun predict(input: Array<DoubleArray>): Float
//}
//
//enum class ModelType {
//    ONNX
//}
//
//object ModelFactory {
//    fun loadModel(type: ModelType, resources: Resources): Model = when (type) {
//        ModelType.ONNX -> ONNXModel(7300, resources)
//    }
//}
