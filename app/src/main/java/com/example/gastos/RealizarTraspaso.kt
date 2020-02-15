package com.example.gastos

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import com.example.gastos.MainActivity.Companion.db
import kotlinx.android.synthetic.main.activity_realizar_traspaso.*

class RealizarTraspaso : AppCompatActivity() {

    private val spinnerMap : MutableMap<Int, Categoria> = mutableMapOf()
    private var spinnerPositionOrigen: Int? = null
    private var spinnerPositionDestino: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_realizar_traspaso)

        val categoriasSpinnerOrigen: MutableList<String> = mutableListOf()
        val categoriasSpinnerDestino: MutableList<String> = mutableListOf()
        categoriasSpinnerOrigen.add("Categoría de origen")
        categoriasSpinnerDestino.add("Categoría de destino")
        var index = 1

        for (i in CuentaActivity.categorias) {
            spinnerMap.put(index, i!!)
            index++
            categoriasSpinnerOrigen.add(i.nombre!!)
            categoriasSpinnerDestino.add(i.nombre!!)
        }

        val spinnerAdapterOrigen = ArrayAdapter<String>(
            this,
            android.R.layout.simple_spinner_item,
            categoriasSpinnerOrigen
        )

        val spinnerAdapterDestino = ArrayAdapter<String>(
            this,
            android.R.layout.simple_spinner_item,
            categoriasSpinnerDestino
        )

        spinnerAdapterOrigen.setDropDownViewResource(
            android.R.layout.simple_spinner_dropdown_item
        )
        spinnerAdapterDestino.setDropDownViewResource(
            android.R.layout.simple_spinner_dropdown_item
        )

        spinnerOrigen.adapter = spinnerAdapterOrigen
        spinnerDestino.adapter = spinnerAdapterDestino

        spinnerOrigen.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {

                override fun onNothingSelected(p0: AdapterView<*>?) {}

                override fun onItemSelected(
                    p0: AdapterView<*>?, p1: View?,
                    p2: Int, p3: Long
                ) {
                    spinnerPositionOrigen = p2
                }
            }

        spinnerDestino.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {

                override fun onNothingSelected(p0: AdapterView<*>?) {}

                override fun onItemSelected(
                    p0: AdapterView<*>?, p1: View?,
                    p2: Int, p3: Long
                ) {
                    spinnerPositionDestino = p2
                }
            }

        cancelarBtn2.setOnClickListener {
            val intentResult: Intent = Intent().apply {
                putExtra("updated", false)
            }

            setResult(Activity.RESULT_CANCELED, intentResult)
            finish()
        }

        anyadirBtn2.setOnClickListener {
            if(efectivo2.text.isNotEmpty() && spinnerPositionOrigen != 0 && spinnerPositionDestino != 0)
            {
                db.collection("categorias")
                    .document(spinnerMap.get(spinnerPositionOrigen!!)!!._id!!)
                    .update("saldo",
                        spinnerMap.get(spinnerPositionOrigen!!)!!.saldo!! -
                                efectivo2.text.toString().toDouble())
                    .addOnSuccessListener {
                        db.collection("categorias")
                            .document(spinnerMap.get(spinnerPositionDestino!!)!!._id!!)
                            .update(
                                "saldo", spinnerMap
                                    .get(spinnerPositionDestino!!)!!.saldo!! +
                                        efectivo2.text.toString().toDouble())
                            .addOnSuccessListener {
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
                var mensajeEmpezado = false
                var message: String = ""

                if(efectivo2.text.isEmpty())
                {
                    message += "Debes introducir efectivo"
                    mensajeEmpezado = true
                }
                if(spinnerPositionOrigen == 0)
                {
                    if(mensajeEmpezado)
                        message += "\n"
                    message += "Debes elegir categoría de origen"
                    mensajeEmpezado = true
                }
                if(spinnerPositionDestino == 0)
                {
                    if(mensajeEmpezado)
                        message += "\n"
                    message += "Debes elegir categoría de destino"
                }

                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            }
        }
    }
}
