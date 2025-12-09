package com.example.descarteintegrador.data

import android.location.Location

data class PesquisaUiState(
    val totalDeLocais: Int = 0,
    val material: TipoResiduo = TipoResiduo.ecoponto, // Valor inicial
    val locaisFiltrados: List<LocalColeta> = emptyList(), // Updated reference
    val isLocationAvailable: Boolean = false,
    val currentLocation: Location? = null,
    val isMaterialSelectionDialogOpen: Boolean = false // Novo estado para o diálogo
)
