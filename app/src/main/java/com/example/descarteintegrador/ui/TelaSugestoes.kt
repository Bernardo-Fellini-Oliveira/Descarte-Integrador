package com.example.descarteintegrador.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.toColorInt
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.descarteintegrador.R
import com.example.descarteintegrador.data.DialogType
import com.example.descarteintegrador.ui.components.BotaoComTexto
import com.example.descarteintegrador.ui.theme.DescarteIntegradorTheme

@Composable
fun TelaSugestoes(
    modifier: Modifier = Modifier,
    onBack: () -> Unit,
    sugestoesViewModel: SugestoesViewModel = viewModel()
) {
    val orangeHex = stringResource(R.string.orange_hex)
    val uiState by sugestoesViewModel.uiState.collectAsState()

    when (uiState.dialogOpen) {
        DialogType.MATERIAL -> {
            SugestaoMaterialDialog(
                sugestao = uiState.sugestaoMaterial,
                erro = uiState.erroMaterial,
                onValueChange = { sugestoesViewModel.onMaterialChange(it) },
                onConfirm = { sugestoesViewModel.confirmarSugestaoMaterial() },
                onDismiss = { sugestoesViewModel.dismissDialog() }
            )
        }
        DialogType.LOCAL -> {
            SugestaoLocalDialog(
                endereco = uiState.sugestaoEndereco,
                numero = uiState.sugestaoNumero,
                onEnderecoChange = { sugestoesViewModel.onEnderecoChange(it) },
                onNumeroChange = { sugestoesViewModel.onNumeroChange(it) },
                onConfirm = { sugestoesViewModel.confirmarSugestaoLocal() },
                onDismiss = { sugestoesViewModel.dismissDialog() }
            )
        }
        DialogType.NONE -> {}
    }

    Card(
        colors = CardDefaults.cardColors(
            containerColor = Color(orangeHex.toColorInt())
        ),
        modifier = modifier
            .fillMaxWidth()
            .padding(12.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .padding(vertical = 24.dp, horizontal = 16.dp)
        ) {
            Text(
                text = stringResource(R.string.suggest),
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 64.sp
            )
            Spacer(modifier = Modifier.height(32.dp))
            Column (
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                BotaoComTexto(
                    texto = stringResource(R.string.suggest_material),
                    onClick = { sugestoesViewModel.openMaterialDialog() }
                )
                BotaoComTexto(
                    texto = stringResource(R.string.suggest_local),
                    onClick = { sugestoesViewModel.openLocalDialog() }
                )
                BotaoComTexto(
                    texto = stringResource(R.string.back),
                    onClick = onBack
                )
            }
        }
    }
}

@Composable
private fun SugestaoMaterialDialog(
    sugestao: String,
    erro: String?,
    onValueChange: (String) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        // CORREÇÃO: Usando o nome de string de título inequívoco
        title = { Text(stringResource(id = R.string.suggest_new_material_title)) },
        text = {
            Column {
                OutlinedTextField(
                    value = sugestao,
                    onValueChange = onValueChange,
                    label = { Text(stringResource(id = R.string.material_name_label)) },
                    singleLine = true,
                    isError = erro != null
                )
                if (erro != null) {
                    Text(text = erro, color = Color.Red, fontSize = 12.sp)
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = onConfirm,
                enabled = sugestao.isNotBlank()
            ) {
                Text(stringResource(id = R.string.confirm))
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss
            ) {
                Text(stringResource(id = R.string.cancel))
            }
        }
    )
}

@Composable
private fun SugestaoLocalDialog(
    endereco: String,
    numero: String,
    onEnderecoChange: (String) -> Unit,
    onNumeroChange: (String) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        // CORREÇÃO: Usando o nome de string de título inequívoco
        title = { Text(stringResource(R.string.suggest_new_local_title)) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = endereco,
                    onValueChange = onEnderecoChange,
                    label = { Text(stringResource(R.string.street_name_label)) },
                    singleLine = true,
                )
                OutlinedTextField(
                    value = numero,
                    onValueChange = onNumeroChange,
                    label = { Text(stringResource(R.string.address_label)) },
                    singleLine = true
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = onConfirm,
                enabled = endereco.isNotBlank() && numero.isNotBlank()
            ) {
                Text(stringResource(id = R.string.confirm))
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss
            ) {
                Text(stringResource(id = R.string.cancel))
            }
        }
    )
}


@Preview
@Composable
fun PreviewTelaSugestoes() {
    DescarteIntegradorTheme {
        TelaSugestoes(onBack = {})
    }
}
