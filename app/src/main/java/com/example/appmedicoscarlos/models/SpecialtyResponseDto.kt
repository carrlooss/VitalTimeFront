package com.example.appmedicoscarlos.models

import com.google.gson.annotations.SerializedName

data class SpecialtyResponseDto(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("description") val description: String? = null,
    )