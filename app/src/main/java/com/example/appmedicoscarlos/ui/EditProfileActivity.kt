package com.example.appmedicoscarlos.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.appmedicoscarlos.R
import com.example.appmedicoscarlos.databinding.EditProfileFragmentBinding
import com.example.appmedicoscarlos.models.DoctorUpdateRequest
import com.example.appmedicoscarlos.models.PatientResponseDto
import com.example.appmedicoscarlos.models.PatientUpdateRequest
import com.example.appmedicoscarlos.models.SpecialtyResponseDto
import com.example.appmedicoscarlos.providers.VitalTimeClient
import com.example.appmedicoscarlos.repository.DoctorRepository
import com.example.appmedicoscarlos.repository.PatientRepository
import com.example.appmedicoscarlos.repository.SpecialtyRepository
import com.example.appmedicoscarlos.utils.TokenManager
import com.google.android.material.appbar.MaterialToolbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class EditProfileActivity : AppCompatActivity() {

    private lateinit var binding: EditProfileFragmentBinding
    private lateinit var tokenManager: TokenManager
    private lateinit var token: String
    private var userId: Long = 0
    private var userRole: String = ""
    private lateinit var specialtyRepository: SpecialtyRepository
    private var specialtiesList: List<SpecialtyResponseDto> = emptyList()
    private var selectedSpecialtyId: Long? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = EditProfileFragmentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        tokenManager = TokenManager(this)
        token = tokenManager.getToken() ?: ""
        userId = intent.getLongExtra("USER_ID", -1)
        userRole = intent.getStringExtra("USER_ROLE") ?: ""

        // API service
        val service = VitalTimeClient(token).apiService
        specialtyRepository = SpecialtyRepository(service)

        if (token.isEmpty() || userRole.isEmpty()) {
            goBackToLogin()
            return
        }

        if (userRole != "PACIENTE") {
            binding.dropdownSpecialty.setOnItemClickListener { _, _, position, _ ->
                selectedSpecialtyId = specialtiesList[position].id?.toLong()
            }
            loadSpecialties()
        }

        setupVisibilityByRole()
        loadUserProfile()

        binding.btnSave.setOnClickListener {
            if (!validarPerfil()) return@setOnClickListener
            saveProfile()
        }

        findViewById<MaterialToolbar>(R.id.topAppBar).setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun goBackToLogin() {
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }

    private fun setupVisibilityByRole() {
        if (userRole.contains("PACIENTE")) {
            // Mostrar campos de paciente
            binding.layoutDateOfBirth.visibility = View.VISIBLE
            binding.layoutGender.visibility = View.VISIBLE
            binding.layoutContact.visibility = View.VISIBLE

            // Ocultar campos de doctor
            binding.layoutLicense.visibility = View.GONE
            binding.layoutSpecialty.visibility = View.GONE
            binding.layoutOffice.visibility = View.GONE
            binding.layoutBio.visibility = View.GONE
        } else if (userRole.contains("DOCTOR")) {
            // Mostrar campos de doctor
            binding.layoutLicense.visibility = View.VISIBLE
            binding.layoutSpecialty.visibility = View.VISIBLE
            binding.layoutOffice.visibility = View.VISIBLE
            binding.layoutBio.visibility = View.VISIBLE

            // Ocultar campos de paciente
            binding.layoutDateOfBirth.visibility = View.GONE
            binding.layoutGender.visibility = View.GONE
            binding.layoutContact.visibility = View.GONE
        }
    }

    private fun loadUserProfile() {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val service = VitalTimeClient(token).apiService
                if (userRole.contains("PACIENTE")) {
                    val repo = PatientRepository(service)
                    val result = repo.getPatientById(userId)
                    result.onSuccess { patient ->
                        fillPatientFields(patient)
                    }.onFailure {
                        showError("Error al cargar datos del paciente")
                    }
                } else if (userRole.contains("DOCTOR")) {
                    val repo = DoctorRepository(service)
                    val result = repo.getDoctor(userId)
                    result.onSuccess { doctor ->
                        fillDoctorFields(doctor)
                    }.onFailure {
                        showError("Error al cargar datos del doctor")
                    }
                }
            } catch (e: Exception) {
                showError("Error inesperado: ${e.message}")
            }
        }
    }

    private fun fillPatientFields(dto: PatientResponseDto) {
        binding.etFirstName.setText(dto.firstName)
        binding.etLastName.setText(dto.lastName)
        binding.etEmail.setText(dto.email)
        binding.etPhone.setText(dto.phone)
        binding.etDateOfBirth.setText(dto.dateOfBirth)
        binding.etGender.setText(
            when (dto.gender?.uppercase()) {
                "F" -> "Femenino"
                "M" -> "Masculino"
                "O" -> "Otro"
                else -> dto.gender
            }
        )
        binding.etContact.setText(dto.contact)
    }

    private fun fillDoctorFields(dto: com.example.appmedicoscarlos.models.DoctorResponse) {
        binding.etFirstName.setText(dto.firstName)
        binding.etLastName.setText(dto.lastName)
        binding.etEmail.setText(dto.email)
        binding.etPhone.setText(dto.phone)
        binding.etLicenseNumber.setText(dto.licenseNumber)
        binding.dropdownSpecialty.setText(dto.specialtyName, false)
        binding.etOfficeAddress.setText(dto.officeAddress)
        binding.etBio.setText(dto.bio)
    }

    private fun saveProfile() {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val service = VitalTimeClient(token).apiService

                if (userRole.contains("PACIENTE")) {
                    val updateDto = PatientUpdateRequest(
                        firstName = binding.etFirstName.text.toString().takeIf { it.isNotBlank() },
                        lastName = binding.etLastName.text.toString().takeIf { it.isNotBlank() },
                        email = binding.etEmail.text.toString().takeIf { it.isNotBlank() },
                        phone = binding.etPhone.text.toString().takeIf { it.isNotBlank() },
                        dateOfBirth = binding.etDateOfBirth.text.toString().takeIf { it.isNotBlank() },
                        gender = mapGenderInputToCode(binding.etGender.text.toString()),
                        contact = binding.etContact.text.toString().takeIf { it.isNotBlank() }
                    )

                    val repo = PatientRepository(service)
                    val result = repo.updatePatient(userId, updateDto)

                    result.onSuccess { updatedPatient ->
                        Toast.makeText(this@EditProfileActivity, "Perfil actualizado", Toast.LENGTH_SHORT).show()
                        val intent = Intent()
                        intent.putExtra("UPDATED_USER", updatedPatient)
                        setResult(RESULT_OK, intent)
                        finish()
                    }.onFailure {
                        showError("Error al guardar: ${it.message}")
                    }


                } else if (userRole.contains("DOCTOR", ignoreCase = true)) {
                    val updateRequest = DoctorUpdateRequest(
                        firstName = binding.etFirstName.text.toString().takeIf { it.isNotBlank() },
                        lastName = binding.etLastName.text.toString().takeIf { it.isNotBlank() },
                        email = binding.etEmail.text.toString().takeIf { it.isNotBlank() },
                        phone = binding.etPhone.text.toString().takeIf { it.isNotBlank() },
                        licenseNumber = binding.etLicenseNumber.text.toString().takeIf { it.isNotBlank() },
                        specialtyId = selectedSpecialtyId,
                        officeAddress = binding.etOfficeAddress.text.toString().takeIf { it.isNotBlank() },
                        bio = binding.etBio.text.toString().takeIf { it.isNotBlank() }
                    )

                    val repo = DoctorRepository(service)
                    val result = repo.updateDoctor(userId, updateRequest)

                    result.onSuccess { updatedDoctor ->
                        Toast.makeText(this@EditProfileActivity, "Perfil actualizado", Toast.LENGTH_SHORT).show()
                        val intent = Intent()
                        intent.putExtra("UPDATED_DOCTOR", updatedDoctor)
                        setResult(RESULT_OK, intent)
                        finish()
                    }.onFailure {
                        showError("Error al guardar: ${it.message}")
                    }
                }
            } catch (e: Exception) {
                showError("Error inesperado: ${e.message}")
            }
        }
    }

    // Convierte Femenino
    private fun mapGenderInputToCode(input: String): String? {
        return when (input.trim().uppercase()) {
            "FEMENINO" -> "F"
            "MASCULINO" -> "M"
            "OTRO" -> "O"
            "F", "M", "O" -> input.uppercase()
            else -> input.takeIf { it.isNotBlank() }
        }
    }

    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    //VALIDACIONES

    private fun isValidEmail(email: String?): Boolean {
        return email != null && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun isValidPhone(phone: String?): Boolean {
        return phone != null && phone.matches(Regex("^\\+?[0-9]{7,15}\$"))
    }

    private fun isValidDate(date: String?): Boolean {
        if (date.isNullOrBlank()) return false
        return try {
            java.time.LocalDate.parse(date)
            true
        } catch (e: Exception) {
            false
        }
    }

    private fun validarPerfil(): Boolean {

        // Nombre y apellido obligatorios
        if (binding.etFirstName.text.isNullOrBlank()) {
            showError("El nombre es obligatorio")
            return false
        }
        if (binding.etLastName.text.isNullOrBlank()) {
            showError("El apellido es obligatorio")
            return false
        }

        // Email válido si se ingresa
        val email = binding.etEmail.text.toString()
        if (email.isNotBlank() && !isValidEmail(email)) {
            showError("El email no tiene un formato válido")
            return false
        }

        // Teléfono válido si se ingresa
        val phone = binding.etPhone.text.toString()
        if (phone.isNotBlank() && !isValidPhone(phone)) {
            showError("El teléfono no es válido")
            return false
        }

        if (userRole.contains("PACIENTE")) {
            // Fecha válida
            val dob = binding.etDateOfBirth.text.toString()
            if (dob.isNotBlank() && !isValidDate(dob)) {
                showError("La fecha de nacimiento no es válida (yyyy-MM-dd)")
                return false
            }

            // Gender válido
            val genderCode = mapGenderInputToCode(binding.etGender.text.toString())
            if (genderCode != null && genderCode !in listOf("F", "M", "O")) {
                showError("El género no es válido")
                return false
            }
        }

        if (userRole.contains("DOCTOR")) {
            // LicenseNumber obligatorio
            if (binding.etLicenseNumber.text.isNullOrBlank()) {
                showError("El número de licencia es obligatorio")
                return false
            }
        }
        return true
    }

    private fun loadSpecialties() {
        lifecycleScope.launch {
            val result = specialtyRepository.getAllSpecialties()

            result.onSuccess { specialties ->
                specialtiesList = specialties
                val adapter = ArrayAdapter(
                    this@EditProfileActivity,
                    android.R.layout.simple_dropdown_item_1line,
                    specialties.map { it.name }
                )
                binding.dropdownSpecialty.setAdapter(adapter)
            }
        }
    }

}