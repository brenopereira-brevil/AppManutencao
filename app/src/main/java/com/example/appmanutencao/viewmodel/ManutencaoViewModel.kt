package com.example.appmanutencao.viewmodel

import androidx.lifecycle.ViewModel
//import com.example.appmanutencao.data.Documento
import com.example.appmanutencao.model.Documento
import com.example.appmanutencao.model.Manutencao // Importe o modelo atualizado
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class ManutencaoViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()

    // StateFlow para expor a lista de manutenções para a UI.
    // A UI irá "observar" este fluxo e se atualizar automaticamente.
    private val _historicoState = MutableStateFlow<List<Manutencao>>(emptyList())
    val historicoState = _historicoState.asStateFlow()

    // StateFlow para expor uma única manutenção (usado na tela de detalhes/edição)
    private val _manutencaoSelecionada = MutableStateFlow<Manutencao?>(null)
    val manutencaoSelecionada = _manutencaoSelecionada.asStateFlow()

    private val _navigateTo3D = MutableStateFlow<Result<String>?>(null)
    val navigateTo3D = _navigateTo3D.asStateFlow()

    private val _documentosState = MutableStateFlow<List<Documento>>(emptyList())
    val documentosState = _documentosState.asStateFlow()

    /**
     * Inicia um "ouvinte" em tempo real para o histórico de manutenções
     * de um equipamento específico.
     */
    fun carregarHistorico(numeroSerie: String) {
        if (numeroSerie.isBlank()) return

        val historicoRef = db.collection("equipamentos")
            .document(numeroSerie)
            .collection("historico_manutencoes")
            .orderBy("data", Query.Direction.DESCENDING) // Mais novos primeiro

        historicoRef.addSnapshotListener { snapshot, error ->
            if (error != null) {
                // Idealmente, trate o erro aqui (ex: expondo um estado de erro para a UI)
                println("Erro ao carregar histórico: ${error.message}")
                return@addSnapshotListener
            }

            if (snapshot != null) {
                // Converte os documentos do Firestore para uma lista de objetos Manutencao
                _historicoState.value = snapshot.toObjects(Manutencao::class.java)
            }
        }
    }

    /**
     * Carrega os dados de UMA manutenção específica para a tela de detalhes/edição.
     */
    fun carregarManutencaoPorId(numeroSerie: String, manutencaoId: String) {
        if (numeroSerie.isBlank() || manutencaoId.isBlank()) return

        db.collection("equipamentos").document(numeroSerie)
            .collection("historico_manutencoes").document(manutencaoId)
            .get()
            .addOnSuccessListener { document ->
                _manutencaoSelecionada.value = document.toObject(Manutencao::class.java)
            }
            .addOnFailureListener {
                _manutencaoSelecionada.value = null
            }
    }

    /**
     * Salva uma nova manutenção ou atualiza uma existente.
     */
    fun salvarManutencao(
        numeroSerie: String,
        manutencao: Manutencao,
        onComplete: (Boolean) -> Unit
    ) {
        if (numeroSerie.isBlank()) {
            onComplete(false)
            return
        }

        val historicoRef = db.collection("equipamentos")
            .document(numeroSerie)
            .collection("historico_manutencoes")

        // Se a manutenção já tem um ID, é uma edição. Se não, é uma adição.
        val task = if (manutencao.id.isNullOrBlank()) {
            // ADIÇÃO: Cria um novo documento
            historicoRef.add(manutencao)
        } else {
            // EDIÇÃO: Atualiza o documento existente
            historicoRef.document(manutencao.id).set(manutencao)
        }

        task.addOnSuccessListener { onComplete(true) }
            .addOnFailureListener {
                it.printStackTrace()
                onComplete(false)
            }
    }

    /**
     * Exclui uma manutenção do Firestore.
     */
    fun excluirManutencao(
        numeroSerie: String,
        manutencaoId: String,
        onComplete: (Boolean) -> Unit
    ) {
        if (numeroSerie.isBlank() || manutencaoId.isBlank()) {
            onComplete(false)
            return
        }

        db.collection("equipamentos").document(numeroSerie)
            .collection("historico_manutencoes").document(manutencaoId)
            .delete()
            .addOnSuccessListener { onComplete(true) }
            .addOnFailureListener { onComplete(false) }
    }

    fun onBotao3dClicado(numeroSerie: String) {
        if (numeroSerie.isBlank()) {
            _navigateTo3D.value = Result.failure(Exception("Número de série inválido."))
            return
        }

        val db = FirebaseFirestore.getInstance()
        db.collection("modelos3D")
            .whereEqualTo("numeroSerie", numeroSerie)
            .limit(1)
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (querySnapshot != null && !querySnapshot.isEmpty) {
                    val document = querySnapshot.documents[0]
                    val url = document.getString("url")
                    if (!url.isNullOrBlank()) {
                        // SUCESSO: Atualiza o StateFlow com a URL
                        _navigateTo3D.value = Result.success(url)
                    } else {
                        _navigateTo3D.value = Result.failure(Exception("O campo 'url' está vazio no banco de dados."))
                    }
                } else {
                    _navigateTo3D.value = Result.failure(Exception("Nenhum modelo 3D encontrado para o número de série: $numeroSerie"))
                }
            }
            .addOnFailureListener { exception ->
                _navigateTo3D.value = Result.failure(exception)
            }
    }

    fun onNavegacao3DCompleta() {
        _navigateTo3D.value = null
    }

    fun carregarDocumentos(numeroSerie: String) {
        if (numeroSerie.isBlank()) return

        db.collection("equipamentos")
            .document(numeroSerie)
            .collection("documentos")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    println("Erro ao carregar documentos: ${error.message}")
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    _documentosState.value = snapshot.toObjects(Documento::class.java)
                }
            }
    }

}
