package com.example.wiz_cast.Model.Repository

import com.example.wiz_cast.Network.WeatherRemoteDataSource
import com.example.wiz_cast.Network.WeatherState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

class WeatherRepository(
    private val remoteDataSource: WeatherRemoteDataSource
) {
    // Fetch weather data as a Flow, emitting different states
    fun fetchWeather(lat: Double, lon: Double, appid: String, units: String, lang: String): Flow<WeatherState> = flow {
        emit(WeatherState.Loading) // Emit loading state
        val response = remoteDataSource.fetchWeather(lat, lon, appid, units, lang)

        if (response.isSuccessful) {
            response.body()?.let { currentWeather ->
                emit(WeatherState.Success(currentWeather))
            } ?: emit(WeatherState.Error("No data available"))
        } else {
            emit(WeatherState.Error("Error fetching weather: ${response.message()}"))
        }
    }.catch { exception ->
        emit(WeatherState.Error("An error occurred: ${exception.localizedMessage}"))
    }
}
