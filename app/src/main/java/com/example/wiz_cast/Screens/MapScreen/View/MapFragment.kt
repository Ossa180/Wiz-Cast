package com.example.wiz_cast.Screens.MapScreen.View

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.wiz_cast.R
import com.example.wiz_cast.databinding.FragmentMapBinding
import com.google.android.material.search.SearchBar
import org.osmdroid.config.Configuration
import org.osmdroid.library.BuildConfig
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker


class MapFragment : Fragment() {

    lateinit var binding: FragmentMapBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Configure a relevant user agent
        Configuration.getInstance().userAgentValue = BuildConfig.LIBRARY_PACKAGE_NAME
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentMapBinding.inflate(inflater, container, false)


        // Initialize the map
        binding.mapView.setTileSource(TileSourceFactory.MAPNIK)
        binding.mapView.setBuiltInZoomControls(true)
        binding.mapView.setMultiTouchControls(true)

        // Retrieve passed latitude and longitude
        val lat = arguments?.getDouble("LATITUDE") ?: 0.0
        val lon = arguments?.getDouble("LONGITUDE") ?: 0.0

        // Set map center and add marker
        setMapLocation(lat, lon)
        Log.d("*****MAPLOCATION*****", " $lat, $lon")

        return binding.root
    }

    private fun setMapLocation(latitude: Double, longitude: Double) {
        val mapController = binding.mapView.controller
        mapController.setZoom(15.0) // Adjust zoom level as needed

        val startPoint = GeoPoint(latitude, longitude)
        mapController.setCenter(startPoint)

        // Add a marker on the map
        val marker = Marker(binding.mapView)
        marker.position = startPoint
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        marker.title = "Your Location"
        binding.mapView.overlays.add(marker)

        binding.mapView.invalidate() // Refresh the map
    }

    override fun onResume() {
        super.onResume()
        binding.mapView.onResume() // Important for the map to work correctly
    }

    override fun onPause() {
        super.onPause()
        binding.mapView.onPause() // Important for the map to work correctly
    }

}