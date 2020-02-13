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
import com.example.gastos.MainActivity.Companion.db
import com.example.recyclerview.RecyclerAdapterCuenta
import com.google.firebase.Timestamp
import com.google.firebase.firestore.CollectionReference
import kotlinx.android.synthetic.main.activity_gastos.*
import kotlinx.android.synthetic.main.anyadir_cuenta.*
import kotlinx.android.synthetic.main.login.*
import java.text.SimpleDateFormat


class GastosActivity : AppCompatActivity() {

    private val myAdapter : RecyclerAdapterCuenta = RecyclerAdapterCuenta()

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
                        Toast.makeText(this, "Añadir gasto", Toast.LENGTH_SHORT).show()
                    }
                    1 -> {
                        Toast.makeText(this, "Añadir ingreso", Toast.LENGTH_SHORT).show()
                    }
                    2 -> {
                        val builder = android.app.AlertDialog.Builder(this)
                        builder.apply {
                            val inflater = layoutInflater
                            setView(inflater.inflate(R.layout.anyadir_cuenta, null))
                            setPositiveButton(android.R.string.ok) { dialog, _ ->
                                val cuenta = hashMapOf(
                                    "nombre" to (dialog as android.app.AlertDialog).nombreCuenta.text.toString(),
                                    "saldo" to dialog.saldoCuenta.text.toString().toDouble()
                                )

                                db.collection("cuentas")
                                    .add(cuenta)
                                    .addOnSuccessListener {
                                        Toast.makeText(applicationContext, it.id + "", Toast.LENGTH_SHORT).show()
                                    }
                            }
                            setNegativeButton(android.R.string.no) { dialog, _ ->
                                dialog.dismiss()
                            }
                        }
                        builder.show()
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

        myAdapter.RecyclerAdapter(getCuentas(), this)

        RVCuentas.adapter = myAdapter
    }

    private fun getCuentas(): MutableList<Cuenta> {
        val cuentas: MutableList<Cuenta> = arrayListOf()
        //Cuentas de prueba
        cuentas.add(Cuenta("1", "General", 1000.0))
        cuentas.add(Cuenta("2", "Secundaria", 2300.45))
        cuentas.add(Cuenta("3", "Boda", 10056.21))
        return cuentas
    }
}
