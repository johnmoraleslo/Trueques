package com.alejo.trueques.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Checkroom
import androidx.compose.material.icons.filled.Chair
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.alejo.trueques.data.model.Articulo
import com.alejo.trueques.util.Fotos

// Muestra la foto del artículo. Mientras carga (o si no tiene foto)
// se ve un fondo de color con el ícono de su categoría.
@Composable
fun FotoArticulo(articulo: Articulo, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.background(MaterialTheme.colorScheme.primaryContainer),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            iconoCategoria(articulo.categoria),
            contentDescription = null,
            modifier = Modifier.size(48.dp),
            tint = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.5f)
        )
        Fotos.modelo(articulo.foto)?.let { modelo ->
            AsyncImage(
                model = modelo,
                contentDescription = "Foto de ${articulo.titulo}",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

fun iconoCategoria(categoria: String): ImageVector = when (categoria) {
    "Tecnología" -> Icons.Filled.Devices
    "Hogar" -> Icons.Filled.Chair
    "Ropa" -> Icons.Filled.Checkroom
    "Libros" -> Icons.AutoMirrored.Filled.MenuBook
    "Deportes" -> Icons.Filled.SportsSoccer
    else -> Icons.Filled.Inventory2
}
