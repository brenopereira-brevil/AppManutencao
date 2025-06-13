package com.example.appmanutencao.ui.screens

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.appmanutencao.R
import com.example.appmanutencao.ui.theme.AppManutencaoTheme
import com.example.appmanutencao.viewmodel.AuthViewModel

@Composable
fun LoginScreen(
    viewModel: AuthViewModel,
    onLoginSuccess: () -> Unit
) {
    val context = LocalContext.current
    var numeroSerie by remember { mutableStateOf("") }
    var senha by remember { mutableStateOf("") }
    var senhaVisivel by remember { mutableStateOf(false) }

    // --- NOVA LÓGICA DE ESTADO ---
    val authResult by viewModel.authSuccess.observeAsState()
    var isLoading by remember { mutableStateOf(false) }

    val focusManager = LocalFocusManager.current

    // LaunchedEffect para reagir ao resultado da autenticação do ViewModel
    LaunchedEffect(authResult) {
        when (authResult) {
            true -> {
                // Sucesso: A navegação ocorre
                onLoginSuccess()
            }
            false -> {
                // Falha: Mostra erro e para o carregamento
                Toast.makeText(context, "Login falhou. Verifique os dados.", Toast.LENGTH_LONG).show()
                isLoading = false
            }
            null -> {
                // Estado inicial, não faz nada
            }
        }
    }

    val handleLogin = {
        focusManager.clearFocus()
        if (numeroSerie.isNotBlank() && senha.isNotBlank()) {
            isLoading = true // Inicia o carregamento
            viewModel.login(numeroSerie, senha)
            // A navegação agora é controlada pelo LaunchedEffect acima
        } else {
            Toast.makeText(context, "Por favor, preencha todos os campos.", Toast.LENGTH_SHORT).show()
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_logo),
                contentDescription = "Logo do Aplicativo",
                modifier = Modifier.size(250.dp) // Reduzi o tamanho para um visual mais equilibrado
            )

            Spacer(modifier = Modifier.height(48.dp))

            Text(text = "Bem-vindo", style = MaterialTheme.typography.headlineSmall)
            Text(
                text = "Faça login para continuar",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = numeroSerie,
                onValueChange = { numeroSerie = it },
                label = { Text("Número de Série") },
                singleLine = true,
                enabled = !isLoading, // Desabilita durante o carregamento
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = senha,
                onValueChange = { senha = it },
                label = { Text("Senha") },
                singleLine = true,
                enabled = !isLoading, // Desabilita durante o carregamento
                visualTransformation = if (senhaVisivel) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(onDone = { handleLogin() }),
                trailingIcon = {
                    val image = if (senhaVisivel) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                    IconButton(onClick = { senhaVisivel = !senhaVisivel }) {
                        Icon(imageVector = image, contentDescription = "Mostrar/Ocultar senha")
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { handleLogin() },
                enabled = !isLoading, // Desabilita o botão durante o carregamento
                modifier = Modifier.fillMaxWidth().height(50.dp)
            ) {
                // --- INDICADOR DE CARREGAMENTO ---
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Entrar")
                }
            }
        }
    }
}

@Preview(showBackground = true, name = "Login Screen Preview")
@Composable
fun LoginScreenPreview() {
    AppManutencaoTheme {
        LoginScreen(
            viewModel = AuthViewModel(),
            onLoginSuccess = {}
        )
    }
}
