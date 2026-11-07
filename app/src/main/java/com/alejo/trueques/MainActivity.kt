package com.alejo.trueques

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.alejo.trueques.ui.navigation.NavGraph
import com.alejo.trueques.ui.theme.TruequesTheme

// Punto de entrada de la app. Solo carga el tema y la navegación;
// cada pantalla vive en su propio archivo dentro de ui/screens.
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TruequesTheme {
                NavGraph()
            }
        }
    }
}
