package com.alejo.trueques.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.alejo.trueques.data.model.Articulo
import com.alejo.trueques.data.model.Estado
import com.alejo.trueques.data.model.Usuario
import com.alejo.trueques.data.repository.ArticuloRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

// Maneja la lista de artículos: publicar, eliminar y marcar como intercambiado.
class ArticuloViewModel(application: Application) : AndroidViewModel(application) {

    private val repositorio = ArticuloRepository(application)

    private val _articulos = MutableStateFlow(repositorio.todos())
    val articulos: StateFlow<List<Articulo>> = _articulos.asStateFlow()

    // Devuelve null si se publicó bien, o el mensaje de error si falta algo.
    fun publicar(
        usuario: Usuario,
        titulo: String,
        descripcion: String,
        estado: Estado?,
        categoria: String?,
        cambiaPor: String,
        foto: String?
    ): String? {
        when {
            titulo.isBlank() -> return "Ponle un título al artículo"
            descripcion.isBlank() -> return "Describe cómo está el artículo"
            estado == null -> return "Elige si es nuevo o de segunda"
            categoria == null -> return "Elige una categoría"
            cambiaPor.isBlank() -> return "Escribe qué te gustaría recibir a cambio"
        }

        repositorio.agregar(
            Articulo(
                id = UUID.randomUUID().toString(),
                duenoId = usuario.id,
                duenoNombre = usuario.nombre,
                duenoBarrio = usuario.barrio,
                titulo = titulo.trim(),
                descripcion = descripcion.trim(),
                estado = estado!!,
                categoria = categoria!!,
                cambiaPor = cambiaPor.trim(),
                foto = foto
            )
        )
        recargar()
        return null
    }

    fun eliminar(id: String) {
        repositorio.eliminar(id)
        recargar()
    }

    fun marcarIntercambiado(id: String) {
        repositorio.marcarIntercambiado(id)
        recargar()
    }

    private fun recargar() {
        _articulos.value = repositorio.todos()
    }
}
