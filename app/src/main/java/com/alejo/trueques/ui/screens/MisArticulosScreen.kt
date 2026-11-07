package com.alejo.trueques.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.alejo.trueques.data.model.Articulo
import com.alejo.trueques.ui.components.TarjetaArticulo

// Las publicaciones del usuario, con opciones para administrarlas.
@Composable
fun MisArticulosScreen(
    usuarioId: String,
    articulos: List<Articulo>,
    onMarcarIntercambiado: (String) -> Unit,
    onEliminar: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val mios = articulos.filter { it.duenoId == usuarioId }
    var porEliminar by remember { mutableStateOf<Articulo?>(null) }

    if (mios.isEmpty()) {
        Box(modifier = modifier.fillMaxSize().padding(32.dp), contentAlignment = Alignment.Center) {
            Text(
                "Todavía no tienes publicaciones.\nToca \"Publicar\" para ofrecer algo.",
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        return
    }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(mios, key = { it.id }) { articulo ->
            TarjetaArticulo(
                articulo = articulo,
                mostrarDueno = false,
                acciones = {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                        if (articulo.disponible) {
                            TextButton(onClick = { onMarcarIntercambiado(articulo.id) }) {
                                Text("Marcar intercambiado")
                            }
                        }
                        TextButton(onClick = { porEliminar = articulo }) {
                            Text("Eliminar", color = MaterialTheme.colorScheme.error)
                        }
                    }
                }
            )
        }
    }

    // Confirmación antes de borrar, para evitar eliminar por error.
    porEliminar?.let { articulo ->
        AlertDialog(
            onDismissRequest = { porEliminar = null },
            title = { Text("Eliminar publicación") },
            text = { Text("¿Seguro que quieres eliminar \"${articulo.titulo}\"? No se puede deshacer.") },
            confirmButton = {
                TextButton(onClick = {
                    onEliminar(articulo.id)
                    porEliminar = null
                }) { Text("Eliminar", color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = {
                TextButton(onClick = { porEliminar = null }) { Text("Cancelar") }
            }
        )
    }
}
