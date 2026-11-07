package com.alejo.trueques.util

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File
import java.util.UUID

// Funciones para manejar las fotos de los artículos.
// Todas las fotos se guardan en una carpeta privada de la app ("fotos"),
// así no se pierden aunque el usuario borre la imagen de su galería.
object Fotos {

    private fun carpeta(context: Context): File =
        File(context.filesDir, "fotos").apply { mkdirs() }

    // Crea un archivo vacío donde la cámara va a guardar la foto.
    fun nuevoArchivo(context: Context): File =
        File(carpeta(context), "${UUID.randomUUID()}.jpg")

    // La cámara es otra app: necesita una dirección segura (content://) para escribir.
    fun uriPara(context: Context, archivo: File): Uri =
        FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", archivo)

    // Copia una imagen elegida de la galería a la carpeta de la app.
    fun copiarDesdeGaleria(context: Context, uri: Uri): String? = try {
        val destino = nuevoArchivo(context)
        context.contentResolver.openInputStream(uri)?.use { entrada ->
            destino.outputStream().use { salida -> entrada.copyTo(salida) }
        }
        destino.absolutePath
    } catch (e: Exception) {
        null
    }

    // Borra la foto si es un archivo local (las de internet no se tocan).
    fun borrar(ruta: String?) {
        if (ruta != null && !ruta.startsWith("http")) File(ruta).delete()
    }

    // Coil necesita saber si la foto es una dirección web o un archivo.
    fun modelo(ruta: String?): Any? = when {
        ruta.isNullOrBlank() -> null
        ruta.startsWith("http") -> ruta
        else -> File(ruta)
    }
}
