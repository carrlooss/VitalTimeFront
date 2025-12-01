package com.example.appmedicoscarlos.ui

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.appmedicoscarlos.databinding.ActivityCreatePatientBinding
import com.example.appmedicoscarlos.models.PatientCreateRequest
import com.example.appmedicoscarlos.providers.VitalTimeClient
import com.example.appmedicoscarlos.providers.VitalTimeService
import com.example.appmedicoscarlos.repository.PatientRepository
import com.example.appmedicoscarlos.utils.TokenManager
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
            createPatient()
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
                setResult(RESULT_OK) // ← Esto indica que hubo cambios
                finish() // volver a la lista
            }

            result.onFailure {
                Toast.makeText(this@CreatePatientActivity, "Error: ${it.message}", Toast.LENGTH_LONG).show()
            }
        }
        binding.progressBar.visibility = android.view.View.VISIBLE

    }
}