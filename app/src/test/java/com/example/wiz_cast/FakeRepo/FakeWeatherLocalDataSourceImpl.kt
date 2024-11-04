package com.example.wiz_cast.FakeRepo

import WeatherLocalDataSource
import com.example.wiz_cast.Model.DataBase.FavoriteLocation
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeWeatherLocalDataSourceImpl : WeatherLocalDataSource {

    private val favoriteLocations = mutableListOf<FavoriteLocation>()

    override suspend fun saveFavoriteLocation(favoriteLocation: FavoriteLocation) {
        favoriteLocations.add(favoriteLocation)
    }

    override fun getFavoriteLocations(): Flow<List<FavoriteLocation>> = flow {
        emit(favoriteLocations)
    }

    override suspend fun deleteFavoriteLocation(lat: Double, lon: Double) {
        favoriteLocations.removeAll { it.latitude == lat && it.longitude == lon }
    }
}


