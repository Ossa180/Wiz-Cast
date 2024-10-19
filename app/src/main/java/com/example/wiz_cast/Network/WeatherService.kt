package com.example.wiz_cast.Network

import com.example.wiz_cast.Model.Pojo.CurrentWeather
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherService {
    @GET("weather?")
    suspend fun getWeather(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("appid") appid: String, // API Key
        @Query("units") units: String,
        @Query("lang") lang: String
    ): Response<CurrentWeather>
}