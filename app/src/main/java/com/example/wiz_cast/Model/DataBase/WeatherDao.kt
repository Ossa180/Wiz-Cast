package com.example.wiz_cast.Model.DataBase

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface WeatherDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavoriteLocation(favoriteLocation: FavoriteLocation)

    @Query("SELECT * FROM favorite_location_table")
    fun getAllFavoriteLocations(): Flow<List<FavoriteLocation>>

    @Query("DELETE FROM favorite_location_table WHERE latitude = :lat AND longitude = :lon")
    suspend fun removeFavoriteLocation(lat: Double, lon: Double)

    // check if location is already favorite
    @Query("SELECT COUNT(*) FROM favorite_location_table WHERE name = :cityName")
    suspend fun isLocationFavorite(cityName: String): Int

    @Query("DELETE FROM favorite_location_table WHERE name = :cityName")
    suspend fun removeFavoriteLocationByName(cityName: String)
}