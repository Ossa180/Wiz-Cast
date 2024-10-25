package com.example.wiz_cast.Screens.FavoriteScreen.ViewModel

import WeatherLocalDataSource
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.wiz_cast.Model.Repository.WeatherRepository

class FavoriteViewModelFactory(
    private val repository: WeatherRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FavoriteViewModel::class.java)) {
            return FavoriteViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

