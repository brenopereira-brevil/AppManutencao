package com.example.appmanutencao.data

import com.google.firebase.firestore.DocumentId

data class Equipamento(
    @DocumentId
    val numeroSerie: String? = null, // O ID do documento será o número de série

    val cliente_nome: String = "N/D",
    val descricao_equipamento: String = "N/D",
    val capacidade: String = "N/D",
    val vao: String = "N/D",
    val grupo_trabalho: String = "N/D",
    val url_imagem: String = "",
    val usuario_responsavel: String = "N/D"
)
