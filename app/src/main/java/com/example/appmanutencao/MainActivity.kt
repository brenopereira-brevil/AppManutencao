package com.example.appmanutencao

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.appmanutencao.data.BottomNavItem
import com.example.appmanutencao.ui.screens.CadastroScreen
import com.example.appmanutencao.ui.screens.DetailScreen
import com.example.appmanutencao.ui.screens.HomeScreen
import com.example.appmanutencao.ui.screens.LoginScreen
import com.example.appmanutencao.ui.screens.MainScreen
import com.example.appmanutencao.ui.screens.Model3DViewerScreen
import com.example.appmanutencao.ui.screens.PdfViewerScreen
import com.example.appmanutencao.ui.theme.AppManutencaoTheme
import com.example.appmanutencao.viewmodel.AuthViewModel
import com.example.appmanutencao.viewmodel.ManutencaoViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppManutencaoTheme {
                AppNavigator()
            }
        }
    }
}


@Composable
fun AppNavigator() {
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = viewModel()
    val manutencaoViewModel: ManutencaoViewModel = viewModel()

    NavHost(navController = navController, startDestination = "login") {
        composable("login") {
            LoginScreen(
                viewModel = authViewModel,
                onLoginSuccess = {
                    navController.navigate("home") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            )
        }

        composable("home") {
            HomeScreen(
                authViewModel = authViewModel,
                manutencaoViewModel = manutencaoViewModel,
                navController = navController
            )
        }

        // Rota para a tela principal com a BottomBar.
        // Aceita um argumento para saber qual aba abrir primeiro.
        composable("main_screen/{startRoute}") { backStackEntry ->
            val startRoute = backStackEntry.arguments?.getString("startRoute") ?: BottomNavItem.Historico.route
            MainScreen(
                mainNavController = navController,
                authViewModel = authViewModel,
                manutencaoViewModel = manutencaoViewModel,
                startDestination = startRoute
            )
        }

        // Telas que são abertas "por cima" de tudo.
        composable("cadastro") {
            CadastroScreen(
                viewModel = manutencaoViewModel,
                authViewModel = authViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable("detalhes/{manutencaoId}") { backStackEntry ->
            val manutencaoId = backStackEntry.arguments?.getString("manutencaoId")
            if (manutencaoId != null) {
                DetailScreen(
                    manutencaoId = manutencaoId,
                    viewModel = manutencaoViewModel,
                    authViewModel = authViewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
        }

        composable("pdfViewer/{pdfUrl}") { backStackEntry ->
            val pdfUrl = backStackEntry.arguments?.getString("pdfUrl")?.let { Uri.decode(it) } ?: ""
            PdfViewerScreen(
                pdfUrl = pdfUrl,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable("model3D/{modelUrl}") { backStackEntry ->
            val modelUrl = backStackEntry.arguments?.getString("modelUrl")?.let { Uri.decode(it) } ?: ""
            if (modelUrl.isNotEmpty()) {
                Model3DViewerScreen(
                    modelUrl = modelUrl,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
        }
    }
}
