package com.example.appmanutencao.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.appmanutencao.model.Manutencao
import com.example.appmanutencao.viewmodel.AuthViewModel
import com.example.appmanutencao.viewmodel.ManutencaoViewModel
import java.text.SimpleDateFormat
import java.util.*

// A tela agora não tem mais Scaffold e recebe o NavController principal
@Composable
fun HistoricoScreen(
    viewModel: ManutencaoViewModel,
    authViewModel: AuthViewModel,
    mainNavController: NavController
) {
    val historico by viewModel.historicoState.collectAsState()
    val numeroSerie by authViewModel.numeroSerie.observeAsState()

    LaunchedEffect(numeroSerie) {
        numeroSerie?.let { ns ->
            if (ns.isNotBlank()) {
                viewModel.carregarHistorico(ns)
            }
        }
    }

    // O conteúdo agora é renderizado diretamente, sem Scaffold
    if (historico.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("Nenhuma manutenção encontrada.")
        }
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            items(historico) { manutencao ->
                ManutencaoCard(manutencao = manutencao, mainNavController = mainNavController)
            }
        }
    }
}

@Composable
private fun ManutencaoCard(
    manutencao: Manutencao,
    mainNavController: NavController
) {
    val manutencaoId = manutencao.id ?: return

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { mainNavController.navigate("detalhes/$manutencaoId") },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Descrição: ${manutencao.descricao}",
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = "Técnico: ${manutencao.tecnico}",
                style = MaterialTheme.typography.bodyMedium
            )
            manutencao.data?.let {
                val formato = SimpleDateFormat("dd/MM/yyyy 'às' HH:mm", Locale.getDefault())
                Text(
                    text = "Data: ${formato.format(it)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
