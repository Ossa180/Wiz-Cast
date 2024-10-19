package com.example.wiz_cast.Network

import com.example.wiz_cast.Model.Pojo.CurrentWeather
import retrofit2.Response

class WeatherRemoteDataSourceImpl(private val weatherService: WeatherService) : WeatherRemoteDataSource {
    override suspend fun fetchWeather(
        lat: Double,
        lon: Double,
        appid: String,
        units: String,
        lang: String
    ): Response<CurrentWeather> {
        return weatherService.getWeather(lat, lon, appid, units, lang)
    }
}
