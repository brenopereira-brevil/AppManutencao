package com.example.appmanutencao.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.appmanutencao.viewmodel.AuthViewModel

@Composable
fun LoginScreen(
    viewModel: AuthViewModel,
    onLoginSuccess: () -> Unit
) {
    var numeroSerie by remember { mutableStateOf("") }
    var senha by remember { mutableStateOf("") }
    val authSuccess by viewModel.authSuccess.observeAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text("Login", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = numeroSerie,
            onValueChange = { numeroSerie = it },
            label = { Text("Número da OF") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = senha,
            onValueChange = { senha = it },
            label = { Text("Senha") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { viewModel.login(numeroSerie, senha) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Entrar")
        }

        authSuccess?.let { success ->
            if (!success) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Login falhou. Verifique os dados.",
                    color = MaterialTheme.colorScheme.error
                )
            } else {
                onLoginSuccess()
            }
        }
    }
}
