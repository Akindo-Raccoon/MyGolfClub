package com.ud.mygolfclub.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.ud.mygolfclub.data.db.GolfDB
import com.ud.mygolfclub.data.entity.Client
import com.ud.mygolfclub.data.repository.ClientRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Success(val clientId: String, val name: String, val phone: String) : AuthState()
    data class Error(val message: String) : AuthState()
}

class LoginVM(application: Application) : AndroidViewModel(application) {

    private val db = GolfDB.getDB(application)
    private val repo = ClientRepository(db.ClientDAO())

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState

    fun login(id: String, pwd: String) {
        if (id.isBlank() || pwd.isBlank()) {
            _authState.value = AuthState.Error("Completa todos los campos")
            return
        }
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val client = repo.login(id.trim(), pwd.trim())
            if (client != null) {
                _authState.value = AuthState.Success(client.idClient, client.name, client.phoneClient)
            } else {
                _authState.value = AuthState.Error("Cédula o contraseña incorrectos")
            }
        }
    }
    fun register(id: String, name: String, phone: String, pwd: String) {
        if (id.isBlank() || name.isBlank() || phone.isBlank() || pwd.isBlank()) {
            _authState.value = AuthState.Error("Completa todos los campos")
            return
        }
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val result = repo.addClient(
                Client(idClient = id.trim(), name = name.trim(),
                    phoneClient = phone.trim(), pwdClient = pwd.trim())
            )
            result.fold(
                onSuccess = {
                    _authState.value = AuthState.Success(id.trim(), name.trim(), phone.trim())
                },
                onFailure = {
                    _authState.value = AuthState.Error(it.message ?: "Error al registrar")
                }
            )
        }
    }

    fun resetState() {
        _authState.value = AuthState.Idle
    }
}