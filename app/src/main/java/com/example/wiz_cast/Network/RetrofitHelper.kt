package com.example.wiz_cast.Network

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitHelper {
    private const val BASE_URL = "https://api.openweathermap.org/data/2.5/"

    // Increase connection to avoid timeout errors
    val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)  // Increase connection timeout to 30 seconds
        .readTimeout(30, TimeUnit.SECONDS)     // Increase read timeout to 30 seconds
        .writeTimeout(30, TimeUnit.SECONDS)    // Increase write timeout to 30 seconds
        .build()

    val retrofitInstance: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .client(okHttpClient)
        .build()

    val apiService: WeatherService = retrofitInstance.create(WeatherService::class.java)
}