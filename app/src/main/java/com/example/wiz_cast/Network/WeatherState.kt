package com.example.wiz_cast.Network

import com.example.wiz_cast.Model.Pojo.CurrentWeather
import com.example.wiz_cast.Model.Pojo.FiveDaysWeather

sealed class WeatherState {
    object Loading : WeatherState()
    data class Success(val weather: CurrentWeather) : WeatherState()
    data class Error(val message: String) : WeatherState()
}

sealed class FiveDayForecastState {
    object Loading : FiveDayForecastState()
    data class Success(val weather: FiveDaysWeather) : FiveDayForecastState()
    data class Error(val message: String) : FiveDayForecastState()
}