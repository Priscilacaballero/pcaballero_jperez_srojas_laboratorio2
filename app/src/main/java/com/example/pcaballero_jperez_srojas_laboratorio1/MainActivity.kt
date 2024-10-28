package com.example.pcaballero_jperez_srojas_laboratorio1

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlin.random.Random

class MainActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var botonLanzarDado: Button
    private lateinit var dadoFragmento: DadoFragmento
    private lateinit var txtPuntos: TextView
    private lateinit var auth: FirebaseAuth
    private lateinit var db: DatabaseReference
    private var puntos: Int = 0
    private var userId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        auth = FirebaseAuth.getInstance()
        db = FirebaseDatabase.getInstance().reference
        userId = auth.currentUser?.uid ?: ""

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
        db.child("usuarios").child(userId).get()
            .addOnSuccessListener { snapshot ->
                puntos = snapshot.child("puntos").getValue(Int::class.java) ?: 0
                actualizarTextoPuntos()
            }
    }

    private fun actualizarTextoPuntos() {
        txtPuntos.text = "Puntos: $puntos"
    }

    override fun onClick(v: View?) {
        if (v?.id == R.id.btn_lanzar) {
            if (puntos >= 100) {
                puntos -= 100
                val valorDado = Random.nextInt(1, 7)

                if (valorDado == 6) {
                    puntos += 500
                }

                actualizarPuntosEnFirebase()
                registrarPuntos(valorDado)
                dadoFragmento.actualizarResultado(valorDado)
                actualizarTextoPuntos()
            } else {
                botonLanzarDado.isEnabled = false
                dadoFragmento.mostrarMensajeSinPuntos()
            }
        }
    }

    private fun actualizarPuntosEnFirebase() {
        db.child("usuarios").child(userId).child("puntos").setValue(puntos)
    }

    private fun registrarPuntos(valorDado: Int) {
        val registroId = db.child("registro_de_puntos").push().key ?: return
        val registroData = mapOf(
            "id" to registroId,
            "cantidad_puntos" to puntos,
            "usuario_id" to userId,
            "fecha_registro" to System.currentTimeMillis()
        )
        db.child("registro_de_puntos").child(registroId).setValue(registroData)
    }

    fun cambiarColorFondo(color: Int) {
        val mainLayout = findViewById<View>(R.id.main_layout)
        mainLayout.setBackgroundColor(color)
    }
}
