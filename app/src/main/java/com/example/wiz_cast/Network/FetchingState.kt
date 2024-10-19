package com.example.wiz_cast.Network

import com.example.wiz_cast.Model.Pojo.CurrentWeather

sealed class FetchingState {
    object Loading : FetchingState()
    data class Success(val weather: CurrentWeather) : FetchingState()
    data class Error(val message: String) : FetchingState()
}