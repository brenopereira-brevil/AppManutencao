package com.example.appmanutencao.ui.screens

import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Article
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material.icons.filled.ViewInAr
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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

    val context = LocalContext.current
    val navigationState3D by manutencaoViewModel.navigateTo3D.collectAsState()

    LaunchedEffect(numeroSerie) {
        numeroSerie?.let { ns ->
            if (ns.isNotBlank()) {
                manutencaoViewModel.carregarDadosEquipamento(ns)
            }
        }
    }

    LaunchedEffect(navigationState3D) {
        navigationState3D?.let { result ->
            result.onSuccess { url ->
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
    manutencaoViewModel: ManutencaoViewModel,
    numeroSerie: String?
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // --- BOTÃO DE CADASTRAR MANUTENÇÃO REMOVIDO DAQUI ---

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
