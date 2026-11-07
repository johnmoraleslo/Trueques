package com.alejo.trueques

import android.app.Application
import coil.ImageLoader
import coil.ImageLoaderFactory
import okhttp3.OkHttpClient

// Clase que Android crea antes que cualquier pantalla.
// Aquí configuramos Coil (la librería que carga las imágenes) para que se
// identifique al pedir fotos a internet; algunos servidores lo exigen.
class TruequesApp : Application(), ImageLoaderFactory {

    override fun newImageLoader(): ImageLoader =
        ImageLoader.Builder(this)
            .okHttpClient {
                OkHttpClient.Builder()
                    .addInterceptor { cadena ->
                        cadena.proceed(
                            cadena.request().newBuilder()
                                .header("User-Agent", "Trueques/1.0 (Android; proyecto academico)")
                                .build()
                        )
                    }
                    .build()
            }
            .crossfade(true)
            .build()
}
