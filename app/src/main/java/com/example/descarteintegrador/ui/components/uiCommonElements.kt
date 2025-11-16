package com.example.descarteintegrador.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun BotaoComTexto(texto: String, modifier: Modifier = Modifier, onClick: () -> Unit = { }) {
    Button(
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
    Column {
        Text(
            text = titulo
        )
        Row() {
            botaoEsquerda()
            botaoDireita()
        }
    }
}