package com.example.descarteintegrador

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.descarteintegrador.ui.TelaPesquisa
import com.example.descarteintegrador.ui.theme.DescarteIntegradorTheme
import android.util.Log // Importar para usar Log.d
import com.example.descarteintegrador.data.DataSource
import com.example.descarteintegrador.data.LocalColeta


class MainActivity : ComponentActivity() {
    private val TAG = "MainActivity" // Uma tag para filtrar no Logcat

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DescarteIntegradorTheme {
                DescarteIntegradorApp()
            }
        }

        DataSource.loadLocaisColeta(this)

        // --- Adicionado para visualização no Logcat ---
        Log.d(TAG, "Total de Locais de Coleta carregados: ${DataSource.locaisColetaList.size}")
        DataSource.locaisColetaList.take(5).forEachIndexed { index, local ->
            Log.d(TAG, "Local ${index + 1}: $local")
        }
        // ------------------------------------------------
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    DescarteIntegradorTheme {
        TelaPesquisa()
    }
}