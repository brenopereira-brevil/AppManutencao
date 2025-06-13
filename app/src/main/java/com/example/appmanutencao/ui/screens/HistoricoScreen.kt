package com.example.appmanutencao.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.appmanutencao.model.Manutencao
import com.example.appmanutencao.viewmodel.AuthViewModel
import com.example.appmanutencao.viewmodel.ManutencaoViewModel
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
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

    // A tela de histórico agora tem seu próprio Scaffold para conter o FAB
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { mainNavController.navigate("cadastro") }
            ) {
                Icon(Icons.Default.Add, contentDescription = "Adicionar Manutenção")
            }
        }
    ) { innerPadding ->
        if (historico.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize().padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Text("Nenhuma manutenção encontrada.")
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(8.dp)
            ) {
                items(historico) { manutencao ->
                    ManutencaoCard(manutencao = manutencao, mainNavController = mainNavController)
                }
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
