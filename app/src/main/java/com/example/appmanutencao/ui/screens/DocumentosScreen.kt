package com.example.appmanutencao.ui.screens

import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Description
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.appmanutencao.model.Documento
import com.example.appmanutencao.viewmodel.AuthViewModel
import com.example.appmanutencao.viewmodel.ManutencaoViewModel

// A assinatura da função foi simplificada
@Composable
fun DocumentosScreen(
    viewModel: ManutencaoViewModel,
    authViewModel: AuthViewModel,
    mainNavController: NavController // Recebe o NavController principal
) {
    val documentos by viewModel.documentosState.collectAsState()
    val numeroSerie by authViewModel.numeroSerie.observeAsState()

    LaunchedEffect(numeroSerie) {
        numeroSerie?.let { ns ->
            if (ns.isNotBlank()) {
                viewModel.carregarDocumentos(ns)
            }
        }
    }

    // A tela agora renderiza seu conteúdo diretamente, sem Scaffold
    if (documentos.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("Nenhum documento encontrado.")
        }
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(documentos) { documento ->
                DocumentoItem(
                    documento = documento,
                    // Usa o NavController principal para navegar para o PDF
                    onClick = { mainNavController.navigate("pdfViewer/${Uri.encode(documento.url)}") }
                )
            }
        }
    }
}

@Composable
fun DocumentoItem(documento: Documento, onClick: () -> Unit) {
    if (documento.url.isBlank()) return

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(Icons.Filled.Description, contentDescription = "Documento", tint = MaterialTheme.colorScheme.primary)
            Text(documento.descricao, style = MaterialTheme.typography.bodyLarge)
        }
    }
}
