package com.alejo.trueques.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.alejo.trueques.data.model.Articulo
import com.alejo.trueques.data.model.Estado
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// Tarjeta que se reutiliza en el panel, en Explorar y en Mis artículos.
// "acciones" permite agregar botones abajo (por ejemplo, eliminar).
@Composable
fun TarjetaArticulo(
    articulo: Articulo,
    modifier: Modifier = Modifier,
    mostrarDueno: Boolean = true,
    onClick: (() -> Unit)? = null,
    acciones: @Composable (() -> Unit)? = null
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier)
    ) {
        FotoArticulo(
            articulo = articulo,
            modifier = Modifier
                .fillMaxWidth()
                .height(170.dp)
        )
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = articulo.titulo,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                Spacer(Modifier.width(8.dp))
                EtiquetaEstado(articulo)
            }

            Spacer(Modifier.height(4.dp))
            Text(
                text = articulo.descripcion,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Filled.SwapHoriz,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(Modifier.width(4.dp))
                Text(
                    text = "Lo cambia por: ${articulo.cambiaPor}",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(Modifier.height(6.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (mostrarDueno) {
                    Icon(
                        Icons.Filled.LocationOn,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.width(2.dp))
                }
                Text(
                    text = buildString {
                        if (mostrarDueno) append("${articulo.duenoNombre} · ${articulo.duenoBarrio} · ")
                        append("${articulo.categoria} · ${fechaCorta(articulo.fecha)}")
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            if (acciones != null) {
                Spacer(Modifier.height(4.dp))
                acciones()
            }
        }
    }
}

// Etiqueta de color: rosa si es nuevo, menta si es de segunda, lavanda si ya se intercambió.
@Composable
fun EtiquetaEstado(articulo: Articulo) {
    val (texto, fondo) = when {
        !articulo.disponible -> "Intercambiado" to MaterialTheme.colorScheme.tertiaryContainer
        articulo.estado == Estado.NUEVO -> articulo.estado.etiqueta to MaterialTheme.colorScheme.primaryContainer
        else -> articulo.estado.etiqueta to MaterialTheme.colorScheme.secondaryContainer
    }
    Surface(color = fondo, shape = RoundedCornerShape(8.dp)) {
        Text(
            text = texto,
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
        )
    }
}

fun fechaCorta(milisegundos: Long): String =
    SimpleDateFormat("d MMM", Locale.forLanguageTag("es-CO")).format(Date(milisegundos))
