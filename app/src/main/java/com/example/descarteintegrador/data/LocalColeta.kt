package com.example.descarteintegrador.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

//os nomes desse enum DEVEM estar iguais aos que estão no CSV
enum class TipoResiduo {
    ecoponto,
    pilhas_baterias,
    pneus,
    lampadas,
    gesso,
    vidro,
    oleo,
    UNKNOWN // para lidar com tipos com erro de escrita
}

@Entity(tableName = "locais")
data class LocalColeta(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val nome: String,
    val endereco: String,
    val lat: Double,
    val lng: Double,
    val tipo: TipoResiduo
){

    //calcula distância entre dois pares de coordenadas usando a fórmula de Haversine, retorna a distância em metros
    fun calcularDistancia(targetLat: Double, targetLng: Double): Double {
        val earthRadius = 6371000.0

        val dLat = Math.toRadians(targetLat - this.lat)
        val dLng = Math.toRadians(targetLng - this.lng)

        val a = sin(dLat / 2) * sin(dLat / 2) +
                cos(Math.toRadians(this.lat)) * cos(Math.toRadians(targetLat)) *
                sin(dLng / 2) * sin(dLng / 2)

        val c = 2 * atan2(sqrt(a), sqrt(1 - a))

        return earthRadius * c
    }
}