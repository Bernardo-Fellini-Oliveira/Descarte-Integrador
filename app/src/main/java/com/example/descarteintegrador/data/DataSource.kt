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
import java.net.URL
import java.net.HttpURLConnection
import java.io.IOException

@Serializable
data class JsonDataWrapper(
    val version: Long,
    val locaisColeta: List<LocalColeta>
)

@Serializable
data class VersionResponse(
    val version: Long
)

object DataSource {
    private const val TAG = "DataSource"
    private const val SUGESTAO_MATERIAL_FILENAME = "sugestoes_materiais.txt"
    private const val SUGESTAO_LOCAL_FILENAME = "sugestoes_locais.txt"
    private const val SERVER_BASE_URL = "http://192.168.15.6:5000" //Esse IP pode mudar. Se mudar aqui tem que mudar também no network_security_config.xml
    private const val PREFS_NAME = "DescarteIntegradorPrefs"
    private const val KEY_LOCAL_DB_VERSION = "local_db_version"

    private lateinit var localColetaDao: LocalColetaDao // Add DAO reference
    private lateinit var locationService: LocationService

    fun initialize(dao: LocalColetaDao, context: Context) { // New initialization function
        localColetaDao = dao
        locationService = LocationService(context)
    }

    suspend fun loadLocaisColeta(context: Context) { // Make it suspend
        // Tenta atualizar do servidor remoto primeiro
        val updatedFromRemote = updateDatabaseIfNewer(context)

        if (!updatedFromRemote) {
            // Se não atualizou do remoto por qualquer motivo, carrega a database com as informações do JSON
            val count = withContext(Dispatchers.IO) { localColetaDao.getLocaisCount().first() }
            if (count == 0) {
                val jsonDataWrapper = readJsonData(context)
                jsonDataWrapper.locaisColeta.forEach { local ->
                    localColetaDao.insert(local)
                }
                setLocalDatabaseVersion(context, jsonDataWrapper.version)
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

    private suspend fun getLocalDatabaseVersion(context: Context): Long = withContext(Dispatchers.IO) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val storedVersion = prefs.getLong(KEY_LOCAL_DB_VERSION, 0L) // Default to 0 if not found in preferences

        // Se a versão nas preferências é significativa (> 0), usa ela
        if (storedVersion > 0L) {
            return@withContext storedVersion
        } else {
            // Senão, pega a versão do JSON
            return@withContext try {
                val jsonString = context.resources.openRawResource(R.raw.locations_data)
                    .bufferedReader().use { it.readText() }
                val jsonData = Json { ignoreUnknownKeys = true }.decodeFromString<JsonDataWrapper>(jsonString)
                Log.d(TAG, "Initial local version derived from local JSON: ${jsonData.version}")
                jsonData.version
            } catch (e: Exception) {
                Log.e(TAG, "Error reading initial version from local JSON. Defaulting to 0.", e)
                0L
            }
        }
    }

    private fun setLocalDatabaseVersion(context: Context, version: Long) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        with(prefs.edit()) {
            putLong(KEY_LOCAL_DB_VERSION, version)
            apply()
        }
    }

    // chama o endpoint /api/v1/version do servidor remoto pra ver qual versão o servidor tem
    private suspend fun fetchRemoteDatabaseVersion(): Long? = withContext(Dispatchers.IO) {
        try {
            val url = URL("$SERVER_BASE_URL/api/v1/version")
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.connectTimeout = 5000
            connection.readTimeout = 5000

            if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                val reader = BufferedReader(InputStreamReader(connection.inputStream))
                val jsonString = reader.readText()
                reader.close()
                val versionResponse = Json { ignoreUnknownKeys = true }.decodeFromString<VersionResponse>(jsonString)
                Log.d(TAG, "Versão da DB remota: ${versionResponse.version}")
                versionResponse.version
            } else {
                Log.e(TAG, "Não conseguiu buscar versão remota. Código: ${connection.responseCode}")
                null
            }
        } catch (e: IOException) {
            Log.e(TAG, "Erro de rede buscando versão remota", e)
            null
        } catch (e: SerializationException) {
            Log.e(TAG, "Erro de serialização buscando versão remota", e)
            null
        } catch (e: Exception) {
            Log.e(TAG, "Erro desconhecido buscando versão remota", e)
            null
        }
    }

    // chama o endpoint /api/v1/locations do servidor remoto para baixar toda a database do servidor
    private suspend fun fetchRemoteLocations(): List<LocalColeta>? = withContext(Dispatchers.IO) {
        try {
            val url = URL("$SERVER_BASE_URL/api/v1/locations")
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.connectTimeout = 5000 // 5 seconds
            connection.readTimeout = 5000 // 5 seconds

            if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                val reader = BufferedReader(InputStreamReader(connection.inputStream))
                val jsonString = reader.readText()
                reader.close()
                // Directly deserialize into a List<LocalColeta>
                val remoteLocations = Json { ignoreUnknownKeys = true }.decodeFromString<List<LocalColeta>>(jsonString)
                Log.d(TAG, "Locais de coleta buscados do servidor: ${remoteLocations.size}")
                remoteLocations
            } else {
                Log.e(TAG, "Busca de locais remoto falhou. Código: ${connection.responseCode}")
                null
            }
        } catch (e: IOException) {
            Log.e(TAG, "Erro de rede buscando locais remoto", e)
            null
        } catch (e: SerializationException) {
            Log.e(TAG, "Erro de serialização buscando locais remoto", e)
            null
        } catch (e: Exception) {
            Log.e(TAG, "Erro desconhecido buscando locais remoto", e)
            null
        }
    }

    // compara a versão da DB local com a remota e atualiza se necessário
    private suspend fun updateDatabaseIfNewer(context: Context): Boolean {
        return withContext(Dispatchers.IO) {
            val localVersion = getLocalDatabaseVersion(context) // Now this gets the correct baseline
            val remoteVersion = fetchRemoteDatabaseVersion()

            if (remoteVersion != null && remoteVersion > localVersion) {
                Log.d(TAG, "DB remota é mais nova (Remota: $remoteVersion, Local: $localVersion). Atualizando...")
                val remoteLocations = fetchRemoteLocations()
                if (remoteLocations != null) {
                    localColetaDao.clearAll()
                    remoteLocations.forEach { localColetaDao.insert(it) }
                    setLocalDatabaseVersion(context, remoteVersion)
                    Log.d(TAG, "DB atualizada do servidor.")
                    true
                } else {
                    Log.e(TAG, "Atualização de locais remoto falhou.")
                    false
                }
            } else if (remoteVersion != null && remoteVersion <= localVersion) {
                Log.d(TAG, "DB local está atualizada ou mais nova. (Remota: $remoteVersion, Local: $localVersion)")
                false
            } else {
                Log.d(TAG, "Servidor remoto não pôde ser alcançado. Usando DB local.")
                false
            }
        }
    }

    private fun readJsonData(context: Context): JsonDataWrapper {
        val jsonString: String
        try {
            val inputStream = context.resources.openRawResource(R.raw.locations_data) // Assumes locations_data.json
            jsonString = inputStream.bufferedReader().use { it.readText() }
            inputStream.close()
        } catch (e: Exception) {
            Log.e(TAG, "Erro ao ler JSON", e)
            return JsonDataWrapper(version = 0L, locaisColeta = emptyList()) // Return a default empty wrapper
        }

        return try {
            val jsonData = Json { ignoreUnknownKeys = true }.decodeFromString<JsonDataWrapper>(jsonString)
            Log.d(TAG, "Locais de Coleta lidos do JSON: ${jsonData.locaisColeta.size}, Version: ${jsonData.version}")
            jsonData
        } catch (e: SerializationException) {
            Log.e(TAG, "Erro parseando JSON", e)
            JsonDataWrapper(version = 0L, locaisColeta = emptyList())
        } catch (e: Exception) {
            Log.e(TAG, "Erro desconhecido no parsing do JSON", e)
            JsonDataWrapper(version = 0L, locaisColeta = emptyList())
        }
    }
}