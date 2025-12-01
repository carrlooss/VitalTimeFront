package com.example.appmedicoscarlos.models

data class DoctorUpdateRequest(
    val email: String? = null,
    val firstName: String? = null,
    val lastName: String? = null,
    val phone: String? = null,
    val licenseNumber: String? = null,
    val specialtyId: Long? = null,
    val officeAddress: String? = null,
    val bio: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null
)