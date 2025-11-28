package com.example.descarteintegrador

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.descarteintegrador.data.ScreenTransitions
import com.example.descarteintegrador.ui.PesquisaViewModel
import com.example.descarteintegrador.ui.TelaPesquisa
import com.example.descarteintegrador.ui.TelaSugestoes
import com.example.descarteintegrador.ui.TelaTransporte

@Composable
fun DescarteIntegradorApp(
    pesquisaViewModel: PesquisaViewModel = viewModel(),
    navController: NavHostController = rememberNavController()
) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentScreen = ScreenTransitions.valueOf(
        backStackEntry?.destination?.route ?: ScreenTransitions.Pesquisa.name
    )

    Scaffold { innerPadding ->
        val pesquisaUiState by pesquisaViewModel.pesquisaUiState.collectAsState()

        NavHost(
            navController = navController,
            startDestination = ScreenTransitions.Pesquisa.name,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(route = ScreenTransitions.Pesquisa.name) {
                TelaPesquisa(
                    modifier = Modifier.fillMaxSize(),
                    cliqueSugestao = { navController.navigate(route = ScreenTransitions.Sugestoes.name) },
                    cliqueTransporte = { navController.navigate(route = ScreenTransitions.Transporte.name) },
                    uiState = pesquisaUiState,
                    onMaterialChange = { novoMaterial -> pesquisaViewModel.setMaterial(novoMaterial) },
                    onOpenDialog = { pesquisaViewModel.openMaterialSelectionDialog() },
                    onDismissDialog = { pesquisaViewModel.dismissMaterialSelectionDialog() }
                )
            }
            composable(route = ScreenTransitions.Sugestoes.name) {
                TelaSugestoes(
                    modifier = Modifier.fillMaxSize(),
                    // Passando a função para voltar
                    onBack = { navController.navigateUp() }
                )
            }
            composable(route = ScreenTransitions.Transporte.name) {
                TelaTransporte(
                    modifier = Modifier
                        .fillMaxSize()
                )
            }
        }
    }
}
