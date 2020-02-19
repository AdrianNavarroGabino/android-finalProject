package com.example.gastos

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Adapter
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gastos.MainActivity.Companion.db
import com.example.gastos.MainActivity.Companion.usuarioLogueado
import com.example.gastos.RecyclerAdapterCuenta
import com.google.firebase.Timestamp
import kotlinx.android.synthetic.main.activity_anyadir_gasto.*
import kotlinx.android.synthetic.main.activity_gastos.*
import kotlinx.android.synthetic.main.anyadir_cuenta.*
import java.lang.Exception
import java.text.SimpleDateFormat


class GastosActivity : AppCompatActivity() {

    companion object
    {
        val cuentas: MutableList<Cuenta> = mutableListOf()
        val myAdapter : RecyclerAdapterCuenta = RecyclerAdapterCuenta()
        var cuentaSeleccionada: Cuenta? = null
    }

    object CommonUtils {
        fun refresh()
        {
            GastosActivity.CommonUtils
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gastos)

        val nombre = usuarioLogueado!!.nombre!!
        val pattern = "dd/MM/yyyy HH:mm"
        val simpleDateFormat = SimpleDateFormat(pattern)
        val ultimoAcceso = simpleDateFormat.format((usuarioLogueado!!.ultimoAcceso as Timestamp).toDate())

        usuarioLbl.text = "¡Hola ".plus(nombre.capitalize()).plus("!")
        ultimoAccesoLbl.text = "Último acceso: ".plus(ultimoAcceso)

        cuentas.clear()

        db.collection("usuarios").document(usuarioLogueado!!._id!!).get().apply {
            addOnSuccessListener { u ->
                val iterator = (u["cuentas"] as List<String>).iterator()

                iterator.forEach {
                    try {
                        db.collection("cuentas").document(it).get().apply {
                            addOnSuccessListener { c ->
                                println("id ".plus(c.id))
                                println("_id ".plus(c["_id"]))
                                val nuevaCuenta = Cuenta(
                                    c.id, c["nombre"] as String,
                                    c["saldo"] as Double, c["categorias"] as MutableList<String>
                                )
                                cuentas.add(nuevaCuenta)
                                setUpRecyclerView()
                            }
                        }
                    }
                    catch (e: Exception){}
                }
            }
        }


        anyadir.setOnClickListener{
            val builder =
                AlertDialog.Builder(this)
            builder.setTitle("Elige una opción")

            val options: MutableList<String> = mutableListOf()
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
                        val builder = android.app.AlertDialog.Builder(this)
                        builder.apply {
                            val inflater = layoutInflater
                            setView(inflater.inflate(R.layout.anyadir_cuenta, null))
                            setPositiveButton(android.R.string.ok) { dialog, _ ->
                                if((dialog as android.app.AlertDialog).nombreCuenta.text.toString().isNotEmpty() && dialog.saldoCuenta.text.toString().isNotEmpty()) {
                                    val cuenta = hashMapOf(
                                        "nombre" to dialog.nombreCuenta.text.toString(),
                                        "saldo" to dialog.saldoCuenta.text.toString().toDouble(),
                                        "categorias" to listOf<String>()
                                    )

                                    db.collection("cuentas")
                                        .add(cuenta)
                                        .addOnSuccessListener { cuentaCreada ->

                                            val categoria = hashMapOf(
                                                "nombre" to "No asignado",
                                                "saldo" to dialog.saldoCuenta.text.toString().toDouble()
                                            )

                                            db.collection("categorias")
                                                .add(categoria)
                                                .addOnSuccessListener { cat ->

                                                    val idCategoriaUpdate = listOf<String>(cat.id)
                                                    db.collection("cuentas")
                                                        .document(cuentaCreada.id)
                                                        .update("categorias", idCategoriaUpdate)
                                                }

                                            db.collection("usuarios")
                                                .document(usuarioLogueado!!._id!!).get().apply {
                                                addOnSuccessListener {
                                                    val tempCuentas: MutableList<String> =
                                                        it["cuentas"] as MutableList<String>

                                                    tempCuentas.add(cuentaCreada.id)

                                                    db.collection("usuarios")
                                                        .document(usuarioLogueado!!._id!!)
                                                        .update("cuentas", tempCuentas)
                                                        .addOnSuccessListener {
                                                            cuentas.add(
                                                                Cuenta(
                                                                    cuentaCreada.id,
                                                                    dialog.nombreCuenta
                                                                        .text.toString(),
                                                                    dialog.saldoCuenta.text.toString().toDouble()
                                                                )
                                                            )
                                                            setUpRecyclerView()
                                                        }
                                                }
                                            }
                                        }
                                }
                                else
                                {
                                    var message: String = ""

                                    if(dialog.nombreCuenta.text.toString().isEmpty())
                                    {
                                        message += "Debes introducir el nombre de la cuenta"
                                    }
                                    if(dialog.nombreCuenta.text.toString().isEmpty() && dialog.saldoCuenta.text.toString().isEmpty())
                                    {
                                        message += "\n"
                                    }
                                    if(dialog.saldoCuenta.text.toString().isEmpty())
                                    {
                                        message += "Debes introducir el saldo"
                                    }

                                    Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
                                }
                            }
                            setNegativeButton(android.R.string.no) { dialog, _ ->
                                dialog.dismiss()
                            }
                        }
                        builder.show()
                    }
                }
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
            R.id.menu_cerrarSesion -> {
                val myIntent = Intent(applicationContext, MainActivity::class.java)
                startActivity(myIntent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    fun setUpRecyclerView(){

        RVCuentas.setHasFixedSize(true)

        RVCuentas.layoutManager = LinearLayoutManager(this)

        myAdapter.RecyclerAdapter(cuentas, this)

        RVCuentas.adapter = myAdapter
    }

    override fun onBackPressed() {
        val intent = Intent(Intent.ACTION_MAIN)
        intent.addCategory(Intent.CATEGORY_HOME)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }
}
