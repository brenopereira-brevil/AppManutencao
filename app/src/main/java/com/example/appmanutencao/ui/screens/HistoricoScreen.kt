package com.example.appmanutencao.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.appmanutencao.viewmodel.ManutencaoViewModel

@Composable
fun HistoricoScreen(
    viewModel: ManutencaoViewModel,
    onNavigateToDetail: (Int) -> Unit,
    onNavigateBack: () -> Unit
) {
    val manutencoes by viewModel.manutencoes.collectAsState()

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {

        Text("Histórico de Manutenções", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn {
            items(manutencoes) { manutencao ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    onClick = { onNavigateToDetail(manutencao.id) }
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Data: ${manutencao.data}", style = MaterialTheme.typography.titleMedium)
                        Text("Técnico: ${manutencao.tecnico}", style = MaterialTheme.typography.bodyMedium)
                        Text("Solução: ${manutencao.solucao}", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(onClick = onNavigateBack) {
            Text("Voltar")
        }
    }
}
