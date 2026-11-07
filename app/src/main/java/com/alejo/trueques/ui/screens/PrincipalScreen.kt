package com.alejo.trueques.ui.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import com.alejo.trueques.data.model.Usuario
import com.alejo.trueques.ui.viewmodel.ArticuloViewModel

// Las cuatro secciones de la barra inferior.
enum class Seccion(val titulo: String, val icono: ImageVector) {
    PANEL("Panel", Icons.Filled.Dashboard),
    EXPLORAR("Explorar", Icons.Filled.Search),
    MIS_ARTICULOS("Mis artículos", Icons.Filled.Inventory2),
    PERFIL("Perfil", Icons.Filled.Person)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrincipalScreen(
    usuario: Usuario,
    articuloViewModel: ArticuloViewModel,
    onPublicar: () -> Unit,
    onVerArticulo: (String) -> Unit,
    onCerrarSesion: () -> Unit
) {
    var seccion by rememberSaveable { mutableStateOf(Seccion.PANEL) }
    val articulos by articuloViewModel.articulos.collectAsState()

    Scaffold(
        topBar = { TopAppBar(title = { Text(if (seccion == Seccion.PANEL) "Trueques" else seccion.titulo) }) },
        bottomBar = {
            NavigationBar {
                Seccion.entries.forEach { item ->
                    NavigationBarItem(
                        selected = seccion == item,
                        onClick = { seccion = item },
                        icon = { Icon(item.icono, contentDescription = null) },
                        label = { Text(item.titulo) }
                    )
                }
            }
        },
        floatingActionButton = {
            if (seccion != Seccion.PERFIL) {
                ExtendedFloatingActionButton(
                    onClick = onPublicar,
                    icon = { Icon(Icons.Filled.Add, contentDescription = null) },
                    text = { Text("Publicar") }
                )
            }
        }
    ) { padding ->
        val modifier = Modifier.padding(padding)
        when (seccion) {
            Seccion.PANEL -> PanelScreen(
                usuario = usuario,
                articulos = articulos,
                onVerArticulo = onVerArticulo,
                onIrAExplorar = { seccion = Seccion.EXPLORAR },
                onIrAMisArticulos = { seccion = Seccion.MIS_ARTICULOS },
                modifier = modifier
            )
            Seccion.EXPLORAR -> ExplorarScreen(
                usuarioId = usuario.id,
                articulos = articulos,
                onVerArticulo = onVerArticulo,
                modifier = modifier
            )
            Seccion.MIS_ARTICULOS -> MisArticulosScreen(
                usuarioId = usuario.id,
                articulos = articulos,
                onMarcarIntercambiado = articuloViewModel::marcarIntercambiado,
                onEliminar = articuloViewModel::eliminar,
                modifier = modifier
            )
            Seccion.PERFIL -> PerfilScreen(
                usuario = usuario,
                totalPublicaciones = articulos.count { it.duenoId == usuario.id },
                onCerrarSesion = onCerrarSesion,
                modifier = modifier
            )
        }
    }
}
