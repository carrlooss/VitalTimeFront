package com.example.appmedicoscarlos.models

import com.google.gson.annotations.SerializedName

data class AppointmentResponseDto(
    @SerializedName("id") val id: Long,
    @SerializedName("doctorId") val doctorId: Long? = null,
    @SerializedName("dateTime") val dateTime: String? = null,
    @SerializedName("durationMinutes") val durationMinutes: Int? = null,
    @SerializedName("status") val status: String? = null,
    @SerializedName("reason") val reason: String? = null,
    @SerializedName("notes") val notes: String? = null,
    @SerializedName("createdAt") val createdAt: String? = null,
    @SerializedName("latitude") val latitude: Double? = null,
    @SerializedName("longitude") val longitude: Double? = null
    )