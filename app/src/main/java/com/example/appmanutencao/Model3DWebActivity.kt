package com.example.appmanutencao

import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore

class Model3DWebActivity : AppCompatActivity() {
    private lateinit var webView: WebView
    private val firestore = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        webView = WebView(this)
        setContentView(webView)

        val numeroSerie = intent.getStringExtra("numeroSerie")

        if (numeroSerie != null) {
            firestore.collection("modelos3D")
                .document(numeroSerie)
                .get()
                .addOnSuccessListener { document ->
                    val url = document.getString("url")
                    if (!url.isNullOrBlank()) {
                        webView.settings.javaScriptEnabled = true
                        webView.webViewClient = WebViewClient()
                        webView.loadUrl(url)
                    } else {
                        webView.loadData("URL não encontrada", "text/html", "UTF-8")
                    }
                }
                .addOnFailureListener {
                    webView.loadData("Erro ao buscar modelo", "text/html", "UTF-8")
                }
        } else {
            webView.loadData("Número de série não encontrado", "text/html", "UTF-8")
        }
    }
}
