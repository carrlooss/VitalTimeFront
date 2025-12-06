    package com.example.appmedicoscarlos.models

    import java.io.Serializable

    data class DoctorResponse(
        // Campos de UserCreateBase
        val id: Long?,
        val userId: Long?,
        val username: String?,
        val email: String?,
        val password: String?,
        val firstName: String?,
        val lastName: String?,
        val phone: String? = null,

        // Campos propios del doctor
        val licenseNumber: String? = null,
        val specialtyId: Long?,
        val specialtyName: String?,
        val officeAddress: String? = null,
        val bio: String? = null,
        val latitude: Double? = null,
        val longitude: Double? = null
    ): Serializable
