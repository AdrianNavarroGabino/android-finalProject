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


class MainActivity : AppCompatActivity() {


    companion object {
        val db: FirebaseFirestore = FirebaseFirestore.getInstance()
        var usuarioLogueado : Usuario? = null
    }

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
                                var usuarioCorrecto = false
                                for (usuario in it) {
                                    if(usuario["correo"].toString().equals(correoLog))
                                    {
                                        usuarioCorrecto = true
                                        val refUpdTopic = db.collection("usuarios").document(usuario.id!!)

                                        refUpdTopic
                                            .update(mapOf(
                                                "ultimoacceso" to usuario["accesoactual"] as Timestamp,
                                                "accesoactual" to Timestamp.now()
                                            ))
                                            .addOnSuccessListener {
                                                usuarioLogueado = Usuario(usuario.id as String,
                                                    usuario["nombre"] as String,
                                                    usuario["apellidos"] as String,
                                                    usuario["correo"] as String,
                                                    usuario["ultimoacceso"] as Timestamp,
                                                    usuario["cuentas"] as MutableList<String>)

                                                val myIntent = Intent(applicationContext, GastosActivity::class.java)

                                                startActivity(myIntent)
                                            }
                                    }
                                }

                                if(!usuarioCorrecto)
                                {
                                    Toast.makeText(
                                        applicationContext,
                                        "Correo y/o contraseña incorrectos",
                                        Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                }
                else
                {
                    Toast.makeText(applicationContext, "Rellene todos los campos", Toast.LENGTH_SHORT).show()
                    dialog.show()
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
                    pass.isNotEmpty() && pass.equals(pass2)) {

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
                                        "accesoactual" to Timestamp.now(),
                                        "cuentas" to listOf<String>())

                                    db.collection("usuarios")
                                        .add(user)
                                        .addOnSuccessListener { a ->
                                            Toast.makeText(applicationContext, "Usuario creado", Toast.LENGTH_SHORT).show()
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
                else
                {
                    if(!pass.equals(pass2))
                    {
                        Toast.makeText(
                            applicationContext,
                            "Las contraseñas no coinciden",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    else {
                        Toast.makeText(
                            applicationContext,
                            "Rellene todos los campos",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    dialog.show()
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

    override fun onBackPressed() {
        val intent = Intent(Intent.ACTION_MAIN)
        intent.addCategory(Intent.CATEGORY_HOME)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }
}