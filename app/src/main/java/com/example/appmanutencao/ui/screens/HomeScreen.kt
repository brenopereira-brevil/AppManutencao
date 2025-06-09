package com.example.appmanutencao.ui.screens

//import Model3DWebActivity
import android.content.Context
import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.appmanutencao.Model3DWebActivity

import com.example.appmanutencao.viewmodel.AuthViewModel
import com.example.appmanutencao.viewmodel.ManutencaoViewModel
import com.example.appmanutencao.utils.openCustomTab
//import kotlinx.coroutines.flow.internal.NoOpContinuation.context



@Composable
fun HomeScreen(
    onNavigateToCadastro: () -> Unit,
    onNavigateToHistorico: () -> Unit,
    onNavigateToDocumentos: () -> Unit,
    authViewModel: AuthViewModel,
    manutencaoViewModel: ManutencaoViewModel,
    navController: NavController

) {
    val context: Context = LocalContext.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Button(
            onClick = onNavigateToCadastro,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Cadastrar Manutenção")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = onNavigateToHistorico,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Ver Histórico")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = onNavigateToDocumentos,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Ver Documentação")
        }
        Spacer(modifier = Modifier.height(16.dp))
        BotaoModelo3D(authViewModel = authViewModel)

//        val context = LocalContext.current
//        val numeroSerie = authViewModel.numeroSerie.value
//        Button(
//            onClick = {val context = LocalContext.current
//                val numeroSerie = authViewModel.numeroSerie.value
//                numeroSerie?.let {
//                    val intent = Intent(context, Model3DWebActivity::class.java)
//                    intent.putExtra("numeroSerie", it)
//                    context.startActivity(intent)
//                }//,
//            //modifier = Modifier.fillMaxWidth()
//        ) {
//            Text("Modelo 3D")
//        }

    }
}

@Composable
fun BotaoModelo3D(authViewModel: AuthViewModel) {
    val context = LocalContext.current
    val numeroSerie = authViewModel.numeroSerie.value

    Button(
        onClick = {
            if (numeroSerie.isNullOrBlank()) {
                // Aqui você pode exibir um Toast, Snackbar ou log
                println("Número de série inválido ou não informado.")
                return@Button
            }

            val intent = Intent(context, Model3DWebActivity::class.java)
            intent.putExtra("numeroSerie", numeroSerie)
            context.startActivity(intent)
        },
        modifier = Modifier.fillMaxWidth()
    ) {
        Text("Modelo 3D")
    }
}

