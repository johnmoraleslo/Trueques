package com.alejo.trueques.ui.viewmodel

import android.app.Application
import android.util.Patterns
import androidx.lifecycle.AndroidViewModel
import com.alejo.trueques.data.model.Usuario
import com.alejo.trueques.data.repository.UsuarioRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

// Maneja el inicio de sesión, el registro y la sesión activa.
// Las pantallas solo leen "usuario" y "error"; toda la lógica vive aquí.
class AuthViewModel(application: Application) : AndroidViewModel(application) {

    private val repositorio = UsuarioRepository(application)

    private val _usuario = MutableStateFlow(repositorio.usuarioEnSesion())
    val usuario: StateFlow<Usuario?> = _usuario.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun iniciarSesion(correo: String, clave: String): Boolean {
        if (correo.isBlank() || clave.isBlank()) {
            _error.value = "Escribe tu correo y tu contraseña"
            return false
        }
        val encontrado = repositorio.iniciarSesion(correo, clave)
        if (encontrado == null) {
            _error.value = "Correo o contraseña incorrectos"
            return false
        }
        _usuario.value = encontrado
        _error.value = null
        return true
    }

    fun registrar(
        nombre: String,
        correo: String,
        telefono: String,
        barrio: String,
        clave: String,
        confirmarClave: String
    ): Boolean {
        val problema = when {
            nombre.isBlank() -> "Escribe tu nombre"
            !Patterns.EMAIL_ADDRESS.matcher(correo.trim()).matches() -> "El correo no es válido"
            telefono.filter { it.isDigit() }.length < 7 -> "Escribe un teléfono válido"
            barrio.isBlank() -> "Escribe tu barrio o municipio"
            clave.length < 6 -> "La contraseña debe tener al menos 6 caracteres"
            clave != confirmarClave -> "Las contraseñas no coinciden"
            else -> null
        }
        if (problema != null) {
            _error.value = problema
            return false
        }

        repositorio.registrar(nombre, correo, telefono, barrio, clave)
            .onSuccess {
                _usuario.value = it
                _error.value = null
                return true
            }
            .onFailure { _error.value = it.message }
        return false
    }

    fun cerrarSesion() {
        repositorio.cerrarSesion()
        _usuario.value = null
    }

    fun limpiarError() {
        _error.value = null
    }
}
