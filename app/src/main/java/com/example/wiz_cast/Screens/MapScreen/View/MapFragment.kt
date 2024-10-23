package com.example.wiz_cast.Screens.MapScreen.View

import android.os.Bundle
import android.util.Log
import android.view.GestureDetector
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
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
import androidx.navigation.fragment.findNavController


class MapFragment : Fragment() {

    private lateinit var binding: FragmentMapBinding
    private lateinit var gestureDetector: GestureDetector

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

        // Set up the GestureDetector for tap events
        gestureDetector = GestureDetector(requireContext(), object : GestureDetector.SimpleOnGestureListener() {
            override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
                handleMapClick(e)
                return true // Indicate that the event has been handled
            }
        })

        // Set the touch listener for the map
        binding.mapView.setOnTouchListener { _, event ->
            // Pass all touch events to the GestureDetector
            gestureDetector.onTouchEvent(event)
            // Return false to let the map handle other gestures (like zoom and pan)
            false
        }

        return binding.root
    }

    private fun handleMapClick(event: MotionEvent) {
        // Get the coordinates of the tap
        val geoPoint = binding.mapView.projection.fromPixels(event.x.toInt(), event.y.toInt())
        val lat = geoPoint.latitude
        val lon = geoPoint.longitude

        // Log the tapped location
        Log.d("Map Click", "Tapped location: Latitude = $lat, Longitude = $lon")

        // Navigate to HomeFragment with selected location
        val bundle = Bundle().apply {
            putDouble("LATITUDE", lat)
            putDouble("LONGITUDE", lon)
        }
        findNavController().navigate(R.id.action_mapFragment_to_detailsFragment, bundle)
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