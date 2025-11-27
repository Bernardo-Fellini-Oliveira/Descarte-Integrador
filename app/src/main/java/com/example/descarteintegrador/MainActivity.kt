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

        // Inicializa o lançador de permissões
        permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
            permissions ->
            val fineLocationGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
            val coarseLocationGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false

            if (fineLocationGranted || coarseLocationGranted) {
                Log.d(TAG, "Permissões de localização concedidas. Iniciando atualizações.")
                DataSource.startLocationUpdates()
            } else {
                Log.w(TAG, "Permissões de localização negadas. Não é possível iniciar as atualizações de localização.")
            }
        }

        // Solicita permissões quando a atividade é criada
        requestLocationPermissions()

        // Carrega dados e inicializa o serviço de localização no DataSource
        DataSource.loadLocaisColeta(this)

        // Observa a localização atual do DataSource e a registra
        lifecycleScope.launch {
            DataSource.getCurrentDeviceLocation().collect {
                location ->
                location?.let {
                    Log.d(TAG, "Localização atual do dispositivo: Lat ${it.latitude}, Lng ${it.longitude}")
                } ?: Log.d(TAG, "Localização atual do dispositivo: Ainda não disponível.")
            }
        }

        setContent {
            DescarteIntegradorTheme {
                TelaPesquisa()
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
                // Permissão de localização precisa já concedida
                Log.d(TAG, "Permissão de localização precisa já concedida. Iniciando atualizações.")
                DataSource.startLocationUpdates()
            }
            ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED -> {
                // Apenas permissão de localização aproximada concedida
                Log.d(TAG, "Permissão de localização aproximada já concedida. Iniciando atualizações.")
                DataSource.startLocationUpdates()
            }
            else -> {
                // Solicita ambas as permissões
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
        Log.d(TAG, "Atualizações de localização interrompidas na destruição da atividade.")
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    DescarteIntegradorTheme {
        TelaPesquisa()
    }
}