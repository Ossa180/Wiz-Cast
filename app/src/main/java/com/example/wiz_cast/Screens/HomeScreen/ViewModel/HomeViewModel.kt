package com.example.wiz_cast.Screens.HomeScreen.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wiz_cast.Model.Repository.WeatherRepository
import com.example.wiz_cast.Network.WeatherState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HomeViewModel(private val repository: WeatherRepository) : ViewModel() {
    private val _weatherState = MutableStateFlow<WeatherState>(WeatherState.Loading)
    val weatherState: StateFlow<WeatherState> get() = _weatherState

    fun fetchWeather(lat: Double, lon: Double, appid: String, units: String, lang: String) {
        viewModelScope.launch {
            repository.fetchWeather(lat, lon, appid, units, lang).collect { state ->
                _weatherState.value = state
            }
        }
    }
}