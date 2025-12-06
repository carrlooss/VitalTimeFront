package com.example.appmedicoscarlos.ui

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.appmedicoscarlos.R
import com.example.appmedicoscarlos.databinding.ActivityCreatePatientBinding
import com.example.appmedicoscarlos.models.PatientCreateRequest
import com.example.appmedicoscarlos.providers.VitalTimeClient
import com.example.appmedicoscarlos.providers.VitalTimeService
import com.example.appmedicoscarlos.repository.PatientRepository
import com.example.appmedicoscarlos.utils.TokenManager
import com.google.android.material.appbar.MaterialToolbar
import kotlinx.coroutines.launch

class CreatePatientActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCreatePatientBinding
    private lateinit var tokenManager: TokenManager
    private lateinit var token: String
    private lateinit var patientRepository : PatientRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreatePatientBinding.inflate(layoutInflater)
        setContentView(binding.root)

        tokenManager = TokenManager(this)
        token = tokenManager.getToken()?: ""
        val service = VitalTimeClient(token).apiService
        patientRepository = PatientRepository(service)

        binding.btnSave.setOnClickListener {
            if (!validarPaciente()) return@setOnClickListener
            createPatient()
        }

        val toolbar = findViewById<MaterialToolbar>(R.id.topAppBar)

        toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun createPatient() {
        val dto = PatientCreateRequest(
            username = binding.editUsername.text.toString(),
            password = binding.editPassword.text.toString(),
            email = binding.editEmail.text.toString(),
            firstName = binding.editFirstName.text.toString(),
            lastName = binding.editLastName.text.toString(),
            phone = binding.editPhone.text.toString(),
            dateOfBirth = binding.editDob.text.toString(),
            gender = binding.editGender.text.toString(),
            contact = binding.editContact.text.toString()
        )

        lifecycleScope.launch {
            binding.progressBar.visibility = android.view.View.VISIBLE

            val result = patientRepository.createPatient(dto)

            result.onSuccess {
                Toast.makeText(this@CreatePatientActivity, "Paciente creado", Toast.LENGTH_LONG).show()
                setResult(RESULT_OK)
                finish()
            }

            result.onFailure {
                Toast.makeText(this@CreatePatientActivity, "Error: ${it.message}", Toast.LENGTH_LONG).show()
            }
        }
        binding.progressBar.visibility = android.view.View.VISIBLE

    }

    //VALIDACIONES

    private fun isValidEmail(email: String?): Boolean {
        return email != null && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun validarPaciente(): Boolean {
        // Username obligatorio
        if (binding.editUsername.text.isNullOrBlank()) {
            Toast.makeText(this, "El nombre de usuario es obligatorio", Toast.LENGTH_SHORT).show()
            return false
        }

        // Password obligatorio y mínimo 6 caracteres
        val password = binding.editPassword.text.toString()
        if (password.isBlank() || password.length < 6) {
            Toast.makeText(this, "La contraseña debe tener al menos 6 caracteres", Toast.LENGTH_SHORT).show()
            return false
        }

        // Email obligatorio y válido
        val email = binding.editEmail.text.toString()
        if (email.isBlank() || !isValidEmail(email)) {
            Toast.makeText(this, "El email es obligatorio y debe ser válido", Toast.LENGTH_SHORT).show()
            return false
        }

        // FirstName obligatorio
        if (binding.editFirstName.text.isNullOrBlank()) {
            Toast.makeText(this, "El nombre es obligatorio", Toast.LENGTH_SHORT).show()
            return false
        }

        // LastName obligatorio
        if (binding.editLastName.text.isNullOrBlank()) {
            Toast.makeText(this, "El apellido es obligatorio", Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }

}