package com.example.wiz_cast.Network

import com.example.wiz_cast.Model.Pojo.CurrentWeather

sealed class WeatherState {
    object Loading : WeatherState()
    data class Success(val weather: CurrentWeather) : WeatherState()
    data class Error(val message: String) : WeatherState()
}