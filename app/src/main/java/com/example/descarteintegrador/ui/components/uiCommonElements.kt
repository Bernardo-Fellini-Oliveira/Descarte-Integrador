package com.example.descarteintegrador.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun BotaoComTexto(texto: String, modifier: Modifier = Modifier, onClick: () -> Unit = { }) {
    Button(
        modifier = modifier,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Black
        ),
        border = BorderStroke(1.dp, Color.White),
        onClick = onClick
    ) {
        Text(
            text = texto
        )
    }
}

@Composable
fun BotoesDeOpcaoComTitulo(
    titulo: String,
    botaoEsquerda: @Composable () -> Unit,
    botaoDireita: @Composable () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
    ) {
        Text(
            text = titulo
        )
        Row() {
            botaoEsquerda()
            botaoDireita()
        }
    }
}