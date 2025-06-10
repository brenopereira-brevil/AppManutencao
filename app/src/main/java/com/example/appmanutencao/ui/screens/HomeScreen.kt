package com.example.appmanutencao.ui.screens

import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.appmanutencao.viewmodel.AuthViewModel
import com.example.appmanutencao.viewmodel.ManutencaoViewModel

@Composable
fun HomeScreen(
    authViewModel: AuthViewModel,
    manutencaoViewModel: ManutencaoViewModel,
    navController: NavController,
    onNavigateToCadastro: () -> Unit,
    onNavigateToHistorico: () -> Unit,
    onNavigateToDocumentos: () -> Unit
) {
    val context = LocalContext.current
    val numeroSerie by authViewModel.numeroSerie.observeAsState()

    // Observa o StateFlow do ViewModel para reagir à tentativa de navegação
    val navigationState by manutencaoViewModel.navigateTo3D.collectAsState()

    // LaunchedEffect para lidar com o resultado da busca da URL.
    // Ele será executado sempre que 'navigationState' mudar.
    LaunchedEffect(navigationState) {
        navigationState?.let { result ->
            result.onSuccess { url ->
                // SUCESSO: Navega para a tela 3D
                navController.navigate("model3D/${Uri.encode(url)}")
                manutencaoViewModel.onNavegacao3DCompleta() // Reseta o estado
            }
            result.onFailure { error ->
                // FALHA: Mostra um Toast com a mensagem de erro
                Toast.makeText(context, error.message, Toast.LENGTH_LONG).show()
                manutencaoViewModel.onNavegacao3DCompleta() // Reseta o estado
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Button(onClick = onNavigateToCadastro, modifier = Modifier.fillMaxWidth()) {
            Text("Cadastrar Manutenção")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onNavigateToHistorico, modifier = Modifier.fillMaxWidth()) {
            Text("Ver Histórico")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onNavigateToDocumentos, modifier = Modifier.fillMaxWidth()) {
            Text("Ver Documentos")
        }
        Spacer(modifier = Modifier.height(16.dp))

        // O botão agora só tem uma responsabilidade: avisar o ViewModel que ele foi clicado.
        Button(
            onClick = {
                numeroSerie?.let { ns ->
                    manutencaoViewModel.onBotao3dClicado(ns)
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Modelo 3D")
        }
    }
}

