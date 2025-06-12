package com.example.appmanutencao.ui.screens

import android.annotation.SuppressLint
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("SetJavaScriptEnabled")
@Composable
fun ClpViewerScreen(
    onNavigateBack: () -> Unit
) {
    // A URL específica do seu CLP. Note o "http://" no início.
    val clpUrl = "http://192.168.0.101:8080/webvisum.htm"

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Visualização do CLP") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Voltar"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        AndroidView(
            modifier = Modifier.padding(innerPadding),
            factory = { context ->
                WebView(context).apply {
                    // Habilitar JavaScript é uma boa prática, mesmo para páginas simples.
                    settings.javaScriptEnabled = true
                    webViewClient = WebViewClient() // Garante que a página carregue dentro do app.

                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                    loadUrl(clpUrl)
                }
            }
        )
    }
}
