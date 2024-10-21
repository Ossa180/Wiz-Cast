package com.example.wiz_cast.Screens.HomeScreen.View

import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.wiz_cast.Model.Pojo.CurrentWeather
import com.example.wiz_cast.Model.Pojo.FiveDaysWeather
import com.example.wiz_cast.Model.Pojo.Item0
import com.example.wiz_cast.Model.Repository.WeatherRepository
import com.example.wiz_cast.Network.FiveDayForecastState
import com.example.wiz_cast.Network.RetrofitHelper
import com.example.wiz_cast.Network.WeatherRemoteDataSourceImpl
import com.example.wiz_cast.Network.WeatherState
import com.example.wiz_cast.R
import com.example.wiz_cast.Screens.HomeScreen.ViewModel.HomeViewModel
import com.example.wiz_cast.Screens.HomeScreen.ViewModel.HomeViewModelFactory
import com.example.wiz_cast.Utils.ConnectivityReceiver
import com.example.wiz_cast.databinding.FragmentHomeBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var viewModel: HomeViewModel
    private lateinit var connectivityReceiver: ConnectivityReceiver
    private lateinit var hourlyAdapter: HourlyAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set up ViewModel
        val remoteDataSource = WeatherRemoteDataSourceImpl(RetrofitHelper.apiService)
        val repository = WeatherRepository(remoteDataSource)
        val factory = HomeViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory).get(HomeViewModel::class.java)

        // Register the connectivity receiver
        connectivityReceiver = ConnectivityReceiver {
            viewModel.fetchWeather(lat = 40.7128, lon = -74.0060, appid = getString(R.string.api_key), units = "metric", lang = "en")
            viewModel.fetchFiveDayForecast(lat = 40.7128, lon = -74.0060, appid = getString(R.string.api_key), units = "metric", lang = "en")

        }
        requireContext().registerReceiver(connectivityReceiver, IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))


        binding.lottiecloudloading.setAnimation(R.raw.cloadloading)
        binding.lottiecloudloading.playAnimation()

        // Observe the weatherState using StateFlow
        lifecycleScope.launch {
            viewModel.weatherState.collect { state ->
                when (state) {
                    is WeatherState.Loading -> {
                        Log.d("HomeFragment", "Loading weather data...")
                        binding.lottiecloudloading.visibility = View.VISIBLE
                        binding.mainContentGroup.visibility = View.GONE
                    }
                    is WeatherState.Success -> {
                        Log.d("HomeFragment", "Weather data received: ${state.weather}")
                        binding.lottiecloudloading.visibility = View.GONE
                        binding.mainContentGroup.visibility = View.VISIBLE
                        updateUI(state.weather)
                    }
                    is WeatherState.Error -> {
                        Log.d("HomeFragment", "Error: ${state.message}")
                        Toast.makeText(requireContext(), "Failed to fetch weather: ${state.message}", Toast.LENGTH_SHORT).show()
                        binding.lottiecloudloading.visibility = View.VISIBLE
                        binding.mainContentGroup.visibility = View.GONE
                    }
                }
            }
        }

        // Observe the *** fiveDayForecastState *** using StateFlow
        lifecycleScope.launch {
            viewModel.fiveDayForecastState.collect { state ->
                when (state) {
                    is FiveDayForecastState.Loading -> { // Corrected to use FiveDayForecastState
                        Log.d("HomeFragment", "Loading 5-day weather forecast...")
                    }
                    is FiveDayForecastState.Success -> { // Corrected to use FiveDayForecastState
                        Log.d("HomeFragment", "5-day forecast data received: ${state.weather}")
                        // You can call a method here to update the UI with the forecast data if needed
                        logForecastData(state.weather) // Log the forecast data for now
                    }
                    is FiveDayForecastState.Error -> { // Corrected to use FiveDayForecastState
                        Log.d("HomeFragment", "Error fetching 5-day forecast: ${state.message}")
                    }
                }
            }
        }


        // Fetch weather data with example parameters
        val apiKey = getString(R.string.api_key)
        viewModel.fetchWeather(lat = 40.7128, lon = -74.0060, appid = apiKey, units = "metric", lang = "en")
        viewModel.fetchFiveDayForecast(lat = 40.7128, lon = -74.0060, appid = apiKey, units = "metric", lang = "en")
    }

    private fun updateUI(weather: CurrentWeather) {
        binding.tvDesc.text = weather.weather[0].description
//        val iconUrl = "https://openweathermap.org/img/wn/${weather.weather[0].icon}.png"
//        val testIcon = "https://openweathermap.org/img/wn/10d@2x.png"
//        Glide.with(this).load(testIcon).into(binding.imgIcon)
        //Picasso.get().load(iconUrl).error(R.drawable.abc).into(binding.imgIcon)
        val weatherIconResId = getCustomIconForWeather(weather.weather[0].icon)
        binding.imgIcon.setImageResource(weatherIconResId)

        binding.tvTemp.text = "${weather.main.temp}°"
        binding.tvPressure.text = "${weather.main.pressure}"
        binding.tvHumidity.text = "${weather.main.humidity}%"

        binding.tvClouds.text = "${weather.clouds.all}%"
        binding.tvWind.text = "${weather.wind.speed}m/s"

        binding.tvSunRise.text = "Sunrise: ${formatUnixTime(weather.sys.sunrise, weather.timezone)}"
        binding.tvSunSet.text = "Sunset: ${formatUnixTime(weather.sys.sunset, weather.timezone)}"

        binding.tvCity.text = weather.name
        binding.tvTime.text = formatTimeFromTimezone(weather.timezone)

        binding.tvDate.text = "${SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(
            Date()
        )}"

        // Fetch the city name using reverse geocoding
        fetchCityName(weather.coord.lat, weather.coord.lon)
    }

    // for sunrise and sunset  coversion
    private fun formatUnixTime(unixTime: Int, timezoneOffset: Int): String {
        val date = Date((unixTime + timezoneOffset) * 1000L)
        val format = SimpleDateFormat("hh:mm a", Locale.getDefault())
        return format.format(date)
    }

    // Setup the hourly recycler view
    private fun setupHourlyRecyclerView(hourlyData: List<Item0>) {
        hourlyAdapter = HourlyAdapter(hourlyData)
        binding.recyclerHourly.apply {
            adapter = hourlyAdapter
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        }
    }

    private fun fetchCityName(lat: Double, lon: Double) {
        val apiKey = getString(R.string.api_key)
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val response = RetrofitHelper.apiService.getCityName(lat, lon, apiKey)
                if (response.isSuccessful) {
                    val cityName = response.body()?.firstOrNull()?.country ?: "Unknown"
                    withContext(Dispatchers.Main) {
                        binding.tvCity.text = cityName
                    }
                    Log.d("fetchCityName", "fetchCityName: $cityName ")
                }
            } catch (e: Exception) {
                Log.d("HomeFragment", "Failed to fetch city name: ${e.message}")
            }
        }
    }

    // for timezone
    private fun formatTimeFromTimezone(timezoneOffset: Int): String {
        // Convert timezone offset
        val currentTimeMillis = System.currentTimeMillis() + (timezoneOffset * 1000L)
        val date = Date(currentTimeMillis)
        val format = SimpleDateFormat("HH:mm", Locale.getDefault())
        return format.format(date)
    }

    // for description, to avoid fetching problem
    private fun getCustomIconForWeather(iconCode: String): Int {
        return when (iconCode) {
            "01d", "01n" -> R.drawable.ic_clear_sky
            "02d", "02n" -> R.drawable.ic_few_cloud
            "03d", "03n" -> R.drawable.ic_scattered_clouds
            "04d", "04n" -> R.drawable.ic_broken_clouds
            "09d", "09n" -> R.drawable.ic_shower_rain
            "10d", "10n" -> R.drawable.ic_rain
            "11d", "11n" -> R.drawable.ic_thunderstorm
            "13d", "13n" -> R.drawable.ic_snow
            "50d", "50n" -> R.drawable.ic_mist
            else -> R.drawable.ic_clear_sky
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Unregister the connectivity receiver to prevent memory leaks
        requireContext().unregisterReceiver(connectivityReceiver)
    }

    // Inside forecast data observer, update the RecyclerView
    private fun logForecastData(forecast: FiveDaysWeather) {
        val hourlyData = forecast.list // Assuming 'list' contains hourly forecast

        // Log each hourly forecast
        hourlyData.forEach { item ->
            Log.d("HomeFragment", "Forecast time: ${item.dt_txt}, Temp: ${item.main.temp}, Weather: ${item.weather[0].description}")
        }

        // Pass the hourly data to the adapter to display in the RecyclerView
        setupHourlyRecyclerView(hourlyData)
    }
}