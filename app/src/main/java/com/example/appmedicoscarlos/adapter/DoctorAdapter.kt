package com.example.appmedicoscarlos.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.appmedicoscarlos.R
import com.example.appmedicoscarlos.models.DoctorResponse
import com.google.android.material.button.MaterialButton
import com.google.android.material.textview.MaterialTextView

class DoctorAdapter(
    doctorList: MutableList<DoctorResponse>,
    private val onDeleteClick: (DoctorResponse) -> Unit,
    private val onEditClick: (DoctorResponse) -> Unit,
    private val onViewAppointmentsClick: (DoctorResponse) -> Unit
) : RecyclerView.Adapter<DoctorAdapter.DoctorViewHolder>() {

    // 🔥 Lista original (sin filtros)
    private var originalList = doctorList.toMutableList()

    // 🔥 Lista que se muestra (con filtros)
    private var workingList = doctorList.toMutableList()

    inner class DoctorViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtNombre: MaterialTextView = view.findViewById(R.id.txtNombreDoctor)
        val txtEspecialidad: MaterialTextView = view.findViewById(R.id.txtEspecialidad)
        val txtTelefono: MaterialTextView = view.findViewById(R.id.txtTelefono)
        val txtEmail: MaterialTextView = view.findViewById(R.id.txtEmail)
        val txtLicense: MaterialTextView = view.findViewById(R.id.txtLicense)
        val txtDireccion: MaterialTextView = view.findViewById(R.id.txtDireccion)

        val btnVerCitas: MaterialButton = view.findViewById(R.id.btnVerCitas)
        val btnEditar: MaterialButton = view.findViewById(R.id.btnEditar)
        val btnEliminar: MaterialButton = view.findViewById(R.id.btnEliminar)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DoctorViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_doctor, parent, false)
        return DoctorViewHolder(view)
    }

    override fun onBindViewHolder(holder: DoctorViewHolder, position: Int) {
        val doc = workingList[position]

        holder.txtNombre.text = "${doc.firstName ?: ""} ${doc.lastName ?: ""}"
        holder.txtEspecialidad.text = "Especialidad ID: ${doc.specialtyId ?: "-"}"
        holder.txtTelefono.text = "Teléfono: ${doc.phone ?: "-"}"
        holder.txtEmail.text = doc.email ?: "-"
        holder.txtLicense.text = "Licencia: ${doc.licenseNumber ?: "-"}"
        holder.txtDireccion.text = "Dirección: ${doc.officeAddress ?: "-"}"

        holder.btnEliminar.setOnClickListener { onDeleteClick(doc) }
        holder.btnEditar.setOnClickListener { onEditClick(doc) }
        holder.btnVerCitas.setOnClickListener { onViewAppointmentsClick(doc) }
    }

    override fun getItemCount(): Int = workingList.size

    // 🔹 Actualizar lista completa desde servidor
    fun updateList(newList: List<DoctorResponse>) {
        originalList = newList.toMutableList()
        workingList = newList.toMutableList()
        notifyDataSetChanged()
    }

    // 🔹 Eliminar doctor
    fun removeDoctor(doctor: DoctorResponse) {
        val indexOriginal = originalList.indexOfFirst { it.id == doctor.id }
        if (indexOriginal != -1) originalList.removeAt(indexOriginal)

        val indexWorking = workingList.indexOfFirst { it.id == doctor.id }
        if (indexWorking != -1) {
            workingList.removeAt(indexWorking)
            notifyItemRemoved(indexWorking)
        }
    }

    // 🔥 FILTRO
    fun filter(query: String) {
        workingList = if (query.isBlank()) {
            originalList.toMutableList()
        } else {
            originalList.filter {
                val name = "${it.firstName} ${it.lastName}".lowercase()
                name.contains(query.lowercase()) ||
                        (it.email?.lowercase()?.contains(query.lowercase()) ?: false)
            }.toMutableList()
        }
        notifyDataSetChanged()
    }

    // 🔥 Actualizar doctor editado SIN recargar la pantalla
    fun updateDoctor(updatedDoctor: DoctorResponse) {

        // Actualizar lista original
        val indexOriginal = originalList.indexOfFirst { it.id == updatedDoctor.id }
        if (indexOriginal != -1) {
            originalList[indexOriginal] = updatedDoctor
        }

        // Actualizar lista visible
        val indexWorking = workingList.indexOfFirst { it.id == updatedDoctor.id }
        if (indexWorking != -1) {
            workingList[indexWorking] = updatedDoctor
            notifyItemChanged(indexWorking)
        }
    }
}
