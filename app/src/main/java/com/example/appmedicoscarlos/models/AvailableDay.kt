package com.example.appmedicoscarlos.models

data class AvailableDay(
    val date: String,          // "2025-11-18"
    val slots: List<String>    // ["09:00", "09:30"]
)