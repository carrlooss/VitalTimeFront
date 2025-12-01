package com.example.appmedicoscarlos.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.appmedicoscarlos.R
import com.example.appmedicoscarlos.models.PatientResponseDto
import com.google.android.material.button.MaterialButton
import com.google.android.material.textview.MaterialTextView

class PacienteAdapter(
    pacientesList: MutableList<PatientResponseDto>,
    private val onDeleteClick: (PatientResponseDto) -> Unit,
    private val onEditClick: (PatientResponseDto) -> Unit,
    private val onViewAppointmentsClick: (PatientResponseDto) -> Unit
) : RecyclerView.Adapter<PacienteAdapter.PacienteViewHolder>() {

    // Lista completa original
    private var originalList = pacientesList.toMutableList()
    // Lista visible (puede estar filtrada)
    private var workingList = pacientesList.toMutableList()

    inner class PacienteViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvNombre = view.findViewById<MaterialTextView>(R.id.tvNombrePaciente)
        val tvEmail = view.findViewById<MaterialTextView>(R.id.tvEmailPaciente)
        val tvTelefono = view.findViewById<MaterialTextView>(R.id.tvTelefonoPaciente)
        val tvFechaNacimiento = view.findViewById<MaterialTextView>(R.id.tvFechaNacimientoPaciente)
        val tvGenero = view.findViewById<MaterialTextView>(R.id.tvGeneroPaciente)
        val tvContactoEmergencia = view.findViewById<MaterialTextView>(R.id.tvContactoEmergenciaPaciente)
        val btnEliminar = view.findViewById<MaterialButton>(R.id.btnEliminarPaciente)
        val btnEditarPaciente = view.findViewById<MaterialButton>(R.id.btnEditarPaciente)
        val btnVerCitas = view.findViewById<MaterialButton>(R.id.btnVerCitasPaciente)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PacienteViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_paciente, parent, false)
        return PacienteViewHolder(view)
    }

    override fun onBindViewHolder(holder: PacienteViewHolder, position: Int) {
        val paciente = workingList[position]
        holder.tvNombre.text = "${paciente.firstName} ${paciente.lastName}"
        holder.tvEmail.text = paciente.email ?: ""
        holder.tvTelefono.text = "Tel: ${paciente.phone ?: "-"}"
        holder.tvFechaNacimiento.text = "Nacimiento: ${paciente.dateOfBirth ?: "-"}"
        holder.tvGenero.text = "Género: ${paciente.gender ?: "-"}"
        holder.tvContactoEmergencia.text = "Emergencia: ${paciente.contact ?: "-"}"

        holder.btnEliminar.setOnClickListener { onDeleteClick(paciente) }
        holder.btnEditarPaciente.setOnClickListener { onEditClick(paciente) }
        holder.btnVerCitas.setOnClickListener { onViewAppointmentsClick(paciente) }
    }

    override fun getItemCount(): Int = workingList.size

    // Actualizar lista completa (por ejemplo, al cargar de servidor)
    fun updateList(newList: List<PatientResponseDto>) {
        originalList = newList.toMutableList()
        workingList = newList.toMutableList()
        notifyDataSetChanged()
    }

    // Eliminar paciente
    fun removePaciente(paciente: PatientResponseDto) {
        val indexOriginal = originalList.indexOfFirst { it.id == paciente.id }
        if (indexOriginal != -1) originalList.removeAt(indexOriginal)

        val indexWorking = workingList.indexOfFirst { it.id == paciente.id }
        if (indexWorking != -1) {
            workingList.removeAt(indexWorking)
            notifyItemRemoved(indexWorking)
        }
    }

    // Filtrar pacientes
    fun filter(query: String) {
        workingList = if (query.isBlank()) {
            originalList.toMutableList()
        } else {
            originalList.filter {
                val nombreCompleto = "${it.firstName} ${it.lastName}".lowercase()
                nombreCompleto.contains(query.lowercase()) ||
                        (it.email?.lowercase()?.contains(query.lowercase()) ?: false)
            }.toMutableList()
        }
        notifyDataSetChanged()
    }

    // Actualizar paciente editado sin recargar todo
    fun updatePaciente(updatedPaciente: PatientResponseDto) {
        // Actualizar lista original
        val indexOriginal = originalList.indexOfFirst { it.id == updatedPaciente.id }
        if (indexOriginal != -1) {
            originalList[indexOriginal] = updatedPaciente
        }

        // Actualizar lista visible
        val indexWorking = workingList.indexOfFirst { it.id == updatedPaciente.id }
        if (indexWorking != -1) {
            workingList[indexWorking] = updatedPaciente
            notifyItemChanged(indexWorking)
        }
    }
}
