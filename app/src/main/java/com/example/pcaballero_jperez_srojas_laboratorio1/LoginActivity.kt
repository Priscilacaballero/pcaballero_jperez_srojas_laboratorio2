package com.example.pcaballero_jperez_srojas_laboratorio1

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class LoginActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var etUsername: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnLogin: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        etUsername = findViewById(R.id.et_username)
        etPassword = findViewById(R.id.et_password)
        btnLogin = findViewById(R.id.btn_login)

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

    private fun signIn(email: String, password: String) {
        auth.signInWithEmailAndPassword("$email@example.com", password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    checkUserPoints()
                } else {
                    Toast.makeText(this, "Error de autenticación", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun checkUserPoints() {
        val userId = auth.currentUser?.uid ?: return

        db.collection("usuarios").document(userId).get()
            .addOnSuccessListener { document ->
                if (!document.exists() || document.getLong("puntos") == null) {
                    // Nuevo usuario, asignar puntos iniciales
                    initializeUserPoints(userId)
                } else {
                    // Usuario existente, ir al juego
                    startMainActivity()
                }
            }
    }

    private fun initializeUserPoints(userId: String) {
        val userData = hashMapOf(
            "puntos" to 1000L,
            "fecha_registro" to com.google.firebase.Timestamp.now()
        )

        db.collection("usuarios").document(userId).set(userData)
            .addOnSuccessListener {
                startMainActivity()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error al inicializar puntos", Toast.LENGTH_SHORT).show()
            }
    }

    private fun startMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}
