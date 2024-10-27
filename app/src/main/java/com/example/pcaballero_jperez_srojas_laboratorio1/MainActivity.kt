/*
 * Priscila Caballero, 8-1000-2151
 * José Pérez, 8-993-595
 * Sergio Rojas, 8-993-906
 */

// MainActivity.kt
package com.example.pcaballero_jperez_srojas_laboratorio1

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlin.random.Random

class MainActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var botonLanzarDado: Button
    private lateinit var dadoFragmento: DadoFragmento
    private lateinit var txtPuntos: TextView
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private var puntos: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        botonLanzarDado = findViewById(R.id.btn_lanzar)
        txtPuntos = findViewById(R.id.txt_puntos)
        botonLanzarDado.setOnClickListener(this)

        dadoFragmento = DadoFragmento()
        supportFragmentManager.beginTransaction()
            .add(R.id.fragmento_resultados, dadoFragmento)
            .commit()

        cargarPuntos()
    }

    private fun cargarPuntos() {
        val userId = auth.currentUser?.uid ?: return
        db.collection("usuarios").document(userId)
            .get()
            .addOnSuccessListener { document ->
                puntos = document.getLong("puntos") ?: 0
                actualizarTextoPuntos()
            }
    }

    private fun actualizarTextoPuntos() {
        txtPuntos.text = "Puntos: $puntos"
    }

    override fun onClick(v: View?) {
        if (v?.id == R.id.btn_lanzar) {
            if (puntos >= 100) {
                puntos -= 100 // Costo del lanzamiento
                val valorDado = Random.nextInt(1, 7)

                if (valorDado == 6) {
                    puntos += 500 // Premio por sacar 6
                }

                actualizarPuntosEnFirebase()
                dadoFragmento.actualizarResultado(valorDado)
                actualizarTextoPuntos()
            } else {
                // No hay suficientes puntos
                botonLanzarDado.isEnabled = false
                dadoFragmento.mostrarMensajeSinPuntos()
            }
        }
    }

    private fun actualizarPuntosEnFirebase() {
        val userId = auth.currentUser?.uid ?: return
        db.collection("usuarios").document(userId)
            .update("puntos", puntos)
    }

    fun cambiarColorFondo(color: Int) {
        val mainLayout = findViewById<View>(R.id.main_layout)
        mainLayout.setBackgroundColor(color)
    }
}