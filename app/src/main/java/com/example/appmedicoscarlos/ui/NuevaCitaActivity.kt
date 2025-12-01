package com.example.appmedicoscarlos.ui

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.appmedicoscarlos.databinding.ActivityNewAppointmentBinding
import com.example.appmedicoscarlos.models.*
import com.example.appmedicoscarlos.providers.VitalTimeClient
import com.example.appmedicoscarlos.repository.AppointmentRepository
import com.example.appmedicoscarlos.repository.DoctorRepository
import com.example.appmedicoscarlos.repository.PatientRepository
import com.example.appmedicoscarlos.utils.TokenManager
import com.google.android.material.chip.Chip
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch
import java.time.LocalDate

class NuevaCitaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNewAppointmentBinding

    private var token: String? = null
    private var userRole: String = ""

    private var selectedDoctorId: Long? = null
    private var selectedPatientId: Long? = null
    private var doctorsList: List<DoctorResponse> = emptyList()
    private var patientList: List<PatientResponseDto> = emptyList()

    private var availability: AvailabilityResponse? = null
    private var selectedDate: String? = null
    private var selectedTime: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewAppointmentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        token = TokenManager(this).getToken()
        userRole = TokenManager(this).getRol()?: ""

        if (token == null) {
            Toast.makeText(this, "No autenticado", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Inicializar botón de ubicación como invisible
        binding.btnShowLocation.visibility = View.GONE

        setupListeners()
        loadDoctors()

        if (userRole == "PACIENTE") {
            binding.spinnerPatient.visibility = View.GONE
            selectedPatientId = TokenManager(this).getUserId()
        }
        else {
            loadPatients()
        }
    }

    // ======================================================
    // LISTENERS
    // ======================================================
    private fun setupListeners() {
        binding.etDate.setOnClickListener {
            mostrarSelectorDeFechas()
        }

        binding.btnConfirm.setOnClickListener {
            confirmarCita()
        }
    }

    // ======================================================
    // CARGA MÉDICOS
    // ======================================================
    private fun loadDoctors() {
        val service = VitalTimeClient(token!!).apiService
        val repo = DoctorRepository(service)

        lifecycleScope.launch {
            val result = repo.getAllDoctor()

            if (result.isSuccess) {
                doctorsList = result.getOrNull() ?: emptyList()
                val nombres = doctorsList.map { "${it.firstName} ${it.lastName}" }

                val adapter = ArrayAdapter(
                    this@NuevaCitaActivity,
                    android.R.layout.simple_spinner_item,
                    nombres
                )
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                binding.spinnerDoctor.adapter = adapter

                // ==========================
                // ON ITEM SELECTED LISTENER
                // ==========================
                binding.spinnerDoctor.onItemSelectedListener =
                    object : AdapterView.OnItemSelectedListener {
                        @RequiresApi(Build.VERSION_CODES.O)
                        override fun onItemSelected(
                            parent: AdapterView<*>,
                            view: View?,
                            position: Int,
                            id: Long
                        ) {
                            val doctor = doctorsList[position]
                            selectedDoctorId = doctor.id
                            cargarDisponibilidad()

                            // Mostrar botón de ubicación si doctor tiene coordenadas
                            if (doctor.latitude != null && doctor.longitude != null) {
                                binding.btnShowLocation.visibility = View.VISIBLE
                                binding.btnShowLocation.setOnClickListener {
                                    abrirMapa(
                                        doctor.latitude,
                                        doctor.longitude,
                                        doctor.firstName ?: "",
                                        doctor.lastName ?: ""
                                    )
                                }
                            } else {
                                binding.btnShowLocation.visibility = View.GONE
                            }
                        }

                        override fun onNothingSelected(parent: AdapterView<*>) {}
                    }

            } else {
                Toast.makeText(
                    this@NuevaCitaActivity,
                    "Error cargando médicos",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    // ======================================================
    // CARGA PACIENTES
    // ======================================================
    private fun loadPatients() {
        binding.spinnerPatient.visibility = View.VISIBLE

        val service = VitalTimeClient(token!!).apiService
        val repo = PatientRepository(service)

        lifecycleScope.launch {
            val result = repo.getAllPatients()

            if (result.isSuccess) {
                patientList = result.getOrNull() ?: emptyList()
                val nombres = patientList.map { "${it.firstName} ${it.lastName}" }

                val adapter = ArrayAdapter(
                    this@NuevaCitaActivity,
                    android.R.layout.simple_spinner_item,
                    nombres
                )
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                binding.spinnerPatient.adapter = adapter

                // ==========================
                // ON ITEM SELECTED LISTENER
                // ==========================
                binding.spinnerPatient.onItemSelectedListener =
                    object : AdapterView.OnItemSelectedListener {
                        @RequiresApi(Build.VERSION_CODES.O)
                        override fun onItemSelected(
                            parent: AdapterView<*>,
                            view: View?,
                            position: Int,
                            id: Long
                        ) {
                            val patient = patientList[position]
                            selectedPatientId = patient.id
                        }
                        override fun onNothingSelected(parent: AdapterView<*>) {}
                    }
            } else {
                Toast.makeText(
                    this@NuevaCitaActivity,
                    "Error cargando pacientes",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    // ======================================================
    // CARGAR DISPONIBILIDAD
    // ======================================================
    @RequiresApi(Build.VERSION_CODES.O)
    private fun cargarDisponibilidad() {
        val doctorId = selectedDoctorId ?: return
        val token = TokenManager(this).getToken()!!
        val service = VitalTimeClient(token).apiService

        val from = LocalDate.now().toString()
        val to = LocalDate.now().plusMonths(1).toString()

        lifecycleScope.launch {
            try {
                availability = service.getAvailability(doctorId, from, to)

                binding.etDate.setText("")
                binding.chipGroupHours.removeAllViews()

                selectedDate = null
                selectedTime = null

            } catch (e: Exception) {
                Toast.makeText(
                    this@NuevaCitaActivity,
                    "Error cargando disponibilidad",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    // ======================================================
    // SELECTOR DE FECHAS
    // ======================================================
    private fun mostrarSelectorDeFechas() {
        val dias = availability?.availableDays?.map { it.date } ?: emptyList()

        if (dias.isEmpty()) {
            Toast.makeText(
                this,
                "El médico no tiene días disponibles",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        MaterialAlertDialogBuilder(this)
            .setTitle("Selecciona una fecha")
            .setItems(dias.toTypedArray()) { _, index ->
                selectedDate = dias[index]
                binding.etDate.setText(selectedDate)

                selectedTime = null
                mostrarHorasDisponibles()
            }
            .show()
    }

    // ======================================================
    // MOSTRAR HORAS COMO CHIPS
    // ======================================================
    private fun mostrarHorasDisponibles() {
        val fecha = selectedDate ?: return

        val horas = availability?.availableDays
            ?.firstOrNull { it.date == fecha }
            ?.slots ?: emptyList()

        binding.chipGroupHours.removeAllViews()

        if (horas.isEmpty()) {
            Toast.makeText(this, "No hay horas ese día", Toast.LENGTH_SHORT).show()
            return
        }

        horas.forEach { hora ->
            val chip = Chip(this)
            chip.text = hora
            chip.isCheckable = true

            chip.setOnClickListener {
                selectedTime = hora
            }

            binding.chipGroupHours.addView(chip)
        }
    }

    // ======================================================
    // CONFIRMAR CITA
    // ======================================================
    private fun confirmarCita() {
        val doctorId = selectedDoctorId
        val date = selectedDate
        val time = selectedTime

        if (doctorId == null || date == null || time == null) {
            Toast.makeText(this, "Selecciona fecha y hora", Toast.LENGTH_SHORT).show()
            return
        }

        if (selectedPatientId == null) {
            Toast.makeText(this, "No se ha indicado el paciente", Toast.LENGTH_SHORT).show()
            return
        }

        val normalizedTime = if (time.count { it == ':' } == 1) {
            "$time:00"
        } else {
            time
        }

        val dateTimeIso = "${date}T${normalizedTime}Z"

        val request = AppointmentCreateRequest(
            patientId = selectedPatientId!!,
            doctorId = doctorId,
            dateTime = dateTimeIso,
            notes = binding.etNotes.text.toString().ifEmpty { null }
        )

        val service = VitalTimeClient(token!!).apiService
        val repo = AppointmentRepository(service)

        lifecycleScope.launch {
            try {
                val response = repo.createAppointment(request)

                if (response.isSuccess) {
                    Toast.makeText(
                        this@NuevaCitaActivity,
                        "Cita creada correctamente",
                        Toast.LENGTH_LONG
                    ).show()
                    setResult(RESULT_OK)
                    finish()
                } else {
                    Toast.makeText(
                        this@NuevaCitaActivity,
                        "Error creando cita",
                        Toast.LENGTH_LONG
                    ).show()
                }

            } catch (e: Exception) {
                Toast.makeText(
                    this@NuevaCitaActivity,
                    "Error: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    // ======================================================
    // ABRIR GOOGLE MAPS
    // ======================================================
    private fun abrirMapa(lat: Double, lng: Double, firstName: String, lastName: String) {
        val uri = Uri.parse("geo:$lat,$lng?q=$lat,$lng(${firstName} ${lastName})")
        val intent = Intent(Intent.ACTION_VIEW, uri)
        intent.setPackage("com.google.android.apps.maps")
        startActivity(intent)
    }
}