package com.example.demoapplication

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

//const val BACKEND_URL = "http://127.0.0.1:5000/"
const val BACKEND_URL = "http://192.168.50.162:3000/"
//const val BACKEND_URL = "http://52.54.218.225:3000/"

//const val BACKEND_URL = "http://10.0.2.2:5000/"
const val URL_ENDPOINT = "$BACKEND_URL/predict"

data class RequestBody(
    val accelerometer: List<FloatArray>,
    val gyroscope: List<FloatArray>,
    val gravity: List<FloatArray>,
    val rotation: List<FloatArray>,
)

data class ResponseBody(
    val result: Int,
    val attackerProbability: Float,
    val userProbability: Float
)

interface ApiService {
    @POST("predict")
    fun makeRequest(@Body requestBody: RequestBody): Call<ResponseBody>
}

/**
 * Object to handle making API call
 */
object RetrofitClient {
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BASIC
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .build()

    val instance: ApiService by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(BACKEND_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()

        retrofit.create(ApiService::class.java)
    }
}