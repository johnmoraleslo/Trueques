package com.alejo.trueques.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.alejo.trueques.data.model.Articulo
import com.alejo.trueques.data.model.Usuario
import com.alejo.trueques.ui.components.TarjetaArticulo

// Panel del usuario: saludo, indicadores y lo más reciente de la comunidad.
@Composable
fun PanelScreen(
    usuario: Usuario,
    articulos: List<Articulo>,
    onVerArticulo: (String) -> Unit,
    onIrAExplorar: () -> Unit,
    onIrAMisArticulos: () -> Unit,
    modifier: Modifier = Modifier
) {
    val mios = articulos.filter { it.duenoId == usuario.id }
    val misDisponibles = mios.count { it.disponible }
    val misIntercambiados = mios.count { !it.disponible }
    val deLaComunidad = articulos.filter { it.duenoId != usuario.id && it.disponible }
    val primerNombre = usuario.nombre.substringBefore(" ")

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Column {
                Text("Hola, $primerNombre", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                Text(
                    "Este es el resumen de tus intercambios",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Indicadores en dos filas de dos tarjetas.
        item {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Indicador("Mis publicaciones", mios.size, Icons.Filled.Inventory2,
                    MaterialTheme.colorScheme.primaryContainer, Modifier.weight(1f))
                Indicador("Disponibles", misDisponibles, Icons.Filled.Storefront,
                    MaterialTheme.colorScheme.secondaryContainer, Modifier.weight(1f))
            }
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Indicador("Intercambiados", misIntercambiados, Icons.Filled.CheckCircle,
                    MaterialTheme.colorScheme.tertiaryContainer, Modifier.weight(1f))
                Indicador("En la comunidad", deLaComunidad.size, Icons.Filled.Groups,
                    MaterialTheme.colorScheme.surfaceVariant, Modifier.weight(1f))
            }
            }
        }

        if (mios.isEmpty()) {
            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp)) {
                        Text("Aún no has publicado nada", fontWeight = FontWeight.Bold)
                        Text(
                            "Toca \"Publicar\" para ofrecer tu primer artículo.",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        } else {
            item {
                TextButton(onClick = onIrAMisArticulos) { Text("Ver mis artículos") }
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Recién publicados", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                TextButton(onClick = onIrAExplorar) { Text("Ver todos") }
            }
        }

        items(deLaComunidad.take(3), key = { it.id }) { articulo ->
            TarjetaArticulo(articulo = articulo, onClick = { onVerArticulo(articulo.id) })
        }
    }
}

@Composable
private fun Indicador(
    titulo: String,
    valor: Int,
    icono: ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier, colors = CardDefaults.cardColors(containerColor = color)) {
        Column(Modifier.padding(16.dp)) {
            Icon(icono, contentDescription = null, modifier = Modifier.size(24.dp))
            Spacer(Modifier.height(8.dp))
            Text("$valor", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            Text(titulo, style = MaterialTheme.typography.bodySmall)
        }
    }
}
