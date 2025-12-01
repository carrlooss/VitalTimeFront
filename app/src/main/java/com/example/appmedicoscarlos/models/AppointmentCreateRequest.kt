package com.example.appmedicoscarlos.models

import com.google.gson.annotations.SerializedName

data class AppointmentCreateRequest(
    @SerializedName("patientId") val patientId: Long,
    @SerializedName("doctorId") val doctorId: Long,
    @SerializedName("dateTime") val dateTime: String,
    @SerializedName("notes") val notes: String? = null
)