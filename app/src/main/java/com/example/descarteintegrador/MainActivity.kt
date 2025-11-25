package com.example.descarteintegrador

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.descarteintegrador.ui.TelaPesquisa
import com.example.descarteintegrador.ui.theme.DescarteIntegradorTheme
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.descarteintegrador.data.DataSource
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private val TAG = "MainActivity"

    private lateinit var permissionLauncher: ActivityResultLauncher<Array<String>>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Initialize the permission launcher
        permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
            permissions ->
            val fineLocationGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
            val coarseLocationGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false

            if (fineLocationGranted || coarseLocationGranted) {
                Log.d(TAG, "Location permissions granted. Starting updates.")
                DataSource.startLocationUpdates()
            } else {
                Log.w(TAG, "Location permissions denied. Cannot start location updates.")
            }
        }

        // Request permissions when the activity is created
        requestLocationPermissions()

        // Load data and initialize location service in DataSource
        DataSource.loadLocaisColeta(this)

        // Observe current location from DataSource and log it
        lifecycleScope.launch {
            DataSource.getCurrentDeviceLocation().collect {
                location ->
                location?.let {
                    Log.d(TAG, "Current Device Location: Lat ${it.latitude}, Lng ${it.longitude}")
                } ?: Log.d(TAG, "Current Device Location: Not yet available.")
            }
        }

        setContent {
            DescarteIntegradorTheme {
                DescarteIntegradorApp()
            }
        }

        Log.d(TAG, "Total de Locais de Coleta carregados: ${DataSource.locaisColetaList.size}")
        DataSource.locaisColetaList.take(5).forEachIndexed { index, local ->
            Log.d(TAG, "Local ${index + 1}: $local")
        }
    }

    private fun requestLocationPermissions() {
        when {
            ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED -> {
                // Fine location permission already granted
                Log.d(TAG, "Fine location permission already granted. Starting updates.")
                DataSource.startLocationUpdates()
            }
            ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED -> {
                // Only coarse location permission granted
                Log.d(TAG, "Coarse location permission already granted. Starting updates.")
                DataSource.startLocationUpdates()
            }
            else -> {
                // Request both permissions
                permissionLauncher.launch(arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ))
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        DataSource.stopLocationUpdates()
        Log.d(TAG, "Location updates stopped on activity destruction.")
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    DescarteIntegradorTheme {
        TelaPesquisa()
    }
}