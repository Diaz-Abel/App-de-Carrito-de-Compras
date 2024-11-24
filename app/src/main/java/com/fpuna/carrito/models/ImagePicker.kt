package com.fpuna.carrito.models
import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun ImagePicker(
    onImageSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    // Crear un lanzador para el selector de imágenes
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        // Cuando se selecciona una imagen, devuelve la URI como cadena
        uri?.let { onImageSelected(it.toString()) }
        onDismiss()
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(onClick = {
                // Lanzar el selector de imágenes con tipo MIME "image/*"
                imagePickerLauncher.launch("image/*")
            }) {
                Text("Seleccionar")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancelar")
            }
        },
        text = { Text("Seleccione una imagen para el ícono") }
    )
}
