package com.example.wiz_cast.Model.Pojo

data class FiveDaysWeather(
    val city: City,
    val cnt: Int,
    val cod: String,
    val list: List<Item0>,
    val message: Int
)