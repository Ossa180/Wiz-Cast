package com.example.wiz_cast.Model.Repository

import com.example.wiz_cast.Model.DataBase.FavoriteLocation
import com.example.wiz_cast.Network.FiveDayForecastState
import com.example.wiz_cast.Network.WeatherState
import kotlinx.coroutines.flow.Flow

interface IWeatherRepository {
    // Fetch weather data as a Flow, emitting different states
    fun fetchWeather(
        lat: Double,
        lon: Double,
        appid: String,
        units: String,
        lang: String
    ): Flow<WeatherState>

    fun fetchFiveDayForecast(
        lat: Double,
        lon: Double,
        apiKey: String,
        units: String,
        lang: String
    ): Flow<FiveDayForecastState>

    // Local data source functions
    fun getFavoriteLocations(): Flow<List<FavoriteLocation>>

    suspend fun addFavoriteLocation(location: FavoriteLocation)

    suspend fun removeFavoriteLocation(lat: Double, lon: Double)
}