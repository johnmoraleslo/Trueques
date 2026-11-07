package com.alejo.trueques.data.repository

import android.content.Context
import com.alejo.trueques.data.model.Articulo
import com.alejo.trueques.data.model.Estado
import com.alejo.trueques.util.Fotos
import org.json.JSONArray
import org.json.JSONObject

// Guarda las publicaciones en el almacenamiento interno del celular.
class ArticuloRepository(context: Context) {

    private val prefs = context.getSharedPreferences("trueques_articulos", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_ARTICULOS = "articulos"
        private const val KEY_VERSION_EJEMPLOS = "version_ejemplos"

        // Si se cambian los artículos de ejemplo, se sube este número
        // y la app los reemplaza sin tocar lo que publicó el usuario.
        private const val VERSION_EJEMPLOS = 2
    }

    fun todos(): List<Articulo> {
        actualizarEjemplos()
        return cargar().sortedByDescending { it.fecha }
    }

    // Carga publicaciones de ejemplo para que la comunidad no se vea vacía.
    private fun actualizarEjemplos() {
        if (prefs.getInt(KEY_VERSION_EJEMPLOS, 0) >= VERSION_EJEMPLOS) return
        val delUsuario = cargar().filterNot { it.id.startsWith("demo-") }
        guardarTodos(ejemplos() + delUsuario)
        prefs.edit().putInt(KEY_VERSION_EJEMPLOS, VERSION_EJEMPLOS).apply()
    }

    fun agregar(articulo: Articulo) {
        guardarTodos(cargar() + articulo)
    }

    fun eliminar(id: String) {
        val lista = cargar()
        Fotos.borrar(lista.find { it.id == id }?.foto)
        guardarTodos(lista.filterNot { it.id == id })
    }

    fun marcarIntercambiado(id: String) {
        guardarTodos(cargar().map { if (it.id == id) it.copy(disponible = false) else it })
    }

    // ---------- Lectura y escritura en JSON ----------

    private fun cargar(): List<Articulo> {
        val texto = prefs.getString(KEY_ARTICULOS, null) ?: return emptyList()
        val arreglo = JSONArray(texto)
        return (0 until arreglo.length()).map { i ->
            val o = arreglo.getJSONObject(i)
            Articulo(
                id = o.getString("id"),
                duenoId = o.getString("duenoId"),
                duenoNombre = o.getString("duenoNombre"),
                duenoBarrio = o.optString("duenoBarrio"),
                titulo = o.getString("titulo"),
                descripcion = o.getString("descripcion"),
                estado = Estado.valueOf(o.getString("estado")),
                categoria = o.optString("categoria", "Otros"),
                cambiaPor = o.getString("cambiaPor"),
                foto = o.optString("foto").ifBlank { null },
                disponible = o.optBoolean("disponible", true),
                fecha = o.optLong("fecha", System.currentTimeMillis())
            )
        }
    }

    private fun guardarTodos(articulos: List<Articulo>) {
        val arreglo = JSONArray()
        articulos.forEach { a ->
            arreglo.put(
                JSONObject()
                    .put("id", a.id)
                    .put("duenoId", a.duenoId)
                    .put("duenoNombre", a.duenoNombre)
                    .put("duenoBarrio", a.duenoBarrio)
                    .put("titulo", a.titulo)
                    .put("descripcion", a.descripcion)
                    .put("estado", a.estado.name)
                    .put("categoria", a.categoria)
                    .put("cambiaPor", a.cambiaPor)
                    .put("foto", a.foto ?: "")
                    .put("disponible", a.disponible)
                    .put("fecha", a.fecha)
            )
        }
        prefs.edit().putString(KEY_ARTICULOS, arreglo.toString()).apply()
    }

    // Fotos de ejemplo tomadas de Wikimedia Commons (licencias libres):
    // Bicicleta: "Mountain bicycle.jpg", Redjar (CC BY-SA 3.0)
    // Audífonos: "Afterglow AGU.1 Wireless Headphones.jpg", Seqqis (CC BY-SA 3.0)
    // Libro: "Warm cup of coffee sits on an open book...", Shixart1985 (CC BY 2.0)
    // Licuadora: "Pink portable blender.jpg", Asecretfanfare (CC BY-SA 4.0)
    // Chaqueta: "Early Gore-Tex hooded rain jacket.jpg", Joe Mabel (CC BY-SA 4.0)
    private fun ejemplos(): List<Articulo> {
        val ahora = System.currentTimeMillis()
        val hora = 60 * 60 * 1000L
        val commons = "https://upload.wikimedia.org/wikipedia/commons"
        val miniaturas = "https://thumb.wikimedia.org/wikipedia/commons/thumb"
        return listOf(
            Articulo("demo-1", "demo-laura", "Laura G.", "Robledo", "Bicicleta todoterreno",
                "Poco uso, frenos nuevos y llantas en buen estado", Estado.SEGUNDA, "Deportes", "Patineta eléctrica",
                foto = "$commons/f/f2/Mountain_bicycle.jpg", fecha = ahora - 2 * hora),
            Articulo("demo-2", "demo-andres", "Andrés M.", "Laureles", "Audífonos inalámbricos",
                "Sellados en caja, con garantía", Estado.NUEVO, "Tecnología", "Mouse inalámbrico",
                foto = "$miniaturas/a/a5/Afterglow_AGU.1_Wireless_Headphones.jpg/960px-Afterglow_AGU.1_Wireless_Headphones.jpg",
                fecha = ahora - 5 * hora),
            Articulo("demo-3", "demo-laura", "Laura G.", "Robledo", "Libro de programación",
                "Edición 2024, sin rayones ni hojas dobladas", Estado.SEGUNDA, "Libros", "Libro de Python",
                foto = "$miniaturas/5/5b/Warm_cup_of_coffee_sits_on_an_open_book_on_a_wooden_table_in_a_cozy_workspace.jpg/960px-Warm_cup_of_coffee_sits_on_an_open_book_on_a_wooden_table_in_a_cozy_workspace.jpg",
                fecha = ahora - 20 * hora),
            Articulo("demo-4", "demo-sofia", "Sofía R.", "Bello", "Licuadora portátil",
                "Regalo repetido, sin abrir", Estado.NUEVO, "Hogar", "Sanduchera",
                foto = "$miniaturas/a/a3/Pink_portable_blender.jpg/960px-Pink_portable_blender.jpg",
                fecha = ahora - 30 * hora),
            Articulo("demo-5", "demo-andres", "Andrés M.", "Laureles", "Chaqueta impermeable talla M",
                "Usada dos veces, con capucha", Estado.SEGUNDA, "Ropa", "Tenis talla 40",
                foto = "$miniaturas/3/36/Early_Gore-Tex_hooded_rain_jacket.jpg/960px-Early_Gore-Tex_hooded_rain_jacket.jpg",
                fecha = ahora - 48 * hora)
        )
    }
}
