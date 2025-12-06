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
import com.example.descarteintegrador.ui.theme.DescarteIntegradorTheme
import android.util.Log
import androidx.core.content.ContextCompat
import com.example.descarteintegrador.data.DataSource
import androidx.room.Room
import com.example.descarteintegrador.data.LocalColetaDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private val TAG = "MainActivity"

    private lateinit var permissionLauncher: ActivityResultLauncher<Array<String>>
    private lateinit var db: LocalColetaDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Initialize Room Database
        db = Room.databaseBuilder(
            applicationContext,
            LocalColetaDatabase::class.java,
            "local_coleta_database"
        ).build()
        val localColetaDao = db.localColetaDao()

        // Initialize DataSource with DAO and context
        DataSource.initialize(localColetaDao, this)

        // Load data from CSV into DB (if empty) in a coroutine
        CoroutineScope(Dispatchers.IO).launch {
            DataSource.loadLocaisColeta(this@MainActivity)
        }

        // Inicializa o lançador de permissões
        permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            val fineLocationGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
            val coarseLocationGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false

            if (fineLocationGranted || coarseLocationGranted) {
                Log.d(TAG, "Permissões de localização concedidas. Iniciando atualizações.")
                DataSource.startLocationUpdates()
            } else {
                Log.w(TAG, "Permissões de localização negadas. Não é possível iniciar as atualizações de localização.")
            }
        }

        // Solicita permissões, o que por sua vez iniciará as atualizações de localização se concedidas
        requestLocationPermissions()

        setContent {
            DescarteIntegradorTheme {
                DescarteIntegradorApp()
            }
        }

        // Removed the direct access to DataSource.locaisColetaList.size
        // The data is now managed by the database and observed via Flows.
        Log.d(TAG, "MainActivity onCreate completed. Data loading is handled by DataSource.")
    }

    private fun requestLocationPermissions() {
        val fineLocationGranted = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        val coarseLocationGranted = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED

        if (fineLocationGranted || coarseLocationGranted) {
            Log.d(TAG, "Permissão de localização já concedida. Iniciando atualizações.")
            DataSource.startLocationUpdates()
        } else {
            // Solicita ambas as permissões
            permissionLauncher.launch(arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ))
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        DataSource.stopLocationUpdates()
        Log.d(TAG, "Atualizações de localização interrompidas na destruição da atividade.")
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    DescarteIntegradorTheme {
        DescarteIntegradorApp()
    }
}
