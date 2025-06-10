package com.example.appmanutencao.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.appmanutencao.model.Manutencao
import com.example.appmanutencao.viewmodel.AuthViewModel
import com.example.appmanutencao.viewmodel.ManutencaoViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CadastroScreen(
    viewModel: ManutencaoViewModel,
    authViewModel: AuthViewModel,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val numeroSerie by authViewModel.numeroSerie.observeAsState()

    // Estados para controlar o texto de cada campo de input
    var descricao by remember { mutableStateOf("") }
    var duracao by remember { mutableStateOf("") }
    var solucao by remember { mutableStateOf("") }
    var tecnico by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Nova Manutenção") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Voltar")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = descricao,
                onValueChange = { descricao = it },
                label = { Text("Descrição do Problema") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = duracao,
                onValueChange = { duracao = it },
                label = { Text("Duração (ex: 2 horas)") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = solucao,
                onValueChange = { solucao = it },
                label = { Text("Solução Aplicada") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = tecnico,
                onValueChange = { tecnico = it },
                label = { Text("Nome do Técnico") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    val ns = numeroSerie
                    if (ns.isNullOrBlank()) {
                        Toast.makeText(context, "Erro: Número de série não encontrado.", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    // Cria o objeto Manutencao com os dados da tela
                    val novaManutencao = Manutencao(
                        descricao = descricao,
                        duracao = duracao,
                        solucao = solucao,
                        tecnico = tecnico
                        // O ID e a data serão gerados pelo Firestore
                    )

                    // Chama a função do ViewModel para salvar
                    viewModel.salvarManutencao(ns, novaManutencao) { sucesso ->
                        if (sucesso) {
                            Toast.makeText(context, "Manutenção salva com sucesso!", Toast.LENGTH_SHORT).show()
                            onNavigateBack()
                        } else {
                            Toast.makeText(context, "Erro ao salvar manutenção.", Toast.LENGTH_SHORT).show()
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Salvar")
            }
        }
    }
}
