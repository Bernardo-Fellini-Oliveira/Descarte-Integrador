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

class MainActivity : ComponentActivity() {
    private val TAG = "MainActivity"

    private lateinit var permissionLauncher: ActivityResultLauncher<Array<String>>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

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

        // Carrega os dados do CSV e inicializa o serviço de localização no DataSource
        DataSource.loadLocaisColeta(this)

        // Solicita permissões, o que por sua vez iniciará as atualizações de localização se concedidas
        requestLocationPermissions()

        setContent {
            DescarteIntegradorTheme {
                DescarteIntegradorApp()
            }
        }

        Log.d(TAG, "Total de Locais de Coleta carregados: ${DataSource.locaisColetaList.size}")
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
