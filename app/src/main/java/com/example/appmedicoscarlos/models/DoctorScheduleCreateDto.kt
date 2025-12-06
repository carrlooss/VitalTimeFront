package com.example.appmedicoscarlos.models

data class DoctorScheduleCreateDto(
    val doctorId: Long,
    val dayOfWeek: Int,
    val startTime: String,
    val endTime: String
)