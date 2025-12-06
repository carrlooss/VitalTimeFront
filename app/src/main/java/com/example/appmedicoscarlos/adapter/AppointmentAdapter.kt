package com.example.appmedicoscarlos.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.appmedicoscarlos.R
import com.example.appmedicoscarlos.models.AppointmentResponseDto
import java.text.SimpleDateFormat
import java.util.*
import com.google.android.material.button.MaterialButton

class AppointmentAdapter : ListAdapter<AppointmentResponseDto, AppointmentAdapter.ViewHolder>(AppointmentDiffCallback()) {

    var onCancelClick: ((Long) -> Unit)? = null
    var onLocationClick: ((Double, Double, String?, String?) -> Unit)? = null

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvDay: TextView = view.findViewById(R.id.tvDay)
        val tvMonth: TextView = view.findViewById(R.id.tvMonth)
        val tvSpecialty: TextView = view.findViewById(R.id.tvSpecialty)
        val tvDoctor: TextView = view.findViewById(R.id.tvDoctor)
        val tvTime: TextView = view.findViewById(R.id.tvTime)
        val btnCancel: Button = view.findViewById(R.id.btnCancel)
        val btnLocation: MaterialButton = view.findViewById(R.id.btnShowLocation)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_appointment, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val appointment = getItem(position)

        // Formatear fecha: soporta múltiples formatos
        val displayInfo = formatDate(appointment.dateTime ?: "")
        holder.tvDay.text = displayInfo.day
        holder.tvMonth.text = displayInfo.month

        // Valores temporales
        holder.tvSpecialty.text = "Medicina General"
        holder.tvDoctor.text = "Dr. [Médico]"
        holder.tvTime.text = displayInfo.time

        // Botón Cancelar
        holder.btnCancel.setOnClickListener {
            onCancelClick?.invoke(appointment.id)
        }

        holder.btnLocation.setOnClickListener {
            appointment.latitude?.let { lat ->
                appointment.longitude?.let { lng ->
                    onLocationClick?.invoke(lat, lng, null, null)
                }
            }
        }
    }

    private fun formatDate(dateTime: String): DisplayDate {
        if (dateTime.isBlank()) return DisplayDate("??", "???", "??:??")

        val formats = arrayOf(
            "yyyy-MM-dd'T'HH:mm:ss.SSSX",
            "yyyy-MM-dd'T'HH:mm:ss.SSS",
            "yyyy-MM-dd'T'HH:mm:ssX",
            "yyyy-MM-dd'T'HH:mm:ss"
        )

        for (pattern in formats) {
            try {
                val inputFormat = SimpleDateFormat(pattern, Locale.getDefault())
                if (pattern.contains("X")) inputFormat.timeZone = TimeZone.getTimeZone("UTC")
                val date = inputFormat.parse(dateTime) ?: continue

                val day = SimpleDateFormat("dd", Locale.getDefault()).format(date)
                val month = SimpleDateFormat("MMM", Locale.getDefault()).format(date).uppercase(Locale.getDefault())
                val time = SimpleDateFormat("HH:mm", Locale.getDefault()).format(date)
                return DisplayDate(day, month, time)
            } catch (e: Exception) { }
        }

        return DisplayDate("??", "???", "??:??")
    }

    data class DisplayDate(val day: String, val month: String, val time: String)
}

class AppointmentDiffCallback : DiffUtil.ItemCallback<AppointmentResponseDto>() {
    override fun areItemsTheSame(oldItem: AppointmentResponseDto, newItem: AppointmentResponseDto): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: AppointmentResponseDto, newItem: AppointmentResponseDto): Boolean {
        return oldItem == newItem
    }
}