package com.example.pcaballero_jperez_srojas_laboratorio1

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class LoginActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var db: DatabaseReference
    private lateinit var etUsername: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnLogin: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Inicializar Firebase Auth y Database
        auth = FirebaseAuth.getInstance()
        db = FirebaseDatabase.getInstance().reference

        etUsername = findViewById(R.id.et_username)
        etPassword = findViewById(R.id.et_password)
        btnLogin = findViewById(R.id.btn_login)

        // Verificar si el usuario ya está autenticado
        if (auth.currentUser != null) {
            startMainActivity()
        }

        btnLogin.setOnClickListener {
            val username = etUsername.text.toString()
            val password = etPassword.text.toString()

            if (username.isNotEmpty() && password.isNotEmpty()) {
                signIn(username, password)
            } else {
                Toast.makeText(this, "Por favor complete todos los campos", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun signIn(username: String, password: String) {
        // Convierte el username al correo electrónico completo
        val email = "$username@example.com"

        // Intentar iniciar sesión con Firebase Auth
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Si el inicio de sesión es exitoso, verifica los puntos del usuario
                    checkUserPoints()
                } else {
                    // Si falla, muestra el error
                    Toast.makeText(this, "Error de autenticación: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun checkUserPoints() {
        val userId = auth.currentUser?.uid ?: return

        // Verifica si el usuario ya tiene puntos registrados en la base de datos
        db.child("usuarios").child(userId).child("puntos").get()
            .addOnSuccessListener { snapshot ->
                if (!snapshot.exists()) {
                    // Si no tiene puntos, inicializa los puntos del usuario
                    initializeUserPoints(userId)
                } else {
                    // Si tiene puntos, inicia la MainActivity
                    startMainActivity()
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error al verificar puntos", Toast.LENGTH_SHORT).show()
            }
    }

    private fun initializeUserPoints(userId: String) {
        // Asigna 1000 puntos y registra la fecha de creación
        val userData = mapOf(
            "puntos" to 1000,
            "fecha_registro" to System.currentTimeMillis()
        )

        // Guarda los puntos iniciales en la base de datos
        db.child("usuarios").child(userId).setValue(userData)
            .addOnSuccessListener {
                startMainActivity()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error al inicializar puntos", Toast.LENGTH_SHORT).show()
            }
    }

    private fun startMainActivity() {
        // Navega a la actividad principal
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}
