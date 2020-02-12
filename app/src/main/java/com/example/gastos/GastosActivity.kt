package com.example.gastos

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.recyclerview.RecyclerAdapter
import kotlinx.android.synthetic.main.activity_gastos.*


class GastosActivity : AppCompatActivity() {

    private val myAdapter : RecyclerAdapter = RecyclerAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gastos)

        val nombre = intent.getStringExtra("nombre")
        val correo = intent.getStringExtra("correo")
        val ultimoAcceso = intent.getStringExtra("ultimoAcceso")

        usuarioLbl.text = "¡Hola ".plus(nombre.capitalize()).plus("!")
        ultimoAccesoLbl.text = "Último acceso: ".plus(ultimoAcceso)
        setUpRecyclerView()

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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.menu1, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId)
        {
            R.id.menu_eliminarGasto -> {
                Toast.makeText(applicationContext, "Has elegido eliminar gasto", Toast.LENGTH_SHORT).show()
                true
            }
            R.id.menu_eliminarCategoria -> {
                Toast.makeText(applicationContext, "Has elegido eliminar categoría", Toast.LENGTH_SHORT).show()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun setUpRecyclerView(){

        RVCuentas.setHasFixedSize(true)

        RVCuentas.layoutManager = LinearLayoutManager(this)

        myAdapter.RecyclerAdapter(getCourses(), this)

        RVCuentas.adapter = myAdapter
    }
    private fun getCourses(): MutableList<Cuenta> {
        val cuentas: MutableList<Cuenta> = arrayListOf()
        //Cuentas de prueba
        cuentas.add(Cuenta("1", "General", 1000.0))
        cuentas.add(Cuenta("2", "Secundaria", 2300.45))
        cuentas.add(Cuenta("3", "Boda", 10056.21))
        return cuentas
    }
}
