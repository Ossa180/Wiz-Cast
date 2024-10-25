package com.example.wiz_cast.Model.Repository

import WeatherLocalDataSource
import com.example.wiz_cast.Model.DataBase.FavoriteLocation
import com.example.wiz_cast.Model.Pojo.FiveDaysWeather
import com.example.wiz_cast.Network.FiveDayForecastState
import com.example.wiz_cast.Network.WeatherRemoteDataSource
import com.example.wiz_cast.Network.WeatherState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.retryWhen
import java.io.IOException

class WeatherRepository(
    private val remoteDataSource: WeatherRemoteDataSource,
    private val localDataSource: WeatherLocalDataSource
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
    }.retryWhen { cause, attempt ->
        // Retry up to 3 times if the cause is a network-related issue
        (cause is IOException && attempt < 3).also {
            if (it) delay(2000) // Wait for 2 seconds before retrying
        }
    }.catch { exception ->
        emit(WeatherState.Error("An error occurred: ${exception.localizedMessage}"))
    }

    fun fetchFiveDayForecast(lat: Double, lon: Double, apiKey: String, units: String, lang: String): Flow<FiveDayForecastState> = flow {
        emit(FiveDayForecastState.Loading) // Emit loading state
        val response = remoteDataSource.fetchFiveDayForecast(lat, lon, apiKey,units, lang)

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

    // Local data source functions
    fun getFavoriteLocations(): Flow<List<FavoriteLocation>> = localDataSource.getFavoriteLocations()

    suspend fun addFavoriteLocation(location: FavoriteLocation) {
        localDataSource.saveFavoriteLocation(location)
    }

    suspend fun removeFavoriteLocation(locationId: Int) {
        localDataSource.deleteFavoriteLocation(locationId)
    }

}
