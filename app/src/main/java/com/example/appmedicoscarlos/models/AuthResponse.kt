package com.example.appmedicoscarlos.models

import com.google.gson.annotations.SerializedName

data class AuthResponse(
    @SerializedName("token") val token: String,
    @SerializedName("username") val username: String,
    @SerializedName("roles") val roles: List<String>,
    @SerializedName("userId") val userId: Long  // ← Nuevo campo
)