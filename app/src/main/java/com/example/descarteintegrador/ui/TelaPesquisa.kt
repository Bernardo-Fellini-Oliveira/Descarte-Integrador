package com.example.descarteintegrador.ui

import android.R.attr.text
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.res.stringResource
import com.example.descarteintegrador.R
import com.example.descarteintegrador.ui.components.BotaoComTexto

@Composable
fun LocalDeDescarte(modifier: Modifier = Modifier) {
    Card() {
        Row() {
            Column {
                Text(
                    text = "nome do local"
                )
                Text(
                    text = "distância até o local"
                )
            }
            BotaoComTexto(stringResource(R.string.more_info))
        }
    }
}

@Composable
fun TelaPesquisa(
    modifier : Modifier = Modifier,
    locaisDeDescarte: List<@Composable () -> Unit> = listOf( {LocalDeDescarte()}, {LocalDeDescarte()})
) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card() {
            Row() {
                Image(
                    painter = painterResource(R.drawable.ic_launcher_background), //placeholder
                    contentDescription = stringResource(R.string.symbol_description)
                )
                Column {
                    Text(
                        text = stringResource(R.string.localization)
                    )
                    Text(
                        text = "placeholder"
                    )
                }
                BotaoComTexto(stringResource(R.string.change_localization))
            }
        }
        Card() {
            Row() {
                Column {
                    Text(
                        text = stringResource(R.string.destination, formatArgs = arrayOf("lâmpadas"))
                    )
                    Text(
                        text = stringResource(R.string.destination_found, formatArgs = arrayOf(2))
                    )
                }
                BotaoComTexto(texto = stringResource(R.string.change_material))
            }
        }
        Card() {
            Text(
                text = stringResource(R.string.disclaimer)
            )
        }
        Text(
            text = stringResource(R.string.closest_locations_text)
        )
        LazyColumn(modifier = modifier) {
            items(items = locaisDeDescarte) {
                LocalDeDescarte()
            }
        }
        Card() {
            Column {
                Text(
                    text = stringResource(R.string.move_suggestion)
                )
                BotaoComTexto(texto = stringResource(R.string.move_order))
            }
        }
        Card() {
            Column {
                Text(
                    text = stringResource(R.string.not_found_suggestion)
                )
                BotaoComTexto(texto = stringResource(R.string.not_found_order))
            }
        }
    }
}
