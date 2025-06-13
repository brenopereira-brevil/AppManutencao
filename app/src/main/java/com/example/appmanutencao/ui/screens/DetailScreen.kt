package com.example.appmanutencao.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.appmanutencao.viewmodel.AuthViewModel
import com.example.appmanutencao.viewmodel.ManutencaoViewModel
import java.text.SimpleDateFormat
import java.util.Locale

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

    var inEditMode by remember { mutableStateOf(false) }
    var showConfirmDialog by remember { mutableStateOf(false) }

    LaunchedEffect(numeroSerie, manutencaoId) {
        numeroSerie?.let { ns ->
            viewModel.carregarManutencaoPorId(ns, manutencaoId)
        }
    }

    var descricao by remember { mutableStateOf("") }
    var duracao by remember { mutableStateOf("") }
    var solucao by remember { mutableStateOf("") }
    var tecnico by remember { mutableStateOf("") }

    LaunchedEffect(manutencao, inEditMode) {
        manutencao?.let {
            if (!inEditMode) {
                descricao = it.descricao
                duracao = it.duracao
                solucao = it.solucao
                tecnico = it.tecnico
            }
        }
    }

    val onSave = {
        val ns = numeroSerie ?: ""
        val manutencaoAtualizada = manutencao!!.copy(
            descricao = descricao,
            duracao = duracao,
            solucao = solucao,
            tecnico = tecnico
        )
        viewModel.salvarManutencao(ns, manutencaoAtualizada) { sucesso ->
            if (sucesso) {
                Toast.makeText(context, "Alterações salvas!", Toast.LENGTH_SHORT).show()
                inEditMode = false
                onNavigateBack()
            } else {
                Toast.makeText(context, "Erro ao salvar.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (inEditMode) "Editar Manutenção" else "Detalhes") },
                navigationIcon = {
                    IconButton(onClick = {
                        if (inEditMode) {
                            inEditMode = false
                        } else {
                            onNavigateBack()
                        }
                    }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Voltar")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        if (inEditMode) {
                            onSave()
                        } else {
                            inEditMode = true
                        }
                    }) {
                        Icon(
                            imageVector = if (inEditMode) Icons.Default.Done else Icons.Default.Edit,
                            contentDescription = if (inEditMode) "Salvar" else "Editar"
                        )
                    }
                    IconButton(onClick = { showConfirmDialog = true }) {
                        Icon(Icons.Default.Delete, contentDescription = "Excluir", tint = MaterialTheme.colorScheme.error)
                    }
                }
            )
        }
    ) { paddingValues ->
        if (manutencao == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                if (inEditMode) {
                    OutlinedTextField(value = descricao, onValueChange = { descricao = it }, label = { Text("Descrição") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = duracao, onValueChange = { duracao = it }, label = { Text("Duração") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = solucao, onValueChange = { solucao = it }, label = { Text("Solução") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = tecnico, onValueChange = { tecnico = it }, label = { Text("Técnico") }, modifier = Modifier.fillMaxWidth())
                } else {
                    ReadOnlyField(label = "Descrição do Problema", value = descricao)
                    ReadOnlyField(label = "Solução Aplicada", value = solucao)
                    ReadOnlyField(label = "Técnico Responsável", value = tecnico)
                    ReadOnlyField(label = "Duração do Serviço", value = duracao)
                    manutencao?.data?.let {
                        val formato = SimpleDateFormat("dd 'de' MMMM 'de' yyyy, HH:mm", Locale.getDefault())
                        ReadOnlyField(label = "Data de Registro", value = formato.format(it))
                    }
                }
            }
        }
    }

    // --- CÓDIGO DO DIÁLOGO DE EXCLUSÃO CORRIGIDO E IMPLEMENTADO ---
    if (showConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmDialog = false },
            title = { Text("Confirmar Exclusão") },
            text = { Text("Você tem certeza que deseja excluir este registro? Esta ação não pode ser desfeita.") },
            confirmButton = {
                Button(
                    onClick = {
                        val ns = numeroSerie
                        if (ns.isNullOrBlank()) {
                            Toast.makeText(context, "Erro: Não foi possível identificar o equipamento.", Toast.LENGTH_SHORT).show()
                            return@Button
                        }

                        // AQUI ESTÁ A CHAMADA QUE FALTAVA
                        viewModel.excluirManutencao(ns, manutencaoId) { sucesso ->
                            if (sucesso) {
                                Toast.makeText(context, "Registro excluído.", Toast.LENGTH_SHORT).show()
                                onNavigateBack() // Volta para a tela de histórico
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

@Composable
private fun ReadOnlyField(label: String, value: String) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}
