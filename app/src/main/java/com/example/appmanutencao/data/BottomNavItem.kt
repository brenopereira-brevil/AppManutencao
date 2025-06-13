package com.example.appmanutencao.data

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Article
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material.icons.filled.ViewInAr
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Representa os itens de navegação da nossa BottomBar.
 * Cada objeto define a rota, o título e o ícone de uma tela.
 */
sealed class BottomNavItem(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    object Historico : BottomNavItem(
        route = "historico",
        title = "Histórico",
        icon = Icons.Default.History
    )
    object Documentos : BottomNavItem(
        route = "documentos",
        title = "Documentos",
        icon = Icons.Default.Article
    )
    object Modelo3D : BottomNavItem(
        route = "modelo3d", // A rota para iniciar o fluxo do 3D
        title = "Modelo 3D",
        icon = Icons.Default.ViewInAr
    )
    object Clp : BottomNavItem(
        route = "clp_viewer",
        title = "CLP",
        icon = Icons.Default.Terminal
    )
}