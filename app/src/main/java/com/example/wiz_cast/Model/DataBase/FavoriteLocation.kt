package com.example.wiz_cast.Model.DataBase

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_location_table")
data class FavoriteLocation(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val description: String,
    val temperature: Double,
    val weatherIcon: String
)

