package com.example.appmanutencao.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.appmanutencao.data.AppDatabase
import com.example.appmanutencao.data.Manutencao
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import com.google.firebase.firestore.FirebaseFirestore
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.appmanutencao.data.Documento



class ManutencaoViewModel(application: Application) : AndroidViewModel(application) {

    private val dao = AppDatabase.getDatabase(application).manutencaoDao()
    private val db = FirebaseFirestore.getInstance()
    private val _modelo3DUrl = MutableLiveData<String>()
    private val _documentos = MutableLiveData<List<Documento>>()
    val modelo3DUrl: LiveData<String> = _modelo3DUrl
    val documentos: LiveData<List<Documento>> = _documentos

    val manutencoes = dao.buscarTodas()
        .stateIn(
            viewModelScope,
            SharingStarted.Lazily,
            emptyList()
        )

    fun inserir(manutencao: Manutencao) {
        viewModelScope.launch {
            dao.inserir(manutencao)
        }
    }


    fun buscarDocumentos(numeroSerie: String) {
        db.collection("documentos")
            .whereEqualTo("numeroSerie", numeroSerie)
            .get()
            .addOnSuccessListener { result ->
                val lista = result.map { doc ->
                    doc.toObject(Documento::class.java)
                }
                _documentos.postValue(lista)
            }
            .addOnFailureListener { e ->
                Log.e("Firebase", "Erro ao buscar documentos", e)
                _documentos.postValue(emptyList())
            }
    }

    fun buscarModelo3D(numeroSerie: String) {
        db.collection("modelos3D")
            .whereEqualTo("numeroSerie", numeroSerie)
            .get()
            .addOnSuccessListener { result ->
                val url = result.firstOrNull()?.getString("url") ?: ""
                _modelo3DUrl.postValue(url)

            }
            .addOnFailureListener { e ->
                Log.e("Firebase", "Erro ao buscar modelo 3D", e)
                _modelo3DUrl.postValue("")
            }
    }


}
