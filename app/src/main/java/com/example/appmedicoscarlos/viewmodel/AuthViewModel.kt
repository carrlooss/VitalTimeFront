package com.example.appmedicoscarlos.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appmedicoscarlos.models.AuthResponse
import com.example.appmedicoscarlos.repository.AuthRepository
import com.example.appmedicoscarlos.utils.TokenManager
import kotlinx.coroutines.launch

class AuthViewModel(private val repository: AuthRepository, private val tokenManager: TokenManager) : ViewModel() {

    private val _loginState = MutableLiveData<LoginState>()
    val loginState: LiveData<LoginState> = _loginState

    private val _registerState = MutableLiveData<RegisterState>()
    val registerState: LiveData<RegisterState> = _registerState

    fun login(username: String, password: String) {
        viewModelScope.launch {
            _loginState.value = LoginState.Loading
            val result = repository.login(username, password)
            result.onSuccess { authResponse ->
                tokenManager.saveToken(authResponse.token)
                tokenManager.saveUsername(username)
                tokenManager.saveUserId(authResponse.userId)
                if (authResponse.roles.joinToString(", ") == "MEDICO")
                    tokenManager.saveRol("ADMIN")
                else
                    tokenManager.saveRol(authResponse.roles.joinToString(", "))
                _loginState.value = LoginState.Success(authResponse)
            }.onFailure { error ->
                _loginState.value = LoginState.Error(error.message ?: "Error desconocido")
            }
        }
    }

    fun register(username: String, password: String, email: String, firstName: String, lastName: String) {
        viewModelScope.launch {
            _registerState.value = RegisterState.Loading
            val result = repository.register(username, password, email, firstName, lastName)
            result.onSuccess { authResponse ->
                tokenManager.saveToken(authResponse.token)
                tokenManager.saveUsername(username)
                tokenManager.saveUserId(authResponse.userId)
                tokenManager.saveRol(authResponse.roles.joinToString(", "))
                _registerState.value = RegisterState.Success(authResponse)
            }.onFailure { error ->
                _registerState.value = RegisterState.Error(error.message ?: "Error desconocido")
            }
        }
    }

    fun logout() {
        tokenManager.clearAll()
    }

    fun isLoggedIn(): Boolean = tokenManager.isLoggedIn()

    fun getAuthToken(): String? = tokenManager.getToken()
}

// Estados de Login
sealed class LoginState {
    object Loading : LoginState()
    data class Success(val response: AuthResponse) : LoginState()
    data class Error(val message: String) : LoginState()
}

// Estados de Registro
sealed class RegisterState {
    object Loading : RegisterState()
    data class Success(val response: AuthResponse) : RegisterState()
    data class Error(val message: String) : RegisterState()
}