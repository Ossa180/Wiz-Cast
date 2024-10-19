package com.example.wiz_cast.Screens.HomeScreen.View

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.wiz_cast.Model.Repository.WeatherRepository
import com.example.wiz_cast.Network.RetrofitHelper
import com.example.wiz_cast.Network.WeatherRemoteDataSourceImpl
import com.example.wiz_cast.Network.WeatherState
import com.example.wiz_cast.R
import com.example.wiz_cast.Screens.HomeScreen.ViewModel.HomeViewModel
import com.example.wiz_cast.Screens.HomeScreen.ViewModel.HomeViewModelFactory
import kotlinx.coroutines.launch


class HomeFragment : Fragment() {

    private lateinit var viewModel: HomeViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set up ViewModel
        val remoteDataSource = WeatherRemoteDataSourceImpl(RetrofitHelper.apiService)
        val repository = WeatherRepository(remoteDataSource)
        val factory = HomeViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory).get(HomeViewModel::class.java)

        // Observe the weatherState using StateFlow
        lifecycleScope.launch {
            viewModel.weatherState.collect { state ->
                when (state) {
                    is WeatherState.Loading -> {
                        Log.d("HomeFragment", "Loading weather data...")
                    }
                    is WeatherState.Success -> {
                        Log.d("HomeFragment", "Weather data received: ${state.weather}")
                    }
                    is WeatherState.Error -> {
                        Log.d("HomeFragment", "Error: ${state.message}")
                        Toast.makeText(requireContext(), "Failed to fetch weather: ${state.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        // Fetch weather data with example parameters
        val apiKey = getString(R.string.api_key)
        viewModel.fetchWeather(lat = 57.0, lon = -2.15, appid = apiKey, units = "metric", lang = "en")
    }
}