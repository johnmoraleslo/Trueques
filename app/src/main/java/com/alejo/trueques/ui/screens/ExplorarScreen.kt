package com.alejo.trueques.ui.screens

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.alejo.trueques.data.model.Articulo
import com.alejo.trueques.data.model.Estado
import com.alejo.trueques.data.model.categorias
import com.alejo.trueques.ui.components.TarjetaArticulo

// Lista de artículos de otros usuarios, con buscador y filtros.
@Composable
fun ExplorarScreen(
    usuarioId: String,
    articulos: List<Articulo>,
    onVerArticulo: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var busqueda by rememberSaveable { mutableStateOf("") }
    var estadoFiltro by rememberSaveable { mutableStateOf<Estado?>(null) }
    var categoriaFiltro by rememberSaveable { mutableStateOf<String?>(null) }

    // Se aplican los filtros uno tras otro.
    val resultado = articulos
        .filter { it.duenoId != usuarioId && it.disponible }
        .filter { estadoFiltro == null || it.estado == estadoFiltro }
        .filter { categoriaFiltro == null || it.categoria == categoriaFiltro }
        .filter {
            busqueda.isBlank() ||
                it.titulo.contains(busqueda, ignoreCase = true) ||
                it.cambiaPor.contains(busqueda, ignoreCase = true)
        }

    Column(modifier = modifier.fillMaxSize()) {
        OutlinedTextField(
            value = busqueda,
            onValueChange = { busqueda = it },
            placeholder = { Text("Buscar artículo o lo que piden a cambio") },
            leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        )

        Row(
            modifier = Modifier
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = estadoFiltro == null,
                onClick = { estadoFiltro = null },
                label = { Text("Todos") }
            )
            Estado.entries.forEach { estado ->
                FilterChip(
                    selected = estadoFiltro == estado,
                    onClick = { estadoFiltro = if (estadoFiltro == estado) null else estado },
                    label = { Text(estado.etiqueta) }
                )
            }
            categorias.forEach { categoria ->
                FilterChip(
                    selected = categoriaFiltro == categoria,
                    onClick = { categoriaFiltro = if (categoriaFiltro == categoria) null else categoria },
                    label = { Text(categoria) }
                )
            }
        }

        if (resultado.isEmpty()) {
            Text(
                "No hay artículos con esos filtros.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(16.dp)
            )
        }

        LazyColumn(
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 96.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(resultado, key = { it.id }) { articulo ->
                TarjetaArticulo(articulo = articulo, onClick = { onVerArticulo(articulo.id) })
            }
        }
    }
}
