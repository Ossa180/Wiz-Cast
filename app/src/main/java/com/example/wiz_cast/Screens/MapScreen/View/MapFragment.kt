package com.example.wiz_cast.Screens.MapScreen.View

import android.os.Bundle
import android.util.Log
import android.view.GestureDetector
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.wiz_cast.R
import com.example.wiz_cast.databinding.FragmentMapBinding
import com.example.wiz_cast.Network.RetrofitHelper
import kotlinx.coroutines.launch
import org.osmdroid.config.Configuration
import org.osmdroid.library.BuildConfig
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import java.net.URLEncoder

class MapFragment : Fragment() {

    private lateinit var binding: FragmentMapBinding
    private lateinit var gestureDetector: GestureDetector

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMapBinding.inflate(inflater, container, false)

        binding.mapView.setTileSource(TileSourceFactory.MAPNIK)
        binding.mapView.setBuiltInZoomControls(true)
        binding.mapView.setMultiTouchControls(true)

        // Retrieve latitude and longitude passed
        val lat = arguments?.getDouble("LATITUDE") ?: 0.0
        val lon = arguments?.getDouble("LONGITUDE") ?: 0.0
        setMapLocation(lat, lon)

        // Set up the SearchView for city search
        binding.searchBar.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let { searchCity(it) }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })

        gestureDetector = GestureDetector(requireContext(), object : GestureDetector.SimpleOnGestureListener() {
            override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
                handleMapClick(e)
                return true
            }
        })

        binding.mapView.setOnTouchListener { _, event ->
            gestureDetector.onTouchEvent(event)
            false
        }

        return binding.root
    }

    private fun searchCity(cityName: String) {
        val apiKey = getString(R.string.api_key)
        val encodedCityName = URLEncoder.encode(cityName, "UTF-8")
        Log.d("API Request", "Requesting URL: https://api.openweathermap.org/data/2.5/weather?q=$encodedCityName&appid=$apiKey")

        lifecycleScope.launch {
            try {
                val response = RetrofitHelper.apiService.getCityWeather(encodedCityName, apiKey)
                Log.d("API Response", "Response: $response") // Add logging for response
                if (response.isSuccessful) {
                    response.body()?.coord?.let { coord ->
                        Log.d("City Coordinates", "Latitude: ${coord.lat}, Longitude: ${coord.lon}")
                        navigateToDetailsFragment(coord.lat, coord.lon)
                    }
                } else {
                    Log.e("API Error", "Response code: ${response.code()}, message: ${response.message()}")
                    Toast.makeText(requireContext(), "City not found.", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Log.e("MapFragment", "Error in searchCity: ${e.message}", e)
                Toast.makeText(requireContext(), "Failed to search city: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun navigateToDetailsFragment(latitude: Double, longitude: Double) {
        Log.d("Navigation", "Navigating to DetailsFragment with Latitude: $latitude, Longitude: $longitude")
        val bundle = Bundle().apply {
            putDouble("LATITUDE", latitude)
            putDouble("LONGITUDE", longitude)
        }
        findNavController().navigate(R.id.action_mapFragment_to_detailsFragment, bundle)
    }


    private fun handleMapClick(event: MotionEvent) {
        val geoPoint = binding.mapView.projection.fromPixels(event.x.toInt(), event.y.toInt())
        val lat = geoPoint.latitude
        val lon = geoPoint.longitude
        Log.d("Map Click", "Tapped location: Latitude = $lat, Longitude = $lon")
        navigateToDetailsFragment(lat, lon)
    }

    private fun setMapLocation(latitude: Double, longitude: Double) {
        val mapController = binding.mapView.controller
        mapController.setZoom(15.0)
        val startPoint = GeoPoint(latitude, longitude)
        mapController.setCenter(startPoint)

        val marker = Marker(binding.mapView)
        marker.position = startPoint
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        marker.title = "Your Location"
        binding.mapView.overlays.add(marker)
        binding.mapView.invalidate()
    }

    override fun onResume() {
        super.onResume()
        binding.mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        binding.mapView.onPause()
    }
}
