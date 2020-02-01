package com.example.gastos

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_gastos.*

class GastosActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gastos)

        val nombre = intent.getStringExtra("nombre")
        val correo = intent.getStringExtra("correo")
        val ultimoAcceso = intent.getStringExtra("ultimoAcceso")

        usuarioLbl.text = "¡Hola ".plus(nombre.capitalize()).plus("!")
        ultimoAccesoLbl.text = "Último acceso: ".plus(ultimoAcceso)
    }
}
