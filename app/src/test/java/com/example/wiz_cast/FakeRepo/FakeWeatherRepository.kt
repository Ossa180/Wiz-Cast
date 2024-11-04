package com.example.wiz_cast.FakeRepo

import com.example.wiz_cast.Model.DataBase.FavoriteLocation
import com.example.wiz_cast.Model.Repository.IWeatherRepository
import com.example.wiz_cast.Model.Repository.WeatherRepository
import com.example.wiz_cast.Network.FiveDayForecastState
import com.example.wiz_cast.Network.WeatherState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class FakeWeatherRepository : IWeatherRepository {

    private val favoriteLocationsFlow = MutableStateFlow<List<FavoriteLocation>>(emptyList())

    fun setFavoriteLocations(locations: List<FavoriteLocation>) {
        favoriteLocationsFlow.value = locations
    }

    override fun getFavoriteLocations() = favoriteLocationsFlow

    override suspend fun addFavoriteLocation(location: FavoriteLocation) {
        val updatedLocations = favoriteLocationsFlow.value.toMutableList().apply { add(location) }
        favoriteLocationsFlow.value = updatedLocations
    }

    override suspend fun removeFavoriteLocation(lat: Double, lon: Double) {
        val updatedLocations = favoriteLocationsFlow.value.filterNot { it.latitude == lat && it.longitude == lon }
        favoriteLocationsFlow.value = updatedLocations
    }

    override fun fetchWeather(
        lat: Double, lon: Double, appid: String, units: String, lang: String
    ): Flow<WeatherState> {
        throw NotImplementedError("This method is not required for the current test")
    }

    override fun fetchFiveDayForecast(
        lat: Double, lon: Double, apiKey: String, units: String, lang: String
    ): Flow<FiveDayForecastState> {
        throw NotImplementedError("This method is not required for the current test")
    }
}
