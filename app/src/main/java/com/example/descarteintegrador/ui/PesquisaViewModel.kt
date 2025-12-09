package com.example.descarteintegrador.ui

import android.location.Location
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.descarteintegrador.data.DataSource
import com.example.descarteintegrador.data.PesquisaUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PesquisaViewModel : ViewModel() {
    private val _pesquisaUiState = MutableStateFlow(PesquisaUiState())
    val pesquisaUiState: StateFlow<PesquisaUiState> = _pesquisaUiState.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                DataSource.getCurrentDeviceLocation(),
                _pesquisaUiState
            ) { location, uiState ->
                Pair(location, uiState.material)
            }.collect { (location, material) ->
                updateFilteredLocations(location, material)
            }
        }
    }


     // Atualiza o tipo de material a ser filtrado.
    fun setMaterial(tipo: DataSource.TipoResiduo) {
        _pesquisaUiState.update { currentState ->
            currentState.copy(material = tipo)
        }
    }

    //Abre o diálogo para seleção de material.
    fun openMaterialSelectionDialog() {
        _pesquisaUiState.update { it.copy(isMaterialSelectionDialogOpen = true) }
    }

     // Fecha o diálogo para seleção de material.
    fun dismissMaterialSelectionDialog() {
        _pesquisaUiState.update { it.copy(isMaterialSelectionDialogOpen = false) }
    }

    private fun updateFilteredLocations(location: Location?, material: DataSource.TipoResiduo) {
        if (location == null) {
            _pesquisaUiState.update { currentState ->
                currentState.copy(
                    locaisFiltrados = emptyList(),
                    isLocationAvailable = false,
                    currentLocation = null
                )
            }
            return
        }

        val filteredList = DataSource.getLocaisColetaFiltered(
            type = material,
            currentLat = location.latitude,
            currentLng = location.longitude,
            radiusKm = 10.0
        )

        _pesquisaUiState.update { currentState ->
            currentState.copy(
                locaisFiltrados = filteredList,
                isLocationAvailable = true,
                totalDeLocais = filteredList.size,
                currentLocation = location
            )
        }
    }
}
