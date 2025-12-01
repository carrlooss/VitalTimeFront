package com.example.appmedicoscarlos.models


data class PatientUpdateRequest(
    val email: String? = null,
    val firstName: String? = null,
    val lastName: String? = null,
    val phone: String? = null,
    val dateOfBirth: String? = null,
    val gender: String? = null,
    val contact: String? = null
    )