package com.example.appmedicoscarlos.utils

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

            // Intenta mapear directamente un Map simple
            val simpleMap: Map<String, Any> =
                gson.fromJson(errorJson, Map::class.java) as Map<String, Any>

            // Si contiene un campo "errors", lo procesamos
            if (simpleMap.containsKey("errors")) {
                val errors = simpleMap["errors"] as Map<*, *>
                return errors.values.joinToString("; ") { it.toString() }
            }

            // Si el JSON es un map plano tipo { "password": "mensaje" }
            simpleMap.values.joinToString("; ") { it.toString() }

        } catch (e: Exception) {
            "Error desconocido"
        }
    }
}
