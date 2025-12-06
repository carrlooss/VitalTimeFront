package com.example.appmedicoscarlos.utils

import com.example.appmedicoscarlos.models.ErrorResponse
import com.google.gson.Gson

object ErrorUtils {

    /**
     * Parsea un JSON de error proveniente de Retrofit (errorBody().string())
     * y devuelve un mensaje legible para el usuario.
     *
     * Soporta estructuras:
     * {
     *    "field": "mensaje"
     * }
     *
     * o incluso:
     * {
     *    "errors": {
     *       "password": "La contraseña es obligatoria"
     *    }
     * }
     */
    fun parseErrorMessage(errorJson: String?): String {
        if (errorJson.isNullOrBlank()) return "Error desconocido"

        return try {
            val gson = Gson()

            val errorResponse: ErrorResponse = Gson().fromJson(errorJson, ErrorResponse::class.java)
            val message: String = errorResponse.message?: "No se ha podido recuperar el error"
            return message

        } catch (e: Exception) {
            "Error desconocido"
        }
    }
}
