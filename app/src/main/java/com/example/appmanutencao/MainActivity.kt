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
    val viewModel: ManutencaoViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
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
                onNavigateBack = { navController.popBackStack() },
                viewModel = viewModel
            )
        }
        composable("historico") {
            HistoricoScreen(
                viewModel = viewModel,
                onNavigateToDetail = { id ->
                    navController.navigate("detalhes/$id")
                },
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable("detalhes/{id}") { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id")?.toIntOrNull()
            if (id != null) {
                DetailScreen(
                    manutencaoId = id,
                    viewModel = viewModel,
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
                pdfUrl = pdfUrl,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable("model3D") {
            val modelUrl by manutencaoViewModel.modelo3DUrl.observeAsState("")

            if (modelUrl.isNotEmpty()) {
                Model3DViewerScreen(
                    modelUrl = modelUrl,
                    onNavigateBack = { navController.popBackStack() }
                )

            } else {
                Text("Carregando modelo 3D...")
            }
        }

        composable("model3DWebView") {
            val modelUrl by manutencaoViewModel.modelo3DUrl.observeAsState("")

            if (modelUrl.isNotEmpty()) {
                Model3DViewerScreen(
                    modelUrl = modelUrl,
                    onNavigateBack = { navController.popBackStack() }
                )
            } else {
                Text("Carregando modelo 3D...")
            }
        }



    }
}
