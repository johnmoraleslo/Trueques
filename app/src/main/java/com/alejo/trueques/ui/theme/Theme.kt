package com.alejo.trueques.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Esquema de colores claro (el principal de la app).
private val EsquemaClaro = lightColorScheme(
    primary = Rosa,
    onPrimary = Blanco,
    primaryContainer = RosaPastel,
    onPrimaryContainer = RosaOscuro,

    secondary = Menta,
    onSecondary = Blanco,
    secondaryContainer = MentaPastel,
    onSecondaryContainer = MentaOscuro,

    tertiary = Lavanda,
    onTertiary = Blanco,
    tertiaryContainer = LavandaPastel,
    onTertiaryContainer = LavandaOscuro,

    background = Crema,
    onBackground = Tinta,
    surface = Crema,
    onSurface = Tinta,
    surfaceVariant = Mantequilla,
    onSurfaceVariant = Cafe,

    // Las tarjetas usan surfaceContainerHighest: blancas sobre el fondo crema.
    surfaceContainerLowest = Blanco,
    surfaceContainerLow = Color(0xFFFFF4EF),
    surfaceContainer = CremaRosada,
    surfaceContainerHigh = Color(0xFFFFF1EC),
    surfaceContainerHighest = Blanco,

    outline = BordeRosa,
    outlineVariant = Color(0xFFF3DDE4),
    error = Coral,
    onError = Blanco
)

// Esquema oscuro: los mismos tonos, más suaves sobre fondo oscuro.
private val EsquemaOscuro = darkColorScheme(
    primary = RosaNoche,
    onPrimary = RosaOscuro,
    primaryContainer = Color(0xFF7A2A4A),
    onPrimaryContainer = RosaPastel,

    secondary = MentaNoche,
    onSecondary = MentaOscuro,
    secondaryContainer = Color(0xFF0F5443),
    onSecondaryContainer = MentaPastel,

    tertiary = LavandaNoche,
    onTertiary = LavandaOscuro,
    tertiaryContainer = Color(0xFF4A3690),
    onTertiaryContainer = LavandaPastel,

    background = FondoNoche,
    onBackground = Color(0xFFF3E9EC),
    surface = FondoNoche,
    onSurface = Color(0xFFF3E9EC),
    surfaceVariant = Color(0xFF4A4030),
    onSurfaceVariant = Color(0xFFE8D9C9),

    surfaceContainer = Color(0xFF26201F),
    surfaceContainerHigh = Color(0xFF2A2326),
    surfaceContainerHighest = TarjetaNoche,

    outline = Color(0xFF8A6F79),
    error = Color(0xFFFF8A8E)
)

// Tema de la app. No usamos los colores del fondo de pantalla del celular
// (dynamicColor) para que la app siempre se vea con su propia paleta.
@Composable
fun TruequesTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) EsquemaOscuro else EsquemaClaro,
        typography = Typography,
        content = content
    )
}
