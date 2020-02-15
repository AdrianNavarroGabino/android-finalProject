package com.example.gastos

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.example.gastos.GastosActivity.Companion.cuentaSeleccionada

class RecyclerAdapterCategoria : RecyclerView.Adapter<RecyclerAdapterCategoria.ViewHolder>() {

    var categorias: MutableList<Categoria> = ArrayList()
    lateinit var context: Context

    fun RecyclerAdapter(categorias: MutableList<Categoria>, context: Context) {
        this.categorias = categorias
        this.context = context
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = categorias.get(position)
        holder.bind(item, context)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ViewHolder(
            layoutInflater.inflate(
                R.layout.item_categoria_list, parent, false
            )
        )
    }

    override fun getItemCount(): Int {
        return categorias.size
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        private val nombre = view.findViewById(
            R.id.nombreCategoria) as TextView
        private val saldo = view.findViewById(
            R.id.saldoCategoria) as TextView
        fun bind(categoria: Categoria, context: Context){
            nombre.text = categoria.nombre
            saldo.text = "%.2f".format(categoria.saldo) + " €"
            itemView.setOnLongClickListener{v ->

                val builder =
                    AlertDialog.Builder(v.context)

                val options: MutableList<String> = ArrayList()
                options.add("Eliminar categoria")

                val dataAdapter = ArrayAdapter(
                    v.context,
                    android.R.layout.simple_dropdown_item_1line, options
                )
                builder.setAdapter(
                    dataAdapter
                ) { dialog, which ->
                    when(which)
                    {
                        0 -> {
                            val builder = AlertDialog.Builder(v.context)
                            builder.setTitle("Eliminar categoría")
                            builder.setMessage("¿Estás seguro que quieres eliminar la categoría?")
                            builder.setPositiveButton(android.R.string.yes){_, _ ->
                                MainActivity.db.collection("categorias").document(categoria._id!!).get().apply {
                                    addOnSuccessListener { c ->

                                        var pvp = -(categoria!!.saldo!!)

                                        for(i in CuentaActivity.categorias)
                                        {
                                            pvp += i.saldo!!
                                        }

                                        MainActivity.db.collection("cuentas")
                                            .document(cuentaSeleccionada!!._id!!)
                                            .update("saldo", pvp)

                                        MainActivity.db.collection("cuentas").document(cuentaSeleccionada!!._id!!).get().apply {
                                            addOnSuccessListener {
                                                val categoriasCuentas = it["categorias"] as MutableList<String>
                                                categoriasCuentas.remove(categoria._id!!)
                                                MainActivity.db.collection("cuentas")
                                                    .document(cuentaSeleccionada!!._id!!)
                                                    .update("categorias", categoriasCuentas)
                                                    .addOnSuccessListener {
                                                        val myIntent = Intent(v.context, CuentaActivity::class.java)
                                                        v.context.startActivity(myIntent)
                                                    }
                                            }
                                        }

                                        MainActivity.db.collection("categorias").document(cuentaSeleccionada!!._id!!).delete()
                                            .addOnSuccessListener {
                                                CuentaActivity.categorias.remove(categoria)
                                            }
                                    }
                                }
                            }
                            builder.setNegativeButton(android.R.string.no, null)
                            builder.show()
                        }
                    }
                }
                val dialog = builder.create()
                dialog.show()
                true
            }
        }
    }
}