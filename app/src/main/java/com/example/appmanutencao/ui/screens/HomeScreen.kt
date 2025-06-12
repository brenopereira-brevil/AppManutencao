package com.example.appmanutencao.ui.screens

import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.appmanutencao.data.BottomNavItem
import com.example.appmanutencao.data.Equipamento
import com.example.appmanutencao.viewmodel.AuthViewModel
import com.example.appmanutencao.viewmodel.ManutencaoViewModel

@Composable
fun HomeScreen(
    authViewModel: AuthViewModel,
    manutencaoViewModel: ManutencaoViewModel,
    navController: NavController
) {
    val numeroSerie by authViewModel.numeroSerie.observeAsState()
    val equipamento by manutencaoViewModel.equipamentoState.collectAsState()

    // --- LÓGICA DO MODELO 3D (REINTEGRADA PARA FUNCIONAR AQUI) ---
    val context = LocalContext.current
    val navigationState3D by manutencaoViewModel.navigateTo3D.collectAsState()

    // Carrega os dados do equipamento quando a tela é aberta
    LaunchedEffect(numeroSerie) {
        numeroSerie?.let { ns ->
            if (ns.isNotBlank()) {
                manutencaoViewModel.carregarDadosEquipamento(ns)
            }
        }
    }

    // Observa o resultado da busca pela URL 3D e navega quando bem-sucedido
    LaunchedEffect(navigationState3D) {
        navigationState3D?.let { result ->
            result.onSuccess { url ->
                // Usa o NavController principal para abrir a tela 3D por cima de tudo
                navController.navigate("model3D/${Uri.encode(url)}")
                manutencaoViewModel.onNavegacao3DCompleta()
            }
            result.onFailure { error ->
                Toast.makeText(context, error.message, Toast.LENGTH_LONG).show()
                manutencaoViewModel.onNavegacao3DCompleta()
            }
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            if (equipamento == null) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                HeaderInfo(equipamento!!)
                // Passamos o ViewModel e o numeroSerie para as ações de navegação
                NavigationActions(
                    navController = navController,
                    manutencaoViewModel = manutencaoViewModel,
                    numeroSerie = numeroSerie
                )
            }
        }
    }
}

@Composable
fun HeaderInfo(equipamento: Equipamento) {
    // Este Composable não foi alterado.
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = equipamento.cliente_nome,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Usuário: ${equipamento.usuario_responsavel}",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(16.dp))
        AsyncImage(
            model = equipamento.url_imagem,
            contentDescription = "Imagem do Equipamento",
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clip(RoundedCornerShape(12.dp)),
            contentScale = ContentScale.Fit
        )
        Spacer(Modifier.height(16.dp))
        Text(equipamento.descricao_equipamento, style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            InfoChip("Capacidade", equipamento.capacidade)
            InfoChip("Vão", equipamento.vao)
            InfoChip("Grupo", equipamento.grupo_trabalho)
        }
        Spacer(Modifier.height(16.dp))
        Card(elevation = CardDefaults.cardElevation(4.dp)) {
            Text(
                text = "N/S: ${equipamento.numeroSerie}",
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp),
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
fun NavigationActions(
    navController: NavController,
    manutencaoViewModel: ManutencaoViewModel, // Parâmetro adicionado
    numeroSerie: String? // Parâmetro adicionado
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        NavigationButton(
            text = "Cadastrar Manutenção",
            icon = Icons.Default.EditNote,
            onClick = { navController.navigate("cadastro") }
        )
        NavigationButton(
            text = "Histórico",
            icon = Icons.Default.History,
            onClick = { navController.navigate("main_screen/${BottomNavItem.Historico.route}") }
        )
        NavigationButton(
            text = "Documentação",
            icon = Icons.Default.Article,
            onClick = { navController.navigate("main_screen/${BottomNavItem.Documentos.route}") }
        )
        NavigationButton(
            text = "Modelo 3D",
            icon = Icons.Default.ViewInAr,
            // Ação do botão 3D corrigida para chamar o ViewModel
            onClick = {
                numeroSerie?.let { ns ->
                    manutencaoViewModel.onBotao3dClicado(ns)
                }
            }
        )
        NavigationButton(
            text = "Visualizar CLP",
            icon = Icons.Default.Terminal,
            onClick = { navController.navigate("main_screen/${BottomNavItem.Clp.route}") }
        )
    }
}

@Composable
fun InfoChip(label: String, value: String) {
    // Este Composable não foi alterado.
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, style = MaterialTheme.typography.labelSmall)
        Text(value, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun NavigationButton(text: String, icon: ImageVector, onClick: () -> Unit) {
    // Este Composable não foi alterado.
    OutlinedButton(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(imageVector = icon, contentDescription = text)
            Spacer(Modifier.width(16.dp))
            Text(text, fontSize = 16.sp)
        }
    }
}
