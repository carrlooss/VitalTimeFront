package com.example.appmedicoscarlos.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class PatientResponseDto(
    @SerializedName("id") val id: Long? = null,
    @SerializedName("userId") val userId: Long? = null,
    @SerializedName("username") val username: String? = null,
    @SerializedName("email") val email: String? = null,
    @SerializedName("firstName") val firstName: String? = null,
    @SerializedName("lastName") val lastName: String? = null,
    @SerializedName("phone") val phone: String? = null,
    @SerializedName("dateOfBirth") val dateOfBirth: String? = null, // "yyyy-MM-dd"
    @SerializedName("gender") val gender: String? = null,
    @SerializedName("contact") val contact: String? = null
): Serializable