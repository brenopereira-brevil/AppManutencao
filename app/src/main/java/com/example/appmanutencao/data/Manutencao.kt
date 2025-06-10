package com.example.appmanutencao.model // Ou o pacote correto do seu modelo

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

/**
 * Representa um único registro de manutenção no Firestore.
 * Os nomes das propriedades correspondem EXATAMENTE aos campos no seu banco de dados.
 */
data class Manutencao(
    // @DocumentId diz ao Firestore para preencher esta variável com o ID do documento
    // automaticamente. Isso é ESSENCIAL para editar e excluir.
    @DocumentId
    val id: String? = null,

    // Seus campos, exatamente como no Firestore
    val descricao: String = "",
    val duracao: String = "",
    val solucao: String = "",
    val tecnico: String = "",

    // @ServerTimestamp diz ao Firestore para preencher a data no servidor no momento da
    // criação, garantindo que a hora esteja sempre correta.
    @ServerTimestamp
    val data: Date? = null
)
