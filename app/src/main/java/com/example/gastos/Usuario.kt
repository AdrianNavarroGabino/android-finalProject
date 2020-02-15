package com.example.gastos

import com.google.firebase.Timestamp

class Usuario {
    var _id: String? = null
    var nombre: String? = null
    var apellidos: String? = null
    var correo : String? = null
    var ultimoAcceso: Timestamp? = null
    var cuentas : MutableList<String>? = null

    constructor(_id: String, nombre: String, apellidos: String, correo: String, ultimoAcceso: Timestamp) {
        this._id = _id
        this.nombre = nombre
        this.apellidos = apellidos
        this.correo = correo
        this.ultimoAcceso = ultimoAcceso
        cuentas = mutableListOf()
    }

    constructor(_id: String, nombre: String, apellidos: String, correo: String, ultimoAcceso: Timestamp, cuentas: MutableList<String>) {
        this._id = _id
        this.nombre = nombre
        this.apellidos = apellidos
        this.correo = correo
        this.ultimoAcceso = ultimoAcceso
        if(cuentas != null)
            this.cuentas = cuentas
        else
            this.cuentas = mutableListOf()
    }
}