package com.example.appmedicoscarlos.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import com.example.appmedicoscarlos.R
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.appmedicoscarlos.providers.PublicVitalTimeClient
import com.example.appmedicoscarlos.repository.AuthRepository
import com.example.appmedicoscarlos.utils.TokenManager
import com.example.appmedicoscarlos.viewmodel.AuthViewModel
import com.example.appmedicoscarlos.viewmodel.AuthViewModelFactory
import com.example.appmedicoscarlos.viewmodel.LoginState

class LoginActivity : AppCompatActivity() {

    private lateinit var viewModel: AuthViewModel
    private lateinit var etUsername: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnLogin: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var tvError: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Inicializar ViewModel
        val tokenManager = TokenManager(this)
        val repository = AuthRepository(PublicVitalTimeClient.apiService)
        val factory = AuthViewModelFactory(repository, tokenManager)
        viewModel = ViewModelProvider(this, factory)[AuthViewModel::class.java]

        // Verificar si ya está logueado
        if (viewModel.isLoggedIn()) {
            navigateToMain()
            return
        }

        findViewById<TextView>(R.id.tvRegister).setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        // Inicializar vistas
        etUsername = findViewById(R.id.etUsername)
        etPassword = findViewById(R.id.etPassword)
        btnLogin = findViewById(R.id.btnLogin)
        progressBar = findViewById(R.id.progressBar)
        tvError = findViewById(R.id.tvError)

        // Configurar botón login
        btnLogin.setOnClickListener {

            val username = etUsername.text.toString().trim()
            val password = etPassword.text.toString().trim()

            val validation = validateLoginFields(username, password)

            if (validation != null) {
                tvError.text = validation
                tvError.visibility = View.VISIBLE
                return@setOnClickListener
            }

            viewModel.login(username, password)
        }

        // Observar estado del login
        viewModel.loginState.observe(this) { state ->
            when (state) {
                is LoginState.Loading -> {
                    progressBar.visibility = View.VISIBLE
                    btnLogin.isEnabled = false
                    tvError.visibility = View.GONE
                }
                is LoginState.Success -> {
                    progressBar.visibility = View.GONE
                    Toast.makeText(this, "Login exitoso", Toast.LENGTH_SHORT).show()
                    navigateToMain()
                }
                is LoginState.Error -> {
                    progressBar.visibility = View.GONE
                    btnLogin.isEnabled = true
                    tvError.text = state.message
                    tvError.visibility = View.VISIBLE
                }
            }
        }
    }



    private fun navigateToMain() {
        startActivity(Intent(this, MainActivity::class.java))
    }

    //VALIDACIONES

    private fun validateLoginFields(username: String, password: String): String? {

        // 1. Validar campos vacíos
        if (username.isEmpty() || password.isEmpty()) {
            return "Por favor completa todos los campos"
        }

        // 2. Longitud mínima del usuario
        if (username.length < 4) {
            return "El usuario debe tener al menos 4 caracteres"
        }

        // 3. Usuario sin espacios
        if (username.contains(" ")) {
            return "El usuario no debe contener espacios"
        }

        // 4. Caracteres permitidos en usuario (solo letras, números y guión bajo)
        val userRegex = "^[a-zA-Z0-9_]+$".toRegex()
        if (!username.matches(userRegex)) {
            return "El usuario solo puede contener letras, números y guiones bajos"
        }

        // 5. Longitud mínima de contraseña
        if (password.length < 3) {
            return "La contraseña debe tener al menos 6 caracteres"
        }

        // 6. Contraseña sin espacios
        if (password.contains(" ")) {
            return "La contraseña no puede contener espacios"
        }


        // Todo válido
        return null
    }

}