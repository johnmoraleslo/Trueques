package com.alejo.trueques.data.model

// Estado del artículo: si es nuevo o de segunda mano.
enum class Estado(val etiqueta: String) {
    NUEVO("Nuevo"),
    SEGUNDA("De segunda")
}

// Categorías disponibles para clasificar las publicaciones.
val categorias = listOf("Tecnología", "Hogar", "Ropa", "Libros", "Deportes", "Otros")

// Un artículo publicado para intercambiar.
data class Articulo(
    val id: String,
    val duenoId: String,          // id del usuario que lo publicó
    val duenoNombre: String,
    val duenoBarrio: String,
    val titulo: String,
    val descripcion: String,
    val estado: Estado,
    val categoria: String,
    val cambiaPor: String,        // lo que el dueño quiere recibir a cambio
    val foto: String? = null,     // dirección web o ruta del archivo en el celular
    val disponible: Boolean = true,
    val fecha: Long = System.currentTimeMillis()
)
