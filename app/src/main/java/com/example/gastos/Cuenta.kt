package com.example.gastos

class Cuenta {
    var _id: String? = null
    var nombre: String? = null
    var saldo: Double? = null
    var categorias : MutableList<String>? = null

    constructor(_id: String, nombre: String, saldo: Double) {
        this._id = _id
        this.nombre = nombre
        this.saldo = saldo
        categorias = mutableListOf()
    }

    constructor(_id: String, nombre: String, saldo: Double, categorias: MutableList<String>) {
        this._id = _id
        this.nombre = nombre
        this.saldo = saldo
        this.categorias = categorias
    }
}