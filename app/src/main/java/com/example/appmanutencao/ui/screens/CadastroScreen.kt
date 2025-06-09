package com.example.appmanutencao.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.appmanutencao.data.Manutencao
import com.example.appmanutencao.viewmodel.ManutencaoViewModel

@Composable
fun CadastroScreen(
    onNavigateBack: () -> Unit,
    viewModel: ManutencaoViewModel
) {
    var tecnico by remember { mutableStateOf("") }
    var data by remember { mutableStateOf("") }
    var motivo by remember { mutableStateOf("") }
    var solucao by remember { mutableStateOf("") }
    var duracao by remember { mutableStateOf("") }

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {

        Text("Cadastro de Manutenção", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(value = tecnico, onValueChange = { tecnico = it }, label = { Text("Técnico") })
        OutlinedTextField(value = data, onValueChange = { data = it }, label = { Text("Data") })
        OutlinedTextField(value = motivo, onValueChange = { motivo = it }, label = { Text("Motivo") })
        OutlinedTextField(value = solucao, onValueChange = { solucao = it }, label = { Text("Solução") })
        OutlinedTextField(value = duracao, onValueChange = { duracao = it }, label = { Text("Duração") })

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            viewModel.inserir(
                Manutencao(
                    tecnico = tecnico,
                    data = data,
                    motivo = motivo,
                    solucao = solucao,
                    duracao = duracao
                )
            )
            onNavigateBack()
        }) {
            Text("Salvar")
        }
    }
}

//@Preview
//@Composable
//fun CadastroScreenPreview() {
//    CadastroScreen(onNavigateBack = {}, viewModel = ManutencaoViewModel())
//}

