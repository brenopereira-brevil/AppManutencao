// Em ui/screens/Model3DViewerScreen.kt

package com.example.appmanutencao.ui.screens

import android.annotation.SuppressLint
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.runtime.Composable
import androidx.compose.ui.viewinterop.AndroidView


// Este é o Composable que será chamado pelo seu NavHost
@SuppressLint("SetJavaScriptEnabled")
@Composable
fun Model3DViewerScreen(
    modelUrl: String,
    onNavigateBack: () -> Unit // Embora não usado aqui, é bom para consistência
) {
    // AndroidView é a ponte entre o mundo Compose e o mundo das Views XML
    AndroidView(
        factory = { context ->
            // A 'factory' cria a View uma única vez
            WebView(context).apply {
                // Aplica todas as configurações avançadas necessárias para o WebGL funcionar
                settings.javaScriptEnabled = true
                settings.domStorageEnabled = true
                settings.databaseEnabled = true
                settings.loadWithOverviewMode = true
                settings.useWideViewPort = true
                settings.javaScriptCanOpenWindowsAutomatically = true

                // WebChromeClient lida com eventos do "navegador" como alertas e console logs
                webChromeClient = WebChromeClient()

                // WebViewClient lida com a navegação (cliques em links, etc.)
                webViewClient = object : WebViewClient() {
                    override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                        // Impede que cliques dentro do viewer abram o navegador externo
                        view?.loadUrl(url!!)
                        return true
                    }
                }

                // Garante que a WebView seja destruída corretamente para evitar vazamento de memória
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )

                // Finalmente, carrega a URL do modelo
                loadUrl(modelUrl)
            }
        },
        // O bloco 'update' é chamado sempre que o Composable é recomposto
        update = { webView ->
            // Se a URL pudesse mudar dinamicamente, nós a carregaríamos aqui.
            // No nosso caso, a factory já carrega a URL inicial.
        }
    )
}