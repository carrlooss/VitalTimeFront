package com.example.appmedicoscarlos.models

data class DiaHorario(
    val dayOfWeek: Int,
    var enabled: Boolean = false,
    var startTime: String = "--:--",
    var endTime: String = "--:--"
)