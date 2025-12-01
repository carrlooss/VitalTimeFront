package com.example.appmedicoscarlos.repository

import com.example.appmedicoscarlos.models.AuthResponse
import com.example.appmedicoscarlos.models.LoginRequest
import com.example.appmedicoscarlos.models.RegisterRequest
import com.example.appmedicoscarlos.providers.PublicVitalTimeService
import com.example.appmedicoscarlos.utils.ErrorUtils

class AuthRepository(private val publicVitalTimeService: PublicVitalTimeService) {

    suspend fun login(username: String, password: String): Result<AuthResponse> {
        return try {
            val response = publicVitalTimeService.login(LoginRequest(username, password))
            Result.success(response)
        } catch (e: retrofit2.HttpException) {
            val errorJson = e.response()?.errorBody()?.string()
            val errorMessage = ErrorUtils.parseErrorMessage(errorJson)
            Result.failure(Exception(errorMessage))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun register(username: String, password: String, email: String, firstName: String, lastName: String): Result<AuthResponse> {
        return try {
            val response = publicVitalTimeService.register(RegisterRequest(username,  password, email, firstName, lastName))
            Result.success(response)
        } catch (e: retrofit2.HttpException) {
            val errorJson = e.response()?.errorBody()?.string()
            val errorMessage = ErrorUtils.parseErrorMessage(errorJson)
            Result.failure(Exception(errorMessage))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}