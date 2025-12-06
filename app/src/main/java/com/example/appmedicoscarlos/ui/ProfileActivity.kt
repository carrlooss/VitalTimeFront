package com.example.appmedicoscarlos.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.appmedicoscarlos.R
import com.example.appmedicoscarlos.databinding.ActivityProfileBinding
import com.example.appmedicoscarlos.models.DoctorResponse
import com.example.appmedicoscarlos.models.PatientResponseDto
import com.example.appmedicoscarlos.providers.VitalTimeClient
import com.example.appmedicoscarlos.repository.DoctorRepository
import com.example.appmedicoscarlos.repository.PatientRepository
import com.example.appmedicoscarlos.utils.TokenManager
import com.google.android.material.appbar.MaterialToolbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding
    private lateinit var tokenManager: TokenManager
    private lateinit var token: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        tokenManager = TokenManager(this)

        token = tokenManager.getToken() ?: ""
        val userId = tokenManager.getUserId() ?: 0
        val role = tokenManager.getRol() ?: ""
        if (role == "")
        {
            startActivity(Intent(this, LoginActivity::class.java))
        }

        if (role.contains("PACIENTE")) {
            loadPatient(userId)
        } else if (role == "DOCTOR") {
            loadDoctor(userId)
        }

        binding.btnEditProfile.setOnClickListener {
            val intent = Intent(this, EditProfileActivity::class.java)
            intent.putExtra("USER_ROLE", role)
            intent.putExtra("USER_ID", userId)
            editProfileLauncher.launch(intent)
        }

        findViewById<MaterialToolbar>(R.id.topAppBar).setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private val editProfileLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val role = tokenManager.getRol() ?: ""
            val userId = tokenManager.getUserId() ?: 0
            if (role.contains("PACIENTE")) {
                loadPatient(userId)
            } else if (role == "DOCTOR") {
                loadDoctor(userId)
            }
        }
    }

    private fun loadPatientProfile(dto: PatientResponseDto) {
        binding.tvFullName.text = "${dto.firstName ?: ""} ${dto.lastName ?: ""}".trim().ifEmpty { "–" }
        binding.tvRole.text = "Paciente"

        binding.rowDateOfBirth.visibility = View.VISIBLE
        binding.rowGender.visibility = View.VISIBLE
        binding.rowContact.visibility = View.VISIBLE

        binding.rowLicense.visibility = View.GONE
        binding.rowSpecialty.visibility = View.GONE
        binding.rowOffice.visibility = View.GONE
        binding.rowBio.visibility = View.GONE

        binding.tvEmail.text = dto.email ?: ""
        binding.tvPhone.text = dto.phone ?: ""
        binding.tvDateOfBirth.text = dto.dateOfBirth ?: ""
        binding.tvGender.text = when (dto.gender?.uppercase()) {
            "F" -> "Femenino"
            "M" -> "Masculino"
            "O" -> "Otro"
            else -> dto.gender ?: ""
        }
        binding.tvContact.text = dto.contact ?: ""
    }

    private fun loadDoctorProfile(dto: DoctorResponse) {
        binding.tvFullName.text = "${dto.firstName} ${dto.lastName}"
        binding.tvRole.text = "Doctor"

        binding.rowDateOfBirth.visibility = View.GONE
        binding.rowGender.visibility = View.GONE
        binding.rowContact.visibility = View.GONE

        binding.rowLicense.visibility = View.VISIBLE
        binding.rowSpecialty.visibility = View.VISIBLE
        binding.rowOffice.visibility = View.VISIBLE
        binding.rowBio.visibility = View.VISIBLE

        binding.tvEmail.text = dto.email
        binding.tvPhone.text = dto.phone ?: "–"
        binding.tvLicense.text = dto.licenseNumber ?: "–"
        binding.tvSpecialty.text = dto.specialtyId.toString()
        binding.tvOffice.text = dto.officeAddress ?: "–"
        binding.tvBio.text = dto.bio ?: "–"
    }


    private fun loadPatient(patientId: Long) {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val service = VitalTimeClient(token).apiService
                val patientRepository = PatientRepository(service)
                val result = patientRepository.getPatientById(patientId)

                result.onSuccess { patient ->
                    loadPatientProfile(patient)
                }.onFailure { error ->
                    showError("Error al cargar el perfil: ${error.message}")
                }
            } catch (e: Exception) {
                showError("Error inesperado: ${e.message}")
            }
        }
    }

    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }


    private fun loadDoctor(doctor: Long) {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val service = VitalTimeClient(token).apiService
                val doctorRepository = DoctorRepository(service)
                val result = doctorRepository.getDoctor(doctor)

                result.onSuccess { doctor ->
                    loadDoctorProfile(doctor)
                }.onFailure { error ->
                    showError("Error al cargar el perfil: ${error.message}")
                }
            } catch (e: Exception) {
                showError("Error inesperado: ${e.message}")
            }
        }
    }

}