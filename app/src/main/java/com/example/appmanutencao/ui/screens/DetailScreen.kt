package com.example.appmanutencao.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done // Ícone para "Salvar"
import androidx.compose.material.icons.filled.Edit // Ícone para "Editar"
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
import java.util.*

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


    // Estado para controlar se estamos em modo de edição. Inicia como 'false'.
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

    // 'LaunchedEffect' para preencher os campos de texto quando os dados chegam
    // ou quando o modo de edição é cancelado, para reverter quaisquer mudanças não salvas.
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

    // --- LÓGICA DE SALVAR EXTRAÍDA ---
    // Esta função pode ser chamada tanto pelo ícone 'Done' quanto por um botão.
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
                inEditMode = false // Sai do modo de edição
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
                            inEditMode = false // Cancela a edição
                        } else {
                            onNavigateBack()
                        }
                    }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Voltar")
                    }
                },
                actions = {
                    // --- NOVA FUNCIONALIDADE: Ícone de Editar/Salvar ---
                    IconButton(onClick = {
                        if (inEditMode) {
                            onSave() // Salva as alterações
                        } else {
                            inEditMode = true // Entra no modo de edição
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
                verticalArrangement = Arrangement.spacedBy(24.dp) // Aumentei o espaçamento
            ) {
                // --- NOVA FUNCIONALIDADE: Layout condicional ---
                if (inEditMode) {
                    // --- MODO DE EDIÇÃO ---
                    OutlinedTextField(value = descricao, onValueChange = { descricao = it }, label = { Text("Descrição") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = duracao, onValueChange = { duracao = it }, label = { Text("Duração") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = solucao, onValueChange = { solucao = it }, label = { Text("Solução") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = tecnico, onValueChange = { tecnico = it }, label = { Text("Técnico") }, modifier = Modifier.fillMaxWidth())
                } else {
                    // --- MODO DE VISUALIZAÇÃO ---
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

    // O diálogo de confirmação permanece igual
    if (showConfirmDialog) {
        // ... (código do AlertDialog de exclusão)
    }
}

/**
 * Um Composable reutilizável para mostrar um campo de dado em modo de leitura.
 */
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
