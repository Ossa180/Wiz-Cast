package com.example.wiz_cast.FakeRepo

import com.example.wiz_cast.Model.Pojo.CurrentWeather
import com.example.wiz_cast.Model.Pojo.FiveDaysWeather
import com.example.wiz_cast.Network.WeatherRemoteDataSource
import retrofit2.Response

class FakeWeatherRemoteDataSourceImpl(
    private val weatherResponse: Response<CurrentWeather>? = null,
    private val forecastResponse: Response<FiveDaysWeather>? = null
) : WeatherRemoteDataSource {
    override suspend fun fetchWeather(
        lat: Double, lon: Double, appid: String, units: String, lang: String
    ): Response<CurrentWeather> {
        return weatherResponse ?: Response.error(404, okhttp3.ResponseBody.create(null, "Not Found"))
    }

    override suspend fun fetchFiveDayForecast(
        lat: Double, lon: Double, appid: String, units: String, lang: String
    ): Response<FiveDaysWeather> {
        return forecastResponse ?: Response.error(404, okhttp3.ResponseBody.create(null, "Not Found"))
    }
}
