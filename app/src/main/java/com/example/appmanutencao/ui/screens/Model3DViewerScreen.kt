package com.example.appmanutencao.ui.screens

import android.annotation.SuppressLint
import android.view.ViewGroup
import android.webkit.WebChromeClient
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
fun Model3DViewerScreen(
    modelUrl: String,
    onNavigateBack: () -> Unit
) {
    // Usamos o Scaffold para estruturar a tela com uma barra superior
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Visualização 3D") },
                navigationIcon = {
                    // Botão para chamar a função de voltar
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Voltar"
                        )
                    }
                }
            )
        }
    ) { innerPadding -> // O Scaffold fornece o padding para não sobrepor o conteúdo

        // O seu AndroidView com a WebView agora fica dentro do Scaffold
        AndroidView(
            // Aplicamos o padding para que a WebView comece ABAIXO da TopAppBar
            modifier = Modifier.padding(innerPadding),
            factory = { context ->
                WebView(context).apply {
                    // Todas as suas configurações avançadas permanecem as mesmas
                    settings.javaScriptEnabled = true
                    settings.domStorageEnabled = true
                    settings.databaseEnabled = true
                    settings.loadWithOverviewMode = true
                    settings.useWideViewPort = true
                    settings.javaScriptCanOpenWindowsAutomatically = true

                    webChromeClient = WebChromeClient()
                    webViewClient = object : WebViewClient() {
                        override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                            view?.loadUrl(url!!)
                            return true
                        }
                    }

                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )

                    loadUrl(modelUrl)
                }
            }
        )
    }
}
