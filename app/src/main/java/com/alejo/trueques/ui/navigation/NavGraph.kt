package com.alejo.trueques.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.alejo.trueques.ui.screens.DetalleScreen
import com.alejo.trueques.ui.screens.LoginScreen
import com.alejo.trueques.ui.screens.PrincipalScreen
import com.alejo.trueques.ui.screens.PublicarScreen
import com.alejo.trueques.ui.screens.RegistroScreen
import com.alejo.trueques.ui.viewmodel.ArticuloViewModel
import com.alejo.trueques.ui.viewmodel.AuthViewModel

// Nombres de cada pantalla (rutas).
object Rutas {
    const val LOGIN = "login"
    const val REGISTRO = "registro"
    const val PRINCIPAL = "principal"
    const val PUBLICAR = "publicar"
    const val DETALLE = "detalle/{id}"
    fun detalle(id: String) = "detalle/$id"
}

@Composable
fun NavGraph() {
    val navController = rememberNavController()

    // Los ViewModels se crean una sola vez y se comparten entre pantallas.
    val authViewModel: AuthViewModel = viewModel()
    val articuloViewModel: ArticuloViewModel = viewModel()

    val usuario by authViewModel.usuario.collectAsState()

    // Si ya había una sesión abierta, entra directo al panel.
    val inicio = remember { if (authViewModel.usuario.value != null) Rutas.PRINCIPAL else Rutas.LOGIN }

    NavHost(navController = navController, startDestination = inicio) {

        composable(Rutas.LOGIN) {
            LoginScreen(
                viewModel = authViewModel,
                onIngresar = {
                    navController.navigate(Rutas.PRINCIPAL) {
                        popUpTo(Rutas.LOGIN) { inclusive = true }
                    }
                },
                onIrARegistro = { navController.navigate(Rutas.REGISTRO) }
            )
        }

        composable(Rutas.REGISTRO) {
            RegistroScreen(
                viewModel = authViewModel,
                onRegistrado = {
                    navController.navigate(Rutas.PRINCIPAL) {
                        popUpTo(Rutas.LOGIN) { inclusive = true }
                    }
                },
                onVolver = { navController.popBackStack() }
            )
        }

        composable(Rutas.PRINCIPAL) {
            usuario?.let { actual ->
                PrincipalScreen(
                    usuario = actual,
                    articuloViewModel = articuloViewModel,
                    onPublicar = { navController.navigate(Rutas.PUBLICAR) },
                    onVerArticulo = { id -> navController.navigate(Rutas.detalle(id)) },
                    onCerrarSesion = {
                        authViewModel.cerrarSesion()
                        navController.navigate(Rutas.LOGIN) {
                            popUpTo(Rutas.PRINCIPAL) { inclusive = true }
                        }
                    }
                )
            }
        }

        composable(Rutas.PUBLICAR) {
            usuario?.let { actual ->
                PublicarScreen(
                    usuario = actual,
                    viewModel = articuloViewModel,
                    onVolver = { navController.popBackStack() }
                )
            }
        }

        composable(Rutas.DETALLE) { entrada ->
            val id = entrada.arguments?.getString("id").orEmpty()
            DetalleScreen(
                articuloId = id,
                usuarioId = usuario?.id.orEmpty(),
                viewModel = articuloViewModel,
                onVolver = { navController.popBackStack() }
            )
        }
    }
}
