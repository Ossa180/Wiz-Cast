package com.example.wiz_cast.Model.Repository

import com.example.wiz_cast.Model.Pojo.FiveDaysWeather
import com.example.wiz_cast.Network.FiveDayForecastState
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

    fun fetchFiveDayForecast(lat: Double, lon: Double, apiKey: String): Flow<FiveDayForecastState> = flow {
        emit(FiveDayForecastState.Loading) // Emit loading state
        val response = remoteDataSource.fetchFiveDayForecast(lat, lon, apiKey)

        if (response.isSuccessful) {
            response.body()?.let { fiveDaysWeather ->
                emit(FiveDayForecastState.Success(fiveDaysWeather))
            } ?: emit(FiveDayForecastState.Error("No forecast data available"))
        } else {
            emit(FiveDayForecastState.Error("Error fetching forecast: ${response.message()}"))
        }
    }.catch { exception ->
        emit(FiveDayForecastState.Error("An error occurred: ${exception.localizedMessage}"))
    }

}
