package com.example.appmanutencao.data

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "manutencoes")
data class Manutencao(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val tecnico: String,
    val data: String,
    val motivo: String,
    val solucao: String,
    val duracao: String
)

