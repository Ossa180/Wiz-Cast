package com.example.wiz_cast.Model.DataBase

import WeatherLocalDataSource
import kotlinx.coroutines.flow.Flow

class WeatherLocalDataSourceImpl(private val weatherDao: WeatherDao) : WeatherLocalDataSource {

    override suspend fun saveFavoriteLocation(favoriteLocation: FavoriteLocation) {
        weatherDao.insertFavoriteLocation(favoriteLocation)
    }

    override fun getFavoriteLocations(): Flow<List<FavoriteLocation>> {
        return weatherDao.getAllFavoriteLocations()
    }

    override suspend fun deleteFavoriteLocation(id: Int) {
        weatherDao.removeFavoriteLocation(id)
    }
}
