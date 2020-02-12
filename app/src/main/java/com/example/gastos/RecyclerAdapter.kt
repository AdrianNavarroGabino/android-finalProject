package com.example.recyclerview

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.gastos.Cuenta
import com.example.gastos.R

class RecyclerAdapter : RecyclerView.Adapter<RecyclerAdapter.ViewHolder>() {
    // Variables internes de la clase
    var cuentas: MutableList<Cuenta> = ArrayList()
    lateinit var context: Context
    // Constructor de la clase
    fun RecyclerAdapter(cuentas: MutableList<Cuenta>, context: Context) {
        this.cuentas = cuentas
        this.context = context
    }
    // Este método se encarga de pasar los objetos al ViewHolder
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = cuentas.get(position)
        holder.bind(item, context)
    }
    // Es el encargado de devolver el ViewHolder ya configurado
    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ViewHolder(
            layoutInflater.inflate(
                R.layout.item_cuenta_list, parent, false
            )
        )
    }
    // Devuelve el tamaño del array
    override fun getItemCount(): Int {
        return cuentas.size
    }
    // Esta clase se encarga de rellenar cada una de las vistas que
    // se inflarán en el RecyclerView
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        // Aquí es necesario utilizar findViewById para localizar el elemento
        // de la vista que se pasa como parámetro
        private val nombre = view.findViewById(
            R.id.nombreCuenta) as TextView
        private val saldo = view.findViewById(
            R.id.saldoCuenta) as TextView
        fun bind(cuenta: Cuenta, context: Context){
            nombre.text = cuenta.nombre
            saldo.text = "%.2f".format(cuenta.saldo) + " €"
            itemView.setOnClickListener {
                Toast.makeText(
                    context,
                    cuenta.nombre,
                    Toast.LENGTH_SHORT
                ).show()
            }
            itemView.setOnLongClickListener{
                Toast.makeText(
                    context,
                    "Click largo",
                    Toast.LENGTH_SHORT
                ).show()
                true
            }
        }
    }
}