package com.example.appmedicoscarlos.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.appmedicoscarlos.repository.AuthRepository
import com.example.appmedicoscarlos.utils.TokenManager

class AuthViewModelFactory(
    private val repository: AuthRepository,
    private val tokenManager: TokenManager
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AuthViewModel(repository, tokenManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}