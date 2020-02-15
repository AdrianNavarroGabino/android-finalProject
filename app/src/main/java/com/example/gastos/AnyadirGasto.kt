package com.example.gastos

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import com.example.gastos.GastosActivity.Companion.cuentaSeleccionada
import com.example.gastos.MainActivity.Companion.db
import kotlinx.android.synthetic.main.activity_anyadir_gasto.*

class AnyadirGasto : AppCompatActivity() {

    private val spinnerMap : MutableMap<Int, Categoria> = mutableMapOf()
    private var spinnerPosition: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_anyadir_gasto)

        val titulo = intent.getIntExtra("titulo", -1)

        when(titulo)
        {
            0-> {
                anyadirTitle.text = "Añadir gasto"
                true
            }

            1-> {
                anyadirTitle.text = "Añadir ingreso"
                true
            }
        }

        val categoriasSpinner: MutableList<String> = mutableListOf()
        categoriasSpinner.add("Categoría")
        var index = 1

        for (i in CuentaActivity.categorias) {
            spinnerMap.put(index, i!!)
            index++
            categoriasSpinner.add(i.nombre!!)
        }

        val spinnerAdapter = ArrayAdapter<String>(
            this,
            android.R.layout.simple_spinner_item,
            categoriasSpinner
        )

        spinnerAdapter.setDropDownViewResource(
            android.R.layout.simple_spinner_dropdown_item
        )

        mySpinner.adapter = spinnerAdapter
        mySpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {

                override fun onNothingSelected(p0: AdapterView<*>?) {}

                override fun onItemSelected(
                    p0: AdapterView<*>?, p1: View?,
                    p2: Int, p3: Long
                ) {
                    spinnerPosition = p2
                }
            }

        cancelarBtn.setOnClickListener {
            val intentResult: Intent = Intent().apply {
                putExtra("updated", false)
            }

            setResult(Activity.RESULT_CANCELED, intentResult)
            finish()
        }

        anyadirBtn.setOnClickListener {

            if(efectivo.text.isNotEmpty() && spinnerPosition != 0)
            {
                if(titulo == 0) {
                    db.collection("categorias")
                        .document(spinnerMap.get(spinnerPosition!!)!!._id!!)
                        .update(
                            "saldo",
                            spinnerMap.get(spinnerPosition!!)!!.saldo!! - efectivo.text.toString().toDouble()
                        )
                        .addOnSuccessListener {

                            var pvp = -(efectivo.text.toString().toDouble())

                            for(i in CuentaActivity.categorias)
                            {
                                pvp += i.saldo!!
                            }

                            db.collection("cuentas")
                                .document(cuentaSeleccionada!!._id!!)
                                .update("saldo", pvp)

                            val intentResult: Intent = Intent().apply {
                                putExtra("updated", true)
                            }

                            setResult(Activity.RESULT_OK, intentResult)
                            finish()
                        }
                }
                else
                {
                    db.collection("categorias")
                        .document(spinnerMap.get(spinnerPosition!!)!!._id!!)
                        .update(
                            "saldo",
                            spinnerMap.get(spinnerPosition!!)!!.saldo!! + efectivo.text.toString().toDouble()
                        )
                        .addOnSuccessListener {
                            var pvp = efectivo.text.toString().toDouble()

                            for(i in CuentaActivity.categorias)
                            {
                                pvp += i.saldo!!
                            }

                            db.collection("cuentas")
                                .document(cuentaSeleccionada!!._id!!)
                                .update("saldo", pvp)

                            val intentResult: Intent = Intent().apply {
                                putExtra("updated", true)
                            }

                            setResult(Activity.RESULT_OK, intentResult)
                            finish()
                        }
                }
            }
            else
            {
                var message: String = ""

                if(efectivo.text.isEmpty())
                {
                    message += "Debes introducir efectivo"
                }
                if(efectivo.text.isEmpty() && spinnerPosition == 0)
                {
                    message += "\n"
                }
                if(spinnerPosition == 0)
                {
                    message += "Debes elegir categoría"
                }

                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            }
        }
    }
}
