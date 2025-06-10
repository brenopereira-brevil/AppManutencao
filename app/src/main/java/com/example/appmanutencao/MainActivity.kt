package com.example.appmanutencao

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.appmanutencao.ui.screens.CadastroScreen
import com.example.appmanutencao.ui.screens.DetailScreen
import com.example.appmanutencao.ui.screens.DocumentosScreen
import com.example.appmanutencao.ui.screens.HistoricoScreen
import com.example.appmanutencao.ui.screens.HomeScreen
import com.example.appmanutencao.ui.screens.LoginScreen
import com.example.appmanutencao.ui.screens.PdfViewerScreen
import com.example.appmanutencao.ui.theme.AppManutencaoTheme
import com.example.appmanutencao.viewmodel.AuthViewModel
import com.example.appmanutencao.viewmodel.ManutencaoViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.appmanutencao.ui.screens.Model3DViewerScreen


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
    // --- CORREÇÃO 1: ViewModels instanciados de forma limpa, sem duplicatas ---
    val authViewModel: AuthViewModel = viewModel()
    val manutencaoViewModel: ManutencaoViewModel = viewModel()

    NavHost(navController = navController, startDestination = "login") {
        composable("login") {
            LoginScreen(
                viewModel = authViewModel,
                onLoginSuccess = { navController.navigate("home") }
            )
        }

        composable("home") {
            HomeScreen(
                onNavigateToCadastro = { navController.navigate("cadastro") },
                onNavigateToHistorico = { navController.navigate("historico") },
                onNavigateToDocumentos = { navController.navigate("documentos") },
                authViewModel = authViewModel,
                manutencaoViewModel = manutencaoViewModel,
                navController = navController
            )
        }

        composable("cadastro") {
            CadastroScreen(
                viewModel = manutencaoViewModel,
                authViewModel = authViewModel, // Adicionar este
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable("historico") {
            HistoricoScreen(
                viewModel = manutencaoViewModel,
                authViewModel = authViewModel, // Adicionar este
                onNavigateToDetail = { manutencaoId ->
                    // Navega para a tela de detalhes passando o ID da manutenção
                    navController.navigate("detalhes/$manutencaoId")
                },
                onNavigateToCadastro = {
                    navController.navigate("cadastro")
                },
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable("detalhes/{manutencaoId}") { backStackEntry ->
            // O nome do argumento deve ser o mesmo que na rota ("manutencaoId")
            val manutencaoId = backStackEntry.arguments?.getString("manutencaoId")
            if (manutencaoId != null) {
                DetailScreen(
                    manutencaoId = manutencaoId,
                    viewModel = manutencaoViewModel,
                    authViewModel = authViewModel, // Adicionar este
                    onNavigateBack = { navController.popBackStack() }
                )
            }
        }

        composable("documentos") {
            DocumentosScreen(
                viewModel = manutencaoViewModel,
                authViewModel = authViewModel,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToPdf = { pdfUrl ->
                    navController.navigate("pdfViewer/${Uri.encode(pdfUrl)}")
                }
            )
        }

        composable("pdfViewer/{pdfUrl}") { backStackEntry ->
            val pdfUrl = backStackEntry.arguments?.getString("pdfUrl") ?: ""
            PdfViewerScreen(
                pdfUrl = Uri.decode(pdfUrl), // Decodificar a URL aqui é uma boa prática
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // --- CORREÇÃO 3: Rota 3D agora espera a URL como argumento ---
        composable("model3D/{modelUrl}") { backStackEntry ->
            // Extrai o argumento da URL que foi codificado e passado na rota
            val encodedUrl = backStackEntry.arguments?.getString("modelUrl") ?: ""
            // Decodifica a URL para o formato original, tratando caracteres especiais
            val modelUrl = Uri.decode(encodedUrl)

            // Apenas mostra a tela se a URL for válida
            if (modelUrl.isNotEmpty()) {
                Model3DViewerScreen(
                    modelUrl = modelUrl,
                    onNavigateBack = { navController.popBackStack() }
                )
            } else {
                // Se a URL não for passada, mostra um erro.
                Text("Erro: URL do modelo 3D não foi fornecida.")
            }
        }

        // --- CORREÇÃO 2: Rota "model3DWebView" duplicada foi removida ---
    }
}