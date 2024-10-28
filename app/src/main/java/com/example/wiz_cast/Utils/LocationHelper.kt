package com.example.wiz_cast.Utils

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.Task

class LocationHelper(private val context: Context) {

    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    companion object {
        const val LOCATION_REQUEST_CODE = 100
    }

    //check location permission
    fun checkLocationPermission(): Boolean {
        val coarseLocation = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
        val fineLocation = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
        return coarseLocation == PackageManager.PERMISSION_GRANTED &&
                fineLocation == PackageManager.PERMISSION_GRANTED
    }

    // request location permissions
    fun requestLocationPermission(activity: Activity) {
        ActivityCompat.requestPermissions(
            activity,
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ),
            LOCATION_REQUEST_CODE
        )
    }

    //  get the current location
    @SuppressLint("MissingPermission")
    fun getCurrentLocation(onLocationReceived: (Location?) -> Unit) {
        fusedLocationClient.lastLocation.addOnCompleteListener { task: Task<Location> ->
            if (task.isSuccessful && task.result != null) {
                onLocationReceived(task.result)
            } else {
                onLocationReceived(null)
            }
        }
    }
}