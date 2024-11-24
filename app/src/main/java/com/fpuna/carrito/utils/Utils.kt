package com.fpuna.carrito.utils

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.fpuna.carrito.models.Categoria
import java.io.File
import java.io.IOException
import java.util.UUID


@Composable
fun ImagePicker(
    initialImageUri: String?,
    onImageSelected: (String) -> Unit = {},  // Callback con valor predeterminado vacío
    context: Context = LocalContext.current,  // Obtenemos el contexto actual
    isListMode: Boolean = false // Parámetro para controlar el modo (listar o agregar/editar)
) {
    val singleImagePickerLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            uri?.let { sourceUri ->
                val copiedUri = copyImageToInternalStorage(context, sourceUri)
                if (copiedUri != null) {
                    Log.d("IMAGEN", "Nueva URI copiada: ${copiedUri.toString()}")
                    onImageSelected(copiedUri.toString())  // Actualiza la URI seleccionada
                } else {
                    Log.e("ImagePicker", "Error copiando la imagen seleccionada")
                }
            }
        }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        if (!initialImageUri.isNullOrEmpty()) {
            Image(
                painter = rememberAsyncImagePainter(model = initialImageUri),
                contentDescription = "",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(150.dp)
                    .padding(8.dp)
            )
        }

        // Solo mostramos el botón para seleccionar una nueva imagen si no estamos en modo de listar
        if (!isListMode) {
            Button(
                onClick = {
                    singleImagePickerLauncher.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                },
                modifier = Modifier.padding(horizontal = 30.dp, vertical = 16.dp)
            ) {
                Text("Seleccionar Imagen")
            }
        }
    }
}

fun copyImageToInternalStorage(
    context: Context,
    sourceUri: Uri,
    destinationFolderName: String = "product_images"
): Uri? {
    return try {
        val contentResolver: ContentResolver = context.contentResolver

        // Crea el directorio destino si no existe
        val destinationFolder = File(context.getExternalFilesDir(null), destinationFolderName)
        if (!destinationFolder.exists()) {
            destinationFolder.mkdirs()
        }

        // Genera un nombre único para el archivo basado en un UUID
        val destinationFileName = "image_${UUID.randomUUID()}.jpg"
        val destinationFile = File(destinationFolder, destinationFileName)

        // Copia el contenido del InputStream al archivo destino
        contentResolver.openInputStream(sourceUri).use { input ->
            destinationFile.outputStream().use { output ->
                input?.copyTo(output)
            }
        }

        // Retorna la URI del archivo copiado
        Log.d("IMAGEN", "Imagen copiada correctamente: ${destinationFile.absolutePath}")
        Uri.fromFile(destinationFile)
    } catch (e: IOException) {
        Log.e("ImageUtils", "Error copiando la imagen", e)
        null
    }
}


@Composable
fun CustomOutlinedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 30.dp, vertical = 8.dp)
    )
}

@Composable
fun CustomButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    containerColor: Color = MaterialTheme.colorScheme.primary,
    contentColor: Color = Color.White
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .padding(horizontal = 30.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor
        )
    ) {
        Text(text)
    }
}

@Composable
fun CategoriaSelectionDialog(
    categorias: List<Categoria>,
    onCategoriaSelected: (Categoria) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Seleccionar Categoría") },
        text = {
            Column {
                categorias.forEach { categoria ->
                    Text(
                        text = categoria.name,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onCategoriaSelected(categoria)
                                onDismiss()
                            }
                            .padding(8.dp)
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Cerrar")
            }
        }
    )
}

@Composable
fun AlertMessageDialog(
    message: String,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Advertencia") },
        text = { Text(message) },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Cerrar")
            }
        }
    )
}