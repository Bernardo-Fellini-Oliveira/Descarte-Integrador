package com.example.descarteintegrador.ui

import android.R.attr.fontWeight
import android.R.attr.text
import android.location.Location
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.toColorInt
import com.example.descarteintegrador.R
import com.example.descarteintegrador.data.PesquisaUiState
import com.example.descarteintegrador.data.TipoResiduo // Added import
import com.example.descarteintegrador.data.LocalColeta // Added import
import com.example.descarteintegrador.ui.components.BotaoComTexto
import com.example.descarteintegrador.ui.theme.DescarteIntegradorTheme

@Composable
fun LocalDeDescarte(localDeDescarte: LocalColeta, localizacao: Location, modifier: Modifier = Modifier) { // Updated reference
    Card(
        modifier = modifier
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1A1A1A)
        ),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(R.drawable.localiza__o),
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(Color.White),
                    modifier = Modifier
                        .size(48.dp)

                )

                Spacer(modifier = Modifier.width(12.dp))

                val darkGreyHex = stringResource(R.string.dark_grey_hex).toColorInt()

                Column(modifier = Modifier.fillMaxWidth()) {
                    val distanciaKm = localDeDescarte.calcularDistancia(localizacao.latitude, localizacao.longitude) / 1000.0

                    Text(
                        text = stringResource(R.string.name, formatArgs = arrayOf(localDeDescarte.nome)),
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = stringResource(R.string.dist, formatArgs = arrayOf("%.2f km".format(distanciaKm))),
                        color = Color(darkGreyHex),
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = stringResource(R.string.address, formatArgs = arrayOf(localDeDescarte.endereco)),
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = localDeDescarte.telefone.takeIf { !it.isNullOrBlank() }
                            ?.let { telefone -> stringResource(R.string.phone, formatArgs = arrayOf(telefone)) }
                            ?: stringResource(R.string.no_phone),
                        color = Color(darkGreyHex),
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = localDeDescarte.email.takeIf { !it.isNullOrBlank() }
                            ?.let { email -> stringResource(R.string.email, formatArgs = arrayOf(email)) }
                            ?: stringResource(R.string.no_mail),
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}


@Composable
fun TelaPesquisa(
    modifier: Modifier = Modifier,
    cliqueSugestao: () -> Unit,
    cliqueTransporte: () -> Unit,
    uiState: PesquisaUiState,
    onMaterialChange: (TipoResiduo) -> Unit, // Updated reference
    onOpenDialog: () -> Unit,
    onDismissDialog: () -> Unit
) {
    if (uiState.isMaterialSelectionDialogOpen) {
        MaterialSelectionDialog(
            currentMaterial = uiState.material,
            onDismiss = onDismissDialog,
            onMaterialSelected = { newMaterial ->
                onMaterialChange(newMaterial)
                onDismissDialog()
            }
        )
    }

    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            // cor branca não muito brilhante
            .background(color = Color(alpha = 0xFF, red = 0xE0, green = 0xE0, blue = 0xE0))
            .padding(12.dp)
            .verticalScroll(rememberScrollState())
    ) {
        val orangeHex = stringResource(R.string.orange_hex)
        val darkOrangeHex = stringResource(R.string.dark_orange_hex)
        val greyHex = stringResource((R.string.grey_hex))

        Text(
            text = stringResource(R.string.descarte),
            fontSize = 64.sp,
            color = Color(orangeHex.toColorInt())
        )
        Text(
            text = stringResource(R.string.integrador),
            fontSize = 64.sp,
            color = Color(orangeHex.toColorInt())
        )
        // CARD DA LOCALIZAÇÃO
        Card(
            colors = CardDefaults.cardColors(
                containerColor = Color(orangeHex.toColorInt())
            ),
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = painterResource(R.drawable.localiza__o),
                        contentDescription = stringResource(R.string.symbol_description),
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = if (uiState.isLocationAvailable) stringResource(R.string.localization)
                            else stringResource(R.string.local_fail),
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                        if (uiState.isLocationAvailable && uiState.currentLocation != null) {
                            Text(
                                text = "Lat: %.4f, Lng: %.4f".format(uiState.currentLocation.latitude, uiState.currentLocation.longitude),
                                color = Color(greyHex.toColorInt())
                            )
                        }
                    }
                }
            }
        }

        // CARD "POSTOS DE DESCARTE"
        Card(
            colors = CardDefaults.cardColors(
                containerColor = Color(orangeHex.toColorInt())
            ),
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = when(uiState.material) {
                            TipoResiduo.UNKNOWN -> stringResource(R.string.unkown)
                            TipoResiduo.ecoponto -> stringResource(R.string.ecopoint)
                            else -> stringResource(R.string.destination, uiState.material.name.replaceFirstChar { it.titlecase() })
                        },
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Text(
                        text = when(uiState.material) {
                            TipoResiduo.UNKNOWN -> ""
                            else -> stringResource(R.string.destination_found, uiState.totalDeLocais.toString())},
                        color = Color(greyHex.toColorInt())
                    )
                    BotaoComTexto(
                        texto = stringResource(R.string.change_material),
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                        onClick = onOpenDialog // Ação de clique para abrir o diálogo
                    )
                }
            }
        }

        // CARD AVISO
        Card(
            colors = CardDefaults.cardColors(
                containerColor = Color(darkOrangeHex.toColorInt())
            ),
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Text(
                modifier = Modifier
                    .padding(12.dp),
                text = stringResource(R.string.disclaimer),
                textAlign = TextAlign.Justify,
                color = Color.Black
            )
        }

        Text(
            text = if (uiState.isLocationAvailable && uiState.currentLocation != null) {
                stringResource(R.string.closest_locations_text)
            } else {
                stringResource(R.string.no_locations)
            },
            color = Color(orangeHex.toColorInt()),
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
        )

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 600.dp)
        ) {
            if (uiState.isLocationAvailable && uiState.currentLocation != null) {
                items(
                    items = uiState.locaisFiltrados,
                    key = { local -> local.nome + local.endereco }
                ) { localDeDescarte ->
                    LocalDeDescarte(localDeDescarte, uiState.currentLocation)
                }
            }
        }

        /*// CARD SUGESTÃO DE MOVIMENTAÇÃO
        // Não implementado nessa versão
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = Color(orangeHex.toColorInt())
            ),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
            ) {
                Text(text = stringResource(R.string.move_suggestion), fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                BotaoComTexto(texto = stringResource(R.string.move_order), onClick = cliqueTransporte)
            }
        }*/

        // CARD "NÃO ENCONTROU?"
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = Color(orangeHex.toColorInt())
            ),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
            ) {
                Text(text = stringResource(R.string.not_found_suggestion), fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                BotaoComTexto(texto = stringResource(R.string.not_found_order), onClick = cliqueSugestao)
            }
        }
    }
}

// Um AlertDialog que mostra a lista de materiais para seleção.
@Composable
fun MaterialSelectionDialog(
    currentMaterial: TipoResiduo, // Updated reference
    onDismiss: () -> Unit,
    onMaterialSelected: (TipoResiduo) -> Unit, // Updated reference
    modifier: Modifier = Modifier
) {
    // Estado temporário para a seleção dentro do diálogo
    var selectedOption by remember { mutableStateOf(currentMaterial) }

    val materialOptions = TipoResiduo.values().filter { // Updated reference
        it != TipoResiduo.UNKNOWN // Updated reference
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = modifier,
        title = { Text(text = stringResource(R.string.change_material)) },
        text = {
            Column(Modifier.verticalScroll(rememberScrollState())) {
                materialOptions.forEach { material ->
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .selectable(
                                selected = (material == selectedOption),
                                onClick = { selectedOption = material }
                            )
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = (material == selectedOption),
                            onClick = { selectedOption = material },
                            colors = RadioButtonDefaults.colors(selectedColor = Color(stringResource(R.string.orange_hex).toColorInt()))
                        )
                        Spacer(Modifier.width(8.dp))

                        Column {
                            Text(text = material.name.replaceFirstChar { it.titlecase() })
                            if (material == TipoResiduo.ecoponto) { // Updated reference
                                Text(
                                    text = stringResource(R.string.ecopoint_help),
                                    fontSize = 12.sp,
                                    color = Color.Gray
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onMaterialSelected(selectedOption)
                }
            ) {
                Text(stringResource(R.string.confirm))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun PesquisaPreview() {
    DescarteIntegradorTheme {
        TelaPesquisa(
            uiState = PesquisaUiState(isLocationAvailable = false),
            cliqueSugestao = {},
            cliqueTransporte = {},
            onMaterialChange = {},
            onOpenDialog = {},
            onDismissDialog = {}
        )
    }
}
