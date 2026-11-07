package com.alejo.trueques.data.repository

import android.content.Context
import com.alejo.trueques.data.model.Usuario
import org.json.JSONArray
import org.json.JSONObject
import java.security.MessageDigest
import java.security.SecureRandom
import java.util.UUID

// Guarda las cuentas y la sesión activa en el almacenamiento interno del celular.
class UsuarioRepository(context: Context) {

    private val prefs = context.getSharedPreferences("trueques_usuarios", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_USUARIOS = "usuarios"
        private const val KEY_SESION = "sesion_usuario_id"
    }

    fun registrar(
        nombre: String,
        correo: String,
        telefono: String,
        barrio: String,
        clave: String
    ): Result<Usuario> {
        val correoLimpio = correo.trim().lowercase()
        val usuarios = cargarTodos()

        if (usuarios.any { it.correo == correoLimpio }) {
            return Result.failure(IllegalArgumentException("Ya existe una cuenta con ese correo"))
        }

        val sal = generarSal()
        val nuevo = Usuario(
            id = UUID.randomUUID().toString(),
            nombre = nombre.trim(),
            correo = correoLimpio,
            telefono = telefono.trim(),
            barrio = barrio.trim(),
            claveHash = cifrar(clave, sal),
            sal = sal
        )
        guardarTodos(usuarios + nuevo)
        guardarSesion(nuevo.id)
        return Result.success(nuevo)
    }

    // Devuelve el usuario si el correo y la contraseña coinciden; si no, null.
    fun iniciarSesion(correo: String, clave: String): Usuario? {
        val usuario = cargarTodos().find { it.correo == correo.trim().lowercase() } ?: return null
        if (usuario.claveHash != cifrar(clave, usuario.sal)) return null
        guardarSesion(usuario.id)
        return usuario
    }

    // Permite que la sesión siga abierta aunque se cierre la app.
    fun usuarioEnSesion(): Usuario? {
        val id = prefs.getString(KEY_SESION, null) ?: return null
        return cargarTodos().find { it.id == id }
    }

    fun cerrarSesion() {
        prefs.edit().remove(KEY_SESION).apply()
    }

    private fun guardarSesion(id: String) {
        prefs.edit().putString(KEY_SESION, id).apply()
    }

    // ---------- Seguridad de la contraseña ----------

    private fun generarSal(): String {
        val bytes = ByteArray(16)
        SecureRandom().nextBytes(bytes)
        return bytes.aHex()
    }

    private fun cifrar(clave: String, sal: String): String =
        MessageDigest.getInstance("SHA-256").digest((sal + clave).toByteArray()).aHex()

    private fun ByteArray.aHex(): String = joinToString("") { "%02x".format(it) }

    // ---------- Lectura y escritura en JSON ----------

    private fun cargarTodos(): List<Usuario> {
        val texto = prefs.getString(KEY_USUARIOS, null) ?: return emptyList()
        val arreglo = JSONArray(texto)
        return (0 until arreglo.length()).map { i ->
            val o = arreglo.getJSONObject(i)
            Usuario(
                id = o.getString("id"),
                nombre = o.getString("nombre"),
                correo = o.getString("correo"),
                telefono = o.optString("telefono"),
                barrio = o.optString("barrio"),
                claveHash = o.getString("claveHash"),
                sal = o.getString("sal"),
                fechaRegistro = o.optLong("fechaRegistro", System.currentTimeMillis())
            )
        }
    }

    private fun guardarTodos(usuarios: List<Usuario>) {
        val arreglo = JSONArray()
        usuarios.forEach { u ->
            arreglo.put(
                JSONObject()
                    .put("id", u.id)
                    .put("nombre", u.nombre)
                    .put("correo", u.correo)
                    .put("telefono", u.telefono)
                    .put("barrio", u.barrio)
                    .put("claveHash", u.claveHash)
                    .put("sal", u.sal)
                    .put("fechaRegistro", u.fechaRegistro)
            )
        }
        prefs.edit().putString(KEY_USUARIOS, arreglo.toString()).apply()
    }
}
