package com.fpuna.carrito.views.categoria

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil3.compose.rememberAsyncImagePainter
import com.fpuna.carrito.models.Categoria
import com.fpuna.carrito.utils.copyCategoryImageToInternalStorage
import com.fpuna.carrito.viewmodel.CategoriaViewModel


@Composable
fun AgregarCategoriaView(navController: NavController, viewModel: CategoriaViewModel) {
    Scaffold() {
        ContentAgregarView(it, navController, viewModel)
    }
}

@Composable
fun ContentAgregarView(
    it: PaddingValues,
    navController: NavController,
    viewModel: CategoriaViewModel
) {
    var nombre by remember { mutableStateOf("") }
    var iconoUri by remember { mutableStateOf<String?>(null) } // Guarda la URI de la imagen

    // Obtén el contexto usando LocalContext
    val context = LocalContext.current

    // ActivityResult para seleccionar una imagen
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        // Llamar a la función para copiar la imagen de categoría
        val copiedUri = uri?.let { sourceUri ->
            copyCategoryImageToInternalStorage(context = context, sourceUri = sourceUri)
        }

        // Si la imagen fue copiada con éxito, guarda la nueva URI
        iconoUri = copiedUri?.toString()
    }

    Column(
        modifier = Modifier
            .padding(it)
            .padding(top = 30.dp)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(
            value = nombre,
            onValueChange = { nombre = it },
            label = { Text("Nombre de la Categoría") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 30.dp)
                .padding(bottom = 15.dp)
        )

        // Botón para seleccionar una imagen
        Button(
            onClick = { launcher.launch("image/*") }, // Filtra solo imágenes
            modifier = Modifier.padding(bottom = 15.dp)
        ) {
            Text("Seleccionar Imagen")
        }

        // Muestra una vista previa de la imagen seleccionada
        if (iconoUri != null) {
            Image(
                painter = rememberAsyncImagePainter(model = iconoUri),
                contentDescription = "Vista previa del ícono",
                modifier = Modifier
                    .size(100.dp)
                    .padding(bottom = 15.dp)
            )
        }

        Button(
            onClick = {
                val categoria = Categoria(
                    name = nombre,
                    icono = iconoUri // Guarda la URI copiada
                )
                viewModel.agregarCategoria(categoria)
                navController.popBackStack()
            }
        ) {
            Text(text = "Agregar Nueva Categoría")
        }
    }
}