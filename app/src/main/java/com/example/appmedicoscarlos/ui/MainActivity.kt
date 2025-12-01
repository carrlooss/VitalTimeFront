package com.example.appmedicoscarlos.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.appmedicoscarlos.R
import com.example.appmedicoscarlos.providers.PublicVitalTimeClient
import com.example.appmedicoscarlos.repository.AuthRepository
import com.example.appmedicoscarlos.utils.TokenManager
import com.example.appmedicoscarlos.viewmodel.AuthViewModel
import com.example.appmedicoscarlos.viewmodel.AuthViewModelFactory

class MainActivity : AppCompatActivity() {

    private lateinit var authViewModel: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val tokenManager = TokenManager(this)
        val repository = AuthRepository(PublicVitalTimeClient.apiService)
        val factory = AuthViewModelFactory(repository, tokenManager)
        authViewModel = ViewModelProvider(this, factory)[AuthViewModel::class.java]

        // 🔥 PRIMERO: comprobar si está logeado
        if (!authViewModel.isLoggedIn()) {
            navigateToLogin()
            return
        }

        // 🔥 NUEVO: Si el rol es admin → abrir layout Admin
        val rol = tokenManager.getRol()
        if (rol.equals("ADMIN", ignoreCase = true)) {
            startActivity(Intent(this, AdminActivity::class.java))
            finish()
            return   // IMPORTANTE
        }

        // Si NO es admin → carga tu layout normal de pacientes
        setContentView(R.layout.main_activity_users)

        // Mostrar nombre del usuario
        val username = tokenManager.getUsername() ?: "Usuario"
        findViewById<TextView>(R.id.tvWelcome).text = "¡Hola, $username!"

        // Cierre de sesión
        findViewById<Button>(R.id.btnLogout).setOnClickListener {
            authViewModel.logout()
            navigateToLogin()
        }

        // 👇 AÑADIDO: Navegación a "Mis Citas"
        findViewById<com.google.android.material.card.MaterialCardView>(R.id.cardMyAppointments)
            .setOnClickListener {
                val intent = Intent(this, MisCitasActivity::class.java)
                intent.putExtra("USER_ROLE",tokenManager.getRol())
                intent.putExtra("USER_ID",tokenManager.getUserId())
                startActivity(intent)
            }

        findViewById<com.google.android.material.card.MaterialCardView>(R.id.cardNewAppointment)
            .setOnClickListener {
                startActivity(Intent(this, NuevaCitaActivity::class.java))
            }

        findViewById<com.google.android.material.card.MaterialCardView>(R.id.cardProfile)
            .setOnClickListener {
                startActivity(Intent(this, ProfileActivity::class.java))
            }
    }

    private fun navigateToLogin() {
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }
}
