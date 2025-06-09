package com.example.appmanutencao.ui.screens

import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.appmanutencao.viewmodel.AuthViewModel
import com.example.appmanutencao.viewmodel.ManutencaoViewModel




@Composable
fun DocumentosScreen(
    viewModel: ManutencaoViewModel,
    authViewModel: AuthViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToPdf: (String) -> Unit
) {
    //var numeroSerie by remember { mutableStateOf("") }
    val documentos by viewModel.documentos.observeAsState(emptyList())
    val context = LocalContext.current
    val numeroSerie by authViewModel.numeroSerie.observeAsState()

    LaunchedEffect(numeroSerie) {
        numeroSerie?.let {
            viewModel.buscarDocumentos(it)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Documentos - Nº Série: $numeroSerie",
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn {
            items(documentos) { doc ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clickable { onNavigateToPdf(doc.url) }
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = doc.nome,
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }
            }
        }

        if (documentos.isEmpty()) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Nenhum documento encontrado.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.error
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = onNavigateBack, modifier = Modifier.fillMaxWidth()) {
            Text("Voltar")
        }
    }

}
