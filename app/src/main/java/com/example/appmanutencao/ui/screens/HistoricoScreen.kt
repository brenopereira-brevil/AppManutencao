package com.example.appmanutencao.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.appmanutencao.model.Manutencao
import com.example.appmanutencao.viewmodel.AuthViewModel
import com.example.appmanutencao.viewmodel.ManutencaoViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoricoScreen(
    viewModel: ManutencaoViewModel,
    authViewModel: AuthViewModel,
    onNavigateToDetail: (manutencaoId: String) -> Unit,
    onNavigateToCadastro: () -> Unit,
    onNavigateBack: () -> Unit
) {
    // Observa o StateFlow do ViewModel. Sempre que a lista no ViewModel mudar,
    // esta variável será atualizada, recompondo a tela.
    val historico by viewModel.historicoState.collectAsState()
    val numeroSerie by authViewModel.numeroSerie.observeAsState()

    // LaunchedEffect garante que o código dentro dele seja executado apenas uma vez
    // quando a tela é criada (ou quando o numeroSerie mudar).
    LaunchedEffect(numeroSerie) {
        numeroSerie?.let { ns ->
            if (ns.isNotBlank()) {
                viewModel.carregarHistorico(ns)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Histórico de Manutenções") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Voltar")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onNavigateToCadastro) {
                Icon(Icons.Default.Add, contentDescription = "Adicionar Manutenção")
            }
        }
    ) { paddingValues ->
        if (historico.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize().padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text("Nenhuma manutenção encontrada.")
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 8.dp)
            ) {
                items(historico) { manutencao ->
                    ManutencaoCard(manutencao = manutencao, onNavigateToDetail = onNavigateToDetail)
                }
            }
        }
    }
}

@Composable
private fun ManutencaoCard(
    manutencao: Manutencao,
    onNavigateToDetail: (manutencaoId: String) -> Unit
) {
    // O ID nunca deve ser nulo se veio do Firestore
    val manutencaoId = manutencao.id ?: return

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onNavigateToDetail(manutencaoId) },
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
            // Formata a data para um formato legível
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
