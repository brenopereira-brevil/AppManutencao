package com.example.appmanutencao.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.appmanutencao.model.Manutencao
import com.example.appmanutencao.viewmodel.AuthViewModel
import com.example.appmanutencao.viewmodel.ManutencaoViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    manutencaoId: String,
    viewModel: ManutencaoViewModel,
    authViewModel: AuthViewModel,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val numeroSerie by authViewModel.numeroSerie.observeAsState()
    val manutencao by viewModel.manutencaoSelecionada.collectAsState()

    // Controla a visibilidade do diálogo de exclusão
    var showConfirmDialog by remember { mutableStateOf(false) }

    // Carrega os dados da manutenção específica quando a tela é aberta
    LaunchedEffect(numeroSerie, manutencaoId) {
        numeroSerie?.let { ns ->
            viewModel.carregarManutencaoPorId(ns, manutencaoId)
        }
    }

    // Estados para os campos de texto
    var descricao by remember { mutableStateOf("") }
    var duracao by remember { mutableStateOf("") }
    var solucao by remember { mutableStateOf("") }
    var tecnico by remember { mutableStateOf("") }

    // Atualiza os campos de texto quando os dados chegam do ViewModel
    LaunchedEffect(manutencao) {
        manutencao?.let {
            descricao = it.descricao
            duracao = it.duracao
            solucao = it.solucao
            tecnico = it.tecnico
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalhes da Manutenção") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Voltar")
                    }
                },
                actions = {
                    IconButton(onClick = { showConfirmDialog = true }) {
                        Icon(Icons.Default.Delete, contentDescription = "Excluir", tint = MaterialTheme.colorScheme.error)
                    }
                }
            )
        }
    ) { paddingValues ->
        if (manutencao == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(value = descricao, onValueChange = { descricao = it }, label = { Text("Descrição") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = duracao, onValueChange = { duracao = it }, label = { Text("Duração") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = solucao, onValueChange = { solucao = it }, label = { Text("Solução") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = tecnico, onValueChange = { tecnico = it }, label = { Text("Técnico") }, modifier = Modifier.fillMaxWidth())

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        val ns = numeroSerie ?: ""
                        // Cria o objeto com os dados atualizados E com o ID original
                        val manutencaoAtualizada = manutencao!!.copy(
                            descricao = descricao,
                            duracao = duracao,
                            solucao = solucao,
                            tecnico = tecnico
                        )
                        viewModel.salvarManutencao(ns, manutencaoAtualizada) { sucesso ->
                            if (sucesso) {
                                Toast.makeText(context, "Alterações salvas!", Toast.LENGTH_SHORT).show()
                                onNavigateBack()
                            } else {
                                Toast.makeText(context, "Erro ao salvar.", Toast.LENGTH_SHORT).show()
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Salvar Alterações")
                }
            }
        }
    }

    if (showConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmDialog = false },
            title = { Text("Confirmar Exclusão") },
            text = { Text("Você tem certeza que deseja excluir este registro de manutenção? Esta ação não pode ser desfeita.") },
            confirmButton = {
                Button(
                    onClick = {
                        val ns = numeroSerie ?: ""
                        viewModel.excluirManutencao(ns, manutencaoId) { sucesso ->
                            if (sucesso) {
                                Toast.makeText(context, "Registro excluído.", Toast.LENGTH_SHORT).show()
                                onNavigateBack()
                            } else {
                                Toast.makeText(context, "Erro ao excluir.", Toast.LENGTH_SHORT).show()
                            }
                        }
                        showConfirmDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Excluir")
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}
