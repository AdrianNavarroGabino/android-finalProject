package com.example.gastos

import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.Timestamp
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.login.*
import kotlinx.android.synthetic.main.register.*
import java.security.MessageDigest
import java.text.SimpleDateFormat


class MainActivity : AppCompatActivity() {

    val db : FirebaseFirestore = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        entrarBtn.setOnClickListener {
            loginDialog()
        }

        registrarBtn.setOnClickListener {
            registerDialog()
        }
    }

    private fun loginDialog() {
        val builder = AlertDialog.Builder(this)
        builder.apply {
            val inflater = layoutInflater
            setView(inflater.inflate(R.layout.login, null))
            setPositiveButton(android.R.string.ok) { dialog, _ ->
                val correoLog = (dialog as AlertDialog).correoLogin.text.toString()
                val passLog = sha1(dialog.passwordLogin.text.toString())

                if(correoLog.isNotEmpty() && passLog.isNotEmpty())
                {
                    val usuarios: CollectionReference = db.collection("usuarios")

                    usuarios
                        .whereEqualTo("correo", correoLog)
                        .whereEqualTo("password", passLog)
                        .get().apply {
                            addOnSuccessListener {
                                for (usuario in it) {
                                    if(usuario["correo"].toString().equals(correoLog))
                                    {
                                        val refUpdTopic = db.collection("usuarios").document(usuario.id)

                                        refUpdTopic
                                            .update(mapOf(
                                                "ultimoacceso" to usuario["accesoactual"] as Timestamp,
                                                "accesoactual" to Timestamp.now()
                                            ))
                                            .addOnSuccessListener {
                                                val pattern = "dd/MM/yyyy HH:mm"
                                                val simpleDateFormat = SimpleDateFormat(pattern)
                                                val fecha = simpleDateFormat.format((usuario!!["ultimoacceso"] as Timestamp).toDate())
                                                val myIntent = Intent(applicationContext, GastosActivity::class.java).apply {
                                                    putExtra("nombre", usuario["nombre"].toString())
                                                    putExtra("correo", usuario["correo"].toString())
                                                    putExtra("ultimoAcceso", fecha)
                                                }

                                                println((usuario["accesoactual"] as Timestamp).toDate())

                                                startActivity(myIntent)
                                            }
                                    }
                                }
                            }
                        }
                }
            }
            setNegativeButton(android.R.string.no) { dialog, _ ->
                dialog.dismiss()
            }
        }
        builder.show()
    }

    private fun registerDialog() {
        val builder = AlertDialog.Builder(this)
        builder.apply {
            val inflater = layoutInflater
            setView(inflater.inflate(R.layout.register, null))
            setPositiveButton(android.R.string.ok) { dialog, _ ->
                val correoReg = (dialog as AlertDialog).correo.text.toString()
                val nombreReg = dialog.nombre.text.toString()
                val apellidosReg = dialog.apellidos.text.toString()
                val pass = dialog.password.text.toString()
                val pass2 = dialog.password2.text.toString()

                if(correoReg.isNotEmpty() && nombreReg.isNotEmpty() && apellidosReg.isNotEmpty() &&
                    pass.isNotEmpty() && pass.equals(pass2))
                {
                    val users: CollectionReference = db.collection("usuarios")
                    var found = false

                    users
                        .whereEqualTo("correo", correoReg)
                        .get().apply {
                            addOnSuccessListener {
                                for (user in it) {
                                    if(user["correo"].toString().equals(correoReg))
                                    {
                                        found = true
                                    }
                                }

                                if(!found)
                                {
                                    val user = hashMapOf(
                                        "correo" to correoReg,
                                        "nombre" to nombreReg,
                                        "apellidos" to apellidosReg,
                                        "password" to sha1(pass),
                                        "ultimoacceso" to Timestamp.now(),
                                        "accesoactual" to Timestamp.now())

                                    db.collection("usuarios")
                                        .add(user)
                                        .addOnSuccessListener { _ ->
                                            dialog.dismiss()
                                        }
                                        .addOnFailureListener{ _ ->
                                            Toast.makeText(applicationContext, "Error añadiendo usuario", Toast.LENGTH_SHORT).show()
                                        }
                                }
                                else
                                {
                                    Toast.makeText(applicationContext, "El usuario ya existe", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                }
            }
            setNegativeButton(android.R.string.no) { dialog, _ ->
                dialog.dismiss()
            }
        }
        builder.show()
    }

    fun sha1(input: String) = hashString("SHA-1", input)

    private fun hashString(type: String, input: String): String {
        val bytes = MessageDigest
            .getInstance(type)
            .digest(input.toByteArray())
        return printHexBinary(bytes).toUpperCase()
    }

    val HEX_CHARS = "0123456789ABCDEF".toCharArray()

    fun printHexBinary(data: ByteArray): String {
        val r = StringBuilder(data.size * 2)
        data.forEach { b ->
            val i = b.toInt()
            r.append(HEX_CHARS[i shr 4 and 0xF])
            r.append(HEX_CHARS[i and 0xF])
        }
        return r.toString()
    }
}
