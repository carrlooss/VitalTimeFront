package com.example.appmedicoscarlos.utils

import android.content.Context

class TokenManager(private val context: Context) {
    private val prefs = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)

    // === TOKEN ===
    fun saveToken(token: String) {
        prefs.edit().putString("jwt_token", token).apply()
    }

    fun getToken(): String? {
        return prefs.getString("jwt_token", null)
    }

    // === USERNAME ===
    fun saveUsername(username: String) {
        prefs.edit().putString("username", username).apply()
    }

    fun getUsername(): String? {
        return prefs.getString("username", null)
    }

    // === ROL ===
    fun saveRol(rol: String) {
        prefs.edit().putString("rol", rol).apply()
    }

    fun getRol(): String? {
        return prefs.getString("rol", null)
    }

    // === USER ID (nuevo) ===
    fun saveUserId(userId: Long) {
        prefs.edit().putLong("user_id", userId).apply()
    }

    fun getUserId(): Long? {
        val id = prefs.getLong("user_id", -1L)
        return if (id == -1L) null else id
    }

    // === LIMPIEZA COMPLETA ===
    fun clearAll() {
        prefs.edit().clear().apply()
    }

    // === UTILIDADES ===
    fun isLoggedIn(): Boolean {
        return getToken() != null
    }

    fun getAuthHeader(): String? {
        return getToken()?.let { "Bearer $it" }
    }
}