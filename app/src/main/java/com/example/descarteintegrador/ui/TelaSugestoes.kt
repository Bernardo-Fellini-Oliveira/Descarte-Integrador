package com.example.descarteintegrador.ui

import android.R.attr.onClick
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.example.descarteintegrador.R
import com.example.descarteintegrador.ui.components.BotaoComTexto
import com.example.descarteintegrador.ui.components.BotoesDeOpcaoComTitulo

@Composable
fun TelaSugestoes(modifier: Modifier = Modifier) {
    BotoesDeOpcaoComTitulo(
        titulo = stringResource(R.string.suggest),
        botaoEsquerda = {
            BotaoComTexto(
                texto = stringResource(R.string.suggest_material)
            ) { }
        },
        botaoDireita = {
            BotaoComTexto(
                texto = stringResource(R.string.suggest_local)
            ) { }
        },
        modifier = modifier
    )
}