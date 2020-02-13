package com.example.gastos

class Categoria {
    var _id: String? = null
    var nombre: String? = null
    var saldo: Double? = null
    constructor(_id: String, nombre: String, saldo: Double) {
        this._id = _id
        this.nombre = nombre
        this.saldo = saldo
    }
}