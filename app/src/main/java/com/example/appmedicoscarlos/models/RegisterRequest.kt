package com.example.appmedicoscarlos.models

data class RegisterRequest(
    val username: String,
    val password: String,
    val email: String,
    val firstName: String,
    val lastName: String
)