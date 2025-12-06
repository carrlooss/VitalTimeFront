package com.example.appmedicoscarlos.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.appmedicoscarlos.R
import com.example.appmedicoscarlos.utils.TokenManager
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView

class AdminActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin)

        // Ver Médicos
        findViewById<MaterialCardView>(R.id.cardViewDoctors).setOnClickListener {
           startActivity(Intent(this, ListaDoctoresActivity::class.java))
        }

        // Ver Pacientes
        findViewById<MaterialCardView>(R.id.cardViewPatients).setOnClickListener {
            startActivity(Intent(this, ListaPacientesActivity::class.java))
        }

        // Cerrar Sesion
        findViewById<MaterialButton>(R.id.btnLogoutAdmin).setOnClickListener {
            TokenManager(this).clearAll()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }
}