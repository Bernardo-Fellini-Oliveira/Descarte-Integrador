package com.example.descarteintegrador.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.toColorInt
import com.example.descarteintegrador.R
import com.example.descarteintegrador.ui.components.BotaoComTexto
import com.example.descarteintegrador.ui.theme.DescarteIntegradorTheme

@Composable
fun TelaTransporte(modifier: Modifier = Modifier) {
    val orangeHex = stringResource(R.string.orange_hex)
    Card(
        colors = CardDefaults.cardColors(
            containerColor = Color(orangeHex.toColorInt())
        ),
        modifier = modifier
            .height(300.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = modifier
                .fillMaxHeight()
        ) {
            Text(
                text = stringResource(R.string.choose),
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 64.sp
            )
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                BotaoComTexto(texto = stringResource(R.string.choose_local))
                BotaoComTexto(texto = stringResource(R.string.choose_time))
            }
            BotaoComTexto(texto = stringResource(R.string.confirm))
        }
    }
}

@Preview
@Composable
fun PreviewTelaTransporte() {
    DescarteIntegradorTheme {
        TelaTransporte(
        )
    }
}