package com.example.appmedicoscarlos.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.appmedicoscarlos.R
import com.example.appmedicoscarlos.adapter.DoctorAdapter
import com.example.appmedicoscarlos.models.DoctorResponse
import com.example.appmedicoscarlos.providers.VitalTimeClient
import com.example.appmedicoscarlos.repository.DoctorRepository
import com.example.appmedicoscarlos.utils.TokenManager
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ListaDoctoresActivity : AppCompatActivity() {

    private lateinit var repository: DoctorRepository
    private lateinit var adapter: DoctorAdapter
    private var doctorList = mutableListOf<DoctorResponse>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lista_doctor)

        // Token y repositorio
        val token = TokenManager(this).getToken()!!
        repository = DoctorRepository(VitalTimeClient(token).apiService)

        // RecyclerView
        val rvDoctores = findViewById<RecyclerView>(R.id.rvDoctores)
        adapter = DoctorAdapter(
            doctorList,
            onDeleteClick = { doctor -> eliminarDoctor(doctor) },
            onEditClick = { doctor ->
                val intent = Intent(this, EditProfileActivity::class.java)
                intent.putExtra("USER_ROLE", "DOCTOR")
                intent.putExtra("USER_ID", doctor.id)
                editDoctorLauncher.launch(intent)
            },
            onViewAppointmentsClick = { doctor ->
                val intent = Intent(this, MisCitasActivity::class.java)
                intent.putExtra("USER_ROLE", "DOCTOR")
                intent.putExtra("USER_ID", doctor.id)
                editDoctorLauncher.launch(intent)
            },

            onAgendaClick = { doctor ->
                val intent = Intent(this, HorarioDoctorActivity::class.java)
                intent.putExtra("USER_ID", doctor.id)
                intent.putExtra("NOMBRE_DOCTOR", "Dr. " + doctor.firstName + " " + doctor.lastName)
                startActivity(intent)
            }
        )

        rvDoctores.layoutManager = LinearLayoutManager(this)
        rvDoctores.adapter = adapter

        // Buscador
        val etBuscar = findViewById<TextInputEditText>(R.id.etBuscarDoctor)
        etBuscar.doOnTextChanged { text, _, _, _ ->
            filtrarDoctores(text.toString())
        }

        // Botón flotante crear Doctor
        findViewById<FloatingActionButton>(R.id.fabNuevoDoctor).setOnClickListener {
            val intent = Intent(this, CreateDoctorActivity::class.java)
            createDoctorLauncher.launch(intent)
        }

        // Cargar doctores al iniciar
        cargarDoctores()

        val toolbar = findViewById<MaterialToolbar>(R.id.topAppBar)
        toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    // Cargar lista de doctores
    private fun cargarDoctores() {
        lifecycleScope.launch {
            try {
                val result = withContext(Dispatchers.IO) {
                    repository.getAllDoctor()
                }

                if (result.isSuccess) {
                    doctorList = result.getOrDefault(listOf()).toMutableList()
                    adapter.updateList(doctorList)
                } else {
                    Toast.makeText(this@ListaDoctoresActivity, "Error al cargar doctores", Toast.LENGTH_SHORT).show()
                }

            } catch (e: Exception) {
                Toast.makeText(this@ListaDoctoresActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Eliminar doctor
    private fun eliminarDoctor(doctor: DoctorResponse) {
        lifecycleScope.launch {
            try {
                val result = withContext(Dispatchers.IO) {
                    repository.deleteDoctor(doctor.id ?: 0)
                }

                result.onSuccess {
                    adapter.removeDoctor(doctor)
                    Toast.makeText(this@ListaDoctoresActivity, "Doctor eliminado", Toast.LENGTH_SHORT).show()
                }

                result.onFailure {
                    Toast.makeText(this@ListaDoctoresActivity, "${it.message}", Toast.LENGTH_SHORT).show()
                }

            } catch (e: Exception) {
                Toast.makeText(this@ListaDoctoresActivity, "Error al eliminar: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Filtrar lista
    private fun filtrarDoctores(query: String) {
        adapter.filter(query)
    }

    // Result al EDITAR doctor
    private val editDoctorLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val updatedDoctor =
                result.data?.getSerializableExtra("UPDATED_DOCTOR", DoctorResponse::class.java)
            updatedDoctor?.let {
                actualizarDoctoresEnLista(it)
            }
        }
    }

        private fun actualizarDoctoresEnLista(updatedDoctor: DoctorResponse) {
            val index = doctorList.indexOfFirst { it.id == updatedDoctor.id }
            if (index != -1) {
                adapter.updateDoctor(updatedDoctor)
            }
        }

    // Result al CREAR doctor
    private val createDoctorLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            cargarDoctores()
        }
    }
}
