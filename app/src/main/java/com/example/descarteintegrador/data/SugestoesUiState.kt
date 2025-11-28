package com.example.descarteintegrador.data

enum class DialogType {
    NONE, MATERIAL, LOCAL
}

data class SugestoesUiState(
    val dialogOpen: DialogType = DialogType.NONE,
    val sugestaoMaterial: String = "",
    val sugestaoEndereco: String = "",
    val sugestaoNumero: String = "",
    val erroMaterial: String? = null
)
