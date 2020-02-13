package com.example.gastos

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_cuenta.*

class CuentaActivity : AppCompatActivity() {

    private val myAdapter : RecyclerAdapterCategoria = RecyclerAdapterCategoria()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cuenta)

        var id = intent.getStringExtra("id")

        setUpRecyclerView()

        anyadir.setOnClickListener{
            val builder =
                AlertDialog.Builder(this)
            builder.setTitle("Elige una opción")

            val options: MutableList<String> = ArrayList()
            options.add("Añadir gasto")
            options.add("Añadir ingreso")
            options.add("Añadir categoría")

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
                        Toast.makeText(this, "Añadir categoría", Toast.LENGTH_SHORT).show()
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

    private fun setUpRecyclerView(){

        RVCategorias.setHasFixedSize(true)

        RVCategorias.layoutManager = LinearLayoutManager(this)

        myAdapter.RecyclerAdapter(getCategorias(), this)

        RVCategorias.adapter = myAdapter
    }

    private fun getCategorias(): MutableList<Categoria> {
        val categorias: MutableList<Categoria> = arrayListOf()
        //categorias de prueba
        categorias.add(Categoria("1", "Luz", 100.0))
        categorias.add(Categoria("2", "Agua", 60.23))
        categorias.add(Categoria("3", "Ahorros", 489.01))
        return categorias
    }
}
