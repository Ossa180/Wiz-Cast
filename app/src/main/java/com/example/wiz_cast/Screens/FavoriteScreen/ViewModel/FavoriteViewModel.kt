package com.example.wiz_cast.Screens.FavoriteScreen.ViewModel

import WeatherLocalDataSource
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wiz_cast.Model.DataBase.FavoriteLocation
import com.example.wiz_cast.Model.Repository.IWeatherRepository
import com.example.wiz_cast.Model.Repository.WeatherRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class FavoriteViewModel(
    private val repository: IWeatherRepository // for testing
) : ViewModel() {

    private val _favoriteLocationsState = MutableStateFlow<List<FavoriteLocation>>(emptyList())
    val favoriteLocationsState: StateFlow<List<FavoriteLocation>> get() = _favoriteLocationsState

    init {
        fetchFavoriteLocations()
    }

    private fun fetchFavoriteLocations() {
        viewModelScope.launch {
            repository.getFavoriteLocations().collect { locations ->
                _favoriteLocationsState.value = locations
            }
        }
    }

    fun addFavoriteLocation(location: FavoriteLocation) {
        viewModelScope.launch {
            repository.addFavoriteLocation(location)
            fetchFavoriteLocations() // Refresh list after adding
        }
    }

    fun removeFavoriteLocation(lat: Double, lon: Double) {
        viewModelScope.launch {
            repository.removeFavoriteLocation(lat, lon)
            fetchFavoriteLocations() // Refresh list after removal
        }
    }
}

