package com.example.appmanutencao.model

import com.google.firebase.firestore.DocumentId

data class Documento(
    @DocumentId
    val id: String? = null,
    val descricao: String = "",
    val url: String = ""
)
