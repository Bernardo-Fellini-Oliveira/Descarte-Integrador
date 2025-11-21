package com.example.descarteintegrador.data

import android.content.Context
import com.example.descarteintegrador.R
import java.io.BufferedReader
import java.io.InputStreamReader

data class LocalColeta(
    val nome: String,
    val endereco: String,
    val lat: Double,
    val lng: Double,
    val tipo: String
)

object DataSource {

    lateinit var locaisColetaList: List<LocalColeta>

    fun loadLocaisColeta(context: Context) {
        locaisColetaList = readCsvData(context)
    }

    private fun readCsvData(context: Context): List<LocalColeta> {
        val locaisColeta = mutableListOf<LocalColeta>()
        try {
            val inputStream = context.resources.openRawResource(R.raw.locais)
            val reader = BufferedReader(InputStreamReader(inputStream))
            var line: String?

            // Skip the header row if your CSV has one
            reader.readLine() 

            while (reader.readLine().also { line = it } != null) {
                line?.let { rawLine ->
                    val nome: String
                    val endereco: String
                    val lat: Double
                    val lng: Double
                    val tipo: String

                    // Find the start of the quoted address field
                    val firstQuoteIndex = rawLine.indexOf('"')

                    if (firstQuoteIndex == -1) {
                        // No quoted field, assume simple comma separation
                        val simpleParts = rawLine.split(",").map { it.trim() }
                        if (simpleParts.size >= 5) {
                            try {
                                nome = simpleParts[0]
                                endereco = simpleParts[1]
                                lat = simpleParts[2].toDouble()
                                lng = simpleParts[3].toDouble()
                                tipo = simpleParts[4]
                                locaisColeta.add(LocalColeta(nome, endereco, lat, lng, tipo))
                            } catch (e: NumberFormatException) {
                                println("Skipping row due to number format error (simple parse): ${simpleParts.joinToString()}")
                                e.printStackTrace()
                            }
                        } else {
                            println("Skipping row due to insufficient columns (simple parse): ${simpleParts.joinToString()}")
                        }
                        return@let // Continue to next line
                    }

                    // Extrair 'nome'
                    val commaBeforeQuote = rawLine.substring(0, firstQuoteIndex).lastIndexOf(',')
                    if (commaBeforeQuote != -1) {
                        nome = rawLine.substring(0, commaBeforeQuote).trim()
                    } else {
                        println("Skipping row due to malformed 'nome' field: $rawLine")
                        return@let
                    }

                    // Find the end of the quoted address field
                    // Start searching from after the first quote
                    val endQuoteIndex = rawLine.indexOf('"', firstQuoteIndex + 1)
                    if (endQuoteIndex == -1) {
                        println("Skipping row due to missing closing quote for address: $rawLine")
                        return@let
                    }

                    // Extract 'endereco' (without quotes)
                    endereco = rawLine.substring(firstQuoteIndex + 1, endQuoteIndex).trim()

                    // Extract the remaining parts (lat, lng, tipo)

                    val remainingString = rawLine.substring(endQuoteIndex + 1).trim()
                    
                    val partsAfterAddress = if (remainingString.startsWith(",")) {
                        remainingString.substring(1).split(",").map { it.trim() }
                    } else {
                        remainingString.split(",").map { it.trim() }
                    }

                    if (partsAfterAddress.size >= 3) { // Expecting lat, lng, tipo
                        try {
                            lat = partsAfterAddress[0].toDouble()
                            lng = partsAfterAddress[1].toDouble()
                            tipo = partsAfterAddress[2]
                            locaisColeta.add(LocalColeta(nome, endereco, lat, lng, tipo))
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

            // Agora 'locaisColeta' contém todos os objetos LocalColeta do CSV.
            println("Locais de Coleta lidos: ${locaisColeta.size}")

        } catch (e: Exception) {
            e.printStackTrace()
        }
        return locaisColeta
    }
}