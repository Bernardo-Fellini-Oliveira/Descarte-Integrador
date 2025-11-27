package com.example.descarteintegrador.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.toColorInt
import com.example.descarteintegrador.R
import com.example.descarteintegrador.ui.components.BotaoComTexto

@Composable
fun LocalDeDescarte(modifier: Modifier = Modifier) {
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
                    painter = painterResource(R.drawable.ic_launcher_background), // coloque seu ícone correto
                    contentDescription = null,
                    modifier = Modifier.size(32.dp)
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.fillMaxWidth()) {
                    val darkGreyHex = stringResource(R.string.dark_grey_hex)
                    Text(
                        text = "nome do local",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "distância até o local",
                        color = Color(darkGreyHex.toColorInt())
                    )
                    BotaoComTexto(
                        stringResource(R.string.more_info),
                    modifier = Modifier.align(Alignment.CenterHorizontally))
                }
            }


        }
    }
}

@Composable
fun TelaPesquisa(
    modifier: Modifier = Modifier,
    locaisDeDescarte: List<@Composable () -> Unit> = listOf({ LocalDeDescarte() }, { LocalDeDescarte() })
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .background(color = Color.Black)
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
                        painter = painterResource(R.drawable.ic_launcher_background),
                        contentDescription = stringResource(R.string.symbol_description),
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = stringResource(R.string.localization),
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                        Text(
                            text = "placeholder",
                            color = Color(greyHex.toColorInt()),
                            modifier = Modifier.align(Alignment.Start)
                        )
                        BotaoComTexto(stringResource(R.string.change_localization))
                    }
                }
            }
        }

        // CARD "POSTOS DE DESTINAÇÃO"
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
                        text = stringResource(R.string.destination, "lâmpadas"),
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Text(
                        text = stringResource(R.string.destination_found, 2),
                        color = Color(greyHex.toColorInt())
                    )
                    BotaoComTexto(
                        texto = stringResource(R.string.change_material),
                        modifier = Modifier.align(Alignment.CenterHorizontally)
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
            text = stringResource(R.string.closest_locations_text),
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
            items(locaisDeDescarte) {
                LocalDeDescarte()
            }
        }

        // CARD SUGESTÃO DE MOVIMENTAÇÃO
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
                BotaoComTexto(texto = stringResource(R.string.move_order))
            }
        }

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
                BotaoComTexto(texto = stringResource(R.string.not_found_order))
            }
        }
    }
}
