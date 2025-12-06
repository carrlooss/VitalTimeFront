package com.example.appmedicoscarlos.models

import com.google.gson.annotations.SerializedName
import java.time.LocalDate

data class PatientCreateRequest(
    @SerializedName("username") val username: String,
    @SerializedName("password") val password: String,
    @SerializedName("email") val email: String,
    @SerializedName("firstName") val firstName: String,
    @SerializedName("lastName") val lastName: String,
    @SerializedName("phone") val phone: String,
    @SerializedName("dateOfBirth") val dateOfBirth: String,
    @SerializedName("gender") val gender: String,
    @SerializedName("contact") val contact: String
)