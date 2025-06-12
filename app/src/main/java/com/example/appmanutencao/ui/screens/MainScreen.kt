package com.example.appmanutencao.ui.screens

import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.appmanutencao.data.BottomNavItem
import com.example.appmanutencao.viewmodel.AuthViewModel
import com.example.appmanutencao.viewmodel.ManutencaoViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    mainNavController: NavController,
    authViewModel: AuthViewModel,
    manutencaoViewModel: ManutencaoViewModel,
    startDestination: String
) {
    val bottomNavController = rememberNavController()

    val navBackStackEntry by bottomNavController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val topBarTitle = when (currentRoute) {
        BottomNavItem.Historico.route -> "Histórico"
        BottomNavItem.Documentos.route -> "Documentos"
        BottomNavItem.Clp.route -> "Visualização CLP"
        else -> "Área de Trabalho"
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(topBarTitle) },
                navigationIcon = {
                    IconButton(onClick = { mainNavController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Voltar para o Painel")
                    }
                },
                actions = {
                    if (currentRoute == BottomNavItem.Historico.route) {
                        IconButton(onClick = { mainNavController.navigate("cadastro") }) {
                            Icon(Icons.Default.Add, "Adicionar Manutenção")
                        }
                    }
                }
            )
        },
        bottomBar = {
            AppBottomNavigationBar(
                navController = bottomNavController,
                mainNavController = mainNavController,
                manutencaoViewModel = manutencaoViewModel,
                authViewModel = authViewModel
            )
        }
    ) { innerPadding ->
        NavHost(
            navController = bottomNavController,
            startDestination = startDestination,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(BottomNavItem.Historico.route) {
                HistoricoScreen(
                    viewModel = manutencaoViewModel,
                    authViewModel = authViewModel,
                    mainNavController = mainNavController
                )
            }
            composable(BottomNavItem.Documentos.route) {
                // --- CHAMADA CORRIGIDA ---
                DocumentosScreen(
                    viewModel = manutencaoViewModel,
                    authViewModel = authViewModel,
                    mainNavController = mainNavController
                )
            }
            composable(BottomNavItem.Clp.route) {
                // --- CHAMADA CORRIGIDA ---
                ClpViewerScreen()
            }
        }
    }
}

@Composable
fun AppBottomNavigationBar(
    navController: NavController,
    mainNavController: NavController,
    manutencaoViewModel: ManutencaoViewModel,
    authViewModel: AuthViewModel
) {
    // Este código não foi alterado.
    val items = listOf(
        BottomNavItem.Historico,
        BottomNavItem.Documentos,
        BottomNavItem.Modelo3D,
        BottomNavItem.Clp,
    )
    val numeroSerie by authViewModel.numeroSerie.observeAsState()
    val context = LocalContext.current

    val navigationState3D by manutencaoViewModel.navigateTo3D.collectAsState()
    LaunchedEffect(navigationState3D) {
        navigationState3D?.let { result ->
            result.onSuccess { url ->
                mainNavController.navigate("model3D/${Uri.encode(url)}")
                manutencaoViewModel.onNavegacao3DCompleta()
            }
            result.onFailure { error ->
                Toast.makeText(context, error.message, Toast.LENGTH_LONG).show()
                manutencaoViewModel.onNavegacao3DCompleta()
            }
        }
    }

    NavigationBar {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination

        items.forEach { item ->
            NavigationBarItem(
                icon = { Icon(item.icon, contentDescription = item.title) },
                label = { Text(item.title) },
                selected = currentDestination?.hierarchy?.any { it.route == item.route } == true,
                onClick = {
                    if (item.route == "modelo3d") {
                        numeroSerie?.let { ns -> manutencaoViewModel.onBotao3dClicado(ns) }
                    } else {
                        navController.navigate(item.route) {
                            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                }
            )
        }
    }
}
