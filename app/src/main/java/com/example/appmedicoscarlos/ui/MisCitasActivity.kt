package com.example.appmedicoscarlos.ui


import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.appmedicoscarlos.R
import com.example.appmedicoscarlos.adapter.AppointmentAdapter
import com.example.appmedicoscarlos.models.AppointmentResponseDto
import com.example.appmedicoscarlos.providers.VitalTimeClient
import com.example.appmedicoscarlos.repository.AppointmentRepository
import com.example.appmedicoscarlos.utils.TokenManager
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MisCitasActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var layoutEmpty: View
    private lateinit var fabNewAppointment: FloatingActionButton
    private lateinit var adapter: AppointmentAdapter
    private var userId: Long = -1
    private var userRole: String = ""


    private val launcherNuevaCita =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                loadAppointments()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_miscitas)

        userId = intent.getLongExtra("USER_ID", -1)
        userRole = intent.getStringExtra("USER_ROLE") ?: ""

        // Inicializar vistas
        recyclerView = findViewById(R.id.recyclerViewAppointments)
        layoutEmpty = findViewById(R.id.layoutEmpty)
        fabNewAppointment = findViewById(R.id.fabNewAppointment)

        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = AppointmentAdapter()
        recyclerView.adapter = adapter

        fabNewAppointment.setOnClickListener {
            val intent = Intent(this, NuevaCitaActivity::class.java)
            launcherNuevaCita.launch(intent)
        }

        loadAppointments()
        // Ver ubicación
        adapter.onLocationClick = { lat, lng, firstName, lastName ->
            abrirMapa(lat, lng)
        }

        adapter.onCancelClick = onCancelClick@{ appointmentId ->
            val token = TokenManager(this).getToken() ?: return@onCancelClick
            CoroutineScope(Dispatchers.Main).launch {
                val repository = AppointmentRepository(VitalTimeClient(token).apiService)
                val result = repository.deleteAppointment(appointmentId)
                result.onSuccess {
                    Toast.makeText(this@MisCitasActivity, "Cita eliminada", Toast.LENGTH_SHORT).show()
                    loadAppointments()
                }.onFailure { error ->
                    Toast.makeText(this@MisCitasActivity, "Error: ${error.message}", Toast.LENGTH_LONG).show()
                }
            }
        }

        findViewById<MaterialToolbar>(R.id.topAppBar).setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }


    private fun loadAppointments() {
        val token = TokenManager(this).getToken()

        if (token == null) {
            showError("Sesión no válida")
            return
        }

        CoroutineScope(Dispatchers.Main).launch {
            try {
                val service = VitalTimeClient(token).apiService
                val repository = AppointmentRepository(service)

                val result: Result<List<AppointmentResponseDto>>
                if (userRole == "PACIENTE")
                    result = repository.getAppointmentsByPatientId(userId)
                else
                    result = repository.getAppointmentsByDoctorId(userId)

                result.onSuccess { appointments ->
                    if (appointments.isEmpty()) {
                        showEmptyState(true)
                    } else {
                        adapter.submitList(appointments)
                        showEmptyState(false)
                    }
                }.onFailure { error ->
                    showError("Error al cargar citas: ${error.message}")
                }

            } catch (e: Exception) {
                showError("Error inesperado: ${e.message}")
            }
        }
    }


    private fun showEmptyState(show: Boolean) {
        layoutEmpty.visibility = if (show) View.VISIBLE else View.GONE
        recyclerView.visibility = if (show) View.GONE else View.VISIBLE
    }

    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
        showEmptyState(true)
    }

    private fun abrirMapa(lat: Double, lng: Double) {
        val uri = "geo:$lat,$lng"
        val intent = Intent(Intent.ACTION_VIEW, android.net.Uri.parse(uri))
        intent.setPackage("com.google.android.apps.maps")
        if (intent.resolveActivity(packageManager) != null) {
            startActivity(intent)
        } else {
            Toast.makeText(this, "No se encontró Google Maps", Toast.LENGTH_SHORT).show()
        }
    }
}