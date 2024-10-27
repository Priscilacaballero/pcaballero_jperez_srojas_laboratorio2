package com.example.pcaballero_jperez_srojas_laboratorio1

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import android.widget.ImageView
import androidx.core.content.ContextCompat

class DadoFragmento : Fragment() {
    private lateinit var imagenDado: ImageView
    private lateinit var fragmentLayout: View

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.dado_fragmento, container, false)
        imagenDado = view.findViewById(R.id.imgvw_dadosresultado)
        fragmentLayout = view.findViewById(R.id.fragment_layout)
        return view
    }

    fun actualizarResultado(valorDado: Int) {
        val activity = requireActivity() as MainActivity

        if (valorDado == 6) {
            val colorDorado = ContextCompat.getColor(requireContext(), R.color.dorado)
            fragmentLayout.setBackgroundColor(colorDorado)
            activity.cambiarColorFondo(colorDorado)
            Toast.makeText(requireContext(), "¡Has ganado 500 puntos extra!", Toast.LENGTH_SHORT).show()
        } else {
            val colorGrisClaro = ContextCompat.getColor(requireContext(), R.color.gris_claro)
            fragmentLayout.setBackgroundColor(colorGrisClaro)
            activity.cambiarColorFondo(colorGrisClaro)
            Toast.makeText(requireContext(), "Resultado: $valorDado", Toast.LENGTH_SHORT).show()
        }

        val recursoImagenDado = when (valorDado) {
            1 -> R.drawable.dice_1
            2 -> R.drawable.dice_2
            3 -> R.drawable.dice_3
            4 -> R.drawable.dice_4
            5 -> R.drawable.dice_5
            else -> R.drawable.dice_6
        }

        imagenDado.setImageResource(recursoImagenDado)
    }

    fun mostrarMensajeSinPuntos() {
        Toast.makeText(requireContext(), "No tienes suficientes puntos para lanzar", Toast.LENGTH_LONG).show()
    }
}