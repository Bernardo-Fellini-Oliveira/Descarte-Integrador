package com.example.descarteintegrador.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.example.descarteintegrador.R
import com.example.descarteintegrador.ui.components.BotaoComTexto
import com.example.descarteintegrador.ui.components.BotoesDeOpcaoComTitulo

@Composable
fun TelaTransporte(modifier: Modifier = Modifier) {
    BotoesDeOpcaoComTitulo(
        titulo = stringResource(R.string.choose),
        botaoEsquerda = {
            BotaoComTexto(
                texto = stringResource(R.string.choose_local)
            ) { }
        },
        botaoDireita = {
            BotaoComTexto(
                texto = stringResource(R.string.choose_time)
            ) { }
        },
        modifier = modifier
    )
    BotaoComTexto(
        texto = stringResource(R.string.confirm)
    )
}