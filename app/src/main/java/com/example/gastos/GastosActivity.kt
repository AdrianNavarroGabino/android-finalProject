package com.example.gastos

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
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

        anyadir.setOnClickListener{
            val builder =
                AlertDialog.Builder(this)
            builder.setTitle("Elige una opción")

            val options: MutableList<String> = ArrayList()
            options.add("Añadir gasto")
            options.add("Añadir ingreso")
            options.add("Añadir categoría")
            options.add("Añadir cuenta")

            val dataAdapter = ArrayAdapter(
                this,
                android.R.layout.simple_dropdown_item_1line, options
            )
            builder.setAdapter(
                dataAdapter
            ) { dialog, which ->
                when(which)
                {
                    0 -> {
                        val myIntent = Intent(this, AnyadirGasto::class.java)
                        startActivityForResult(myIntent, 1110)
                    }
                    1 -> {
                        val myIntent = Intent(this, AnyadirGasto::class.java)
                        startActivityForResult(myIntent, 1111)
                    }
                    2 -> {
                        val myIntent = Intent(this, AnyadirGasto::class.java)
                        startActivityForResult(myIntent, 1112)
                    }
                    3 -> {
                        val myIntent = Intent(this, AnyadirGasto::class.java)
                        startActivityForResult(myIntent, 1113)
                    }
                }
                Toast.makeText(
                    applicationContext,
                    "Has elegido " + options[which],
                    Toast.LENGTH_LONG
                ).show()
            }
            val dialog = builder.create()
            dialog.show()
        }
    }
}
