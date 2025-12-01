package com.example.appmedicoscarlos.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.appmedicoscarlos.R
import com.example.appmedicoscarlos.providers.PublicVitalTimeClient
import com.example.appmedicoscarlos.providers.PublicVitalTimeService
import com.example.appmedicoscarlos.repository.AuthRepository
import com.example.appmedicoscarlos.utils.TokenManager
import com.example.appmedicoscarlos.viewmodel.AuthViewModel
import com.example.appmedicoscarlos.viewmodel.AuthViewModelFactory
import com.example.appmedicoscarlos.viewmodel.RegisterState

class RegisterActivity : AppCompatActivity() {

    private lateinit var viewModel: AuthViewModel
    private lateinit var etFirstName: EditText
    private lateinit var etLastName: EditText
    private lateinit var etRegUsername: EditText
    private lateinit var etEmail: EditText
    private lateinit var etRegPassword: EditText
    private lateinit var btnRegister: Button
    private lateinit var progressBarRegister: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // Inicializar ViewModel (igual que en login)
        val tokenManager = TokenManager(this)
        val repository = AuthRepository(PublicVitalTimeClient.apiService)
        val factory = AuthViewModelFactory(repository, tokenManager)
        viewModel = ViewModelProvider(this, factory)[AuthViewModel::class.java]

        // Si ya está logueado, ir a main (opcional, pero coherente)
        if (viewModel.isLoggedIn()) {
            navigateToMain()
            return
        }

        // Inicializar vistas
        etFirstName = findViewById(R.id.etFirstName)
        etLastName = findViewById(R.id.etLastName)
        etRegUsername = findViewById(R.id.etRegUsername)
        etEmail = findViewById(R.id.etEmail)
        etRegPassword = findViewById(R.id.etRegPassword)
        btnRegister = findViewById(R.id.btnRegister)
        progressBarRegister = findViewById(R.id.progressBarRegister)

        // Configurar botón de registro
        btnRegister.setOnClickListener {
            val firstName = etFirstName.text.toString().trim()
            val lastName = etLastName.text.toString().trim()
            val username = etRegUsername.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val password = etRegPassword.text.toString().trim()

            if (firstName.isEmpty() || lastName.isEmpty() || username.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Por favor completa todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            viewModel.register(username, password, email, firstName, lastName)
        }

        // Observar el estado del registro
        viewModel.registerState.observe(this) { state ->
            when (state) {
                is RegisterState.Loading -> {
                    progressBarRegister.visibility = View.VISIBLE
                    btnRegister.isEnabled = false
                }
                is RegisterState.Success -> {
                    progressBarRegister.visibility = View.GONE
                    Toast.makeText(this, "Registro exitoso", Toast.LENGTH_SHORT).show()
                    navigateToMain()
                }
                is RegisterState.Error -> {
                    progressBarRegister.visibility = View.GONE
                    btnRegister.isEnabled = true
                    Toast.makeText(this, "Error: ${state.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun navigateToMain() {
        startActivity(Intent(this, MainActivity::class.java))
        finish() // Opcional: evita volver al registro con el botón atrás
    }
}