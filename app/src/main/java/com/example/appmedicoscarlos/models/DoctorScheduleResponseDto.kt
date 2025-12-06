package com.example.appmedicoscarlos.models

data class DoctorScheduleResponseDto(
    val id: Long,
    val doctorId: Long,
    val dayOfWeek: Int,
    val startTime: String,
    val endTime: String
)