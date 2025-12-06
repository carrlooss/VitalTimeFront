package com.example.appmedicoscarlos.models

import com.google.gson.annotations.SerializedName
import java.time.LocalDateTime

data class ErrorResponse(
    @SerializedName("timestamp") val timestamp: String?,
    @SerializedName("status") val status: Int,
    @SerializedName("error") val error: String?,
    @SerializedName("message") val message: String?
)
