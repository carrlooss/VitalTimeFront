package com.example.appmedicoscarlos.ui

import android.app.TimePickerDialog
import android.os.Bundle
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.appmedicoscarlos.R
import com.example.appmedicoscarlos.models.DiaHorario
import com.example.appmedicoscarlos.models.DoctorScheduleCreateDto
import com.example.appmedicoscarlos.models.DoctorScheduleResponseDto
import com.example.appmedicoscarlos.providers.VitalTimeClient
import com.example.appmedicoscarlos.repository.DoctorScheduleRepository
import com.example.appmedicoscarlos.utils.TokenManager
import com.google.android.material.appbar.MaterialToolbar
import kotlinx.coroutines.*
import java.util.*

class HorarioDoctorActivity : AppCompatActivity() {

    private lateinit var repository: DoctorScheduleRepository
    private var doctorId: Long = 0L
    private val dias = mutableMapOf<Int, DiaHorario>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_schedule_doctor)

        // Inicializa repositorio
        val token = TokenManager(this).getToken()!!
        repository = DoctorScheduleRepository(VitalTimeClient(token).apiService)

        // Recibir doctorId y nombre desde intent
        doctorId = intent.getLongExtra("USER_ID", -1L)
        val doctorNombre = intent.getStringExtra("NOMBRE_DOCTOR") ?: "Dr. Desconocido"

        findViewById<TextView>(R.id.txtNombreDoctor).text = doctorNombre

        // Inicializar días
        for (i in 1..7) {
            dias[i] = DiaHorario(dayOfWeek = i)
        }

        // Configurar switches y clicks
        configurarDia(1, R.id.switchLunes, R.id.txtInicioLunes, R.id.txtFinLunes)
        configurarDia(2, R.id.switchMartes, R.id.txtInicioMartes, R.id.txtFinMartes)
        configurarDia(3, R.id.switchMiercoles, R.id.txtInicioMiercoles, R.id.txtFinMiercoles)
        configurarDia(4, R.id.switchJueves, R.id.txtInicioJueves, R.id.txtFinJueves)
        configurarDia(5, R.id.switchViernes, R.id.txtInicioViernes, R.id.txtFinViernes)
        configurarDia(6, R.id.switchSabado, R.id.txtInicioSabado, R.id.txtFinSabado)
        configurarDia(7, R.id.switchDomingo, R.id.txtInicioDomingo, R.id.txtFinDomingo)

        // Botón guardar (AQUÍ SOLO AÑADO LA VALIDACIÓN)
        findViewById<android.widget.Button>(R.id.btnGuardarHorario).setOnClickListener {

            if (!validarHorarios()) return@setOnClickListener   // ✅ VALIDACIÓN AÑADIDA

            //Primero borramos los horarios antiguos del doctor y luego creamos los nuevos
            guardarHorarios()
        }

        // Cargar horarios existentes
        cargarHorariosExistentes()

        findViewById<MaterialToolbar>(R.id.topAppBar).setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun configurarDia(dayOfWeek: Int, switchId: Int, txtInicioId: Int, txtFinId: Int) {
        val dia = dias[dayOfWeek]!!
        val switch = findViewById<Switch>(switchId)
        val txtInicio = findViewById<TextView>(txtInicioId)
        val txtFin = findViewById<TextView>(txtFinId)

        // Cambiar habilitado según switch
        switch.setOnCheckedChangeListener { _, isChecked ->
            dia.enabled = isChecked
        }

        // Click para elegir hora de inicio
        txtInicio.setOnClickListener {
            mostrarTimePicker { hora ->
                dia.startTime = hora
                txtInicio.text = hora
            }
        }

        // Click para elegir hora de fin
        txtFin.setOnClickListener {
            mostrarTimePicker { hora ->
                dia.endTime = hora
                txtFin.text = hora
            }
        }
    }

    private fun mostrarTimePicker(onTimeSelected: (String) -> Unit) {
        val cal = Calendar.getInstance()
        val dialog = TimePickerDialog(this, { _, hour, minute ->
            val horaFormateada = String.format("%02d:%02d", hour, minute)
            onTimeSelected(horaFormateada)
        }, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true)
        dialog.show()
    }

    private fun guardarHorarios() {
        val horariosAEnviar = dias.values.filter { it.enabled }.map {
            DoctorScheduleCreateDto(
                doctorId = doctorId,
                dayOfWeek = it.dayOfWeek,
                startTime = it.startTime,
                endTime = it.endTime
            )
        }

        if (horariosAEnviar.isEmpty()) {
            Toast.makeText(this, "No se ha seleccionado ningún horario", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            val result = repository.createSchedule(horariosAEnviar)

            result.onSuccess {
                Toast.makeText(
                    this@HorarioDoctorActivity,
                    "Horarios guardados correctamente",
                    Toast.LENGTH_SHORT
                ).show()
            }

            result.onFailure {
                Toast.makeText(
                    this@HorarioDoctorActivity,
                    "Error al guardar.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }


//    private fun borrarHorarios() {
//        CoroutineScope(Dispatchers.Main).launch {
//            val result = repository.deleteScheduleByDoctorId(doctorId)
//            result.onFailure {
//                Toast.makeText(
//                    this@HorarioDoctorActivity,
//                    "Error al guardar: ${it.message}",
//                    Toast.LENGTH_SHORT
//                ).show()
//            }
//        }
//    }

    private fun cargarHorariosExistentes() {
        CoroutineScope(Dispatchers.Main).launch {
            val result = repository.getSchedulesByDoctor(doctorId)
            result.onSuccess { lista ->
                lista.forEach { dto ->
                    val dia = dias[dto.dayOfWeek]
                    dia?.enabled = true
                    dia?.startTime = dto.startTime
                    dia?.endTime = dto.endTime

                    val switchId = when (dto.dayOfWeek) {
                        1 -> R.id.switchLunes
                        2 -> R.id.switchMartes
                        3 -> R.id.switchMiercoles
                        4 -> R.id.switchJueves
                        5 -> R.id.switchViernes
                        6 -> R.id.switchSabado
                        7 -> R.id.switchDomingo
                        else -> 0
                    }
                    val txtInicioId = when (dto.dayOfWeek) {
                        1 -> R.id.txtInicioLunes
                        2 -> R.id.txtInicioMartes
                        3 -> R.id.txtInicioMiercoles
                        4 -> R.id.txtInicioJueves
                        5 -> R.id.txtInicioViernes
                        6 -> R.id.txtInicioSabado
                        7 -> R.id.txtInicioDomingo
                        else -> 0
                    }
                    val txtFinId = when (dto.dayOfWeek) {
                        1 -> R.id.txtFinLunes
                        2 -> R.id.txtFinMartes
                        3 -> R.id.txtFinMiercoles
                        4 -> R.id.txtFinJueves
                        5 -> R.id.txtFinViernes
                        6 -> R.id.txtFinSabado
                        7 -> R.id.txtFinDomingo
                        else -> 0
                    }

                    if (switchId != 0) findViewById<Switch>(switchId).isChecked = true
                    if (txtInicioId != 0) findViewById<TextView>(txtInicioId).text = dto.startTime
                    if (txtFinId != 0) findViewById<TextView>(txtFinId).text = dto.endTime
                }
            }
        }
    }

    // ----------------------------------------------------------------------
    //  VALIDACIONES → AÑADIDAS SIN MODIFICAR TU CÓDIGO EXISTENTE
    // ----------------------------------------------------------------------

    private fun validarFormatoHora(hora: String?): Boolean {

        return hora != null && (Regex("^\\d{2}:\\d{2}$").matches(hora)
                || Regex("^\\d{2}:\\d{2}:\\d{2}$").matches(hora))
    }

    private fun validarOrdenHoras(start: String, end: String): Boolean {
        return start < end
    }

    private fun nombreDia(day: Int): String {
        return when (day) {
            1 -> "Lunes"
            2 -> "Martes"
            3 -> "Miércoles"
            4 -> "Jueves"
            5 -> "Viernes"
            6 -> "Sábado"
            7 -> "Domingo"
            else -> "Día desconocido"
        }
    }

    private fun validarHorarios(): Boolean {
        val activos = dias.values.filter { it.enabled }

        if (activos.isEmpty()) {
            Toast.makeText(this, "Debes activar al menos un día", Toast.LENGTH_SHORT).show()
            return false
        }

        for (dia in activos) {
            val start = dia.startTime
            val end = dia.endTime

            if (start == null || end == null) {
                Toast.makeText(
                    this,
                    "Selecciona ambas horas en ${nombreDia(dia.dayOfWeek)}",
                    Toast.LENGTH_SHORT
                ).show()
                return false
            }

            if (!validarFormatoHora(start) || !validarFormatoHora(end)) {
                Toast.makeText(
                    this,
                    "Formato de hora inválido en ${nombreDia(dia.dayOfWeek)}",
                    Toast.LENGTH_SHORT
                ).show()
                return false
            }

            if (!validarOrdenHoras(start, end)) {
                Toast.makeText(
                    this,
                    "La hora de inicio debe ser menor que la de fin en ${nombreDia(dia.dayOfWeek)}",
                    Toast.LENGTH_SHORT
                ).show()
                return false
            }
        }

        return true
    }
}