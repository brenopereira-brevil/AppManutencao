package com.example.appmanutencao.ui.screens

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView

// Função auxiliar para obter as SharedPreferences de forma limpa
private fun getClpPreferences(context: Context): SharedPreferences {
    return context.getSharedPreferences("clp_settings", Context.MODE_PRIVATE)
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun ClpViewerScreen() {
    val context = LocalContext.current
    val sharedPreferences = remember { getClpPreferences(context) }

    // URL padrão caso nenhuma esteja salva
    val defaultUrl = "http://192.168.0.101:8080/webvisum.htm"

    // Estado que guarda a URL atual. Ele é inicializado com o valor salvo localmente.
    var clpUrl by remember {
        mutableStateOf(sharedPreferences.getString("clp_url", defaultUrl) ?: defaultUrl)
    }

    // Estado para controlar a visibilidade do diálogo de edição
    var showEditDialog by remember { mutableStateOf(false) }

    // Guardamos a instância da WebView para podermos chamar o método .reload() nela
    var webViewInstance by remember { mutableStateOf<WebView?>(null) }

    // Usamos um Box para sobrepor a WebView e os botões
    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            modifier = Modifier.fillMaxSize(), // WebView ocupa todo o espaço
            factory = { ctx ->
                WebView(ctx).apply {
                    settings.javaScriptEnabled = true
                    webViewClient = WebViewClient()
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                    // Salva a instância da WebView quando ela é criada
                    webViewInstance = this
                }
            },
            // O bloco 'update' é chamado sempre que o estado 'clpUrl' muda
            update = { webView ->
                webView.loadUrl(clpUrl)
            }
        )

        // Row para agrupar os botões flutuantes
        Row(
            modifier = Modifier
                .align(Alignment.BottomStart) // Alinha os botões no canto inferior esquerdo
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Botão de Refresh
            FloatingActionButton(
                onClick = { webViewInstance?.reload() },
                elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 4.dp)
            ) {
                Icon(Icons.Default.Refresh, contentDescription = "Atualizar Página")
            }

            // Botão de Editar
            FloatingActionButton(
                onClick = { showEditDialog = true },
                elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 4.dp)
            ) {
                Icon(Icons.Default.Edit, contentDescription = "Editar Endereço")
            }
        }
    }


    // Diálogo de Edição (lógica inalterada)
    if (showEditDialog) {
        var tempUrl by remember { mutableStateOf(clpUrl) }
        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            title = { Text("Editar Endereço do CLP") },
            text = {
                Column {
                    Text("Insira a nova URL, incluindo 'http://'", style = MaterialTheme.typography.bodySmall)
                    OutlinedTextField(
                        value = tempUrl,
                        onValueChange = { tempUrl = it },
                        label = { Text("URL do CLP") },
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        // Salva a nova URL localmente nas SharedPreferences
                        sharedPreferences.edit().putString("clp_url", tempUrl).apply()
                        // Atualiza o estado, o que fará a WebView recarregar com a nova URL
                        clpUrl = tempUrl
                        showEditDialog = false
                    }
                ) {
                    Text("Confirmar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}
