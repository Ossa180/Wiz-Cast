package com.example.wiz_cast.Screens.HomeScreen.View

import android.content.IntentFilter
import android.content.pm.PackageManager
import android.location.Location
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
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.wiz_cast.Model.DataBase.WeatherDao
import com.example.wiz_cast.Model.DataBase.WeatherDatabase
import com.example.wiz_cast.Model.DataBase.WeatherLocalDataSourceImpl
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
import com.example.wiz_cast.Screens.MapScreen.View.MapFragment
import com.example.wiz_cast.Utils.ConnectivityReceiver
import com.example.wiz_cast.Utils.LocationHelper
import com.example.wiz_cast.Utils.PreferencesHelper
import com.example.wiz_cast.databinding.FiveDaysDialogBinding
import com.example.wiz_cast.databinding.FragmentHomeBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
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
    private lateinit var sheetBinding: FiveDaysDialogBinding
    private lateinit var dialog : BottomSheetDialog
    private lateinit var locationHelper: LocationHelper
    lateinit var weatherDao: WeatherDao

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set up the location helper
        locationHelper = LocationHelper(requireContext())
        if (locationHelper.checkLocationPermission()) {
            fetchLocationData()
        } else {
            locationHelper.requestLocationPermission(requireActivity())
        }

        // Set up the 5-day forecast dialog
        sheetBinding = FiveDaysDialogBinding.inflate(layoutInflater)
        dialog = BottomSheetDialog(requireContext(), R.style.BottomSheetTheme)
        dialog.setContentView(sheetBinding.root)
        dialog.window?.attributes?.windowAnimations = R.style.DialogAnimation // for animation
        binding.btnOpenSheet.setOnClickListener {
            dialog.show()
        }

        // Initialize weatherDao
        val database = WeatherDatabase.getInstance(requireContext())
        weatherDao = database.weatherDao()
        // Set up ViewModel
        val remoteDataSource = WeatherRemoteDataSourceImpl(RetrofitHelper.apiService)
        val localDataSource = WeatherLocalDataSourceImpl(weatherDao) // initialize with your DAO
        val repository = WeatherRepository(remoteDataSource, localDataSource)
        val factory = HomeViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory).get(HomeViewModel::class.java)



        // Register the connectivity receiver
        connectivityReceiver = ConnectivityReceiver {
            fetchLocationData()
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
                        Log.d("HomeFragment : Weather state: ", "Error: ${state.message}")
                        Toast.makeText(requireContext(), "Failed to fetch weather: ${state.message}", Toast.LENGTH_SHORT).show()
                        binding.lottiecloudloading.visibility = View.GONE
                        binding.mainContentGroup.visibility = View.VISIBLE // show latest data
                    }
                }
            }
        }
        // Observe the *** fiveDayForecastState *** using StateFlow
        lifecycleScope.launch {
            viewModel.fiveDayForecastState.collect { state ->
                when (state) {
                    is FiveDayForecastState.Loading -> {
                        Log.d("HomeFragment", "Loading 5-day weather forecast...")
                    }
                    is FiveDayForecastState.Success -> {
                        Log.d("HomeFragment", "5-day forecast data received: ${state.weather}")
                        viewModel.weatherState.value.let { weatherState ->
                            if (weatherState is WeatherState.Success) {
                                logForecastData(state.weather, weatherState.weather)
                            }
                        }
                    }
                    is FiveDayForecastState.Error -> {
                        Log.d("HomeFragment", "Error fetching 5-day forecast: ${state.message}")
                    }
                }
            }
        }

        // Fetch weather data with example parameters
//        val apiKey = getString(R.string.api_key)
//        viewModel.fetchWeather(lat = 40.7128, lon = -74.0060, appid = apiKey, units = "metric", lang = "en")
//        viewModel.fetchFiveDayForecast(lat = 40.7128, lon = -74.0060, appid = apiKey, units = "metric", lang = "en")
    }

    private fun updateUI(weather: CurrentWeather) {
        // Initialize PreferencesHelper to access user settings
        val preferencesHelper = PreferencesHelper(requireContext())
        val units = preferencesHelper.getUnits()

        binding.tvDesc.text = weather.weather[0].description
        val weatherIconResId = getCustomIconForWeather(weather.weather[0].icon)
        binding.imgIcon.setImageResource(weatherIconResId)

        // Determine the temperature unit symbol
        val temperatureUnit = when (units) {
            "standard" -> "K"    // Kelvin
            "metric" -> "°C"     // Celsius
            "imperial" -> "°F"   // Fahrenheit
            else -> ""           // Default case (shouldn't occur)
        }

        // Display temperature with the correct unit
        val temperature = weather.main.temp.toInt()
        binding.tvTemp.text = "$temperature $temperatureUnit"

        // Pressure is always in hPa
        binding.tvPressure.text = "${weather.main.pressure} hPa"
        binding.tvHumidity.text = "${weather.main.humidity}%"

        binding.tvClouds.text = "${weather.clouds.all}%"

        // Determine the wind speed unit
        val windSpeedUnit = when (units) {
            "standard", "metric" -> "m/s"   // Meters per second
            "imperial" -> "mph"             // Miles per hour
            else -> ""
        }

        // Display wind speed with the correct unit
        binding.tvWind.text = "${weather.wind.speed} $windSpeedUnit"

        binding.tvSunRise.text = "Sunrise: ${formatUnixTime(weather.sys.sunrise, weather.timezone)}"
        binding.tvSunSet.text = "Sunset: ${formatUnixTime(weather.sys.sunset, weather.timezone)}"

        binding.tvCity.text = weather.name
        binding.tvTime.text = formatTimeFromTimezone(weather.timezone)

        binding.tvDate.text = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

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
    /********************************************/
    /****** Five Days - 3 Hours Fetching ********/
    /********************************************/
    // Inside forecast data observer, update the RecyclerView
    private fun logForecastData(forecast: FiveDaysWeather, currentWeather: CurrentWeather) {
        val timezoneOffset = currentWeather.timezone * 1000L
        val currentTime = System.currentTimeMillis() + timezoneOffset

        val futureForecast = forecast.list.filter { item ->
            val forecastTimeMillis = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                .parse(item.dt_txt)?.time ?: 0
            forecastTimeMillis >= currentTime
        }

        // Log filtered forecast data
        futureForecast.forEach { item ->
            Log.d("HomeFragment", "Forecast time: ${item.dt_txt}, Temp: ${item.main.temp}, Weather: ${item.weather[0].description}")
        }

        // Pass the filtered future forecast data to the hourly adapter (unchanged for current day)
        setupHourlyRecyclerView(futureForecast)

        // Get a single forecast for each day for the 5-day forecast
        val uniqueDayForecast = getUniqueDayForecast(futureForecast)

        // Setup RecyclerView in the BottomSheetDialog with the unique day forecast
        setupFiveDayRecyclerView(uniqueDayForecast)
    }

    // Function to get a single forecast entry per day
    private fun getUniqueDayForecast(forecastList: List<Item0>): List<Item0> {
        val dailyForecastMap = mutableMapOf<String, Item0>()

        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        // Iterate through the forecastList and store one forecast entry per day
        forecastList.forEach { item ->
            val dateKey = sdf.format(SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).parse(item.dt_txt)!!)
            if (!dailyForecastMap.containsKey(dateKey)) {
                dailyForecastMap[dateKey] = item // Store the first forecast entry of each day
            }
        }

        return dailyForecastMap.values.toList() // Return the filtered list of forecasts (one per day)
    }

    // Setup RecyclerView for 5-day forecast in the BottomSheetDialog
    private fun setupFiveDayRecyclerView(forecastList: List<Item0>) {
        val dialogAdapter = DialogAdapter(forecastList)
        sheetBinding.recycler5Days.apply {
            adapter = dialogAdapter
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        }
    }

    // Fetch location data
    private fun fetchLocationData() {
        locationHelper.getCurrentLocation { location: Location? ->
            location?.let {
                val latitude = it.latitude
                val longitude = it.longitude
                // Log the current latitude and longitude
                Log.d("**Location**", "Location fetched - Latitude: $latitude, Longitude: $longitude")

                // Now fetch the weather data with the actual coordinates
                fetchWeatherData(latitude, longitude)
                // Navigate to the MapFragment and pass the latitude and longitude as arguments
                binding.btnGoToMap.setOnClickListener {
                    val bundle = Bundle().apply {
                        putDouble("LATITUDE", latitude)
                        putDouble("LONGITUDE", longitude)
                    }
                    findNavController().navigate(R.id.action_homeFragment_to_mapFragment, bundle)
                }
            } ?: run {
                // Handle error if location is null
                Toast.makeText(requireContext(), "Failed to get location. Please enable GPS.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Function to fetch weather and forecast data
    private fun fetchWeatherData(lat: Double, lon: Double) {
        val apiKey = getString(R.string.api_key)
        val preferencesHelper = PreferencesHelper(requireContext())

        // Retrieve saved preferences for language and units
        val units = preferencesHelper.getUnits()
        val language = preferencesHelper.getLanguage()

        // Log the current location being used to fetch weather and forecast
        Log.d("**Location 5 **", "Fetching weather and 5-day forecast for Latitude: $lat, Longitude: $lon")

        // Pass the retrieved settings to the ViewModel
        viewModel.fetchWeather(lat = lat, lon = lon, appid = apiKey, units = units, lang = language)
        viewModel.fetchFiveDayForecast(lat = lat, lon = lon, appid = apiKey, units = units, lang = language)
    }


    // Handle permission result
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == LocationHelper.LOCATION_REQUEST_CODE &&
            grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED
        ) {
            fetchLocationData()
        } else {
            Toast.makeText(requireContext(), "Location permission denied", Toast.LENGTH_SHORT).show()
        }
    }
}