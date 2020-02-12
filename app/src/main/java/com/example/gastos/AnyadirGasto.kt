package com.example.gastos

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import com.example.gastos.MainActivity.Companion.idUsuario
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_anyadir_gasto.*

class AnyadirGasto : AppCompatActivity() {
    val db : FirebaseFirestore = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_anyadir_gasto)



        /*val adapter = ArrayAdapter.createFromResource(
            this,
            R.array.array_subjects,
            android.R.layout.simple_spinner_item
        )*/

        cuentaSpinner
    }
}
