package com.example.descarteintegrador.data

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Looper
import android.util.Log
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class LocationService(private val context: Context) {

    private val fusedLocationClient: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)

    private val _currentLocation = MutableStateFlow<Location?>(null)
    val currentLocation: StateFlow<Location?> = _currentLocation

    private val locationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            _currentLocation.value = locationResult.lastLocation
            Log.d("LocationService", "New location: ${_currentLocation.value?.latitude}, ${_currentLocation.value?.longitude}")
        }
    }

    private val locationRequest: LocationRequest = LocationRequest.Builder(10000L) // Update every 10 seconds
        .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
        .setMinUpdateIntervalMillis(5000L) // Smallest displacement between location updates in milliseconds
        .build()

    fun startLocationUpdates() {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            try {
                fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
                Log.d("LocationService", "Location updates started.")
            } catch (e: SecurityException) {
                Log.e("LocationService", "Location permission denied: ${e.message}")
            }
        } else {
            Log.w("LocationService", "Location permissions not granted. Cannot start updates.")
        }
    }

    fun stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
        Log.d("LocationService", "Location updates stopped.")
    }
}
