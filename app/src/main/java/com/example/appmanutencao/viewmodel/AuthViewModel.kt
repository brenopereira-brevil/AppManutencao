package com.example.appmanutencao.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore

class AuthViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()

    // Estado de autenticação (sucesso ou falha)
    private val _authSuccess = MutableLiveData<Boolean>()
    val authSuccess: LiveData<Boolean> get() = _authSuccess

    // Armazena o número de série do equipamento autenticado
    private val _numeroSerie = MutableLiveData<String?>()
    val numeroSerie: LiveData<String?> get() = _numeroSerie

    /**
     * Realiza o login com número de série e senha.
     * Se autenticado, salva o número de série e emite sucesso.
     */
    fun login(numeroSerie: String, senha: String) {
        db.collection("usuarios")
            .whereEqualTo("numeroSerie", numeroSerie)
            .get()
            .addOnSuccessListener { result ->
                val user = result.firstOrNull()
                if (user != null && user.getString("senha") == senha) {
                    Log.d("AuthViewModel", "Login bem-sucedido para $numeroSerie")
                    _numeroSerie.postValue(numeroSerie)
                    _authSuccess.postValue(true)
                } else {
                    Log.w("AuthViewModel", "Login falhou: usuário não encontrado ou senha incorreta")
                    _authSuccess.postValue(false)
                }
            }
            .addOnFailureListener { e ->
                Log.e("AuthViewModel", "Erro ao tentar autenticar", e)
                _authSuccess.postValue(false)
            }
    }

    /**
     * Opcional: método para limpar o login (logout)
     */
    fun logout() {
        _numeroSerie.postValue(null)
        _authSuccess.postValue(false)
    }
}
