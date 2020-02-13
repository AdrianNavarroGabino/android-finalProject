package com.example.gastos

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView

class RecyclerAdapterCategoria : RecyclerView.Adapter<RecyclerAdapterCategoria.ViewHolder>() {
    // Variables internes de la clase
    var categorias: MutableList<Categoria> = ArrayList()
    lateinit var context: Context
    // Constructor de la clase
    fun RecyclerAdapter(categorias: MutableList<Categoria>, context: Context) {
        this.categorias = categorias
        this.context = context
    }
    // Este método se encarga de pasar los objetos al ViewHolder
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = categorias.get(position)
        holder.bind(item, context)
    }
    // Es el encargado de devolver el ViewHolder ya configurado
    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ViewHolder(
            layoutInflater.inflate(
                R.layout.item_categoria_list, parent, false
            )
        )
    }
    // Devuelve el tamaño del array
    override fun getItemCount(): Int {
        return categorias.size
    }
    // Esta clase se encarga de rellenar cada una de las vistas que
    // se inflarán en el RecyclerView
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        // Aquí es necesario utilizar findViewById para localizar el elemento
        // de la vista que se pasa como parámetro
        private val nombre = view.findViewById(
            R.id.nombreCategoria) as TextView
        private val saldo = view.findViewById(
            R.id.saldoCategoria) as TextView
        fun bind(cuenta: Categoria, context: Context){
            nombre.text = cuenta.nombre
            saldo.text = "%.2f".format(cuenta.saldo) + " €"
            itemView.setOnLongClickListener{

                val builder =
                    AlertDialog.Builder(it.context)

                val options: MutableList<String> = ArrayList()
                options.add("Eliminar categoria")

                val dataAdapter = ArrayAdapter(
                    it.context,
                    android.R.layout.simple_dropdown_item_1line, options
                )
                builder.setAdapter(
                    dataAdapter
                ) { dialog, which ->
                    when(which)
                    {
                        0 -> {
                            Toast.makeText(it.context, "Eliminar", Toast.LENGTH_SHORT).show()
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