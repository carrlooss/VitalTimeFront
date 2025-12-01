package com.example.appmedicoscarlos.models

data class AvailabilityResponse(
    val doctorId: Long,
    val availableDays: List<AvailableDay>
)