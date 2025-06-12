package com.example.appmanutencao.ui.screens

import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.appmanutencao.model.Documento
import com.example.appmanutencao.viewmodel.AuthViewModel
import com.example.appmanutencao.viewmodel.ManutencaoViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DocumentosScreen(
    viewModel: ManutencaoViewModel,
    authViewModel: AuthViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToPdf: (pdfUrl: String) -> Unit
) {
    val documentos by viewModel.documentosState.collectAsState()
    val numeroSerie by authViewModel.numeroSerie.observeAsState()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        LaunchedEffect(numeroSerie) {
            numeroSerie?.let { ns ->
                if (ns.isNotBlank()) {
                    viewModel.carregarDocumentos(ns)
                }
            }
        }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Documentos do Equipamento") },
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Voltar")
                        }
                    }
                )
            }
        ) { paddingValues ->
            if (documentos.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize().padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Nenhum documento encontrado.")
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(documentos) { documento ->
                        DocumentoItem(documento = documento, onNavigateToPdf = onNavigateToPdf)
                    }
                }
            }
        }
    }
}

@Composable
fun DocumentoItem(documento: Documento, onNavigateToPdf: (pdfUrl: String) -> Unit) {
    if (documento.url.isBlank()) return

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onNavigateToPdf(documento.url) },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(Icons.Default.PictureAsPdf, contentDescription = "PDF", tint = MaterialTheme.colorScheme.primary)
            Text(documento.descricao, style = MaterialTheme.typography.bodyLarge)
        }
    }
}
