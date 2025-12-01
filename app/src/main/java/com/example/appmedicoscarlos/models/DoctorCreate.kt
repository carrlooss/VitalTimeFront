package com.example.appmedicoscarlos.models

data class DoctorCreate(
    // Campos de UserCreateBase
    val username: String,
    val email: String,
    val password: String,
    val firstName: String,
    val lastName: String,
    val phone: String? = null,

    // Campos propios del doctor
    val licenseNumber: String? = null,
    val specialtyId: Int,         // obligatorio → no nullable
    val officeAddress: String? = null,
    val bio: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null
)
