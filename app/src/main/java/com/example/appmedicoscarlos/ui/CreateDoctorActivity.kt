package com.example.appmedicoscarlos.ui

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.appmedicoscarlos.R
import com.example.appmedicoscarlos.databinding.ActivityCreateDoctorBinding
import com.example.appmedicoscarlos.models.DoctorCreate
import com.example.appmedicoscarlos.models.SpecialtyResponseDto
import com.example.appmedicoscarlos.providers.VitalTimeClient
import com.example.appmedicoscarlos.repository.DoctorRepository
import com.example.appmedicoscarlos.repository.SpecialtyRepository
import com.example.appmedicoscarlos.utils.TokenManager
import com.google.android.material.appbar.MaterialToolbar
import kotlinx.coroutines.launch

class CreateDoctorActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCreateDoctorBinding
    private lateinit var tokenManager: TokenManager
    private lateinit var token: String
    private lateinit var doctorRepository: DoctorRepository
    private lateinit var specialtyRepository: SpecialtyRepository
    private var specialtiesList: List<SpecialtyResponseDto> = emptyList()
    private var selectedSpecialtyId: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateDoctorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Token
        tokenManager = TokenManager(this)
        token = tokenManager.getToken() ?: ""

        // API service
        val service = VitalTimeClient(token).apiService
        doctorRepository = DoctorRepository(service)
        specialtyRepository = SpecialtyRepository(service)

        binding.dropdownSpecialty.setOnItemClickListener { _, _, position, _ ->
            selectedSpecialtyId = specialtiesList[position].id
        }

        loadSpecialties()

        // Evento del botón
        binding.btnCrearDoctor.setOnClickListener {
            if (!validarDoctor()) return@setOnClickListener
            createDoctor()
        }

        val toolbar = findViewById<MaterialToolbar>(R.id.topAppBar)

        toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun createDoctor() {
        val dto = DoctorCreate(
            username = binding.inputUsername.text.toString(),
            email = binding.inputEmail.text.toString(),
            password = binding.inputPassword.text.toString(),
            firstName = binding.inputFirstName.text.toString(),
            lastName = binding.inputLastName.text.toString(),
            phone = binding.inputPhone.text.toString(),
            licenseNumber = binding.inputLicense.text.toString(),
            specialtyId = selectedSpecialtyId?: -1,
            officeAddress = binding.inputOfficeAddress.text.toString(),
            bio = binding.inputBio.text.toString(),
            latitude = binding.inputLatitude.text.toString().toDoubleOrNull(),
            longitude = binding.inputLongitude.text.toString().toDoubleOrNull()
        )

        lifecycleScope.launch {
            binding.progressBar.visibility = android.view.View.VISIBLE

            val result = doctorRepository.createDoctor(dto)

            result.onSuccess {
                Toast.makeText(
                    this@CreateDoctorActivity,
                    "Doctor creado correctamente",
                    Toast.LENGTH_LONG
                ).show()
                setResult(RESULT_OK)
                finish()
            }

            result.onFailure {
                Toast.makeText(this@CreateDoctorActivity, "Error: ${it.message}", Toast.LENGTH_LONG)
                    .show()
            }

            binding.progressBar.visibility = android.view.View.GONE
        }
    }

    //VALIDACIONES

    private fun isValidEmail(email: String?): Boolean {
        return email != null && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun validarDoctor(): Boolean {
        // Username obligatorio
        if (binding.inputUsername.text.isNullOrBlank()) {
            Toast.makeText(this, "El nombre de usuario es obligatorio", Toast.LENGTH_SHORT).show()
            return false
        }

        // Email obligatorio y válido
        val email = binding.inputEmail.text.toString()
        if (email.isBlank() || !isValidEmail(email)) {
            Toast.makeText(this, "El email es obligatorio y debe ser válido", Toast.LENGTH_SHORT)
                .show()
            return false
        }

        // Password obligatorio y mínimo 6 caracteres
        val password = binding.inputPassword.text.toString()
        if (password.isBlank() || password.length < 6) {
            Toast.makeText(
                this,
                "La contraseña debe tener al menos 6 caracteres",
                Toast.LENGTH_SHORT
            ).show()
            return false
        }

        // Nombre obligatorio
        if (binding.inputFirstName.text.isNullOrBlank()) {
            Toast.makeText(this, "El nombre es obligatorio", Toast.LENGTH_SHORT).show()
            return false
        }

        // Apellido obligatorio
        if (binding.inputLastName.text.isNullOrBlank()) {
            Toast.makeText(this, "El apellido es obligatorio", Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }

    private fun loadSpecialties() {
        lifecycleScope.launch {
            val result = specialtyRepository.getAllSpecialties()

            result.onSuccess { specialties ->
                specialtiesList = specialties
                val adapter = ArrayAdapter(
                    this@CreateDoctorActivity,
                    android.R.layout.simple_dropdown_item_1line,
                    specialties.map { it.name }
                )
                binding.dropdownSpecialty.setAdapter(adapter)
            }
        }
    }
}