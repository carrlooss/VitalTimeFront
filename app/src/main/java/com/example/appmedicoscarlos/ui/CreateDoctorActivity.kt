package com.example.appmedicoscarlos.ui

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.appmedicoscarlos.databinding.ActivityCreateDoctorBinding
import com.example.appmedicoscarlos.models.DoctorCreate
import com.example.appmedicoscarlos.providers.VitalTimeClient
import com.example.appmedicoscarlos.repository.DoctorRepository
import com.example.appmedicoscarlos.utils.TokenManager
import kotlinx.coroutines.launch

class CreateDoctorActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCreateDoctorBinding
    private lateinit var tokenManager: TokenManager
    private lateinit var token: String
    private lateinit var doctorRepository : DoctorRepository

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

        // Evento del botón
        binding.btnCrearDoctor.setOnClickListener {
            createDoctor()
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
            specialtyId = binding.inputSpecialtyId.text.toString().toIntOrNull() ?: 0,
            officeAddress = binding.inputOfficeAddress.text.toString(),
            bio = binding.inputBio.text.toString(),
            latitude = binding.inputLatitude.text.toString().toDoubleOrNull(),
            longitude = binding.inputLongitude.text.toString().toDoubleOrNull()
        )

        lifecycleScope.launch {
            binding.progressBar.visibility = android.view.View.VISIBLE

            val result = doctorRepository.createDoctor(dto)

            result.onSuccess {
                Toast.makeText(this@CreateDoctorActivity, "Doctor creado correctamente", Toast.LENGTH_LONG).show()
                setResult(RESULT_OK)
                finish()
            }

            result.onFailure {
                Toast.makeText(this@CreateDoctorActivity, "Error: ${it.message}", Toast.LENGTH_LONG).show()
            }

            binding.progressBar.visibility = android.view.View.GONE
        }
    }
}