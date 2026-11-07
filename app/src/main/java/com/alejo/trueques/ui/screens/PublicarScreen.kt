package com.alejo.trueques.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import coil.compose.AsyncImage
import com.alejo.trueques.data.model.Estado
import com.alejo.trueques.data.model.Usuario
import com.alejo.trueques.data.model.categorias
import com.alejo.trueques.ui.viewmodel.ArticuloViewModel
import com.alejo.trueques.util.Fotos
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PublicarScreen(
    usuario: Usuario,
    viewModel: ArticuloViewModel,
    onVolver: () -> Unit
) {
    val context = LocalContext.current

    var titulo by rememberSaveable { mutableStateOf("") }
    var descripcion by rememberSaveable { mutableStateOf("") }
    var estado by rememberSaveable { mutableStateOf<Estado?>(null) }
    var categoria by rememberSaveable { mutableStateOf<String?>(null) }
    var cambiaPor by rememberSaveable { mutableStateOf("") }
    var foto by rememberSaveable { mutableStateOf<String?>(null) }
    var error by rememberSaveable { mutableStateOf<String?>(null) }
    var avisoCamara by rememberSaveable { mutableStateOf<String?>(null) }

    // Ruta del archivo que la cámara está llenando en este momento.
    var fotoPendiente by rememberSaveable { mutableStateOf<String?>(null) }

    // ---------- Cámara ----------
    // 1. La cámara toma la foto y la guarda en el archivo que le pasamos.
    val tomarFoto = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { exito ->
        if (exito) {
            Fotos.borrar(foto)          // si ya había una foto, se reemplaza
            foto = fotoPendiente
        } else {
            Fotos.borrar(fotoPendiente) // el usuario canceló: se borra el archivo vacío
        }
        fotoPendiente = null
    }

    fun abrirCamara() {
        val archivo = Fotos.nuevoArchivo(context)
        fotoPendiente = archivo.absolutePath
        tomarFoto.launch(Fotos.uriPara(context, archivo))
    }

    // 2. Antes de abrir la cámara se pide el permiso al usuario.
    val pedirPermisoCamara = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { concedido ->
        if (concedido) {
            avisoCamara = null
            abrirCamara()
        } else {
            avisoCamara = "Sin permiso de cámara no podemos tomar la foto. " +
                "Puedes activarlo en Ajustes > Apps > Trueques > Permisos, o elegir una de la galería."
        }
    }

    fun alTocarCamara() {
        val yaTienePermiso = ContextCompat.checkSelfPermission(
            context, Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED

        if (yaTienePermiso) abrirCamara() else pedirPermisoCamara.launch(Manifest.permission.CAMERA)
    }

    // ---------- Galería ----------
    // El selector de fotos de Android no necesita permiso.
    val elegirDeGaleria = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            val copia = Fotos.copiarDesdeGaleria(context, uri)
            if (copia != null) {
                Fotos.borrar(foto)
                foto = copia
            }
        }
    }

    fun salir() {
        Fotos.borrar(foto) // si no se publicó, la foto no se queda ocupando espacio
        onVolver()
    }

    // También aplica cuando se usa el botón "atrás" del celular.
    BackHandler { salir() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Publicar artículo") },
                navigationIcon = {
                    IconButton(onClick = { salir() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            // ---------- Foto ----------
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .border(2.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(20.dp)),
                contentAlignment = Alignment.Center
            ) {
                if (foto == null) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Filled.AddAPhoto,
                            contentDescription = null,
                            modifier = Modifier.size(48.dp),
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            "Agrega una foto de tu artículo",
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                } else {
                    AsyncImage(
                        model = File(foto!!),
                        contentDescription = "Foto del artículo",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    FilledTonalIconButton(
                        onClick = {
                            Fotos.borrar(foto)
                            foto = null
                        },
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(8.dp)
                    ) {
                        Icon(Icons.Filled.Close, contentDescription = "Quitar foto")
                    }
                }
            }

            Spacer(Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedButton(onClick = { alTocarCamara() }, modifier = Modifier.weight(1f)) {
                    Icon(Icons.Filled.PhotoCamera, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Cámara")
                }
                OutlinedButton(
                    onClick = {
                        elegirDeGaleria.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Filled.PhotoLibrary, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Galería")
                }
            }
            avisoCamara?.let {
                Spacer(Modifier.height(8.dp))
                Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
            }

            // ---------- Datos ----------
            Spacer(Modifier.height(16.dp))
            OutlinedTextField(
                value = titulo,
                onValueChange = { titulo = it },
                label = { Text("Título") },
                placeholder = { Text("Ej: Bicicleta rin 26") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value = descripcion,
                onValueChange = { descripcion = it },
                label = { Text("Descripción") },
                placeholder = { Text("¿Cómo está? ¿Qué incluye?") },
                minLines = 3,
                modifier = Modifier.fillMaxWidth()
            )

            TituloSeccion("Estado")
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Estado.entries.forEach { opcion ->
                    FilterChip(
                        selected = estado == opcion,
                        onClick = { estado = opcion },
                        label = { Text(opcion.etiqueta) }
                    )
                }
            }

            TituloSeccion("Categoría")
            Row(
                modifier = Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                categorias.forEach { opcion ->
                    FilterChip(
                        selected = categoria == opcion,
                        onClick = { categoria = opcion },
                        label = { Text(opcion) }
                    )
                }
            }

            Spacer(Modifier.height(16.dp))
            OutlinedTextField(
                value = cambiaPor,
                onValueChange = { cambiaPor = it },
                label = { Text("¿Qué quieres a cambio?") },
                placeholder = { Text("Ej: Patineta, libros, o lo que ofrezcan") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            error?.let {
                Spacer(Modifier.height(12.dp))
                Text(it, color = MaterialTheme.colorScheme.error)
            }

            Spacer(Modifier.height(24.dp))
            Button(
                onClick = {
                    error = viewModel.publicar(usuario, titulo, descripcion, estado, categoria, cambiaPor, foto)
                    if (error == null) onVolver() // se publicó: la foto ya pertenece al artículo
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text("Publicar")
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun TituloSeccion(texto: String) {
    Spacer(Modifier.height(16.dp))
    Text(texto, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
    Spacer(Modifier.height(8.dp))
}
