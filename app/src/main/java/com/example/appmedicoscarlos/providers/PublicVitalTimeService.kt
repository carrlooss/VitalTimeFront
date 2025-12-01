package com.example.appmedicoscarlos.providers

import com.example.appmedicoscarlos.models.AuthResponse
import com.example.appmedicoscarlos.models.DoctorCreate
import com.example.appmedicoscarlos.models.DoctorResponse
import com.example.appmedicoscarlos.models.LoginRequest
import com.example.appmedicoscarlos.models.RegisterRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface PublicVitalTimeService {
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): AuthResponse

    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): AuthResponse
}