package com.fpuna.carrito.views.categoria

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
    // Lanzador para el selector de contenido (selección de imágenes)
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            onImageSelected(it.toString()) // Devuelve la URI seleccionada como cadena
        }
        onDismiss()
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(onClick = { launcher.launch("image/*") }) { // Lanza el selector de imágenes
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
