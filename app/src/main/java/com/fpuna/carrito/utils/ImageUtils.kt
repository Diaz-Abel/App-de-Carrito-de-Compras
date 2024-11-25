// En ImageUtils.kt
package com.fpuna.carrito.utils

import android.content.Context
import android.net.Uri
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStream
import java.util.UUID

// Renombrada para ser específica para categorías
fun copyCategoryImageToInternalStorage(
    context: Context,
    sourceUri: Uri,
    destinationFolderName: String = "category_images" // Usamos un nombre de carpeta diferente
): Uri? {
    return try {
        val contentResolver = context.contentResolver
        val inputStream: InputStream? = contentResolver.openInputStream(sourceUri)

        if (inputStream != null) {
            // Crear un archivo temporal en almacenamiento interno
            val storageDir = File(context.filesDir, destinationFolderName)
            if (!storageDir.exists()) {
                storageDir.mkdir() // Crear la carpeta si no existe
            }

            val fileName = UUID.randomUUID().toString() + ".jpg"
            val file = File(storageDir, fileName)

            // Copiar el archivo de la imagen a almacenamiento interno
            val outputStream = FileOutputStream(file)
            inputStream.copyTo(outputStream)

            // Retornar la URI del archivo copiado
            Uri.fromFile(file)
        } else {
            null
        }
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}
