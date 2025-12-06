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
import com.example.appmedicoscarlos.adapter.PacienteAdapter
import com.example.appmedicoscarlos.models.PatientResponseDto
import com.example.appmedicoscarlos.providers.VitalTimeClient
import com.example.appmedicoscarlos.repository.PatientRepository
import com.example.appmedicoscarlos.utils.TokenManager
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ListaPacientesActivity : AppCompatActivity() {

    private lateinit var repository: PatientRepository
    private lateinit var adapter: PacienteAdapter
    private var pacientesList = mutableListOf<PatientResponseDto>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lista_paciente)

        // Instanciamos Repository
        val token = TokenManager(this).getToken()!!
        repository = PatientRepository(VitalTimeClient(token).apiService)

        //  RecyclerView
        val rvPacientes = findViewById<RecyclerView>(R.id.rvPacientes)
        adapter = PacienteAdapter(
            pacientesList,
            onDeleteClick = { paciente -> eliminarPaciente(paciente) },
            onEditClick = { paciente ->
                val intent = Intent(this, EditProfileActivity::class.java)
                intent.putExtra("USER_ROLE", "PACIENTE")
                intent.putExtra("USER_ID", paciente.id)
                editPatientLauncher.launch(intent)
            },
            onViewAppointmentsClick = { paciente ->
                val intent = Intent(this, MisCitasActivity::class.java)
                intent.putExtra("USER_ROLE", "PACIENTE")
                intent.putExtra("USER_ID", paciente.id)
                editPatientLauncher.launch(intent)
            }
        )

        rvPacientes.layoutManager = LinearLayoutManager(this)
        rvPacientes.adapter = adapter

        //Buscador
        val etBuscar = findViewById<TextInputEditText>(R.id.etBuscarPaciente)
        etBuscar.doOnTextChanged { text, _, _, _ ->
            filtrarPacientes(text.toString())
        }

        // FloatingActionButton para crear paciente
        findViewById<FloatingActionButton>(R.id.fabNuevoPaciente).setOnClickListener {
            val intent = Intent(this, CreatePatientActivity::class.java)
            createPatientLauncher.launch(intent)
        }

        // Cargar pacientes
        cargarPacientes()

        findViewById<MaterialToolbar>(R.id.topAppBar).setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun cargarPacientes() {
        lifecycleScope.launch {
            try {
                val result = withContext(Dispatchers.IO) {
                    repository.getAllPatients()
                }

                if (result.isSuccess) {
                    pacientesList = result.getOrDefault(listOf()).toMutableList()
                    adapter.updateList(pacientesList)
                } else {
                    val errorMsg = result.exceptionOrNull()?.message
                    Toast.makeText(this@ListaPacientesActivity, "Error cargando pacientes: $errorMsg", Toast.LENGTH_SHORT).show()
                }

            } catch (e: Exception) {
                Toast.makeText(this@ListaPacientesActivity, "Error cargando pacientes: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun eliminarPaciente(paciente: PatientResponseDto) {
        lifecycleScope.launch {
            try {
                val result = withContext(Dispatchers.IO) {
                    repository.deletePatient(paciente.id ?: 0)
                }

                result.onSuccess {
                    adapter.removePaciente(paciente)
                    Toast.makeText(this@ListaPacientesActivity, "Paciente eliminado", Toast.LENGTH_SHORT).show()
                }

                result.onFailure {
                    Toast.makeText(this@ListaPacientesActivity, "Error al eliminar el paciente: ${it.message}", Toast.LENGTH_SHORT).show()
                }

            } catch (e: Exception) {
                Toast.makeText(this@ListaPacientesActivity, "Error al eliminar: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun filtrarPacientes(query: String) {
        adapter.filter(query)
    }

    // Resultado al volver a editar
    private val editPatientLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val updatedPatient =
                result.data?.getSerializableExtra("UPDATED_USER", PatientResponseDto::class.java)
            updatedPatient?.let {
                actualizarPacienteEnLista(it)
            }
        }
    }

    private fun actualizarPacienteEnLista(updatedPatient: PatientResponseDto) {
        val index = pacientesList.indexOfFirst { it.id == updatedPatient.id }
        if (index != -1) {
            adapter.updatePaciente(updatedPatient)
        }
    }

    // Resultado al volver de crear
    private val createPatientLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            cargarPacientes()
        }
    }
}