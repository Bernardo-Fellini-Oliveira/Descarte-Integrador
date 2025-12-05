package com.example.descarteintegrador.data

import android.content.Context
import android.location.Location
import android.util.Log
import com.example.descarteintegrador.R
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import kotlinx.coroutines.flow.StateFlow
import kotlin.collections.filter

object DataSource {
    private const val TAG = "DataSource"
    private const val SUGESTAO_MATERIAL_FILENAME = "sugestoes_materiais.txt"
    private const val SUGESTAO_LOCAL_FILENAME = "sugestoes_locais.txt"

    lateinit var locaisColetaList: List<LocalColeta>
    private lateinit var locationService: LocationService

    fun loadLocaisColeta(context: Context) {
        locaisColetaList = readCsvData(context)
        locationService = LocationService(context)
    }

    // Retorna uma lista de locais de coleta filtrada por tipo de resíduo
    fun getLocaisColetaByType(type: TipoResiduo): List<LocalColeta> {
        return locaisColetaList.filter { it.tipo == type }
    }

    // Retorna uma lista de locais de coleta filtrada por distância
    fun getLocaisColetaInRadius(currentLat: Double, currentLng: Double, radiusKm: Double): List<LocalColeta> {
        val radiusMeters = radiusKm * 1000 // Convert km to meters
        return locaisColetaList.filter { localColeta ->
            localColeta.calcularDistancia(currentLat, currentLng) <= radiusMeters
        }
    }

    // Retorna uma lista filtrada por tipo de resíduo e distância, pronta para exibir no frontend
    fun getLocaisColetaFiltered(
        type: TipoResiduo,
        currentLat: Double,
        currentLng: Double,
        radiusKm: Double
    ): List<LocalColeta> {
        val filteredByType = if (type == TipoResiduo.ecoponto) {
            locaisColetaList.filter { it.tipo == TipoResiduo.ecoponto }
        } else {
            locaisColetaList.filter { it.tipo == type || it.tipo == TipoResiduo.ecoponto }
        }

        val radiusMeters = radiusKm * 1000
        return filteredByType.filter { localColeta ->
            localColeta.calcularDistancia(currentLat, currentLng) <= radiusMeters
        }.sortedBy { it.calcularDistancia(currentLat, currentLng) }
    }

    // função para expor a localização atual do dispositivo
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
            file.appendText("$material\n")
            Log.d(TAG, "Sugestão de material '$material' salva em ${file.absolutePath}")
        } catch (e: Exception) {
            Log.e(TAG, "Falha ao salvar sugestão de material", e)
        }
    }


    // Salva uma sugestão de novo local em um arquivo de texto.
    fun salvarSugestaoLocal(context: Context, endereco: String, numero: String) {
        try {
            val file = File(context.filesDir, SUGESTAO_LOCAL_FILENAME)
            val linha = "Endereço: $endereco, Número: $numero"
            file.appendText("$linha\n")
            Log.d(TAG, "Sugestão de local '$linha' salva em ${file.absolutePath}")
        } catch (e: Exception) {
            Log.e(TAG, "Falha ao salvar sugestão de local", e)
        }
    }

    private fun readCsvData(context: Context): List<LocalColeta> {
        val locaisColeta = mutableListOf<LocalColeta>()
        try {
            val inputStream = context.resources.openRawResource(R.raw.locais)
            val reader = BufferedReader(InputStreamReader(inputStream))
            var line: String?

            // Pula linha de cabeçalho se tiver
            reader.readLine()

            while (reader.readLine().also { line = it } != null) {
                line?.let { rawLine ->
                    val nome: String
                    val endereco: String
                    val lat: Double
                    val lng: Double
                    val tipoString: String // convertido depois pra TipoResiduo

                    // Encontra o início do campo de endereço entre aspas
                    val firstQuoteIndex = rawLine.indexOf('"')

                    if (firstQuoteIndex == -1) {
                        // se não tem campo com aspas, assume separação simples por vírgula
                        val simpleParts = rawLine.split(",").map { it.trim() }
                        if (simpleParts.size >= 5) {
                            try {
                                nome = simpleParts[0]
                                endereco = simpleParts[1]
                                lat = simpleParts[2].toDouble()
                                lng = simpleParts[3].toDouble()
                                tipoString = simpleParts[4]
                                val tipo = try {
                                    TipoResiduo.valueOf(tipoString.lowercase())
                                } catch (e: IllegalArgumentException) {
                                    println("Unknown residue type: $tipoString. Assigning UNKNOWN.")
                                    TipoResiduo.UNKNOWN
                                }
                                locaisColeta.add(LocalColeta(nome = nome, endereco = endereco, lat = lat, lng = lng, tipo = tipo))
                            } catch (e: NumberFormatException) {
                                println("Skipping row due to number format error (simple parse): ${simpleParts.joinToString()}")
                                e.printStackTrace()
                            }
                        } else {
                            println("Skipping row due to insufficient columns (simple parse): ${simpleParts.joinToString()}")
                        }
                        return@let // Continua para a próxima linha
                    }

                    // Encontra o fim do campo de endereço entre aspas, começa a procurar depois da primeira aspa
                    val endQuoteIndex = rawLine.indexOf('"', firstQuoteIndex + 1)
                    if (endQuoteIndex == -1) {
                        println("Skipping row due to missing closing quote for address: $rawLine")
                        return@let
                    }

                    // Extrai nome
                    val commaBeforeQuote = rawLine.substring(0, firstQuoteIndex).lastIndexOf(',')
                    if (commaBeforeQuote != -1) {
                        nome = rawLine.substring(0, commaBeforeQuote).trim()
                    } else {
                        println("Skipping row due to malformed 'nome' field: $rawLine")
                        return@let
                    }

                    // Extrai endereco sem aspas
                    endereco = rawLine.substring(firstQuoteIndex + 1, endQuoteIndex).trim()

                    // Extrai as partes restantes
                    val remainingString = rawLine.substring(endQuoteIndex + 1).trim()

                    val partsAfterAddress = if (remainingString.startsWith(",")) {
                        remainingString.substring(1).split(",").map { it.trim() }
                    } else {
                        remainingString.split(",").map { it.trim() }
                    }

                    if (partsAfterAddress.size >= 3) { // Esperando lat, lng, tipo
                        try {
                            lat = partsAfterAddress[0].toDouble()
                            lng = partsAfterAddress[1].toDouble()
                            tipoString = partsAfterAddress[2]
                            val tipo = try {
                                TipoResiduo.valueOf(tipoString.lowercase())
                            } catch (e: IllegalArgumentException) {
                                println("Unknown residue type: $tipoString. Assigning UNKNOWN.")
                                TipoResiduo.UNKNOWN
                            }
                            locaisColeta.add(LocalColeta(nome = nome, endereco = endereco, lat = lat, lng = lng, tipo = tipo))
                        } catch (e: NumberFormatException) {
                            println("Skipping row due to number format error: ${partsAfterAddress.joinToString()}")
                            e.printStackTrace()
                        }
                    } else {
                        println("Skipping row due to insufficient columns after address: ${partsAfterAddress.joinToString()}")
                    }
                }
            }
            reader.close()
            inputStream.close()

            println("Locais de Coleta lidos: ${locaisColeta.size}")

        } catch (e: Exception) {
            e.printStackTrace()
        }
        return locaisColeta
    }
}
