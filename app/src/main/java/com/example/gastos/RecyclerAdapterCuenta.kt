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
import com.example.gastos.MainActivity.Companion.db

class RecyclerAdapterCuenta : RecyclerView.Adapter<RecyclerAdapterCuenta.ViewHolder>() {

    var cuentas: MutableList<Cuenta> = ArrayList()
    lateinit var context: Context

    fun RecyclerAdapter(cuentas: MutableList<Cuenta>, context: Context) {
        this.cuentas = cuentas
        this.context = context
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = cuentas.get(position)
        holder.bind(item, context)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ViewHolder(
            layoutInflater.inflate(
                R.layout.item_cuenta_list, parent, false
            )
        )
    }

    override fun getItemCount(): Int {
        return cuentas.size
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        private val nombre = view.findViewById(
            R.id.nombreCuenta) as TextView
        private val saldo = view.findViewById(
            R.id.saldoCuenta) as TextView

        fun bind(cuenta: Cuenta, context: Context){
            nombre.text = cuenta.nombre
            saldo.text = "%.2f".format(cuenta.saldo) + " €"
            itemView.setOnClickListener {
                cuentaSeleccionada = cuenta
                val myIntent: Intent = Intent(it.context, CuentaActivity::class.java)
                it.context.startActivity(myIntent)
            }
            itemView.setOnLongClickListener{ v->

                val builder =
                    AlertDialog.Builder(v.context)

                val options: MutableList<String> = ArrayList()
                options.add("Eliminar cuenta")

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
                            builder.setTitle("Eliminar cuenta")
                            builder.setMessage("¿Estás seguro que quieres eliminar la cuenta?")
                            builder.setPositiveButton(android.R.string.yes){_, _ ->
                                db.collection("cuentas").document(cuenta._id!!).get().apply {
                                    addOnSuccessListener { c ->
                                        val categoriasCuenta = c["categorias"] as MutableList<String>

                                        for(i in categoriasCuenta)
                                        {
                                            db.collection("categorias").document(i).delete()
                                        }

                                        db.collection("usuarios").document(MainActivity.usuarioLogueado!!._id!!).get().apply {
                                            addOnSuccessListener {
                                                val cuentasUsuario = it["cuentas"] as MutableList<String>
                                                cuentasUsuario.remove(cuenta._id!!)
                                                db.collection("usuarios")
                                                    .document(MainActivity.usuarioLogueado!!._id!!)
                                                    .update("cuentas", cuentasUsuario)
                                                    .addOnSuccessListener {
                                                        val myIntent = Intent(v.context, GastosActivity::class.java)
                                                        v.context.startActivity(myIntent)
                                                    }
                                            }
                                        }

                                        db.collection("cuentas").document(cuenta._id!!).delete()
                                            .addOnSuccessListener {
                                                GastosActivity.cuentas.remove(cuenta)
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