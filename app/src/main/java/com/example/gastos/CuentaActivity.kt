package com.example.gastos

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.gastos.GastosActivity.Companion.cuentaSeleccionada
import kotlinx.android.synthetic.main.activity_cuenta.*
import kotlinx.android.synthetic.main.anyadir_cuenta.*
import java.lang.Exception

class CuentaActivity : AppCompatActivity() {

    private val myAdapter : RecyclerAdapterCategoria = RecyclerAdapterCategoria()

    companion object
    {
        val categorias: MutableList<Categoria> = mutableListOf()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cuenta)

        categorias.clear()

        MainActivity.db.collection("cuentas").document(cuentaSeleccionada!!._id!!).get().apply {
            addOnSuccessListener { c ->
                @Suppress("UNCHECKED_CAST")
                val iterator = (c["categorias"] as List<String>).iterator()

                iterator.forEach {
                    try {
                        MainActivity.db.collection("categorias").document(it).get().apply {
                            addOnSuccessListener { c ->
                                val nuevaCategoria = Categoria(
                                    c.id, c["nombre"] as String,
                                    c["saldo"] as Double
                                )
                                categorias.add(nuevaCategoria)
                                setUpRecyclerView()
                            }
                        }
                    }
                    catch (e: Exception){}
                }

                var pvp = 0
            }
        }

        setUpRecyclerView()

        anyadir.setOnClickListener{v ->
            val builder =
                AlertDialog.Builder(this)
            builder.setTitle("Elige una opción")

            val options: MutableList<String> = mutableListOf()
            options.add("Añadir gasto")
            options.add("Añadir ingreso")
            options.add("Añadir categoría")
            options.add("Realizar traspaso")

            val dataAdapter = ArrayAdapter(
                this,
                android.R.layout.simple_dropdown_item_1line, options
            )
            builder.setAdapter(
                dataAdapter
            ) { _, which ->
                when(which)
                {
                    0 -> {
                        val myIntent = Intent(v.context, AnyadirGasto::class.java).apply {
                            putExtra("titulo", 0)
                        }

                        startActivityForResult(myIntent, 1234)
                    }
                    1 -> {
                        val myIntent = Intent(v.context, AnyadirGasto::class.java).apply {
                            putExtra("titulo", 1)
                        }

                        startActivityForResult(myIntent, 1235)
                    }
                    2 -> {
                        val builder2 = android.app.AlertDialog.Builder(this)
                        builder2.apply {
                            val inflater = layoutInflater
                            setView(inflater.inflate(R.layout.anyadir_cuenta, null))
                            setPositiveButton(android.R.string.ok) { dialog, _ ->
                                if((dialog as android.app.AlertDialog).nombreCuenta.text.toString().isNotEmpty() && dialog.saldoCuenta.text.toString().isNotEmpty()) {
                                    val categoria = hashMapOf(
                                        "nombre" to (dialog as android.app.AlertDialog).nombreCuenta.text.toString(),
                                        "saldo" to dialog.saldoCuenta.text.toString().toDouble()
                                    )

                                    MainActivity.db.collection("categorias")
                                        .add(categoria)
                                        .addOnSuccessListener { categoriaCreada ->

                                            var pvp = dialog.saldoCuenta.text.toString().toDouble()

                                            for(i in categorias)
                                            {
                                                pvp += i.saldo!!
                                            }

                                            MainActivity.db.collection("cuentas")
                                                .document(cuentaSeleccionada!!._id!!)
                                                .update("saldo", pvp)

                                            MainActivity.db.collection("cuentas")
                                                .document(cuentaSeleccionada!!._id!!).get().apply {
                                                addOnSuccessListener {
                                                    @Suppress("UNCHECKED_CAST")
                                                    val tempCategorias: MutableList<String> =
                                                        it["categorias"] as MutableList<String>

                                                    tempCategorias.add(categoriaCreada.id)

                                                    MainActivity.db.collection("cuentas")
                                                        .document(cuentaSeleccionada!!._id!!)
                                                        .update("categorias", tempCategorias)
                                                        .addOnSuccessListener {
                                                            categorias.add(
                                                                Categoria(
                                                                    categoriaCreada.id,
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
                        builder2.show()
                    }
                    3 -> {
                        val myIntent = Intent(v.context, RealizarTraspaso::class.java)

                        startActivityForResult(myIntent, 1236)
                    }
                }
            }
            val dialog = builder.create()
            dialog.show()
        }
    }

    private fun setUpRecyclerView(){

        RVCategorias.setHasFixedSize(true)

        RVCategorias.layoutManager = LinearLayoutManager(this)

        myAdapter.RecyclerAdapter(categorias, this)

        RVCategorias.adapter = myAdapter
    }

    override fun onBackPressed() {
        val intent = Intent(applicationContext, GastosActivity::class.java)
        startActivity(intent)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data:
    Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1234 || requestCode == 1235 || requestCode == 1236) {
            if (resultCode == Activity.RESULT_OK) {
                val result = data?.getBooleanExtra(
                    "updated", true).toString().toBoolean()

                if(result)
                {
                    categorias.clear()

                    MainActivity.db.collection("cuentas").document(cuentaSeleccionada!!._id!!).get().apply {
                        addOnSuccessListener { c ->
                            @Suppress("UNCHECKED_CAST")
                            val iterator = (c["categorias"] as List<String>).iterator()

                            iterator.forEach {
                                try {
                                    MainActivity.db.collection("categorias").document(it).get().apply {
                                        addOnSuccessListener { c ->
                                            val nuevaCategoria = Categoria(
                                                c.id, c["nombre"] as String,
                                                c["saldo"] as Double
                                            )
                                            categorias.add(nuevaCategoria)
                                            setUpRecyclerView()
                                        }
                                    }
                                }
                                catch (e: Exception){}
                            }
                        }
                    }

                    setUpRecyclerView()
                }
            }
        }
    }
}
