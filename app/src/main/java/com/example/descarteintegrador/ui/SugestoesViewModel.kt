package com.example.descarteintegrador.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.descarteintegrador.data.DataSource
import com.example.descarteintegrador.data.DialogType
import com.example.descarteintegrador.data.SugestoesUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class SugestoesViewModel(application: Application) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(SugestoesUiState())
    val uiState = _uiState.asStateFlow()

    // Funções para controlar os diálogos
    fun openMaterialDialog() {
        _uiState.update { it.copy(dialogOpen = DialogType.MATERIAL, erroMaterial = null) }
    }

    fun openLocalDialog() {
        _uiState.update { it.copy(dialogOpen = DialogType.LOCAL) }
    }

    fun dismissDialog() {
        // Limpa os campos de texto ao fechar o diálogo
        _uiState.update {
            it.copy(
                dialogOpen = DialogType.NONE,
                sugestaoMaterial = "",
                sugestaoEndereco = "",
                sugestaoNumero = ""
            )
        }
    }

    // Funções para atualizar os textos
    fun onMaterialChange(text: String) {
        _uiState.update { it.copy(sugestaoMaterial = text, erroMaterial = null) }
    }

    fun onEnderecoChange(text: String) {
        _uiState.update { it.copy(sugestaoEndereco = text) }
    }

    fun onNumeroChange(text: String) {
        _uiState.update { it.copy(sugestaoNumero = text) }
    }

    // Funções de confirmação
    fun confirmarSugestaoMaterial() {
        val materialSugerido = _uiState.value.sugestaoMaterial.trim()

        // Verifica se o material já existe (comparação case-insensitive)
        val existe = DataSource.TipoResiduo.values().any {
            it.name.equals(materialSugerido.replace(" ", "_"), ignoreCase = true)
        }

        if (existe) {
            _uiState.update { it.copy(erroMaterial = "Este material já existe!") }
        } else {
            // Salva no arquivo
            DataSource.salvarSugestaoMaterial(getApplication(), materialSugerido)
            dismissDialog() // Fecha o diálogo após o sucesso
        }
    }

    fun confirmarSugestaoLocal() {
        val endereco = _uiState.value.sugestaoEndereco.trim()
        val numero = _uiState.value.sugestaoNumero.trim()

        // Salva no arquivo (sem validação, como solicitado)
        DataSource.salvarSugestaoLocal(getApplication(), endereco, numero)
        dismissDialog() // Fecha o diálogo após o sucesso
    }
}
