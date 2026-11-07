package com.alejo.trueques.data.model

// Datos de una cuenta registrada en la app.
// La contraseña nunca se guarda tal cual: se guarda su huella (hash) y una "sal" aleatoria.
data class Usuario(
    val id: String,
    val nombre: String,
    val correo: String,
    val telefono: String,
    val barrio: String,
    val claveHash: String,
    val sal: String,
    val fechaRegistro: Long = System.currentTimeMillis()
)
