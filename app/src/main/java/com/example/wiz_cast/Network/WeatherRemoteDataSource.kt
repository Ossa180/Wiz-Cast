package com.example.wiz_cast.Network

import com.example.wiz_cast.Model.Pojo.CurrentWeather
import com.example.wiz_cast.R
import retrofit2.Response

interface WeatherRemoteDataSource {
    suspend fun fetchWeather(lat: Double, lon: Double, appid: String= R.string.api_key.toString(),
                             units: String, lang: String): Response<CurrentWeather>
}