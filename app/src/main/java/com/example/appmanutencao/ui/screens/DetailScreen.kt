package com.example.appmanutencao.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.appmanutencao.data.Manutencao
import com.example.appmanutencao.viewmodel.ManutencaoViewModel

@Composable
fun DetailScreen(
    manutencaoId: Int,
    viewModel: ManutencaoViewModel,
    onNavigateBack: () -> Unit
) {
    val manutencoes by viewModel.manutencoes.collectAsState()
    val manutencao = manutencoes.find { it.id == manutencaoId }

    var isEditing by remember { mutableStateOf(false) }

    var tecnico by remember { mutableStateOf(manutencao?.tecnico ?: "") }
    var data by remember { mutableStateOf(manutencao?.data ?: "") }
    var motivo by remember { mutableStateOf(manutencao?.motivo ?: "") }
    var solucao by remember { mutableStateOf(manutencao?.solucao ?: "") }
    var duracao by remember { mutableStateOf(manutencao?.duracao ?: "") }

    if (manutencao == null) {
        Text("Manutenção não encontrada.")
        return
    }

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {

        Text("Detalhes da Manutenção", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        if (isEditing) {
            OutlinedTextField(value = tecnico, onValueChange = { tecnico = it }, label = { Text("Técnico") })
            OutlinedTextField(value = data, onValueChange = { data = it }, label = { Text("Data") })
            OutlinedTextField(value = motivo, onValueChange = { motivo = it }, label = { Text("Motivo") })
            OutlinedTextField(value = solucao, onValueChange = { solucao = it }, label = { Text("Solução") })
            OutlinedTextField(value = duracao, onValueChange = { duracao = it }, label = { Text("Duração") })

            Spacer(modifier = Modifier.height(16.dp))

            Row {
                Button(onClick = {
                    val atualizado = manutencao.copy(
                        tecnico = tecnico,
                        data = data,
                        motivo = motivo,
                        solucao = solucao,
                        duracao = duracao
                    )
                    viewModel.inserir(atualizado)
                    isEditing = false
                }) {
                    Text("Salvar")
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(onClick = {
                    isEditing = false
                }) {
                    Text("Cancelar")
                }
            }
        } else {
            Text("Técnico: ${manutencao.tecnico}")
            Text("Data: ${manutencao.data}")
            Text("Motivo: ${manutencao.motivo}")
            Text("Solução: ${manutencao.solucao}")
            Text("Duração: ${manutencao.duracao}")

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = { isEditing = true }) {
                Text("Editar")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onNavigateBack) {
            Text("Voltar")
        }
    }
}
