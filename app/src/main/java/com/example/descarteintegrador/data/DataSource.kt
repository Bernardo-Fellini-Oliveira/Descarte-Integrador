package com.example.descarteintegrador.data

import android.content.Context
import android.location.Location
import android.util.Log
import com.example.descarteintegrador.R
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.flow.StateFlow

import kotlinx.serialization.*
import kotlinx.serialization.json.Json

@Serializable
data class JsonDataWrapper(
    val version: Long,
    val locaisColeta: List<LocalColeta>
)

object DataSource {
    private const val TAG = "DataSource"
    private const val SUGESTAO_MATERIAL_FILENAME = "sugestoes_materiais.txt"
    private const val SUGESTAO_LOCAL_FILENAME = "sugestoes_locais.txt"

    private lateinit var localColetaDao: LocalColetaDao // Add DAO reference
    private lateinit var locationService: LocationService

    fun initialize(dao: LocalColetaDao, context: Context) { // New initialization function
        localColetaDao = dao
        locationService = LocationService(context)
    }

    suspend fun loadLocaisColeta(context: Context) { // Make it suspend
        // Check if database is empty
        val count = withContext(Dispatchers.IO) { localColetaDao.getLocaisCount().first() } // Assuming a getLocaisCount() in DAO
        if (count == 0) {
            val locaisColeta = readJsonData(context) // Changed to readJsonData
            locaisColeta.forEach { local ->
                localColetaDao.insert(local)
            }
        }
    }

    // Retorna uma lista de locais de coleta filtrada por tipo de resíduo
    // This will now return Flow<List<LocalColeta>> directly from the DAO
    fun getLocaisColetaByType(type: TipoResiduo): Flow<List<LocalColeta>> {
        return if (type == TipoResiduo.ecoponto) {
            localColetaDao.getAllLocais().map { list -> list.filter { it.tipo == TipoResiduo.ecoponto } }
        } else {
            localColetaDao.getLocaisByType(type.name.lowercase()).map { list ->
                list.filter { it.tipo == type || it.tipo == TipoResiduo.ecoponto }
            }
        }
    }

    // Retorna uma lista de locais de coleta filtrada por distância
    fun getLocaisColetaInRadius(currentLat: Double, currentLng: Double, radiusKm: Double): Flow<List<LocalColeta>> {
        val radiusMeters = radiusKm * 1000 // Convert km to meters
        return localColetaDao.getAllLocais().map { list ->
            list.filter { localColeta ->
                localColeta.calcularDistancia(currentLat, currentLng) <= radiusMeters
            }
        }
    }

    // Retorna uma lista filtrada por tipo de resíduo e distância, pronta para exibir no frontend
    fun getLocaisColetaFiltered(
        type: TipoResiduo,
        currentLat: Double,
        currentLng: Double,
        radiusKm: Double
    ): Flow<List<LocalColeta>> {
        val radiusMeters = radiusKm * 1000
        return localColetaDao.getAllLocais().map { allLocais ->
            val filteredByType = if (type == TipoResiduo.ecoponto) {
                allLocais.filter { it.tipo == TipoResiduo.ecoponto }
            } else {
                allLocais.filter { it.tipo == type || it.tipo == TipoResiduo.ecoponto }
            }

            filteredByType.filter { localColeta ->
                localColeta.calcularDistancia(currentLat, currentLng) <= radiusMeters
            }.sortedBy { it.calcularDistancia(currentLat, currentLng) }
        }
    }

    fun getCurrentDeviceLocation(): StateFlow<Location?> {
        return locationService.currentLocation
    }

    fun startLocationUpdates() {
        locationService.startLocationUpdates()
    }
    fun stopLocationUpdates() {
        locationService.stopLocationUpdates()
    }

    // Salva uma sugestão de novo material em um arquivo de texto.
    fun salvarSugestaoMaterial(context: Context, material: String) {
        try {
            val file = File(context.filesDir, SUGESTAO_MATERIAL_FILENAME)
            file.appendText("${material}" + System.lineSeparator())
            Log.d(TAG, "Sugestão de material '${material}' salva em ${file.absolutePath}")
        } catch (e: Exception) {
            Log.e(TAG, "Falha ao salvar sugestão de material", e)
        }
    }


    // Salva uma sugestão de novo local em um arquivo de texto.
    fun salvarSugestaoLocal(context: Context, endereco: String, numero: String) {
        try {
            val file = File(context.filesDir, SUGESTAO_LOCAL_FILENAME)
            val linha = "Endereço: ${endereco}, Número: ${numero}"
            file.appendText("${linha}" + System.lineSeparator())
            Log.d(TAG, "Sugestão de local '${linha}' salva em ${file.absolutePath}")
        } catch (e: Exception) {
            Log.e(TAG, "Falha ao salvar sugestão de local", e)
        }
    }

    private fun readJsonData(context: Context): List<LocalColeta> {
        val jsonString: String
        try {
            val inputStream = context.resources.openRawResource(R.raw.locations_data) // Assumes locais.json
            jsonString = inputStream.bufferedReader().use { it.readText() }
            inputStream.close()
        } catch (e: Exception) {
            Log.e(TAG, "Error reading JSON file", e)
            return emptyList()
        }

        return try {
            val jsonData = Json { ignoreUnknownKeys = true }.decodeFromString<JsonDataWrapper>(jsonString)
            Log.d(TAG, "Locais de Coleta lidos do JSON: ${jsonData.locaisColeta.size}")
            jsonData.locaisColeta
        } catch (e: SerializationException) {
            Log.e(TAG, "Error parsing JSON data", e)
            emptyList()
        } catch (e: Exception) {
            Log.e(TAG, "An unexpected error occurred during JSON parsing", e)
            emptyList()
        }
    }

    // The original readCsvData is removed. If you still need it, please clarify.
    // private fun readCsvData(context: Context): List<LocalColeta> {
    //     ... (original content)
    // }
}