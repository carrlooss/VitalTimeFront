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
        setContentView(R.layout.activity_admin) // 🟢 Tu layout de administrador

        // === TARJETAS DEL PANEL ===

        // Ver Médicos
        findViewById<MaterialCardView>(R.id.cardViewDoctors).setOnClickListener {
           startActivity(Intent(this, ListaDoctoresActivity::class.java))
        }

        // Ver Pacientes
        findViewById<MaterialCardView>(R.id.cardViewPatients).setOnClickListener {
            startActivity(Intent(this, ListaPacientesActivity::class.java))
        }

        // Ajustes del sistema
        //findViewById<MaterialCardView>(R.id.cardSystemSettings).setOnClickListener {
          //  startActivity(Intent(this, AjustesSistemaActivity::class.java))
       // }

        // === CERRAR SESIÓN ===
        findViewById<MaterialButton>(R.id.btnLogoutAdmin).setOnClickListener {
            TokenManager(this).clearAll() // limpia token, username, rol, todo
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }
}